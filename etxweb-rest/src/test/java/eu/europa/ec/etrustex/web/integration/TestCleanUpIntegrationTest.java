package eu.europa.ec.etrustex.web.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import eu.europa.ec.etrustex.web.common.EtrustexWebProperties;
import eu.europa.ec.etrustex.web.integration.utils.MessageResult;
import eu.europa.ec.etrustex.web.integration.utils.MessageUtils;
import eu.europa.ec.etrustex.web.persistence.entity.Attachment;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.service.AttachmentService;
import eu.europa.ec.etrustex.web.service.EncryptionService;
import eu.europa.ec.etrustex.web.util.crypto.Aes;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.util.exchange.model.keys.SymmetricKey;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.IOException;
import java.util.List;

import static eu.europa.ec.etrustex.web.exchange.util.SystemAdminUrlTemplates.CLEAN_UP_TEST;
import static eu.europa.ec.etrustex.web.integration.RestTestUtils.TEMPLATE_VARIABLES;
import static eu.europa.ec.etrustex.web.integration.RestTestUtils.TEXT_BODY;
import static eu.europa.ec.etrustex.web.util.crypto.Aes.generateAesKey;
import static eu.europa.ec.etrustex.web.util.crypto.Aes.generateIvParameterSpec;
import static eu.europa.ec.etrustex.web.util.crypto.Base64.encodeToString;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TestCleanUpIntegrationTest extends AbstractControllerTest {
    @Autowired
    private EtrustexWebProperties etrustexWebProperties;
    @Autowired
    private AttachmentService attachmentService;
    @Autowired
    private EncryptionService encryptionService;
    @Autowired
    private MessageUtils messageUtils;


    @AfterEach
    public void cleanUp() throws IOException {
        FileUtils.deleteDirectory(etrustexWebProperties.getFileUploadDirPath().toFile());
    }

    @Test
    void integrationTest() throws Exception {
        should_clean_after_tests();
        securityAccessTests();
    }

    void should_clean_after_tests() throws Exception {
        MessageResult messageResult = sendMessage();

        Long messageId = messageResult.getMessage().getId();
        List<Attachment> attachmentList = attachmentService.findByMessageId(messageId);
        Long attachmentId = attachmentList.get(0).getId();

        String parentIdentifier = messageResult.getSenderEntity().getParent().getIdentifier();
        String groupIdentifier = messageResult.getSenderEntity().getIdentifier();

        mockMvc.perform(delete(CLEAN_UP_TEST, parentIdentifier, groupIdentifier)
                                .with(user(sysAdminUserDetails)))
                .andExpect(status().isOk());

        assertFalse(messageRepository.existsById(messageId));
        assertFalse(attachmentService.findOptionalById(attachmentId).isPresent());
        assertThrows(EtxWebException.class, () -> attachmentService.getResource(attachmentId));
        assertThat(attachmentService.findByMessageId(messageId).size()).isZero();
    }


    void securityAccessTests() throws Exception {
        should_be_forbidden(operatorUserDetails);
        should_be_forbidden(entityAdminUserDetails);
        should_be_forbidden(businessAdminUserDetails);
    }

    private void should_be_forbidden(SecurityUserDetails userDetails) throws Exception {
        mockMvc.perform(delete(CLEAN_UP_TEST, 1L, 1L)
                        .with(user(userDetails)))
                .andExpect(status().isForbidden());
    }

    private MessageResult sendMessage() throws JsonProcessingException {
        MessageResult messageResult = messageUtils.createMessageWithNewConfigurations(businessAdminUserDetails, abstractTestUser, 1, false, false);
        Attachment attachment = messageUtils.createMessageAttachment(messageResult.getSenderUserDetails(), messageResult.getMessage());
        messageUtils.uploadAttachment(attachment, messageResult.getSenderUserDetails());

        SecretKey secretKey = generateAesKey();
        IvParameterSpec ivParameterSpec = generateIvParameterSpec();
        String randomBits = encodeToString(secretKey.getEncoded());
        String iv = encodeToString(ivParameterSpec.getIV());
        SymmetricKey symmetricKey = SymmetricKey.builder()
                .randomBits(randomBits)
                .encryptionMethod(SymmetricKey.EncryptionMethod.RSA_OAEP_SERVER)
                .build();

        byte[] encryptedMessageTextBytes = Aes.gcmEncrypt(secretKey, ivParameterSpec.getIV(), TEXT_BODY.getBytes());
        String encryptedText = encodeToString(encryptedMessageTextBytes);

        byte[] encryptedTemplatesTextBytes = Aes.gcmEncrypt(secretKey, ivParameterSpec.getIV(), objectMapper.writeValueAsString(TEMPLATE_VARIABLES).getBytes());
        String encryptedTemplates = encodeToString(encryptedTemplatesTextBytes);


        String encryptedRandomBits =  encryptionService.encryptWithServerPublicKey(secretKey.getEncoded());
        symmetricKey.setRandomBits(encryptedRandomBits);

        messageUtils.sendMessage(messageResult.getMessage(), messageResult.getSenderUserDetails(), symmetricKey, iv, encryptedText, encryptedTemplates, false, messageResult.getRecipientEntities().get(0).getId());

        Long messageId = messageResult.getMessage().getId();
        List<Attachment> attachmentList = attachmentService.findByMessageId(messageId);

        assertTrue(messageRepository.existsById(messageId));
        assertThat(attachmentList.isEmpty()).isFalse();

        Long attachmentId = attachmentList.get(0).getId();

        assertTrue(attachmentService.getResource(attachmentId)
                .exists());
        assertThat(messageUtils.getTotalNumberOfMessages()).isPositive();
        assertThat(messageUtils.getTotalNumberOfMessages() > 0).isTrue();

        return messageResult;
    }
}
