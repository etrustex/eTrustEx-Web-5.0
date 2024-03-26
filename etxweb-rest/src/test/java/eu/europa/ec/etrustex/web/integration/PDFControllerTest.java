package eu.europa.ec.etrustex.web.integration;

import eu.europa.ec.etrustex.web.exchange.util.UrlTemplates;
import eu.europa.ec.etrustex.web.integration.utils.MessageResult;
import eu.europa.ec.etrustex.web.integration.utils.MessageUtils;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import static eu.europa.ec.etrustex.web.util.crypto.Rsa.CLIENT_PUBLIC_KEY_HEADER_NAME;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PDFControllerTest extends AbstractControllerTest {
    private static final String CLIENT_PUBLIC_KEY_BASE64 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwB3nZzmkaQZgTM2UyZOFfqHRhAWEcvTu2ka0RN4vpHxxMFzCaZMeLykxLpyZBRg3dILGGeNaL+MjPaAJEWcL1CEUxmxFJ9vPiCifG2sBynY5YTUYv64H2DfI76n5TVD+lugZMblWb3FQI2PZwQfhkLjNIXgdGVrrj0y88seUC85ZJqQQiBy3BuCQDC1j+yBp0/tWYEe8Cvlai2E/N0ltnTuQXw4a7CROUhiMJPJYcOaJWrArCGCCBrPix4pkiGcYFrqiThpjil8NbWvyVhFNAdvIiOcxkLc2SWheqOXU1yM1H07dntYUNw580lJcMFpkrhRLCzEO8wAOmpAQw/h38wIDAQAB";
    @Autowired
    private MessageUtils messageUtils;

    private MessageResult messageResult;


    @AfterEach
    public void cleanUp() {
        messageUtils.cleanUp();
    }


    @Test
    void integrationTest() throws Exception {
        initConfigurations();
        happyFlows();
        securityAccessTests();
    }

    private void initConfigurations() {
        messageResult = messageUtils.createMessageWithNewConfigurations(businessAdminUserDetails, abstractTestUser, 1, false, true);
    }

    private void happyFlows() throws Exception {
        should_get_the_sent_message_receipt(messageResult.getMessage().getId(), messageResult.getSenderUserDetails());
    }

    private void securityAccessTests() throws Exception {
        should_not_get_the_sent_message_receipt(RANDOM.nextLong(), messageResult.getSenderUserDetails());
        should_not_get_the_sent_message_receipt(messageResult.getMessage().getId(), operatorUserDetails);
    }

    void should_get_the_sent_message_receipt(Long messageId, SecurityUserDetails userDetails) throws Exception {
        MvcResult result = mockMvc.perform(get(UrlTemplates.SENT_MESSAGE_RECEIPT, messageId)
                        .with(user(userDetails))
                        .header(CLIENT_PUBLIC_KEY_HEADER_NAME, CLIENT_PUBLIC_KEY_BASE64)
                )
                .andExpect(status().isOk())
                .andReturn();

        byte[] fileContent = result.getResponse().getContentAsByteArray();

        try (PDDocument document = PDDocument.load(fileContent)) {
            assertNotNull(document.getPage(0).getContents());
        }
    }

    void should_not_get_the_sent_message_receipt(Long messageId, SecurityUserDetails userDetails) throws Exception {
        mockMvc.perform(get(UrlTemplates.SENT_MESSAGE_RECEIPT, messageId)
                        .with(user(userDetails))
                        .header(CLIENT_PUBLIC_KEY_HEADER_NAME, CLIENT_PUBLIC_KEY_BASE64)
                )
                .andExpect(status().isForbidden());
    }
}
