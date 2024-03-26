package eu.europa.ec.etrustex.web.integration;

import eu.europa.ec.etrustex.web.exchange.model.UserDetails;
import eu.europa.ec.etrustex.web.exchange.util.UrlTemplates;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends AbstractControllerTest {

    @Test
    void integrationTest() throws Exception {
        happyFlows();
        securityAccessTests();
    }


    private void happyFlows() throws Exception {
        should_get_user_details(operatorUserDetails, true);
        should_get_user_details(entityAdminUserDetails, false);
        should_get_user_details(businessAdminUserDetails, false);
        should_get_user_details(sysAdminUserDetails, false);
    }


    private void securityAccessTests() throws Exception {
        mockMvc.perform(get(UrlTemplates.USER_DETAILS))
                .andExpect(status().isForbidden());
    }

    private void should_get_user_details(SecurityUserDetails userDetails, boolean withRestDocs) throws Exception {
        ResultActions resultActions = mockMvc.perform(
                        get(UrlTemplates.USER_DETAILS)
                                .with(user(userDetails))
                )
                .andExpect(status().isOk());

        if (withRestDocs) {
            resultActions.andDo(document("user_details",
                    relaxedResponseFields(
                            fieldWithPath("username").description("The login of the authenticated user"),
                            fieldWithPath("firstName").description("First name of the group the current user"),
                            fieldWithPath("lastName").description("Last name of the group the current user"),
                            fieldWithPath("email").description("First email address of the group the current user"),
                            fieldWithPath("roles[].groupDescription").description("The role description of the group"),
                            fieldWithPath("roles[].role").description("The role assigned to the user in this group")
                    )
            ));
        }


        MvcResult result = resultActions.andReturn();

        UserDetails returnedUserDetails = objectMapper.readValue(result.getResponse().getContentAsString(), UserDetails.class);

        assertEquals(userDetails.getUser().getEcasId(), returnedUserDetails.getUsername());
    }
}
