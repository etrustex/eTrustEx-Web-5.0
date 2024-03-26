package eu.europa.ec.etrustex.web.integration;

import eu.europa.ec.etrustex.web.exchange.util.SystemAdminUrlTemplates;
import eu.europa.ec.etrustex.web.exchange.util.UrlTemplates;
import eu.europa.ec.etrustex.web.integration.utils.GrantedAuthorityUtils;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.service.pagination.RestResponsePage;
import eu.europa.ec.etrustex.web.service.validation.model.GroupSpec;
import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.*;
import static eu.europa.ec.etrustex.web.service.validation.model.GroupSpecTestUtils.mockGroupSpec;
import static eu.europa.ec.etrustex.web.util.exchange.model.GroupType.BUSINESS;
import static eu.europa.ec.etrustex.web.util.exchange.model.GroupType.ENTITY;
import static eu.europa.ec.etrustex.web.util.validation.ValidationMessage.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings({"java:S100"}) /* Suppress method names false positive. */
class GroupControllerTest extends AbstractControllerTest {
    public static final String THE_TECHNICAL_ID_OF_THE_GROUP = "The technical id of the group";
    public static final String UNIQUE_PER_PARENT_GROUP = "The identifier of the group. Unique per parent group.";
    public static final String THE_NAME_OF_THE_GROUP = "The name of the group";
    public static final String THE_TYPE_OF_THE_GROUP = "The type of the group";
    public static final String THE_DESCRIPTION_OF_THE_GROUP = "The description of the group";
    public static final String THE_PARENT_OF_THE_GROUP = "The parent of the group";
    public static final String THE_RECIPIENT_PREFERENCES_OF_THE_GROUP = "The recipientPreferences of the group";
    public static final String DESCRIPTION = "description";
    private static final String VALID_EMAIL_ADDRESS = "valid@email.address";
    public static final String PARENT_ID_PARAM_NAME = "parentId";
    private static final String GROUP_ID_PARAM_NAME = "groupId";
    private static final String GROUP_TYPE_PARAM_NAME = "groupType";

    private final ResponseFieldsSnippet groupResponseFields = relaxedResponseFields(
            fieldWithPath("id").description(THE_TECHNICAL_ID_OF_THE_GROUP),
            fieldWithPath("identifier").description(UNIQUE_PER_PARENT_GROUP),
            fieldWithPath("name").description(THE_NAME_OF_THE_GROUP),
            fieldWithPath("type").description(THE_TYPE_OF_THE_GROUP + " (" + Arrays.toString(GroupType.values()) + ")"),
            fieldWithPath(DESCRIPTION).description(THE_DESCRIPTION_OF_THE_GROUP),
            fieldWithPath("parent").description(THE_PARENT_OF_THE_GROUP),
            fieldWithPath("recipientPreferences").description(THE_RECIPIENT_PREFERENCES_OF_THE_GROUP),
            fieldWithPath("senderPreferences").description("The senderPreferences of the group"),
            fieldWithPath("newMessageNotificationEmailAddresses").description("New message notification email address"),
            fieldWithPath("statusNotificationEmailAddress").description("Status notification email address"),
            fieldWithPath("templates").description("Group templates"),
            fieldWithPath("pendingDeletion").description("Boolean indicating if the business is marked as deleted")
    );

    private final ResponseFieldsSnippet groupPageResponseFields = relaxedResponseFields(
            fieldWithPath("content[].id").description(THE_TECHNICAL_ID_OF_THE_GROUP),
            fieldWithPath("content[].identifier").description(UNIQUE_PER_PARENT_GROUP),
            fieldWithPath("content[].name").description(THE_NAME_OF_THE_GROUP),
            fieldWithPath("content[].type").description(THE_TYPE_OF_THE_GROUP + " (" + Arrays.toString(GroupType.values()) + ")"),
            fieldWithPath("content[].description").description(THE_DESCRIPTION_OF_THE_GROUP),
            fieldWithPath("content[].parent").description(THE_PARENT_OF_THE_GROUP),
            fieldWithPath("content[].recipientPreferences").description(THE_RECIPIENT_PREFERENCES_OF_THE_GROUP),
            fieldWithPath("content[].senderPreferences").description("The senderPreferences of the group"),
            fieldWithPath("content[].newMessageNotificationEmailAddresses").description("New message notification email address"),
            fieldWithPath("content[].statusNotificationEmailAddress").description("Status notification email address")
    );



