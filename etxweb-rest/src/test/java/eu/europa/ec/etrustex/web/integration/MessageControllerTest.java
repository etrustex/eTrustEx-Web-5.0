package eu.europa.ec.etrustex.web.integration;

import eu.europa.ec.etrustex.web.common.exchange.ExchangeMode;
import eu.europa.ec.etrustex.web.exchange.model.groupconfiguration.BooleanGroupConfigurationSpec;
import eu.europa.ec.etrustex.web.exchange.model.groupconfiguration.ForbiddenExtensionsGroupConfigurationSpec;
import eu.europa.ec.etrustex.web.exchange.util.UrlTemplates;
import eu.europa.ec.etrustex.web.integration.utils.GroupUtils;
import eu.europa.ec.etrustex.web.integration.utils.MessageUtils;
import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.Channel;
import eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.impl.ForbiddenExtensionsGroupConfiguration;
import eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.impl.WindowsCompatibleFilenamesGroupConfiguration;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.service.MessageService;
import eu.europa.ec.etrustex.web.util.exchange.model.*;
import eu.europa.ec.etrustex.web.util.validation.ValidationMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.request.PathParametersSnippet;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.Arrays;
import java.util.Collections;

import static eu.europa.ec.etrustex.web.integration.RestTestUtils.mockMessageSummarySpec;
import static eu.europa.ec.etrustex.web.integration.RestTestUtils.mockSendMessageRequest;
import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.TEST_USER_ID;
import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.mockMessage;
import static eu.europa.ec.etrustex.web.util.crypto.Rsa.CLIENT_PUBLIC_KEY_HEADER_NAME;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/* Sending message with encryption is already tested in UpdateCertificateBatchIntegrationTest */
class MessageControllerTest extends AbstractControllerTest {
    private static final String SUBJECT_PARAM = "subject";
    private static final String SUBJECT_DESCRIPTION = "The subject";
    private static final String SENT_ON_PARAM = "sentOn";
    private static final String SENT_ON_DESCRIPTION = "The date and time the message was sent";
    private static final String ATTACHMENTS_TOTAL_SIZE_PARAM = "attachmentsTotalByteLength";
    private static final String ATTACHMENTS_TOTAL_SIZE_DESCRIPTION = "The cumulative size of the attachments";
    private static final String ATTACHMENTS_TOTAL_NUMBER_PARAM = "attachmentTotalNumber";
    private static final String ATTACHMENTS_TOTAL_NUMBER_DESCRIPTION = "The total number of attachments";
    private static final String TEXT_PARAM = "text";
    private static final String TEXT_DESCRIPTION = "The body of the message";
    private static final String ATTACHMENT_SPECS_PARAM = "attachmentSpecs";
    private static final String ATTACHMENT_SPECS_DESCRIPTION = "The list of attachments with metadata, business specific";
    private static final String TEMPLATE_VARIABLES_PARAM = "templateVariables";
    private static final String TEMPLATE_VARIABLES_DESCRIPTION = "Business specific fields";
    private static final String MESSAGE_ID_PARAM = "messageId";
    private static final String MESSAGE_ID_DESCRIPTION = "The technical id of the message";
    private static final String SENDER_ENTITY_ID_PARAM = "senderEntityId";
    private static final String SYMMETRIC_KEY_PARAM = "symmetricKey";
    private static final String SYMMETRIC_KEY_DESCRIPTION = "The symmetric key used to encrypt the message text";
    private static final String IV_PARAM = "iv";
    private static final String IV_DESCRIPTION = "The Initialisation Vector used to encrypt the message text along the symmetric key";
    


    private final PathParametersSnippet messageIdPathParametersSnippet = pathParameters(
            parameterWithName(MESSAGE_ID_PARAM).description(MESSAGE_ID_DESCRIPTION)
    );

