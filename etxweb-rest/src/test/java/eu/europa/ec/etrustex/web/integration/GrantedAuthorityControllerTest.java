package eu.europa.ec.etrustex.web.integration;

import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import eu.europa.ec.etrustex.web.util.validation.ValidationMessage;
import eu.europa.ec.etrustex.web.exchange.util.UrlTemplates;
import eu.europa.ec.etrustex.web.persistence.entity.UserProfile;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.service.validation.model.GrantedAuthoritySpec;
import eu.europa.ec.etrustex.web.service.validation.model.UserProfileSpec;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.Arrays;
import java.util.List;

import static eu.europa.ec.etrustex.web.util.exchange.model.RoleName.*;
import static eu.europa.ec.etrustex.web.service.validation.model.GrantedAuthoritySpec.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GrantedAuthorityControllerTest extends AbstractControllerTest {
    private User user;

    @Test
    void integrationTest() throws Exception {
        initConfigurations();
        happyFlows();
        securityAccessTests();
        validationTests();
    }

    private void initConfigurations() throws Exception {
        user = createUser(UserProfileSpec.builder()
                .ecasId("otherUser")
                .groupId(root.getId())
                .alternativeEmail(GrantedAuthorityControllerTest.class.getSimpleName() + "@domain.com")
                .alternativeEmailUsed(true)
                .name(GrantedAuthorityControllerTest.class.getSimpleName())
                .build());

    }

    private void happyFlows() throws Exception {
        should_create(SYS_ADMIN, root.getId(), sysAdminUserDetails, false);
        should_update(SYS_ADMIN, root.getId(), sysAdminUserDetails, false);
        should_delete(SYS_ADMIN, root.getId(), sysAdminUserDetails, false);
        should_create(GROUP_ADMIN, abstractTestBusiness.getId(), sysAdminUserDetails, false);
        should_update(GROUP_ADMIN, abstractTestBusiness.getId(), sysAdminUserDetails, false);
        should_delete(GROUP_ADMIN, abstractTestBusiness.getId(), sysAdminUserDetails, false);
        should_create(GROUP_ADMIN, abstractTestEntity.getId(), sysAdminUserDetails, false);
        should_update(GROUP_ADMIN, abstractTestEntity.getId(), sysAdminUserDetails, false);
        should_delete(GROUP_ADMIN, abstractTestEntity.getId(), sysAdminUserDetails, false);
        should_create(OPERATOR, abstractTestEntity.getId(), sysAdminUserDetails, false);
        should_update(OPERATOR, abstractTestEntity.getId(), sysAdminUserDetails, false);
        should_delete(OPERATOR, abstractTestEntity.getId(), sysAdminUserDetails, false);

        should_create(GROUP_ADMIN, abstractTestBusiness.getId(), businessAdminUserDetails, false);
        should_update(GROUP_ADMIN, abstractTestBusiness.getId(), businessAdminUserDetails, false);
        should_delete(GROUP_ADMIN, abstractTestBusiness.getId(), businessAdminUserDetails, false);
        should_create(GROUP_ADMIN, abstractTestEntity.getId(), businessAdminUserDetails, false);
        should_update(GROUP_ADMIN, abstractTestEntity.getId(), businessAdminUserDetails, false);
        should_delete(GROUP_ADMIN, abstractTestEntity.getId(), businessAdminUserDetails, false);
        should_create(OPERATOR, abstractTestEntity.getId(), businessAdminUserDetails, false);
        should_update(OPERATOR, abstractTestEntity.getId(), businessAdminUserDetails, false);
        should_delete(OPERATOR, abstractTestEntity.getId(), businessAdminUserDetails, false);

        should_create(GROUP_ADMIN, abstractTestEntity.getId(), entityAdminUserDetails, false);
        should_update(GROUP_ADMIN, abstractTestEntity.getId(), entityAdminUserDetails, false);
        should_create(OPERATOR, abstractTestEntity.getId(), entityAdminUserDetails, true);
        should_update(OPERATOR, abstractTestEntity.getId(), entityAdminUserDetails, true);
        should_delete(OPERATOR, abstractTestEntity.getId(), entityAdminUserDetails, true);

        should_bulk_update(abstractTestEntity.getId(), entityAdminUserDetails);
        should_bulk_create(abstractTestEntity.getId(), entityAdminUserDetails);
    }

    private void securityAccessTests() throws Exception {
        should_not_delete_self_granted_authority(sysAdminUserDetails, SYS_ADMIN);

        should_not_create(SYS_ADMIN, root.getId(), businessAdminUserDetails);
        should_not_update(SYS_ADMIN, root.getId(), businessAdminUserDetails);
        should_not_delete_self_granted_authority(businessAdminUserDetails, GROUP_ADMIN);

        should_not_create(GROUP_ADMIN, abstractTestBusiness.getId(), entityAdminUserDetails);
        should_not_update(GROUP_ADMIN, abstractTestBusiness.getId(), entityAdminUserDetails);
        should_not_delete_self_granted_authority(entityAdminUserDetails, GROUP_ADMIN);

        should_not_create(GROUP_ADMIN, abstractTestEntity.getId(), operatorUserDetails);
        should_not_update(GROUP_ADMIN, abstractTestEntity.getId(), operatorUserDetails);
        should_not_delete(GROUP_ADMIN, abstractTestEntity.getId(), operatorUserDetails);
        should_not_create(OPERATOR, abstractTestEntity.getId(), operatorUserDetails);
        should_not_update(OPERATOR, abstractTestEntity.getId(), operatorUserDetails);
        should_not_delete(OPERATOR, abstractTestEntity.getId(), operatorUserDetails);
    }

    private void validationTests() throws Exception {
        should_validate_empty_fields_on_create();
        should_validate_empty_fields_on_delete();
        should_validate_granted_authority_exists_on_create();
    }


    private void should_create(RoleName role, Long groupId, SecurityUserDetails userDetails, boolean withRestDocs) throws Exception {
        ResultActions resultActions = runCreate(role, groupId, userDetails, status().isCreated());

        if (withRestDocs) {
            resultActions.andDo(document("create_granted_authority"));
        }

        resultActions.andReturn();
    }

    private void should_not_create(RoleName role, Long groupId, SecurityUserDetails userDetails) throws Exception {
        runCreate(role, groupId, userDetails, status().isForbidden())
                .andReturn();
    }

    private void should_update(RoleName role, Long groupId, SecurityUserDetails userDetails, boolean withRestDocs) throws Exception {
        ResultActions resultActions = runUpdate(role, groupId, userDetails, status().isOk());

        if (withRestDocs) {
            resultActions.andDo(document("update_granted_authority"));
        }

        resultActions.andReturn();
    }

    private void should_bulk_update(Long groupId, SecurityUserDetails userDetails) throws Exception {
        User user2 = createUser(UserProfileSpec.builder()
                .ecasId("otherUser2")
                .groupId(root.getId())
                .alternativeEmail(GrantedAuthorityControllerTest.class.getSimpleName() + "@domain.com")
                .alternativeEmailUsed(true)
                .name(GrantedAuthorityControllerTest.class.getSimpleName())
                .build());

        List<GrantedAuthoritySpec> grantedAuthoritySpecs = Arrays.asList(
                GrantedAuthoritySpec.builder()
                        .groupId(groupId)
                        .userName(user.getEcasId())
                        .roleName(OPERATOR)
                        .enabled(false)
                        .build(),
                GrantedAuthoritySpec.builder()
                        .groupId(groupId)
                        .userName(user2.getEcasId())
                        .roleName(OPERATOR)
                        .enabled(false)
                        .build()
        );

        mockMvc.perform(
                        put(UrlTemplates.GRANTED_AUTHORITIES_UPDATE_GROUP_BULK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(grantedAuthoritySpecs))
                                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andReturn();
    }

    private void should_bulk_create(Long groupId, SecurityUserDetails userDetails) throws Exception {
        List<GrantedAuthoritySpec> grantedAuthoritySpecs = Arrays.asList(
                GrantedAuthoritySpec.builder()
                        .groupId(groupId)
                        .userName(user.getEcasId())
                        .roleName(OPERATOR)
                        .enabled(false)
                        .build(),
                GrantedAuthoritySpec.builder()
                        .groupId(groupId)
                        .userName(otherUser.getEcasId())
                        .roleName(OPERATOR)
                        .enabled(false)
                        .build()
        );

        mockMvc.perform(
                        post(UrlTemplates.GRANTED_AUTHORITIES_CREATE_BULK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(grantedAuthoritySpecs))
                                .with(user(userDetails)))
                .andExpect(status().isCreated())
                .andReturn();
    }

    private void should_not_update(RoleName role, Long groupId, SecurityUserDetails userDetails) throws Exception {
        runUpdate(role, groupId, userDetails, status().isForbidden())
                .andReturn();
    }

    private void should_delete(RoleName role, Long groupId, SecurityUserDetails userDetails, boolean withRestDocs) throws Exception {
        ResultActions resultActions = runDelete(role, groupId, userDetails, status().isOk());

        if (withRestDocs) {
            resultActions.andDo(document("should_delete_granted_authority"));
        }

        resultActions.andReturn();
    }

    @SuppressWarnings("SameParameterValue")
    private void should_not_delete(RoleName role, Long groupId, SecurityUserDetails userDetails) throws Exception {
        runDelete(role, groupId, userDetails, status().isForbidden())
                .andReturn();
    }

    private void should_not_delete_self_granted_authority(SecurityUserDetails userDetails, RoleName gaRole) throws Exception {
        GrantedAuthoritySpec grantedAuthoritySpec = GrantedAuthoritySpec.builder()
                .groupId(userDetails.getAuthorities().iterator().next().getGroup().getId())
                .userName(userDetails.getUsername())
                .roleName(gaRole)
                .build();

        mockMvc.perform(
                        delete(UrlTemplates.GRANTED_AUTHORITIES)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(grantedAuthoritySpec))
                                .with(user(userDetails))
                )
                .andExpect(status().isForbidden());
    }

    private ResultActions runCreate(RoleName role, Long groupId, SecurityUserDetails userDetails, ResultMatcher expectedStatus) throws Exception {
        GrantedAuthoritySpec grantedAuthoritySpec = GrantedAuthoritySpec.builder()
                .groupId(groupId)
                .userName(user.getEcasId())
                .roleName(role)
                .build();

        return mockMvc.perform(
                        post(UrlTemplates.GRANTED_AUTHORITIES)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(grantedAuthoritySpec))
                                .with(user(userDetails)))
                .andExpect(expectedStatus);
    }

    private ResultActions runUpdate(RoleName role, Long groupId, SecurityUserDetails userDetails, ResultMatcher expectedStatus) throws Exception {
        GrantedAuthoritySpec grantedAuthoritySpec = GrantedAuthoritySpec.builder()
                .groupId(groupId)
                .userName(user.getEcasId())
                .roleName(role)
                .build();

        return mockMvc.perform(
                        put(UrlTemplates.GRANTED_AUTHORITIES_UPDATE_GROUP)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(grantedAuthoritySpec))
                                .with(user(userDetails)))
                .andExpect(expectedStatus);
    }

    private ResultActions runDelete(RoleName role, Long groupId, SecurityUserDetails userDetails, ResultMatcher expectedStatus) throws Exception {
        GrantedAuthoritySpec grantedAuthoritySpec = GrantedAuthoritySpec.builder()
                .groupId(groupId)
                .userName(user.getEcasId())
                .roleName(role)
                .build();

        return mockMvc.perform(
                        delete(UrlTemplates.GRANTED_AUTHORITIES)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(grantedAuthoritySpec))
                                .with(user(userDetails))
                )
                .andExpect(expectedStatus);
    }


    private void should_validate_empty_fields_on_create() throws Exception {
        GrantedAuthoritySpec grantedAuthoritySpec = GrantedAuthoritySpec.builder()
                .userName("")
                .roleName(null)
                .build();

        runCreateValidation(grantedAuthoritySpec, entityAdminUserDetails, status().isBadRequest(), USER_NAME_BLANK_ERROR_MSG,
                GROUP_ID_BLANK_ERROR_MSG,
                ROLE_NAME_BLANK_ERROR_MSG);
    }

    private void should_validate_empty_fields_on_delete() throws Exception {
        GrantedAuthoritySpec grantedAuthoritySpec = GrantedAuthoritySpec.builder()
                .userName("")
                .roleName(null)
                .build();

        runDeleteValidation(grantedAuthoritySpec, entityAdminUserDetails, status().isBadRequest(), USER_NAME_BLANK_ERROR_MSG,
                GROUP_ID_BLANK_ERROR_MSG,
                ROLE_NAME_BLANK_ERROR_MSG);
    }


    private void should_validate_granted_authority_exists_on_create() throws Exception {
        Group group = entityAdminUserDetails.getAuthorities().iterator().next().getGroup();
        GrantedAuthoritySpec grantedAuthoritySpec = GrantedAuthoritySpec.builder()
                .groupId(group.getId())
                .userName(entityAdminUserDetails.getUsername())
                .roleName(GROUP_ADMIN)
                .build();

        runCreateValidation(grantedAuthoritySpec, entityAdminUserDetails, status().isBadRequest(),
                String.format(ValidationMessage.Constants.USER_ALREADY_HAS_GRANTED_AUTHORITY_VALUE, GROUP_ADMIN, StringUtils.capitalize(group.getType().toString().toLowerCase())));
    }

    private void runCreateValidation(GrantedAuthoritySpec grantedAuthoritySpec, SecurityUserDetails userDetails, ResultMatcher expectedStatus, String... expectedErrors) throws Exception {
        MvcResult result = mockMvc.perform(
                        post(UrlTemplates.GRANTED_AUTHORITIES)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(grantedAuthoritySpec))
                                .param("groupId", "" + abstractTestEntity.getId())
                                .with(user(userDetails)))
                .andExpect(expectedStatus)
                .andReturn();

        ValidationTestUtils.checkValidationErrors(result.getResponse(), expectedErrors);
    }

    private void runDeleteValidation(GrantedAuthoritySpec grantedAuthoritySpec, SecurityUserDetails userDetails, ResultMatcher expectedStatus, String... expectedErrors) throws Exception {
        MvcResult result = mockMvc.perform(
                        delete(UrlTemplates.GRANTED_AUTHORITIES)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(grantedAuthoritySpec))
                                .with(user(userDetails))
                )
                .andExpect(expectedStatus)
                .andReturn();

        ValidationTestUtils.checkValidationErrors(result.getResponse(), expectedErrors);
    }


    private User createUser(UserProfileSpec userProfileSpec) throws Exception {
        MvcResult result = mockMvc.perform(
                        post(UrlTemplates.USER_PROFILES)
                                .with(user(sysAdminUserDetails))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userProfileSpec))
                )
                .andExpect(status().isCreated())
                .andReturn();

        UserProfile createdUserProfile = objectMapper.readValue(result.getResponse().getContentAsString(), UserProfile.class);

        return createdUserProfile.getUser();
    }
}