    @Autowired
    private GrantedAuthorityUtils grantedAuthorityUtils;


    @Test
    @Transactional
    void integrationTest() throws Exception {
        happyFlows();
        securityAccessTests();
        validationTests();
    }

    private void happyFlows() throws Exception {
        should_create_a_business(sysAdminUserDetails, true);
        should_create_an_entity(abstractTestBusiness.getId(), sysAdminUserDetails, false);
        Group entity = should_create_an_entity(abstractTestBusiness.getId(), businessAdminUserDetails, true);

        should_update_a_group(abstractTestBusiness, sysAdminUserDetails, false);
        should_update_a_group(abstractTestBusiness, businessAdminUserDetails, true);
        should_update_a_group(entity, sysAdminUserDetails, false);
        should_update_a_group(entity, businessAdminUserDetails, false);

        should_disable_a_group(entity, businessAdminUserDetails);

        should_get_a_group(root.getId(), sysAdminUserDetails, false);
        should_get_a_group(abstractTestBusiness.getId(), businessAdminUserDetails, false);
        should_get_a_group(abstractTestEntity.getId(), entityAdminUserDetails, false);
        should_get_a_group(abstractTestEntity.getId(), operatorUserDetails, true);

        should_get_groups(BUSINESS, root.getId(), sysAdminUserDetails, false);
        should_get_groups(ENTITY, abstractTestEntity.getBusinessId(), entityAdminUserDetails, true);

        should_check_is_empty_business(businessAdminUserDetails, abstractTestBusiness.getId());
        should_check_is_empty_entity(businessAdminUserDetails, abstractTestEntity.getId());

        should_get_all_groups(sysAdminUserDetails);

        Group otherBusiness = should_create_a_business(sysAdminUserDetails, false);
        Group otherEntity = should_create_an_entity(abstractTestBusiness.getBusinessId(), businessAdminUserDetails, false);

        should_delete_a_group(otherBusiness.getId(), sysAdminUserDetails, true);
        should_delete_a_group(otherEntity.getId(), businessAdminUserDetails, false);

        should_mark_a_business_to_be_deleted(abstractTestBusiness.getId(), sysAdminUserDetails);
        should_confirm_business_deletion(abstractTestBusiness.getId(), officialInChargeUserDetails);
        should_cancel_business_deletion(abstractTestBusiness.getId(), officialInChargeUserDetails);
    }

    private void securityAccessTests() throws Exception {
        Group otherBusiness = should_create_a_business(sysAdminUserDetails, false);
        Group otherEntity = should_create_an_entity(otherBusiness.getId(), sysAdminUserDetails, false);

        should_not_create_a_group(BUSINESS, root.getId(), businessAdminUserDetails);
        should_not_create_a_group(BUSINESS, root.getId(), entityAdminUserDetails);
        should_not_create_a_group(BUSINESS, root.getId(), operatorUserDetails);
        should_not_create_a_group(ENTITY, abstractTestBusiness.getId(), entityAdminUserDetails);
        should_not_create_a_group(ENTITY, abstractTestBusiness.getId(), operatorUserDetails);
        should_not_create_a_group(ENTITY, otherBusiness.getId(), entityAdminUserDetails);
        should_not_create_a_group(ENTITY, otherBusiness.getId(), operatorUserDetails);

        should_not_update_a_group(root, businessAdminUserDetails);
        should_not_update_a_group(otherBusiness, businessAdminUserDetails);
        should_not_update_a_group(otherBusiness, entityAdminUserDetails);
        should_not_update_a_group(otherBusiness, operatorUserDetails);
        should_not_update_a_group(abstractTestBusiness, entityAdminUserDetails);
        should_not_update_a_group(abstractTestBusiness, operatorUserDetails);
        should_not_update_a_group(otherEntity, businessAdminUserDetails);

        should_not_get_a_group(otherBusiness.getId(), businessAdminUserDetails);
        should_not_get_a_group(otherBusiness.getId(), entityAdminUserDetails);
        should_not_get_a_group(otherBusiness.getId(), operatorUserDetails);

        should_not_get_groups(otherBusiness.getId(), businessAdminUserDetails);
        should_not_get_groups(otherBusiness.getId(), entityAdminUserDetails);

        should_not_delete_a_group(root.getId(), sysAdminUserDetails);
        should_not_delete_a_group(otherBusiness.getId(), businessAdminUserDetails);
        should_not_delete_a_group(otherEntity.getId(), entityAdminUserDetails);
        should_not_delete_a_group(otherEntity.getId(), operatorUserDetails);
    }

