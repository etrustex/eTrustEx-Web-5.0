package eu.europa.ec.etrustex.web.integration.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import eu.europa.ec.etrustex.web.common.exchange.ExchangeMode;
import eu.europa.ec.etrustex.web.exchange.util.UrlTemplates;
import eu.europa.ec.etrustex.web.integration.RestTestUtils;
import eu.europa.ec.etrustex.web.node.persistence.repository.ApplicationResponseRepository;
import eu.europa.ec.etrustex.web.persistence.entity.Attachment;
import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.Channel;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.persistence.repository.*;
import eu.europa.ec.etrustex.web.persistence.repository.redirect.RedirectRepository;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.service.EncryptionService;
import eu.europa.ec.etrustex.web.service.validation.model.ChannelSpec;
import eu.europa.ec.etrustex.web.service.validation.model.ExchangeRuleSpec;
import eu.europa.ec.etrustex.web.util.cia.Confidentiality;
import eu.europa.ec.etrustex.web.util.cia.Integrity;
import eu.europa.ec.etrustex.web.util.crypto.Aes;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.util.exchange.model.AttachmentSpec;
import eu.europa.ec.etrustex.web.util.exchange.model.MessageSummarySpec;
import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import eu.europa.ec.etrustex.web.util.exchange.model.SendMessageRequestSpec;
import eu.europa.ec.etrustex.web.util.exchange.model.keys.SymmetricKey;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.crypto.SecretKey;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.*;

import static eu.europa.ec.etrustex.web.integration.RestTestUtils.*;
import static eu.europa.ec.etrustex.web.util.crypto.Base64.encodeToString;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
@Component
@SuppressWarnings({"squid:S107"})
public class MessageUtilsImpl implements MessageUtils {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final MessageRepository messageRepository;
    private final MessageUserStatusRepository messageUserStatusRepository;
    private final AttachmentRepository attachmentRepository;
    private final MessageSummaryRepository messageSummaryRepository;
    private final MessageSummaryUserStatusRepository messageSummaryUserStatusRepository;
    private final RedirectRepository redirectRepository;
    private final UserUtils userUtils;
    private final GroupUtils groupUtils;
    private final EncryptionService encryptionService;
    private final ApplicationResponseRepository applicationResponseRepository;

    private final Random random = new SecureRandom();


    @Override
    public void cleanUp() {
        redirectRepository.deleteAll();
        applicationResponseRepository.deleteAll();
        messageSummaryRepository.deleteAll();
        messageSummaryUserStatusRepository.deleteAll();
        attachmentRepository.deleteAll();
        messageRepository.deleteAll();
        messageUserStatusRepository.deleteAll();
    }

    @Override
    public MessageResult createMessageWithNewConfigurations(SecurityUserDetails businessAdminUserDetails, User senderUser, int numberOfRecipients, boolean withNotifications, boolean andSend, boolean isSenderSystemEntity) {
        Long businessId = businessAdminUserDetails.getAuthorities().iterator().next().getGroup().getBusinessId();
        Channel channel = createChannel(businessId, businessAdminUserDetails);
        Group senderEntity = groupUtils.createEntity(businessId, "sender_" + random.nextLong(), businessAdminUserDetails, isSenderSystemEntity);
        List<Group> recipientEntities = new ArrayList<>();

        createExchangeRule(channel.getId(), senderEntity.getId(), ExchangeMode.SENDER, businessAdminUserDetails);

        try {
            for (int i = 0; i < numberOfRecipients; i++) {
                Group recipientEntity = groupUtils.createEntity(businessId, "recipient_" + random.nextLong(), businessAdminUserDetails, i == 0);
                recipientEntities.add(recipientEntity);
                createExchangeRule(channel.getId(), recipientEntity.getId(), ExchangeMode.RECIPIENT, businessAdminUserDetails);
                userUtils.createUserProfile(senderUser.getEcasId(), recipientEntity.getId(), businessAdminUserDetails, mockMvc, withNotifications);
            }

            userUtils.createUserProfile(senderUser.getEcasId(), senderEntity.getId(), businessAdminUserDetails, mockMvc, withNotifications);
            SecurityUserDetails senderUserDetails = userUtils.buildUserDetails(senderUser, senderEntity, RoleName.OPERATOR);

            Message message;
            if (andSend) {
                Long[] recipientIds = recipientEntities.stream()
                        .map(Group::getId)
                        .toArray(Long[]::new);
                message = createAndSendMessage(senderUserDetails, recipientIds);
            } else {
                message = createMessage(mockMvc, senderUserDetails);
            }

            addAttachmentToMessage(message);

            return MessageResult.builder()
                    .senderUserDetails(senderUserDetails)
                    .message(message)
                    .recipientEntities(recipientEntities)
                    .senderEntity(senderEntity)
                    .build();
        } catch (Exception e) {
            throw new EtxWebException(e);
        }
    }


