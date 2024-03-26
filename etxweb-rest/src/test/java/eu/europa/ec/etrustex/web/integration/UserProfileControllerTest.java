package eu.europa.ec.etrustex.web.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import eu.europa.ec.etrustex.web.util.validation.ValidationMessage;
import eu.europa.ec.etrustex.web.exchange.util.UrlTemplates;
import eu.europa.ec.etrustex.web.persistence.entity.UserProfile;
import eu.europa.ec.etrustex.web.persistence.entity.security.GrantedAuthority;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.service.pagination.RestResponsePage;
import eu.europa.ec.etrustex.web.service.validation.model.CreateUserProfileSpec;
import eu.europa.ec.etrustex.web.service.validation.model.DeleteUserProfileSpec;
import eu.europa.ec.etrustex.web.service.validation.model.UserProfileSpec;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.Arrays;
import java.util.LinkedHashMap;

import static eu.europa.ec.etrustex.web.util.exchange.model.RoleName.GROUP_ADMIN;
import static eu.europa.ec.etrustex.web.util.exchange.model.RoleName.OPERATOR;
import static eu.europa.ec.etrustex.web.util.validation.ValidationMessage.Constants.*;
import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.TEST_ENTITY_IDENTIFIER;
import static eu.europa.ec.etrustex.web.security.SecurityTestUtils.mockUserDetailsWithRole;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserProfileControllerTest extends AbstractControllerTest {

    public static final String DOMAIN = "@domain.eu";
    public static final String NEW_NAME = " NEWname";
    public static final String ECAS_ID = "ecasID";

    public static final String GROUP_ID_PARAM = "groupId";

    @Test
    void integrationTest() throws Exception {
        happyFlows();
        securityAccessTests();
        validationTests();
    }

    private void happyFlows() throws Exception {
        should_get_the_user_list_items_for_role();
        should_create();
    }

    private void securityAccessTests() throws Exception {
        should_not_be_allowed_to_get_the_user_list_items_for_role();
        should_not_be_allowed_to_get_the_user_list_items_business();
        should_fail_validation_with_not_allowed_sort_field();
        should_fail_to_create_when_group_admin_of_another_business();
        should_allow_business_admin_to_create_for_an_entity_in_his_business();
        should_allow_entity_admin_to_create_for_his_entity();
        should_allow_system_admin_to_delete_for_an_entity_in_his_business();
    }

    private void validationTests() throws Exception {
        should_fail_validation_with_short_fields();
        should_fail_validation_with_null_fields();
        should_fail_validation_with_untrimmed_fields();
        should_fail_validation_with_too_long_fields();
        should_fail_validation_with_no_email_and_new_message_notifications();
        should_fail_validation_with_no_email_and_status_notifications();
        should_fail_validation_if_user_exists_in_group();
    }

    private final TypeReference<RestResponsePage<LinkedHashMap<String, Object>>> restResponsePageOfLinkedHashMap = new TypeReference<RestResponsePage<LinkedHashMap<String, Object>>>() {
    };

    private final ResponseFieldsSnippet createUserProfileResponseFields = relaxedResponseFields(
            fieldWithPath("alternativeEmail").description("The email address for the created profile")
    );


    private static UserProfileSpec mockUserProfileSpec() {
        return UserProfileSpec.builder()
                .ecasId(ECAS_ID)
                .groupId(1L)
                .alternativeEmailUsed(true)
                .alternativeEmail("email@domain.com")
                .name("name")
                .build();
    }

    void should_get_the_user_list_items_for_role() throws Exception {

        grantedAuthorityRepository.save(new GrantedAuthority(otherUser, abstractTestEntity, groupAdminRole));

        MvcResult result = runGetUserListItemsTest(entityAdminUserDetails, status().isOk(), new StringsPair("role", GROUP_ADMIN.name()), new StringsPair(GROUP_ID_PARAM, abstractTestEntity.getId().toString()));

        RestResponsePage<? extends LinkedHashMap<String, Object>> restResponsePage = objectMapper.readValue(result.getResponse().getContentAsString(), restResponsePageOfLinkedHashMap);
        assertEquals(1, restResponsePage.getContent().size());
        assertEquals(otherUser.getEcasId(), restResponsePage.getContent().get(0).get("ecasId"));
    }

    void should_not_be_allowed_to_get_the_user_list_items_for_role() throws Exception {
        runGetUserListItemsTest(operatorUserDetails, status().isForbidden(), new StringsPair("role", GROUP_ADMIN.toString()), new StringsPair(GROUP_ID_PARAM, "3"));
    }


    void should_not_be_allowed_to_get_the_user_list_items_business() throws Exception {
        runGetUserListItemsTest(entityAdminUserDetails, status().isForbidden(), new StringsPair("sortBy", "(up.group.id)"), new StringsPair(GROUP_ID_PARAM, abstractTestBusiness.getId().toString()));
    }


    void should_fail_validation_with_not_allowed_sort_field() throws Exception {
        MvcResult result = runGetUserListItemsTest(businessAdminUserDetails, status().isBadRequest(), new StringsPair(GROUP_ID_PARAM, abstractTestBusiness.getId().toString()), new StringsPair("sortBy", "(forbidden.field)"));
        ValidationTestUtils.checkValidationErrors(result.getResponse(), "sortField.notValid");
    }


    void should_create() throws Exception {
         UserProfileSpec userProfileSpec = CreateUserProfileSpec.builder()
                .ecasId(otherUser.getEcasId()+"NEW")
                .name(otherUser.getEcasId() + NEW_NAME)
                .groupId(abstractTestBusiness.getId())
                .alternativeEmail(otherUser.getEcasId() + DOMAIN)
                .alternativeEmailUsed(true)
                .statusNotification(true)
                .build();

        MvcResult result = runCreate(userProfileSpec, user(sysAdminUserDetails))
                .andExpect(status().isCreated())
                .andDo(document(
                        "create_user_profile",
                        createUserProfileResponseFields
                ))
                .andReturn();

        UserProfile createdUserProfile = objectMapper.readValue(result.getResponse().getContentAsString(), UserProfile.class);

        assertEquals(otherUser.getEcasId() + DOMAIN, createdUserProfile.getAlternativeEmail());
    }


    void should_fail_to_create_when_group_admin_of_another_business() throws Exception {
        UserProfileSpec userProfileSpec = mockUserProfileSpec();

        runCreate(userProfileSpec, user(entityAdminUserDetails))
                .andExpect(status().isForbidden()).andReturn();
    }


    void should_allow_business_admin_to_create_for_an_entity_in_his_business() throws Exception {
        UserProfileSpec userProfileSpec = CreateUserProfileSpec.builder()
                .ecasId(otherUser.getEcasId()+"NEW")
                .name(otherUser.getEcasId() + NEW_NAME)
                .groupId(abstractTestEntity.getId())
                .alternativeEmail(otherUser.getEcasId() + DOMAIN)
                .alternativeEmailUsed(true)
                .statusNotification(true)
                .build();

        runCreate(userProfileSpec, user(businessAdminUserDetails))
                .andExpect(status().isCreated()).andReturn();
    }


    void should_allow_entity_admin_to_create_for_his_entity() throws Exception {
        UserProfileSpec userProfileSpec = CreateUserProfileSpec.builder()
                .ecasId(otherUser.getEcasId()+"AnotherOne")
                .name(otherUser.getEcasId() + NEW_NAME)
                .groupId(abstractTestEntity.getId())
                .alternativeEmail(otherUser.getEcasId() + DOMAIN)
                .alternativeEmailUsed(true)
                .statusNotification(true)
                .build();


        runCreate(userProfileSpec, user(entityAdminUserDetails))
                .andExpect(status().isCreated()).andReturn();
    }


    void should_fail_validation_with_short_fields() throws Exception {
        UserProfileSpec userProfileSpec = UserProfileSpec.builder()
                .name("")
                .ecasId("")
                .build();

        runFailValidationCreateTest(userProfileSpec,
                NAME_LENGTH_ERROR_MSG,
                EU_LOGIN_ID_LENGTH_ERROR_MSG,
                GROUP_ID_NOT_NULL_ERROR_MSG
        );
    }


    void should_fail_validation_with_null_fields() throws Exception {
        UserProfileSpec userProfileSpec = UserProfileSpec.builder()
                .build();

        runFailValidationCreateTest(userProfileSpec,
                GROUP_ID_NOT_NULL_ERROR_MSG,
                NAME_NOT_NULL_ERROR_MSG,
                EU_LOGIN_ID_NOT_NULL_ERROR_MSG
        );
    }


    void should_fail_validation_with_untrimmed_fields() throws Exception {
        UserProfileSpec userProfileSpec = UserProfileSpec.builder()
                .name("def ")
                .alternativeEmail(" a@cd.com")
                .alternativeEmailUsed(true)
                .ecasId(" id  ")
                .build();

        runFailValidationCreateTest(userProfileSpec,
                NAME_TRIM_ERROR_MSG,
                EMAIL_ADDRESS_TRIM_ERROR_MSG,
                EMAIL_ADDRESS_NOT_VALID_ERROR_MSG,
                EU_LOGIN_ID_TRIM_ERROR_MSG,
                GROUP_ID_NOT_NULL_ERROR_MSG
        );
    }


    void should_fail_validation_with_too_long_fields() throws Exception {

        String str256 = RandomStringUtils.random(256, true, false);

        UserProfileSpec userProfileSpec = UserProfileSpec.builder()
                .name(str256)
                .alternativeEmail(str256)
                .alternativeEmailUsed(true)
                .ecasId(RandomStringUtils.random(51, true, false))
                .build();

        runFailValidationCreateTest(userProfileSpec,
                NAME_LENGTH_ERROR_MSG,
                EMAIL_ADDRESS_LENGTH_ERROR_MSG,
                EMAIL_ADDRESS_NOT_VALID_ERROR_MSG,
                EU_LOGIN_ID_LENGTH_ERROR_MSG,
                GROUP_ID_NOT_NULL_ERROR_MSG
        );
    }


    void should_fail_validation_with_no_email_and_new_message_notifications() throws Exception {
        UserProfileSpec userProfileSpec = CreateUserProfileSpec.builder()
                .ecasId(otherUser.getEcasId()+"User2")
                .name(otherUser.getEcasId() + NEW_NAME)
                .groupId(abstractTestEntity.getId())
                .alternativeEmail("")
                .alternativeEmailUsed(true)
                .statusNotification(false)
                .newMessageNotification(true)
                .build();


        MvcResult result = runCreate(userProfileSpec, user(sysAdminUserDetails))
                .andExpect(status().isBadRequest())
                .andReturn();

        ValidationTestUtils.checkValidationErrors(result.getResponse(), NOTIFICATION_PREFERENCES_WITHOUT_EMAIL_ERROR_MSG);
    }


    void should_fail_validation_with_no_email_and_status_notifications() throws Exception {
        UserProfileSpec userProfileSpec = CreateUserProfileSpec.builder()
                .ecasId(otherUser.getEcasId()+"User2")
                .name(otherUser.getEcasId() + NEW_NAME)
                .groupId(abstractTestEntity.getId())
                .alternativeEmail("")
                .alternativeEmailUsed(true)
                .statusNotification(true)
                .newMessageNotification(false)
                .build();

        MvcResult result = runCreate(userProfileSpec, user(sysAdminUserDetails))
                .andExpect(status().isBadRequest())
                .andReturn();

        ValidationTestUtils.checkValidationErrors(result.getResponse(), NOTIFICATION_PREFERENCES_WITHOUT_EMAIL_ERROR_MSG);
    }


    void should_fail_validation_if_user_exists_in_group() throws Exception {
        UserProfileSpec userProfileSpec = CreateUserProfileSpec.builder()
                .ecasId(otherUser.getEcasId())
                .name(otherUser.getEcasId() + NEW_NAME)
                .groupId(abstractTestEntity.getId())
                .alternativeEmail(otherUser.getEcasId() + DOMAIN)
                .alternativeEmailUsed(true)
                .statusNotification(true)
                .build();

        MvcResult result = runCreate(userProfileSpec, user(sysAdminUserDetails))
                .andExpect(status().isBadRequest())
                .andReturn();

        ValidationTestUtils.checkValidationErrors(result.getResponse(), ValidationMessage.formatUserExistsInGroupMessage(abstractTestEntity.getType()));
    }


    void should_allow_system_admin_to_delete_for_an_entity_in_his_business() throws Exception {
        DeleteUserProfileSpec deleteUserProfileSpec = DeleteUserProfileSpec.builder()
                    .ecasId(ECAS_ID)
                    .groupId(1L)
                    .build();

        userProfileService.delete(deleteUserProfileSpec.getEcasId(), deleteUserProfileSpec.getGroupId());

        runDelete(deleteUserProfileSpec, user(sysAdminUserDetails))
                .andExpect(status().isOk()).andReturn();
    }

    private MvcResult runGetUserListItemsTest(SecurityUserDetails userDetails, ResultMatcher expectedResult, StringsPair... params) throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = get(UrlTemplates.USER_LIST_ITEMS)
                .with(user(userDetails));
        Arrays.stream(params).forEach(stringStringPair -> mockHttpServletRequestBuilder.param(stringStringPair.getKey(), stringStringPair.getValue()));

        return mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(expectedResult)
                .andReturn();
    }

    private ResultActions runCreate(UserProfileSpec userProfileSpec, RequestPostProcessor user) throws Exception {
        return mockMvc.perform(
                post(UrlTemplates.USER_PROFILES)
                        .with(user)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userProfileSpec))
                        .param(GROUP_ID_PARAM, TEST_ENTITY_IDENTIFIER)
        );
    }

    private ResultActions runDelete(DeleteUserProfileSpec deleteUserProfileSpec, RequestPostProcessor user) throws Exception {
        return mockMvc.perform(
                        delete(UrlTemplates.USER_PROFILE_DELETE)
                                .with(user)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(deleteUserProfileSpec)
                )
        );
    }

    private void runFailValidationCreateTest(UserProfileSpec userProfileSpec, String... expectedErrors) throws Exception {
        MvcResult result = runCreate(userProfileSpec, user(mockUserDetailsWithRole(OPERATOR)))
                .andExpect(status().isBadRequest())
                .andReturn();

        ValidationTestUtils.checkValidationErrors(result.getResponse(), expectedErrors);
    }

    static class StringsPair extends MutablePair<String, String> {
        StringsPair(String left, String right) {
            super(left, right);
        }
    }
}