    private void validationTests() throws Exception {
        create_validation_should_fail_if_name_exists_for_parent();
        create_validation_should_fail_if_id_or_name_are_not_trimmed();
        create_validation_should_fail_if_fields_are_too_long();
        create_validation_should_fail_if_fields_are_too_short();
        create_validation_should_fail_if_id_contains_not_allowed_chars();
        create_validation_should_fail_with_too_long_notification_addresses();
        create_validation_should_fail_with_invalid_notification_addresses();
        create_validation_should_fail_with_duplicate_addresses();
        create_validation_should_fail_with_too_many_notification_addresses();

        Group business = should_create_a_business(sysAdminUserDetails, true);
        Group entity = should_create_an_entity(business.getId(), sysAdminUserDetails, false);

        update_validation_should_fail_with_too_many_notification_addresses(entity);
        update_validation_should_fail_with_duplicate_addresses(entity);
        update_validation_should_fail_with_invalid_notification_addresses(entity);
        update_validation_should_fail_with_too_long_notification_addresses(entity);
        update_validation_should_fail_if_name_exists_for_parent();
        update_with_type_business_should_fail_validation_with_fields_reserved_for_entities(business);
        update_validation_should_fail_if_name_is_not_trimmed(entity);
        update_validation_should_fail_if_fields_are_too_long(entity);
        update_validation_should_fail_if_fields_are_too_short(entity);

    }

    private Group should_create_a_business(SecurityUserDetails userDetails, boolean withRestDocs) throws Exception {
        GroupSpec groupSpec = GroupSpec.builder()
                .type(BUSINESS)
                .parentGroupId(root.getId())
                .identifier("businessId_" + RANDOM.nextLong())
                .description(THE_DESCRIPTION_OF_THE_GROUP)
                .displayName("business_name_" + RANDOM.nextLong())
                .isActive(true)
                .build();

        return createGroup(groupSpec, userDetails, withRestDocs);
    }


    private Group should_create_an_entity(Long businessId, SecurityUserDetails userDetails, boolean withRestDocs) throws Exception {
        GroupSpec groupSpec = GroupSpec.builder()
                .type(ENTITY)
                .parentGroupId(businessId)
                .identifier("entityId_" + RANDOM.nextLong())
                .displayName("entity_name_" + RANDOM.nextLong())
                .description(THE_DESCRIPTION_OF_THE_GROUP)
                .isActive(true)
                .newMessageNotificationEmailAddresses("entity@email.com,two@email.com")
                .statusNotificationEmailAddress("")
                .build();

        Group group = createGroup(groupSpec, userDetails, withRestDocs);

        assertNotNull(group.getId());
        assertNotNull(group.getIdentifier());

        return group;
    }

    private void should_update_a_group(Group group, SecurityUserDetails userDetails, boolean withRestDocs) throws Exception {
        GroupSpec groupSpec = toSpec(group);
        groupSpec.setIdentifier(String.valueOf(RANDOM.nextLong()));
        groupSpec.setDisplayName(String.valueOf(RANDOM.nextLong()));
        groupSpec.setDescription("loren ipsum....");

        ResultActions resultActions = mockMvc.perform(
                        put(UrlTemplates.GROUP, group.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(groupSpec))
                                .with(user(userDetails)))
                .andExpect(status().isOk());

        if (withRestDocs) {
            resultActions.andDo(document("should_update_a_group",
                    pathParameters(
                            parameterWithName(GROUP_ID_PARAM_NAME).description("The technical id of the group.")
                    ),
                    groupResponseFields
            ));
        }

        MvcResult result = resultActions.andReturn();

        Group modified = objectMapper.readValue(result.getResponse().getContentAsString(), Group.class);

        assertEquals(groupSpec.getDisplayName(), modified.getName());
        assertEquals(groupSpec.getIdentifier(), modified.getIdentifier());
    }