    private static final String CLIENT_PUBLIC_KEY_BASE64 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwB3nZzmkaQZgTM2UyZOFfqHRhAWEcvTu2ka0RN4vpHxxMFzCaZMeLykxLpyZBRg3dILGGeNaL+MjPaAJEWcL1CEUxmxFJ9vPiCifG2sBynY5YTUYv64H2DfI76n5TVD+lugZMblWb3FQI2PZwQfhkLjNIXgdGVrrj0y88seUC85ZJqQQiBy3BuCQDC1j+yBp0/tWYEe8Cvlai2E/N0ltnTuQXw4a7CROUhiMJPJYcOaJWrArCGCCBrPix4pkiGcYFrqiThpjil8NbWvyVhFNAdvIiOcxkLc2SWheqOXU1yM1H07dntYUNw580lJcMFpkrhRLCzEO8wAOmpAQw/h38wIDAQAB";

    private SecurityUserDetails senderUserDetails;
    private Group recipientEntity1;
    private Group recipientEntity2;

    @Autowired
    private MessageService messageService;
    @Autowired
    private MessageUtils messageUtils;
    @Autowired
    private GroupUtils groupUtils;


    @AfterEach
    public void cleanUp() {
        messageUtils.cleanUp();
    }

    @Test
    void integrationTest() throws Exception {
        initConfigurations();
        happyFlows();
        securityAccessTests();
        validationTests();
    }


    private void initConfigurations() {
        Group senderEntity = groupUtils.createEntity(abstractTestBusiness.getId(), MessageControllerTest.class.getSimpleName() + "_sender", businessAdminUserDetails);
        recipientEntity1 = groupUtils.createEntity(abstractTestBusiness.getId(), MessageControllerTest.class.getSimpleName() + "_recipient1", businessAdminUserDetails);
        recipientEntity2 = groupUtils.createEntity(abstractTestBusiness.getId(), MessageControllerTest.class.getSimpleName() + "_recipient2", businessAdminUserDetails);

        Channel channel = messageUtils.createChannel(abstractTestBusiness.getId(), businessAdminUserDetails);

        messageUtils.createExchangeRule(channel.getId(), senderEntity.getId(), ExchangeMode.SENDER, businessAdminUserDetails);
        messageUtils.createExchangeRule(channel.getId(), recipientEntity1.getId(), ExchangeMode.RECIPIENT, businessAdminUserDetails);
        messageUtils.createExchangeRule(channel.getId(), recipientEntity2.getId(), ExchangeMode.RECIPIENT, businessAdminUserDetails);


        User testUser = userService.findByEcasId(TEST_USER_ID);
        Group testEntity = groupService.findById(senderEntity.getId());
        userUtils.createUserProfile(testUser.getEcasId(), senderEntity.getId(), businessAdminUserDetails, mockMvc);
        senderUserDetails = userUtils.buildUserDetails(testUser, testEntity, RoleName.OPERATOR);
    }


    private void happyFlows() throws Exception {
        Message message = should_create(senderUserDetails, true);
        should_send(message, senderUserDetails, true);

        should_get_the_sent_messages();
        should_mark_some_messages_as_read();
        should_get_the_details_of_a_sent_message();
        should_get_the_sent_messages_without_passing_parameters();

        Message draft = should_create(senderUserDetails, false);
        should_save_a_draft(draft);

    }

    private void securityAccessTests() throws Exception {
        should_not_create(entityAdminUserDetails);
        should_not_create(businessAdminUserDetails);

        Message message = should_create(senderUserDetails, true);
        should_not_send(message, operatorUserDetails);
        should_not_get_the_details_of_a_sent_message(operatorUserDetails);
    }


