package eu.europa.ec.etrustex.web.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import eu.europa.ec.etrustex.web.exchange.model.UserGuideSpec;
import eu.europa.ec.etrustex.web.exchange.util.UrlTemplates;
import eu.europa.ec.etrustex.web.service.UserGuideService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static eu.europa.ec.etrustex.web.integration.AttachmentControllerTest.TEST_FILE_STRING;
import static eu.europa.ec.etrustex.web.util.exchange.model.GroupType.ENTITY;
import static eu.europa.ec.etrustex.web.util.exchange.model.RoleName.OPERATOR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.relaxedRequestParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings({"java:S112", "java:S100"})
class UserGuideControllerTest extends AbstractControllerTest {
    private static final String ROLE_PARAM = "role";
    private static final String GROUP_TYPE_PARAM = "groupType";
    private static final String FILE_NAME_PARAM = "fileName";
    private static final String BUSINESS_ID_PARAM = "businessId";
    private static final String FILE_NAME = "test_file.pdf";

    @Autowired
    UserGuideService userGuideService;

    @Test
    void integrationTest() throws Exception {
        happyFlows();
        securityAccessTests();
    }

    private void happyFlows() throws Exception {
        should_create();
        should_get_user_guide();
        should_get_all_user_guides_by_business_id();
        should_delete();
    }

    private void securityAccessTests() throws Exception {
        should_not_allow_non_authenticated_user();
    }

    void should_create() throws Exception {
        byte[] fakeContent = TEST_FILE_STRING.getBytes();
        mockMvc.perform(put(UrlTemplates.USER_GUIDE_FILE)
                        .with(user(businessAdminUserDetails))
                        .param(BUSINESS_ID_PARAM, String.valueOf(abstractTestBusiness.getId()))
                        .param(ROLE_PARAM, OPERATOR.toString())
                        .param(GROUP_TYPE_PARAM, ENTITY.name())
                        .param(FILE_NAME_PARAM, FILE_NAME)
                        .content(fakeContent)
                        .contentType("application/octet-stream"))
                .andExpect(status().isOk())
                .andReturn();
    }

    void should_get_user_guide() throws Exception {
        MvcResult result = mockMvc
                .perform(get(UrlTemplates.USER_GUIDE)
                        .with(user(operatorUserDetails))
                        .param(BUSINESS_ID_PARAM, String.valueOf(abstractTestBusiness.getId()))
                        .param(ROLE_PARAM, OPERATOR.toString())
                        .param(GROUP_TYPE_PARAM, ENTITY.name()))
                .andExpect(status().isOk())
                .andDo(document(
                        "should_get_user_guide",
                        relaxedRequestParameters(
                                parameterWithName(ROLE_PARAM).description("The Role (OPERATOR, GROUP_ADMIN, SYS_ADMIN) for the combination role-groupType that the User Guide is"),
                                parameterWithName(GROUP_TYPE_PARAM).description("The Group Type (ENTITY, BUSINESS, ROOT) for the combination role-groupType that the User Guide is")
                        )))
                .andReturn();

        byte[] fileContent = result.getResponse().getContentAsByteArray();


        Assertions.assertArrayEquals(TEST_FILE_STRING.getBytes(), fileContent);
    }

    void should_get_all_user_guides_by_business_id() throws Exception {
        MvcResult result = mockMvc
                .perform(get(UrlTemplates.USER_GUIDES, abstractTestBusiness.getId())
                        .with(user(businessAdminUserDetails)))
                .andExpect(status().isOk())
                .andReturn();
        List<UserGuideSpec> userGuideSpec = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<UserGuideSpec>>() {
        });
        assertEquals(userGuideSpec.get(0).getFilename(), FILE_NAME);
    }

    void should_delete() throws Exception {
        mockMvc.perform(delete(UrlTemplates.USER_GUIDE_DELETE)
                        .with(user(businessAdminUserDetails))
                        .param(BUSINESS_ID_PARAM, String.valueOf(abstractTestBusiness.getId()))
                        .param(ROLE_PARAM, OPERATOR.toString())
                        .param(GROUP_TYPE_PARAM, ENTITY.name()))
                .andExpect(status().isOk());
    }

    void should_not_allow_non_authenticated_user() throws Exception {
        mockMvc.perform(
                        get(UrlTemplates.USER_GUIDE)
                                .param(ROLE_PARAM, OPERATOR.toString())
                                .param(GROUP_TYPE_PARAM, ENTITY.name()))
                .andExpect(status().isForbidden());
    }
}