    private void should_disable_a_group(Group group, SecurityUserDetails userDetails) throws Exception {
        GroupSpec groupSpec = toSpec(group);
        groupSpec.setIdentifier(String.valueOf(RANDOM.nextLong()));
        groupSpec.setDisplayName(String.valueOf(RANDOM.nextLong()));
        groupSpec.setActive(false);

        ResultActions resultActions = mockMvc.perform(
                        put(UrlTemplates.GROUP, group.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(groupSpec))
                                .with(user(userDetails)))
                .andExpect(status().isOk());

        MvcResult result = resultActions.andReturn();

        Group modified = objectMapper.readValue(result.getResponse().getContentAsString(), Group.class);

        assertFalse(modified.isActive());
    }

    private Group should_get_a_group(Long groupId, SecurityUserDetails userDetails, boolean withRestDocs) throws Exception {
        ResultActions resultActions = mockMvc.perform(
                        get(UrlTemplates.GROUP, groupId)
                                .with(user(userDetails)))
                .andExpect(status().isOk());

        if (withRestDocs) {
            resultActions.andDo(document("should_get_a_group",
                    pathParameters(
                            parameterWithName(GROUP_ID_PARAM_NAME).description("The technical id of the group.")
                    ),
                    groupResponseFields
            ));
        }

        MvcResult result = resultActions.andReturn();

        Group group = objectMapper.readValue(result.getResponse().getContentAsString(), Group.class);

        assertNotNull(group.getId());
        assertNotNull(group.getIdentifier());
        return group;
    }

    private void should_get_groups(GroupType groupType, Long parentId, SecurityUserDetails userDetails, boolean withRestDocs) throws Exception {
        ResultActions resultActions = mockMvc.perform(
                        get(UrlTemplates.GROUPS)
                                .param(GROUP_TYPE_PARAM_NAME, groupType.name())
                                .param(PARENT_ID_PARAM_NAME, String.valueOf(parentId))
                                .with(user(userDetails)))
                .andExpect(status().isOk());

        if (withRestDocs) {
            resultActions.andDo(document("should_get_a_list_of_groups",
                    relaxedRequestParameters(
                            parameterWithName(GROUP_TYPE_PARAM_NAME).description("The group type of the list (BUSINESS or ENTITY)."),
                            parameterWithName(PARENT_ID_PARAM_NAME).description("The parent id of the groups.")
                    ),
                    groupPageResponseFields
            ));
        }

        MvcResult result = resultActions.andReturn();

        RestResponsePage<?> businessDomainsPage = objectMapper.readValue(result.getResponse().getContentAsString(), RestResponsePage.class);
        assertThat(businessDomainsPage.getContent().size()).isNotZero();
    }

    private void should_check_is_empty_business(SecurityUserDetails userDetails, Long groupId) throws Exception {
        ResultActions resultActions = mockMvc.perform(
                        get(UrlTemplates.IS_BUSINESS_EMPTY, groupId)
                                .with(user(userDetails)))
                .andExpect(status().isOk());

        MvcResult result = resultActions.andReturn();

        assertFalse(Boolean.getBoolean(result.getResponse().getContentAsString()));
    }

    private void should_check_is_empty_entity(SecurityUserDetails userDetails, Long groupId) throws Exception {
        ResultActions resultActions = mockMvc.perform(
                        get(UrlTemplates.IS_GROUP_EMPTY, groupId)
                                .with(user(userDetails)))
                .andExpect(status().isOk());

        MvcResult result = resultActions.andReturn();

        assertFalse(Boolean.getBoolean(result.getResponse().getContentAsString()));
    }

    private void should_get_all_groups(SecurityUserDetails userDetails) throws Exception {
        ResultActions resultActions = mockMvc.perform(
                        get(SystemAdminUrlTemplates.GROUPS_SYS_ADMIN)
                                .with(user(userDetails)))
                .andExpect(status().isOk());

        MvcResult result = resultActions.andReturn();

        RestResponsePage<?> businessDomainsPage = objectMapper.readValue(result.getResponse().getContentAsString(), RestResponsePage.class);
        assertThat(businessDomainsPage.getContent().size()).isNotZero();
    }