    private void validationTests() throws Exception {
        Message message = should_create(senderUserDetails, false);

        updateGroupConfiguration(ForbiddenExtensionsGroupConfiguration.URL, ForbiddenExtensionsGroupConfigurationSpec.builder().value("PDF,EXE").build());
        should_fail_validation_with_pdf_files(message);
        updateGroupConfiguration(ForbiddenExtensionsGroupConfiguration.URL, ForbiddenExtensionsGroupConfigurationSpec.builder().value("").build());
        updateGroupConfiguration(WindowsCompatibleFilenamesGroupConfiguration.URL, BooleanGroupConfigurationSpec.builder().active(true).build());
        should_fail_validation_with_com3_reserved_name(message);
        should_fail_validation_with_reserved_name_with_extension(message);
        should_fail_validation_with_backslash_reserved_characters_in_the_name(message);
        should_fail_validation_with_question_mark_reserved_characters_in_the_name(message);
        should_fail_validation_with_leading_or_trailing_spaces(message);
        should_fail_validation_with_empty_recipients();
        should_fail_validation_with_null_subject(message);
        should_fail_validation_with_empty_subject(message);
        should_fail_validation_with_subject_too_long(message);
        should_fail_validation_with_repeated_file_names(message);
        updateGroupConfiguration(WindowsCompatibleFilenamesGroupConfiguration.URL, BooleanGroupConfigurationSpec.builder().active(false).build());

        should_send(message, senderUserDetails, false);
        should_fail_to_send_a_message_when_status_is_not_null_or_draft(message);

        Message draft = should_create(senderUserDetails, false);
        should_fail_to_save_as_draft_when_status_is_not_null_or_draft(draft);
    }


    private void should_get_the_sent_messages() throws Exception {
        mockMvc.perform(get(UrlTemplates.MESSAGES)
                        .param(SENDER_ENTITY_ID_PARAM, String.valueOf(senderUserDetails.getAuthorities().iterator().next().getGroup().getId()))
                        .with(user(senderUserDetails)))
                .andExpect(status().isOk())
                .andDo(document("should_get_the_sent_messages",
                        relaxedRequestParameters(
                                parameterWithName("page").optional().description("Page to retrieve"),
                                parameterWithName("size").optional().description("Maximum number of items in the response page."),
                                parameterWithName("sortBy").optional().description("Field by which the results must be sorted."),
                                parameterWithName("sortOrder").optional().description("Sorting direction (ASC/DEC)"),
                                parameterWithName(MESSAGE_ID_PARAM).optional().description("The id of a message we want to page or results to contain (overrides the page parameter).")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("content[].subject").description(SUBJECT_DESCRIPTION),
                                fieldWithPath("content[].sentOn").description(SENT_ON_DESCRIPTION),
                                fieldWithPath("content[].messageSummaries").description("The recipient-specific information of the message"),
                                fieldWithPath("content[].attachmentTotalNumber").description(ATTACHMENTS_TOTAL_NUMBER_DESCRIPTION),
                                fieldWithPath("content[].attachmentsTotalByteLength").description(ATTACHMENTS_TOTAL_SIZE_DESCRIPTION)
                        )
                )).andReturn();
    }

    private void should_get_the_details_of_a_sent_message() throws Exception {
        Message message = messageUtils.createAndSendMessage(senderUserDetails, recipientEntity1.getId(), recipientEntity2.getId());

        mockMvc.perform(get(UrlTemplates.MESSAGE, message.getId())
                        .param(SENDER_ENTITY_ID_PARAM, String.valueOf(senderUserDetails.getAuthorities().iterator().next().getGroup().getId()))
                        .with(user(senderUserDetails))
                        .header(CLIENT_PUBLIC_KEY_HEADER_NAME, CLIENT_PUBLIC_KEY_BASE64))
                .andExpect(status().isOk())
                .andDo(document("should_get_the_details_of_a_sent_message",
                        relaxedPathParameters(
                                parameterWithName(MESSAGE_ID_PARAM).description("The id of the message to retrieve")
                        ),
                        relaxedResponseFields(
                                fieldWithPath(SUBJECT_PARAM).description(SUBJECT_DESCRIPTION),
                                fieldWithPath(SENT_ON_PARAM).description(SENT_ON_DESCRIPTION),
                                fieldWithPath(TEXT_PARAM).description("The message body"),
                                fieldWithPath("messageSummaries").description("The recipient-specific information of the message"),
                                fieldWithPath(SYMMETRIC_KEY_PARAM).description("The symmetric key"),
                                fieldWithPath(ATTACHMENTS_TOTAL_NUMBER_PARAM).description(ATTACHMENTS_TOTAL_NUMBER_DESCRIPTION),
                                fieldWithPath(ATTACHMENTS_TOTAL_SIZE_PARAM).description(ATTACHMENTS_TOTAL_SIZE_DESCRIPTION),
                                fieldWithPath(ATTACHMENT_SPECS_PARAM).description("The list of attachments")
                        )
                ));
    }