    @Override
    public MessageResult createMessageWithNewConfigurations(SecurityUserDetails businessAdminUserDetails,
                                                            User senderUser,
                                                            int numberOfRecipients,
                                                            boolean withNotifications,
                                                            boolean andSend) {
       return createMessageWithNewConfigurations(businessAdminUserDetails, senderUser, numberOfRecipients, withNotifications, andSend, false);
    }


    @Override
    public Attachment createMessageAttachment(SecurityUserDetails userDetails, Message message) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("messageId", String.valueOf(message.getId()));
        params.add("numberOfAttachments", "1");

        try {
            MvcResult result = mockMvc.perform(
                            post(UrlTemplates.ATTACHMENTS)
                                    .with(user(userDetails))
                                    .params(params))
                    .andExpect(status().isCreated())
                    .andReturn();

            Attachment attachment = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<Attachment>>() {
            }).get(0);

            assertNotNull(attachment.getId());

            attachment.setMessage(message);

            return attachment;
        } catch (Exception e) {
            throw new EtxWebException(e);
        }

    }

    @Override
    public void uploadAttachment(Attachment attachment, SecurityUserDetails userDetails) {
        try {
            mockMvc.perform(
                            put(UrlTemplates.ATTACHMENT_FILE, attachment.getId())
                                    .with(user(userDetails))
                                    .param("messageId", String.valueOf(attachment.getMessage().getId()))
                                    .param("senderEntityId", String.valueOf(attachment.getMessage().getSenderGroup().getId()))
                                    .content("I am an attachment".getBytes())
                                    .contentType("application/octet-stream")
                    )
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            throw new EtxWebException(e);
        }
    }

    @Override
    public Message createAndSendMessage(SecurityUserDetails userDetails, Long... recipientEntityIds) throws JsonProcessingException {
        return createAndSendMessage(userDetails, false, recipientEntityIds);
    }

    @Override
    public Message createAndSendMessage(SecurityUserDetails userDetails, boolean isSigned, Long... recipientEntityIds) throws JsonProcessingException {
        SecretKey messageAesKey = Aes.generateAesKey();
        byte[] iv = Aes.generateIv();
        String encryptedMessageText = Aes.gcmEncryptAndEncode(messageAesKey, iv, TEXT_BODY);

        byte[] encryptedTemplatesTextBytes = Aes.gcmEncrypt(messageAesKey, iv, objectMapper.writeValueAsString(TEMPLATE_VARIABLES).getBytes());
        String encryptedTemplates = encodeToString(encryptedTemplatesTextBytes);

        byte[] messageAesKeyB64 = messageAesKey.getEncoded();

        String encryptedKey = encryptionService.encryptWithServerPublicKey(messageAesKeyB64);
        SymmetricKey encryptedSymmetricKey = SymmetricKey.builder()
                .randomBits(encryptedKey)
                .encryptionMethod(SymmetricKey.EncryptionMethod.RSA_OAEP_E2E)
                .build();

        return createAndSendMessage(userDetails, encryptedSymmetricKey, encodeToString(iv), encryptedMessageText, encryptedTemplates, isSigned, recipientEntityIds);
    }

    @Override
    public Channel createChannel(Long businessId, SecurityUserDetails userDetails) {
        try {
            ChannelSpec channelSpec = ChannelSpec.builder()
                    .businessId(businessId)
                    .description(businessId + " channel description")
                    .isActive(true)
                    .name(businessId + " channel name")
                    .defaultChannel(false)
                    .defaultExchangeMode(null)
                    .build();

            MvcResult result = mockMvc.perform(
                            post(UrlTemplates.CHANNELS)
                                    .with(user(userDetails))
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(channelSpec))
                    )
                    .andExpect(status().isCreated())
                    .andReturn();

            return objectMapper.readValue(result.getResponse().getContentAsString(), Channel.class);
        } catch (Exception e) {
            throw new EtxWebException(e);
        }
    }

    @Override
    public void createExchangeRule(Long channelId, Long groupId, ExchangeMode exchangeMode, SecurityUserDetails userDetails) {
        List<ExchangeRuleSpec> exchangeRuleSpec = Collections.singletonList(ExchangeRuleSpec.builder()
                .exchangeMode(exchangeMode)
                .channelId(channelId)
                .memberId(groupId)
                .build());

        try {
            mockMvc.perform(post(UrlTemplates.EXCHANGE_RULES_BULK)
                            .with(user(userDetails))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(exchangeRuleSpec))

                    )
                    .andExpect(status().isCreated())
                    .andReturn();
        } catch (Exception e) {
            throw new EtxWebException(e);
        }
    }

    @Override
    public void sendMessage(Message message, SecurityUserDetails userDetails, SymmetricKey encryptedSymmetricKey, String iv, String encryptedText, String encryptedTemplates, boolean isSigned, Long... recipientEntityIds) {
        MessageSummarySpec[] messageSummarySpecs = Arrays.stream(recipientEntityIds)
                .map(RestTestUtils::mockMessageSummarySpec).toArray(MessageSummarySpec[]::new);


        SendMessageRequestSpec sendMessageRequestSpec = mockSendMessageRequestWithEncryptedText(encryptedSymmetricKey, iv, encryptedText, encryptedTemplates, messageSummarySpecs);

        try {
            sendMessageRequestSpec.setAttachmentSpecs(Arrays.asList(
                    AttachmentSpec.builder().id(1L).name("/a/" + UUID.randomUUID()).build(),
                    AttachmentSpec.builder().id(2L).name("/b/" + UUID.randomUUID()).build()));

            if (isSigned) {
                Object[] fieldsForSignature = new Object[]{
                        sendMessageRequestSpec.getAttachmentSpecs(),
                        sendMessageRequestSpec.getAttachmentsTotalByteLength(),
                        sendMessageRequestSpec.getAttachmentTotalNumber(),
                        sendMessageRequestSpec.getSubject(),
                        Confidentiality.LIMITED_HIGH,
                        Integrity.MODERATE,
                        sendMessageRequestSpec.getText(),
                        sendMessageRequestSpec.getTemplateVariables()
                };

                String payload = objectMapper.writeValueAsString(fieldsForSignature);
                String signature = sign(payload);
                sendMessageRequestSpec.getRecipients().forEach(messageSummarySpec -> messageSummarySpec.setSignature(signature));
                for (MessageSummarySpec messageSummarySpec : messageSummarySpecs) {
                    messageSummarySpec.setSignature(signature);
                }
            }

            mockMvc.perform(put(UrlTemplates.MESSAGE, message.getId().toString())
                            .with(user(userDetails))
                            .content(new ObjectMapper().writeValueAsBytes(sendMessageRequestSpec))
                            .contentType("application/json"))
                    .andExpect(status().isOk())
                    .andReturn();

        } catch (Exception e) {
            throw new EtxWebException(e);
        }
    }

    @Override
    public PublicKey getServerPublicKey() {
        return encryptionService.getServerKeyPair().getPublic();
    }

    @Override
    public long getTotalNumberOfMessages() {
        return messageRepository.count();
    }


    private Message createMessage(MockMvc mockMvc, SecurityUserDetails userDetails) {
        try {
            MvcResult result = mockMvc.perform(
                            post(UrlTemplates.MESSAGES)
                                    .param("senderEntityId", String.valueOf(userDetails.getAuthorities().iterator().next().getGroup().getId()))
                                    .with(user(userDetails)))
                    .andExpect(status().isCreated())
                    .andReturn();

            Message message = objectMapper.readValue(result.getResponse().getContentAsString(), Message.class);
            assertNotNull(message.getId());

            return message;
        } catch (Exception e) {
            throw new EtxWebException(e);
        }

    }

    private Message createAndSendMessage(SecurityUserDetails userDetails, SymmetricKey encryptedSymmetricKey, String iv, String encryptedText, String encryptedTemplates, boolean isSigned, Long... recipientEntityIds) {
        Message message = createMessage(mockMvc, userDetails);
        sendMessage(message, userDetails, encryptedSymmetricKey, iv, encryptedText, encryptedTemplates, isSigned, recipientEntityIds);

        return message;
    }


    private String sign(String payload) throws JOSEException {
        PrivateKey privateKey = encryptionService.getServerKeyPair().getPrivate();
        JWSSigner signer = new RSASSASigner(privateKey);

        String issuer = "https://etrustexweb.ec.europa.eu";
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(payload)
                .issuer(issuer)
                .expirationTime(new Date(new Date().getTime() + 60 * 1000))
                .build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), claimsSet);
        signedJWT.sign(signer);

        return signedJWT.serialize();
    }

    private void addAttachmentToMessage(Message message) {
        message.setAttachmentSpecs(Arrays.asList(
                AttachmentSpec.builder().name("/a/" + UUID.randomUUID()).build(),
                AttachmentSpec.builder().name("/b/" + UUID.randomUUID()).build()));
    }
}
