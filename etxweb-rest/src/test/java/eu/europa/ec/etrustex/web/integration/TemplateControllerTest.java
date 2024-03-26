package eu.europa.ec.etrustex.web.integration;

import eu.europa.ec.etrustex.web.common.template.TemplateType;
import eu.europa.ec.etrustex.web.exchange.model.TemplateSpec;
import eu.europa.ec.etrustex.web.exchange.util.UrlTemplates;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings({"java:S112", "java:S100"})
class TemplateControllerTest extends AbstractControllerTest {

    @Test
    void integrationTest() throws Exception {
        happyFlows();
    }


    private void happyFlows() throws Exception {
        business_admins_can_get_their_templates();
        sys_admins_can_get_any_templates();
        sys_admins_can_get_default_templates();

        business_admins_can_create_a_template_for_their_business();
        sys_admins_can_create_any_template();
    }

    private void business_admins_can_get_their_templates() throws Exception {
        runGetTemplateTest(businessAdminUserDetails, abstractTestBusiness.getId(), status().isOk());
    }

    private void sys_admins_can_get_any_templates() throws Exception {
        runGetTemplateTest(sysAdminUserDetails, abstractTestBusiness.getId(), status().isOk());
    }

    private void sys_admins_can_get_default_templates() throws Exception {
        mockMvc.perform(get(UrlTemplates.DEFAULT_TEMPLATES, abstractTestBusiness.getId())
                        .with(user(sysAdminUserDetails))
                        .queryParam("type", TemplateType.USER_CONFIGURED_NOTIFICATION.name()))
                .andExpect(status().isOk());
    }

    private void business_admins_can_create_a_template_for_their_business() throws Exception {
        runCreateTest(businessAdminUserDetails, abstractTestBusiness.getId(), status().isOk());
    }

    private void sys_admins_can_create_any_template() throws Exception {
        runCreateTest(sysAdminUserDetails, abstractTestBusiness.getId(), status().isOk());
    }

    private void runGetTemplateTest(SecurityUserDetails userDetails, Long businessId, ResultMatcher expectedResult) throws Exception {
        mockMvc.perform(get(UrlTemplates.TEMPLATE, businessId)
                        .with(user(userDetails))
                        .queryParam("type", TemplateType.USER_CONFIGURED_NOTIFICATION.name()))
                .andExpect(expectedResult);
    }

    private void runCreateTest(SecurityUserDetails userDetails, Long businessId, ResultMatcher expectedResult) throws Exception {
        TemplateSpec templateSpec = mockTemplateSpec(businessId);

        mockMvc.perform(
                put(UrlTemplates.TEMPLATES)
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(templateSpec))
        ).andExpect(expectedResult);
    }

    private TemplateSpec mockTemplateSpec(Long businessId) {
        return TemplateSpec.builder()
                .groupId(businessId)
                .type(TemplateType.USER_CONFIGURED_NOTIFICATION)
                .content("<template>")
                .build();
    }
}