    @SuppressWarnings("SameParameterValue")
    private void should_mark_some_messages_as_read() throws Exception {
        Message message = messageUtils.createAndSendMessage(senderUserDetails, recipientEntity1.getId(), recipientEntity2.getId());
        mockMvc.perform(
                        put(UrlTemplates.MESSAGES)
                                .with(user(senderUserDetails))
                                .header(CLIENT_PUBLIC_KEY_HEADER_NAME, CLIENT_PUBLIC_KEY_BASE64)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(Collections.singletonList(message.getId()))
                                ))
                .andExpect(status().isOk())
                .andReturn();

        assertTrue(messageUserStatusRepository.existsByMessageAndUserAndSenderGroupId(message, senderUserDetails.getUser(), senderUserDetails.getAuthorities().iterator().next().getGroup().getId()));
    }

    private void should_get_the_sent_messages_without_passing_parameters() throws Exception {
        mockMvc.perform(get(UrlTemplates.MESSAGES)
                        .param(SENDER_ENTITY_ID_PARAM, String.valueOf(senderUserDetails.getAuthorities().iterator().next().getGroup().getId()))
                        .with(user(senderUserDetails)))
                .andExpect(status().isOk());
    }

    private Message should_create(SecurityUserDetails userDetails, boolean withRestDocs) throws Exception {
        ResultActions resultActions = runCreate(userDetails, status().isCreated());

        if (withRestDocs) {
            resultActions.andDo(document(
                    "should_create_a_message",
                    relaxedResponseFields(
                            fieldWithPath("id").description(MESSAGE_ID_DESCRIPTION),
                            fieldWithPath(SUBJECT_PARAM).description(SUBJECT_DESCRIPTION),
                            fieldWithPath(SENT_ON_PARAM).description("The date and time when the message has been sent"),
                            fieldWithPath(ATTACHMENTS_TOTAL_SIZE_PARAM).description(ATTACHMENTS_TOTAL_SIZE_DESCRIPTION),
                            fieldWithPath(ATTACHMENTS_TOTAL_NUMBER_PARAM).description(ATTACHMENTS_TOTAL_NUMBER_DESCRIPTION),
                            fieldWithPath(TEXT_PARAM).description(TEXT_DESCRIPTION),
                            fieldWithPath(ATTACHMENT_SPECS_PARAM).description(ATTACHMENT_SPECS_DESCRIPTION),
                            fieldWithPath(TEMPLATE_VARIABLES_PARAM).description(TEMPLATE_VARIABLES_DESCRIPTION)
                    ),
                    RestTestUtils.messageHateoasLinks
            ));
        }

        MvcResult result = resultActions.andReturn();

        Message message = objectMapper.readValue(result.getResponse().getContentAsString(), Message.class);
        assertNotNull(message.getId());
        assertFalse(message.getLinks().isEmpty());


        return message;
    }

    private void should_send(Message message, SecurityUserDetails userDetails, boolean withRestDocs) throws Exception {
        ResultActions resultActions = runSend(
                message,
                userDetails,
                status().isOk());

        if (withRestDocs) {
            resultActions.andDo(document("should_send_a_message",
                    messageIdPathParametersSnippet,
                    relaxedRequestFields(
                            fieldWithPath("recipients").description("The list of recipients of the message"),
                            fieldWithPath(SUBJECT_PARAM).description(SUBJECT_DESCRIPTION),
                            fieldWithPath(ATTACHMENTS_TOTAL_SIZE_PARAM).description(ATTACHMENTS_TOTAL_SIZE_DESCRIPTION),
                            fieldWithPath(ATTACHMENTS_TOTAL_NUMBER_PARAM).description(ATTACHMENTS_TOTAL_NUMBER_DESCRIPTION),
                            fieldWithPath(TEXT_PARAM).description(TEXT_DESCRIPTION),
                            fieldWithPath(SYMMETRIC_KEY_PARAM).description(SYMMETRIC_KEY_DESCRIPTION),
                            fieldWithPath(IV_PARAM).description(IV_DESCRIPTION),
                            fieldWithPath(ATTACHMENT_SPECS_PARAM).description(ATTACHMENT_SPECS_DESCRIPTION),
                            fieldWithPath(TEMPLATE_VARIABLES_PARAM).description(TEMPLATE_VARIABLES_DESCRIPTION)
                    )
            ));
        }

        resultActions.andReturn();
    }

    private void should_not_create(SecurityUserDetails userDetails) throws Exception {
        runCreate(userDetails, status().isForbidden());
    }

    private void should_not_send(Message message, SecurityUserDetails userDetails) throws Exception {
        runSend(
                message,
                userDetails,
                status().isForbidden());
    }

    private void should_not_get_the_details_of_a_sent_message(SecurityUserDetails userDetails) throws Exception {
        Message message = should_create(senderUserDetails, false);

        mockMvc.perform(get(UrlTemplates.MESSAGE, message.getId())
                        .param(SENDER_ENTITY_ID_PARAM, String.valueOf(senderUserDetails.getAuthorities().iterator().next().getGroup().getId()))
                        .with(user(userDetails))
                        .header(CLIENT_PUBLIC_KEY_HEADER_NAME, CLIENT_PUBLIC_KEY_BASE64)
                )
                .andExpect(status().isForbidden());
    }

    private ResultActions runCreate(SecurityUserDetails userDetails, ResultMatcher expectedStatus) throws Exception {
        return mockMvc.perform(post(UrlTemplates.MESSAGES)
                        .param(SENDER_ENTITY_ID_PARAM, String.valueOf(userDetails.getAuthorities().iterator().next().getGroup().getId()))
                        .with(user(userDetails)))
                .andExpect(expectedStatus);
    }

    private ResultActions runSend(Message message,
                                  SecurityUserDetails userDetails,
                                  ResultMatcher expectedStatus) throws Exception {
        SendMessageRequestSpec sendMessageRequestSpec = mockSendMessageRequest(
                mockMessageSummarySpec(recipientEntity1.getId()),
                mockMessageSummarySpec(recipientEntity2.getId()));

        AttachmentSpec[] attachmentSpecs = new AttachmentSpec[] {AttachmentSpec.builder().name("/a/name").build(),
                AttachmentSpec.builder().name("/b/name").build()};
                
        sendMessageRequestSpec.setAttachmentSpecs(Arrays.asList(attachmentSpecs));

        return mockMvc.perform(
                        put(UrlTemplates.MESSAGE, message.getId().toString())
                                .with(user(userDetails))
                                .content(objectMapper.writeValueAsBytes(sendMessageRequestSpec))
                                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(expectedStatus);
    }

    void should_fail_to_send_a_message_when_status_is_not_null_or_draft(Message message) throws Exception {
        for (Status status : Status.values()) {
            if (status != null && !Status.DRAFT.equals(status)) {
                runSendMessageTest(
                        message,
                        status().isForbidden(),
                        status,
                        AttachmentSpec.builder().name("/a/name").build(),
                        AttachmentSpec.builder().name("/b/name").build()
                );
            }
        }
    }

    private void should_fail_to_save_as_draft_when_status_is_not_null_or_draft(Message message) throws Exception {
        for (Status status : Status.values()) {
            if (status != null && !Status.DRAFT.equals(status)) {
                runSaveAsDraftTest(message.getId(),
                        status().isForbidden(),
                        status
                );
            }
        }
    }

    private void should_save_a_draft(Message message) throws Exception {
        runSaveAsDraftTest(message.getId(),
                status().isOk(),
                Status.DRAFT
        );
    }

    private void should_fail_validation_with_pdf_files(Message message) throws Exception {
        ForbiddenExtensionsGroupConfiguration forbiddenExtensionsGroupConfiguration = ForbiddenExtensionsGroupConfiguration.builder().stringValue("PDF,EXE").build();

        ResultActions resultActions = runSendMessageTest(message, status().isBadRequest(), null);
        ValidationTestUtils.checkValidationErrors(resultActions.andReturn().getResponse(),
                ValidationMessage.formatForbiddenExtensionsMessage(forbiddenExtensionsGroupConfiguration.getForbiddenExtensions()));
    }

    private void should_fail_validation_with_com3_reserved_name(Message message) throws Exception {
        AttachmentSpec attachmentSpec =
                AttachmentSpec.builder()
                        .path("some/path/with/com3/reserved/folderName")
                        .name("some/path/with/com3/reserved/folderName/aName.pdf")
                        .build();

        runWindowsCompatibleValidationTest(message, attachmentSpec, "com3");
    }

    private void should_fail_validation_with_reserved_name_with_extension(Message message) throws Exception {
        AttachmentSpec attachmentSpec =
                AttachmentSpec.builder()
                        .path("some/path/with/reserved/folderName")
                        .name("some/path/with/reserved/folderName/LPT3.pdf")
                        .build();
        runWindowsCompatibleValidationTest(message, attachmentSpec, "LPT3.pdf");
    }

    private void should_fail_validation_with_backslash_reserved_characters_in_the_name(Message message) throws Exception {
        AttachmentSpec attachmentSpec =
                AttachmentSpec.builder()
                        .name("aNameWithReservedCharacter\\.pdf")
                        .build();
        runWindowsCompatibleValidationTest(message, attachmentSpec, "aNameWithReservedCharacter\\.pdf");

    }

    private void should_fail_validation_with_question_mark_reserved_characters_in_the_name(Message message) throws Exception {
        AttachmentSpec attachmentSpec =
                AttachmentSpec.builder()
                        .name("?")
                        .build();
        runWindowsCompatibleValidationTest(message, attachmentSpec, "?");
    }

    private void should_fail_validation_with_leading_or_trailing_spaces(Message message) throws Exception {
        AttachmentSpec attachmentSpec =
                AttachmentSpec.builder()
                        .path("a/path/with/trailing /space")
                        .name("a/path/with/trailing /space/aValidName")
                        .build();
        runWindowsCompatibleValidationTest(message, attachmentSpec, "trailing ");
    }

    private void should_fail_validation_with_empty_recipients() throws Exception {
        Message message = mockMessage();
        assertNotNull(message);

        SendMessageRequestSpec sendMessageRequestSpec = mockSendMessageRequest();

        ResultActions resultActions = mockMvc.perform(
                put(UrlTemplates.MESSAGE, "" + message.getId())
                        .with(user(senderUserDetails))
                        .content(objectMapper.writeValueAsBytes(sendMessageRequestSpec))
                        .contentType(MediaType.APPLICATION_JSON_VALUE));
        resultActions.andExpect(status().isBadRequest());
    }

    private void should_fail_validation_with_null_subject(Message message) throws Exception {
        runSubjectTest(message, null, status().isBadRequest());
    }

    private void should_fail_validation_with_empty_subject(Message message) throws Exception {
        runSubjectTest(message, "", status().isBadRequest());
    }

    private void should_fail_validation_with_subject_too_long(Message message) throws Exception {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <= Math.ceil(UpdateMessageRequestSpec.MAX_SUBJECT_BYTE_LENGTH / 3F); i++) {
            sb.append("€");
        }
        String longSubject = sb.toString();
        runSubjectTest(message, longSubject, status().isBadRequest());
    }

    private void should_fail_validation_with_repeated_file_names(Message message) throws Exception {
        AttachmentSpec attachmentSpec = AttachmentSpec.builder().name("a/repeated/name").build();

        ResultActions resultActions = runSendMessageTest(message, status().isBadRequest(), null, attachmentSpec, attachmentSpec);
        ValidationTestUtils.checkValidationErrors(resultActions.andReturn().getResponse(),
                ValidationMessage.Constants.FILE_NAME_REPEATED_ERROR_MSG);
    }

    private void runSubjectTest(Message message,
                                String subject,
                                ResultMatcher expected) throws Exception {
        SendMessageRequestSpec sendMessageRequestSpec = mockSendMessageRequest(subject,
                mockMessageSummarySpec(recipientEntity1.getId()),
                mockMessageSummarySpec(recipientEntity2.getId()));

        mockMvc.perform(
                        put(UrlTemplates.MESSAGE, message.getId().toString())
                                .with(user(senderUserDetails))
                                .content(objectMapper.writeValueAsBytes(sendMessageRequestSpec))
                                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(expected);
    }


    private void runWindowsCompatibleValidationTest(Message message, AttachmentSpec attachmentSpec, String expectedFailure) throws Exception {
        ResultActions resultActions = runSendMessageTest(message, status().isBadRequest(), null, attachmentSpec);

        ValidationTestUtils.checkValidationErrors(resultActions.andReturn().getResponse(),
                ValidationMessage.formatWindowsCompatibilityErrorMessage(expectedFailure));
    }

    private ResultActions runSendMessageTest(Message message,
                                             ResultMatcher expectedResult,
                                             Status messageStatus,
                                             AttachmentSpec... attachmentSpecs) throws Exception {
        message.setStatus(messageStatus);

        SendMessageRequestSpec sendMessageRequestSpec = mockSendMessageRequest(
                mockMessageSummarySpec(recipientEntity1.getId()),
                mockMessageSummarySpec(recipientEntity2.getId()));

        if (attachmentSpecs != null && attachmentSpecs.length > 0) {
            sendMessageRequestSpec.setAttachmentSpecs(Arrays.asList(attachmentSpecs));
        }

        return mockMvc.perform(
                        put(UrlTemplates.MESSAGE, message.getId().toString())
                                .with(user(senderUserDetails))
                                .content(objectMapper.writeValueAsBytes(sendMessageRequestSpec))
                                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(expectedResult);
    }

    private void runSaveAsDraftTest(Long messageId,
                                    ResultMatcher expectedResult,
                                    Status messageStatus) throws Exception {
        Message message = messageService.findById(messageId);
        message.setStatus(messageStatus);
        messageService.save(message);

        UpdateMessageRequestSpec updateMessageRequestSpec = mockSendMessageRequest(
                mockMessageSummarySpec(recipientEntity1.getId()),
                mockMessageSummarySpec(recipientEntity2.getId()));

        mockMvc.perform(
                        put(UrlTemplates.DRAFT_MESSAGE, "" + message.getId())
                                .with(user(senderUserDetails))
                                .content(objectMapper.writeValueAsBytes(updateMessageRequestSpec))
                                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(expectedResult);
    }

    private void updateGroupConfiguration(String url, Object spec) throws Exception {
        mockMvc.perform(
                        put(url, abstractTestBusiness.getBusinessId())
                                .with(user(businessAdminUserDetails))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(spec))

                )
                .andExpect(status().isOk())
                .andReturn();
    }
}