    private void should_delete_a_group(Long groupId, SecurityUserDetails userDetails, boolean withRestDocs) throws Exception {
        ResultActions resultActions = mockMvc.perform(
                        delete(UrlTemplates.GROUP_DELETE, groupId)
                                .with(user(userDetails)))
                .andExpect(status().isOk());

        if (withRestDocs) {
            resultActions.andDo(document("should_delete_a_group",
                    pathParameters(
                            parameterWithName(GROUP_ID_PARAM_NAME).description("The technical id of the group to be deleted.")
                    )
            ));
        }

        resultActions.andReturn();
    }

    private void should_mark_a_business_to_be_deleted(Long groupId, SecurityUserDetails userDetails) throws Exception {
        mockMvc.perform(delete(UrlTemplates.BUSINESS_DELETE, groupId)
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andReturn();

        Group group = should_get_a_group(groupId, userDetails, true);
        assertTrue(group.isPendingDeletion());
    }

    private void should_confirm_business_deletion(Long groupId, SecurityUserDetails userDetails) throws Exception {
        mockMvc.perform(delete(UrlTemplates.BUSINESS_CONFIRM_DELETION, groupId)
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andReturn();

        Group group = should_get_a_group(groupId, userDetails, true);
        assertNotNull(group.getRemovedDate());
    }

    private void should_cancel_business_deletion(Long groupId, SecurityUserDetails userDetails) throws Exception {
        mockMvc.perform(put(UrlTemplates.BUSINESS_CANCEL_DELETION, groupId)
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andReturn();

        Group group = should_get_a_group(groupId, userDetails, true);
        assertNull(group.getRemovedDate());
        assertFalse(group.isPendingDeletion());
    }

    private void should_not_create_a_group(GroupType groupType, Long parentGroupId, SecurityUserDetails userDetails) throws Exception {
        GroupSpec groupSpec = GroupSpec.builder()
                .type(groupType)
                .parentGroupId(parentGroupId)
                .identifier(groupType + "_" + RANDOM.nextLong())
                .displayName(String.valueOf(RANDOM.nextLong()))
                .description(THE_DESCRIPTION_OF_THE_GROUP)
                .isActive(true)
                .build();

        mockMvc.perform(post(UrlTemplates.GROUPS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(groupSpec))
                        .with(user(userDetails)))
                .andExpect(status().isForbidden());
    }

    private void should_not_update_a_group(Group group, SecurityUserDetails userDetails) throws Exception {
        GroupSpec groupSpec = toSpec(group);
        groupSpec.setIdentifier(String.valueOf(RANDOM.nextLong()));
        groupSpec.setDisplayName(String.valueOf(RANDOM.nextLong()));
        groupSpec.setDescription(THE_DESCRIPTION_OF_THE_GROUP);

        mockMvc.perform(
                        put(UrlTemplates.GROUP, group.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(groupSpec))
                                .with(user(userDetails)))
                .andExpect(status().isForbidden());
    }

    private void should_not_get_a_group(Long groupId, SecurityUserDetails userDetails) throws Exception {
        mockMvc.perform(get(UrlTemplates.GROUP, groupId)
                        .with(user(userDetails)))
                .andExpect(status().isForbidden());
    }

    private void should_not_get_groups(Long parentId, SecurityUserDetails userDetails) throws Exception {
        mockMvc.perform(
                        get(UrlTemplates.GROUPS)
                                .param(GROUP_TYPE_PARAM_NAME, GroupType.BUSINESS.name())
                                .param(PARENT_ID_PARAM_NAME, String.valueOf(parentId))
                                .with(user(userDetails)))
                .andExpect(status().isForbidden());
    }

    private void should_not_delete_a_group(Long groupId, SecurityUserDetails userDetails) throws Exception {
        mockMvc.perform(delete(UrlTemplates.BUSINESS_DELETE, groupId)
                        .with(user(userDetails)))
                .andExpect(status().isForbidden());

    }

    private Group createGroup(GroupSpec groupSpec, SecurityUserDetails userDetails, boolean withRestDocs) throws Exception {
        ResultActions resultActions = mockMvc.perform(
                        post(UrlTemplates.GROUPS)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(groupSpec))
                                .with(user(userDetails)))
                .andExpect(status().isCreated());

        if (withRestDocs) {
            resultActions.andDo(document("should_create_a_group",
                    groupResponseFields,
                    RestTestUtils.groupHateoasLinks
            ));
        }

        MvcResult result = resultActions.andReturn();

        return objectMapper.readValue(result.getResponse().getContentAsString(), Group.class);
    }


    void create_validation_should_fail_if_name_exists_for_parent() throws Exception {
        GroupSpec groupSpec = mockGroupSpec(
                abstractTestBusiness.getBusinessId(),
                String.valueOf(RANDOM.nextLong()),
                abstractTestEntity.getName(), DESCRIPTION);

        runCreateGroupValidationTest(groupSpec, businessAdminUserDetails, UNIQUE_GROUP_NAME_ERROR_MSG);
    }


    void create_validation_should_fail_if_id_or_name_are_not_trimmed() throws Exception {
        GroupSpec groupSpec = mockGroupSpec(null, " id", "name ", "description ");

        runCreateGroupValidationTest(groupSpec, sysAdminUserDetails, GROUP_IDENTIFIER_TRIM_ERROR_MSG, DISPLAY_NAME_TRIM_ERROR_MSG, NAME_VALID_ERROR_MSG);
    }


    void create_validation_should_fail_if_id_contains_not_allowed_chars() throws Exception {
        GroupSpec groupSpec = mockGroupSpec(null, "id*1", "name", DESCRIPTION);

        runCreateGroupValidationTest(groupSpec, sysAdminUserDetails, NAME_VALID_ERROR_MSG);
    }


    void create_validation_should_fail_if_fields_are_too_long() throws Exception {
        GroupSpec groupSpec = mockGroupSpec(null, StringUtils.repeat("c", 256), StringUtils.repeat("c", 101), StringUtils.repeat("c", 256));

        runCreateGroupValidationTest(groupSpec, sysAdminUserDetails, GROUP_IDENTIFIER_LENGTH_ERROR_MSG, DISPLAY_NAME_LENGTH_ERROR_MSG, DESCRIPTION_LENGTH_ERROR_MSG);
    }


    void create_validation_should_fail_if_fields_are_too_short() throws Exception {
        GroupSpec groupSpec = mockGroupSpec(null, "", null, "");

        runCreateGroupValidationTest(groupSpec, sysAdminUserDetails, GROUP_IDENTIFIER_LENGTH_ERROR_MSG, DISPLAY_NAME_LENGTH_ERROR_MSG, DESCRIPTION_LENGTH_ERROR_MSG);
    }

    void create_validation_should_fail_with_too_many_notification_addresses() throws Exception {
        GroupSpec groupSpec = mockGroupSpecWithTooManyAddresses();
        runCreateGroupValidationTest(groupSpec, sysAdminUserDetails, EMAIL_ADDRESS_LIST_NOT_VALID_ERROR_MSG, EMAIL_ADDRESS_NOT_VALID_ERROR_MSG);
    }

    void create_validation_should_fail_with_duplicate_addresses() throws Exception {
        GroupSpec groupSpec = mockGroupSpecWithDuplicateAddresses();
        runCreateGroupValidationTest(groupSpec, sysAdminUserDetails, EMAIL_ADDRESS_LIST_NOT_VALID_ERROR_MSG, EMAIL_ADDRESS_NOT_VALID_ERROR_MSG);
    }


    void create_validation_should_fail_with_invalid_notification_addresses() throws Exception {
        GroupSpec groupSpec = mockGroupSpecWithInvalidAddresses();
        runCreateGroupValidationTest(groupSpec, sysAdminUserDetails, EMAIL_ADDRESS_LIST_NOT_VALID_ERROR_MSG, EMAIL_ADDRESS_NOT_VALID_ERROR_MSG);
    }


    void create_validation_should_fail_with_too_long_notification_addresses() throws Exception {
        GroupSpec groupSpec = mockGroupSpec(TEST_BUSINESS_ID, TEST_ENTITY_IDENTIFIER, TEST_ENTITY_NAME, DESCRIPTION);

        String tooLong = RandomStringUtils.random(246, true, false) + "@email.com";
        groupSpec.setNewMessageNotificationEmailAddresses(tooLong);
        groupSpec.setStatusNotificationEmailAddress(tooLong);

        runCreateGroupValidationTest(groupSpec, sysAdminUserDetails, EMAIL_ADDRESS_LIST_NOT_VALID_ERROR_MSG, EMAIL_ADDRESS_LENGTH_ERROR_MSG);
    }

    void update_validation_should_fail_with_too_many_notification_addresses(Group group) throws Exception {
        GroupSpec groupSpec = mockGroupSpecWithTooManyAddresses();
        runUpdateGroupValidationTest(group.getId(), groupSpec, sysAdminUserDetails, EMAIL_ADDRESS_LIST_NOT_VALID_ERROR_MSG, EMAIL_ADDRESS_NOT_VALID_ERROR_MSG);
    }

    void update_validation_should_fail_with_duplicate_addresses(Group group) throws Exception {
        GroupSpec groupSpec = mockGroupSpecWithDuplicateAddresses();
        runUpdateGroupValidationTest(group.getId(), groupSpec, sysAdminUserDetails, EMAIL_ADDRESS_LIST_NOT_VALID_ERROR_MSG, EMAIL_ADDRESS_NOT_VALID_ERROR_MSG);
    }

    void update_validation_should_fail_with_invalid_notification_addresses(Group group) throws Exception {
        GroupSpec groupSpec = mockGroupSpecWithInvalidAddresses();
        runUpdateGroupValidationTest(group.getId(), groupSpec, sysAdminUserDetails, EMAIL_ADDRESS_LIST_NOT_VALID_ERROR_MSG, EMAIL_ADDRESS_NOT_VALID_ERROR_MSG);
    }


    void update_validation_should_fail_with_too_long_notification_addresses(Group group) throws Exception {
        GroupSpec groupSpec = mockGroupSpec(TEST_BUSINESS_ID, TEST_ENTITY_IDENTIFIER, TEST_ENTITY_NAME, DESCRIPTION);

        String tooLong = RandomStringUtils.random(246, true, false) + "@email.com";
        groupSpec.setNewMessageNotificationEmailAddresses(tooLong);
        groupSpec.setStatusNotificationEmailAddress(tooLong);

        runUpdateGroupValidationTest(group.getId(), groupSpec, sysAdminUserDetails, EMAIL_ADDRESS_LIST_NOT_VALID_ERROR_MSG, EMAIL_ADDRESS_LENGTH_ERROR_MSG);
    }

    void update_validation_should_fail_if_name_exists_for_parent() throws Exception {
        Group entity = should_create_an_entity(abstractTestBusiness.getId(), sysAdminUserDetails, false);

        GroupSpec groupSpec = mockGroupSpec(entity);
        groupSpec.setDisplayName(abstractTestEntity.getName());

        runUpdateGroupValidationTest(entity.getId(), groupSpec, sysAdminUserDetails, UNIQUE_GROUP_NAME_ERROR_MSG);
    }


    void update_with_type_business_should_fail_validation_with_fields_reserved_for_entities(Group group) throws Exception {
        GroupSpec groupSpec = mockGroupSpec(group);

        groupSpec.setType(BUSINESS);
        groupSpec.setNewMessageNotificationEmailAddresses("fake1@address.com");
        groupSpec.setStatusNotificationEmailAddress("fake2@address.com");
        groupSpec.setRecipientPreferencesId(1234L);
        groupSpec.setSenderPreferencesId(5678L);

        runUpdateGroupValidationTest(group.getId(), groupSpec, sysAdminUserDetails,
                BUSINESS_CANNOT_HAVE_A_STATUS_NOTIFICATION_EMAIL,
                BUSINESS_CANNOT_HAVE_NEW_MESSAGE_NOTIFICATION_EMAILS,
                BUSINESS_CANNOT_HAVE_RECIPIENT_PREFERENCES
        );
    }


    void update_validation_should_fail_if_name_is_not_trimmed(Group group) throws Exception {
        GroupSpec groupSpec = mockGroupSpec(group);
        groupSpec.setDisplayName("name ");

        runUpdateGroupValidationTest(group.getId(), groupSpec, sysAdminUserDetails, DISPLAY_NAME_TRIM_ERROR_MSG);
    }


    void update_validation_should_fail_if_fields_are_too_long(Group group) throws Exception {
        GroupSpec groupSpec = mockGroupSpec(group);
        groupSpec.setIdentifier(StringUtils.repeat("c", 256));
        groupSpec.setDisplayName(StringUtils.repeat("c", 101));
        groupSpec.setDescription(StringUtils.repeat("c", 256));

        runUpdateGroupValidationTest(group.getId(), groupSpec, sysAdminUserDetails,
                GROUP_IDENTIFIER_LENGTH_ERROR_MSG,
                DISPLAY_NAME_LENGTH_ERROR_MSG,
                DESCRIPTION_LENGTH_ERROR_MSG);
    }


    void update_validation_should_fail_if_fields_are_too_short(Group group) throws Exception {
        GroupSpec groupSpec = mockGroupSpec(group);
        groupSpec.setIdentifier("");
        groupSpec.setDisplayName("");
        groupSpec.setDescription("");

        runUpdateGroupValidationTest(group.getId(), groupSpec, sysAdminUserDetails,
                GROUP_IDENTIFIER_LENGTH_ERROR_MSG,
                DISPLAY_NAME_LENGTH_ERROR_MSG,
                DESCRIPTION_LENGTH_ERROR_MSG);
    }

    @SuppressWarnings("SameParameterValue")
    private void runCreateGroupValidationTest(GroupSpec groupSpec, SecurityUserDetails userDetails, String... expectedErrors) throws Exception {
        MvcResult result = mockMvc.perform(
                        post(UrlTemplates.GROUPS)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(groupSpec))
                                .with(user(userDetails)))
                .andExpect(status().isBadRequest())
                .andReturn();


        ValidationTestUtils.checkValidationErrors(result.getResponse(), expectedErrors);
    }

    private void runUpdateGroupValidationTest(Long groupId, GroupSpec groupSpec, SecurityUserDetails userDetails, String... expectedErrors) throws Exception {
        MvcResult result = mockMvc.perform(
                        put(UrlTemplates.GROUP, groupId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(groupSpec))
                                .with(user(userDetails)))
                .andExpect(status().isBadRequest())
                .andReturn();


        ValidationTestUtils.checkValidationErrors(result.getResponse(), expectedErrors);
    }

    private void runDeleteGroupValidationTest(Group groupToDelete, SecurityUserDetails userDetails, String... expectedErrors) throws Exception {
        MvcResult result = mockMvc.perform(
                        delete(UrlTemplates.GROUP_DELETE, groupToDelete.getId())
                                .with(user(userDetails)))
                .andExpect(status().isBadRequest())
                .andReturn();

        ValidationTestUtils.checkValidationErrors(result.getResponse(), expectedErrors);
    }


    private GroupSpec mockGroupSpecWithTooManyAddresses() {
        GroupSpec groupSpec = mockGroupSpec(TEST_BUSINESS_ID, TEST_ENTITY_IDENTIFIER, TEST_ENTITY_NAME, DESCRIPTION);

        groupSpec.setNewMessageNotificationEmailAddresses(
                IntStream.rangeClosed(1, 11)
                        .mapToObj(i -> VALID_EMAIL_ADDRESS)
                        .collect(Collectors.joining(","))
        );
        groupSpec.setStatusNotificationEmailAddress("first@email.com,oneTooMany@email.com");
        return groupSpec;
    }

    private GroupSpec mockGroupSpecWithInvalidAddresses() {
        GroupSpec groupSpec = mockGroupSpec(TEST_BUSINESS_ID, TEST_ENTITY_IDENTIFIER, TEST_ENTITY_NAME, DESCRIPTION);

        groupSpec.setNewMessageNotificationEmailAddresses("first@email.com,invalid;@email.com");
        groupSpec.setStatusNotificationEmailAddress(" invalid@email.com");
        return groupSpec;
    }

    private GroupSpec mockGroupSpecWithDuplicateAddresses() {
        GroupSpec groupSpec = mockGroupSpec(TEST_BUSINESS_ID, TEST_ENTITY_IDENTIFIER, TEST_ENTITY_NAME, DESCRIPTION);

        groupSpec.setNewMessageNotificationEmailAddresses("first@email.com,first@email.com");
        groupSpec.setStatusNotificationEmailAddress(" invalid@email.com");
        return groupSpec;
    }


    private GroupSpec toSpec(Group group) {
        return GroupSpec.builder()
                .identifier(group.getIdentifier())
                .parentGroupId(group.getBusinessId())
                .displayName(group.getName())
                .description(group.getDescription())
                .isActive(group.isActive())
                .type(group.getType())
                .newMessageNotificationEmailAddresses(group.getNewMessageNotificationEmailAddresses())
                .statusNotificationEmailAddress(group.getStatusNotificationEmailAddress())
                .build();
    }

}
