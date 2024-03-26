package eu.europa.ec.etrustex.web.integration;

import eu.europa.ec.etrustex.web.exchange.util.UrlTemplates;
import eu.europa.ec.etrustex.web.service.EncryptionService;
import eu.europa.ec.etrustex.web.util.exchange.model.keys.RsaPublicKeyDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings({"java:S112", "java:S100"}) /* Suppress Define and throw a dedicated exception instead of using a generic one & method names false positive. */
class SettingsControllerTest extends AbstractControllerTest {
    @Autowired
    private EncryptionService encryptionService;

    @Test
    void should_return_the_environment() throws Exception {
        MvcResult result = mockMvc.perform(get(UrlTemplates.ENVIRONMENT))
                .andExpect(status().isOk())
                .andReturn();

        String environment = result.getResponse().getContentAsString();

        assertEquals("dev", environment);
    }

    @Test
    void should_return_the_server_public_key() throws Exception {
        MvcResult result = mockMvc.perform(get(UrlTemplates.PUBLIC_KEY)
                        .with(user(operatorUserDetails))
                )
                .andExpect(status().isOk())
                .andReturn();

        RsaPublicKeyDto rsaPublicKeyDto = objectMapper.readValue(result.getResponse().getContentAsString(), RsaPublicKeyDto.class);

        assertEquals(encryptionService.getServerPublicKey(), rsaPublicKeyDto);
    }
}
