package eu.europa.ec.etrustex.web.integration;

import eu.europa.ec.etrustex.web.exchange.util.UrlTemplates;
import eu.europa.ec.etrustex.web.integration.utils.MessageResult;
import eu.europa.ec.etrustex.web.integration.utils.MessageUtils;
import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummary;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummaryUserStatus;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.repository.MessageSummaryRepository;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import eu.europa.ec.etrustex.web.util.exchange.model.Status;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static eu.europa.ec.etrustex.web.util.crypto.Rsa.CLIENT_PUBLIC_KEY_HEADER_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class StatusAspectTest extends AbstractControllerTest {
    private static final String CLIENT_PUBLIC_KEY_BASE64 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwB3nZzmkaQZgTM2UyZOFfqHRhAWEcvTu2ka0RN4vpHxxMFzCaZMeLykxLpyZBRg3dILGGeNaL+MjPaAJEWcL1CEUxmxFJ9vPiCifG2sBynY5YTUYv64H2DfI76n5TVD+lugZMblWb3FQI2PZwQfhkLjNIXgdGVrrj0y88seUC85ZJqQQiBy3BuCQDC1j+yBp0/tWYEe8Cvlai2E/N0ltnTuQXw4a7CROUhiMJPJYcOaJWrArCGCCBrPix4pkiGcYFrqiThpjil8NbWvyVhFNAdvIiOcxkLc2SWheqOXU1yM1H07dntYUNw580lJcMFpkrhRLCzEO8wAOmpAQw/h38wIDAQAB";
    public static final String THE_SYMMETRIC_KEY = "The symmetric key";
    public static final String MESSAGE_SUMMARY_USER_STATUS_NOT_FOUND = "MessageSummaryUserStatus not found!";

    @Autowired
    private MessageSummaryRepository messageSummaryRepository;

    @Autowired
    private MessageUtils messageUtils;
    private SecurityUserDetails senderUserDetails;
    private SecurityUserDetails recipientUserDetails;
    private Group recipientEntity1;
    private MessageResult messageResult;


    @AfterEach
    public void cleanUp() {
        messageUtils.cleanUp();
    }


    @Test
    void integrationTest() throws Exception {
        initConfigurations();
        happyFlows();
    }

    public void initConfigurations() throws Exception {
        messageResult = messageUtils.createMessageWithNewConfigurations(businessAdminUserDetails, abstractTestUser, 2, false, true);
        recipientEntity1 = messageResult.getRecipientEntities().get(0);
        senderUserDetails = messageResult.getSenderUserDetails();
        recipientUserDetails = userUtils.buildUserDetails(abstractTestUser, recipientEntity1, RoleName.OPERATOR);
    }

    private void happyFlows() throws Exception {
        should_be_called_after_msg_update();  // send message
        should_be_called_after_msg_summary_create();
        should_be_called_after_msg_get();
        should_be_called_after_msg_summary_get();
    }

    public void should_be_called_after_msg_update() {
        assertTrue(messageUserStatusRepository.existsByMessageAndUserAndSenderGroupId(messageResult.getMessage(), senderUserDetails.getUser(), senderUserDetails.getAuthorities().iterator().next().getGroup().getId()));
    }

    public void should_be_called_after_msg_summary_create() {
        MessageSummary messageSummary = messageSummaryRepository
                .findByMessageIdAndRecipientId(messageResult.getMessage().getId(), recipientEntity1.getId())
                .orElseThrow(() -> new Error(MESSAGE_SUMMARY_USER_STATUS_NOT_FOUND));
        Message messageLoaded = messageRepository.findById(messageResult.getMessage().getId())
                .orElseThrow(() -> new Error(MESSAGE_SUMMARY_USER_STATUS_NOT_FOUND));
        assertEquals(Status.SENT, messageSummary.getStatus());
        assertEquals(Status.MULTIPLE, messageLoaded.getStatus());
    }

    public void should_be_called_after_msg_get() throws Exception {
        ResultActions resultActions = mockMvc.perform(
                        RestDocumentationRequestBuilders.get(UrlTemplates.MESSAGE, messageResult.getMessage().getId())
                                .with(user(senderUserDetails))
                                .param("senderEntityId", String.valueOf(senderUserDetails.getAuthorities().iterator().next().getGroup().getId()))
                                .header(CLIENT_PUBLIC_KEY_HEADER_NAME, CLIENT_PUBLIC_KEY_BASE64))
                .andExpect(status().isOk());

        resultActions.andDo(
                document("should_get_the_details_of_a_sent_message",
                        relaxedPathParameters(
                                parameterWithName("messageId").description("The id of the message to retrieve")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("subject").description("The subject"),
                                fieldWithPath("sentOn").description("The date and time the message was sent"),
                                fieldWithPath("text").description("The message body"),
                                fieldWithPath("messageSummaries").description("The recipient-specific information of the message"),
                                fieldWithPath("symmetricKey").description(THE_SYMMETRIC_KEY),
                                fieldWithPath("attachmentTotalNumber").description("The total number of attachments"),
                                fieldWithPath("attachmentsTotalByteLength").description("The cumulative size of the attachments"),
                                fieldWithPath("attachmentSpecs").description("The list of attachments")
                        )
                )
        );

        MvcResult result = resultActions.andReturn();
        Message message = objectMapper.readValue(result.getResponse().getContentAsString(), Message.class);

        assertTrue(messageUserStatusRepository.existsByMessageAndUserAndSenderGroupId(message, senderUserDetails.getUser(), message.getSenderGroup().getId()));
    }

    public void should_be_called_after_msg_summary_get() throws Exception {
        ResultActions resultActions = mockMvc.perform(
                        RestDocumentationRequestBuilders.get(UrlTemplates.MESSAGE_SUMMARY, messageResult.getMessage().getId())
                                .param("recipientEntityId", String.valueOf(recipientEntity1.getId()))
                                .with(user(recipientUserDetails))
                                .header(CLIENT_PUBLIC_KEY_HEADER_NAME, CLIENT_PUBLIC_KEY_BASE64))
                .andExpect(status().isOk());

        resultActions.andDo(
                document("should_get_one_received_message_details",
                        pathParameters(
                                parameterWithName("messageId").description("The UUID of the message to retrieve")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("confidentiality").description("The confidentiality requirements of the message"),
                                fieldWithPath("symmetricKey").description(THE_SYMMETRIC_KEY),
                                fieldWithPath("integrity").description("The integrity requirements of the message"),
                                fieldWithPath("message").description("The message."),
                                fieldWithPath("message.attachmentSpecs").description("The list of attachments with names and types"),
                                fieldWithPath("message.subject").description("The message subject"),
                                fieldWithPath("message.text").description("The message content"),
                                fieldWithPath("message.sentOn").description("The date when the message has been sent"),
                                fieldWithPath("message.symmetricKey").description(THE_SYMMETRIC_KEY),
                                fieldWithPath("message.attachmentsTotalByteLength").description("The total size of the attachments"),
                                fieldWithPath("message.attachmentTotalNumber").description("The attachments number")
                        )
                )
        ).andReturn();

        MessageSummary messageSummary = messageSummaryRepository
                .findByMessageIdAndRecipientId(messageResult.getMessage().getId(), recipientEntity1.getId())
                .orElseThrow(() -> new Error("MessageSummary not found!"));
        MessageSummaryUserStatus messageSummaryUserStatus = messageSummaryUserStatusRepository
                .findByMessageSummaryAndUser(messageSummary, recipientUserDetails.getUser())
                .orElseThrow(() -> new Error(MESSAGE_SUMMARY_USER_STATUS_NOT_FOUND));
        assertEquals(Status.READ, messageSummary.getStatus());
        assertEquals(Status.MULTIPLE, messageSummary.getMessage().getStatus());
        assertEquals(Status.READ, messageSummaryUserStatus.getStatus());
    }

}
