/*
 * Copyright (c) 2018-2023. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package eu.europa.ec.etrustex.web.rest.api.messages;

import eu.europa.ec.etrustex.web.common.exchange.ExchangeMode;
import eu.europa.ec.etrustex.web.exchange.util.UrlTemplates;
import eu.europa.ec.etrustex.web.integration.AbstractControllerTest;
import eu.europa.ec.etrustex.web.integration.utils.GroupUtils;
import eu.europa.ec.etrustex.web.integration.utils.MessageUtils;
import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.Channel;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.util.exception.ApiError;
import eu.europa.ec.etrustex.web.util.exchange.LinkUtils;
import eu.europa.ec.etrustex.web.util.exchange.model.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.Arrays;
import java.util.List;

import static eu.europa.ec.etrustex.web.exchange.api.ApiUrlTemplates.MESSAGE;
import static eu.europa.ec.etrustex.web.exchange.api.ApiUrlTemplates.MESSAGES;
import static eu.europa.ec.etrustex.web.integration.RestTestUtils.mockMessageSummarySpec;
import static eu.europa.ec.etrustex.web.integration.RestTestUtils.mockSendMessageRequest;
import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.TEST_USER_ID;
import static eu.europa.ec.etrustex.web.util.exchange.model.UpdateMessageRequestSpec.MAX_SUBJECT_BYTE_LENGTH;
import static eu.europa.ec.etrustex.web.util.validation.ValidationMessage.Constants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings({"java:S112", "java:S100"}) /* Suppress Define and throw a dedicated exception instead of using a generic one & method names false positive. */
class ApiMessageControllerTest extends AbstractControllerTest {
    private static final String BUSINESS_IDENTIFIER_MESSAGE_PREFIX = "businessIdentifier: ";
    private static final String ENTITY_IDENTIFIER_MESSAGE_PREFIX = "entityIdentifier: ";

    @Autowired
    private MessageUtils messageUtils;
    @Autowired
    private GroupUtils groupUtils;

    private Group recipientEntity1;
    private Group recipientEntity2;
    private Group senderEntity;
    private Group nonSystemSenderEntity;
    private SecurityUserDetails senderUserDetails;
    private SecurityUserDetails otherEntityUserDetails;

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
        senderEntity = groupUtils.createEntity(abstractTestBusiness.getId(), ApiMessageControllerTest.class.getSimpleName() + "_sender", businessAdminUserDetails, true);
        nonSystemSenderEntity = groupUtils.createEntity(abstractTestBusiness.getId(), ApiMessageControllerTest.class.getSimpleName() + "_web_sender", businessAdminUserDetails, false);
        recipientEntity1 = groupUtils.createEntity(abstractTestBusiness.getId(), ApiMessageControllerTest.class.getSimpleName() + "_recipient1", businessAdminUserDetails);
        recipientEntity2 = groupUtils.createEntity(abstractTestBusiness.getId(), ApiMessageControllerTest.class.getSimpleName() + "_recipient2", businessAdminUserDetails);

        Channel channel = messageUtils.createChannel(abstractTestBusiness.getId(), businessAdminUserDetails);

        messageUtils.createExchangeRule(channel.getId(), senderEntity.getId(), ExchangeMode.SENDER, businessAdminUserDetails);
        messageUtils.createExchangeRule(channel.getId(), nonSystemSenderEntity.getId(), ExchangeMode.SENDER, businessAdminUserDetails);
        messageUtils.createExchangeRule(channel.getId(), recipientEntity1.getId(), ExchangeMode.RECIPIENT, businessAdminUserDetails);
        messageUtils.createExchangeRule(channel.getId(), recipientEntity2.getId(), ExchangeMode.RECIPIENT, businessAdminUserDetails);

        User testUser = userService.findByEcasId(TEST_USER_ID);
        userUtils.createUserProfile(testUser.getEcasId(), senderEntity.getId(), businessAdminUserDetails, mockMvc);
        userUtils.createUserProfile(testUser.getEcasId(), nonSystemSenderEntity.getId(), businessAdminUserDetails, mockMvc);
        senderUserDetails = userUtils.buildUserDetails(testUser, senderEntity, RoleName.OPERATOR);
        otherEntityUserDetails = createEntityAndOperatorUserDetails(abstractTestBusiness);
    }

    private void happyFlows() throws Exception {
        Long messageId = should_create(senderUserDetails, senderEntity).getId();
        should_send(messageId, senderUserDetails);
    }

    private void securityAccessTests() throws Exception {
        should_not_create(senderEntity.getBusinessIdentifier(), senderEntity.getIdentifier(), entityAdminUserDetails);
        should_not_create(senderEntity.getBusinessIdentifier(), senderEntity.getIdentifier(), businessAdminUserDetails);
        should_not_create(senderEntity.getBusinessIdentifier(), senderEntity.getIdentifier(), otherEntityUserDetails);
        should_not_create(nonSystemSenderEntity.getBusinessIdentifier(), nonSystemSenderEntity.getIdentifier(), senderUserDetails);

        Long messageId = should_create(senderUserDetails, senderEntity).getId();
        should_not_send(messageId, operatorUserDetails);
        should_not_send(messageId, entityAdminUserDetails);
        should_not_send(messageId, businessAdminUserDetails);
    }

    private void validationTests() throws Exception {
        Message message = should_create(senderUserDetails, senderEntity);

        should_fail_validation_create_with_missing_params();
        should_fail_validation_create_with_empty_params();
        should_fail_validation_create_with_not_trimmed_params();
        should_fail_validation_create_with_wrong_pattern_params();
        should_fail_validation_create_with_too_long_params();

        should_fail_validation_send_empty_fields();
        should_fail_validation_send_duplicate_names();
        should_fail_validation_send_subject_max_byte_length();

        should_send(message.getId(), senderUserDetails);
        should_fail_to_send_a_message_when_status_is_not_null(message);
    }


    private Message should_create(SecurityUserDetails userDetails, Group sender) throws Exception {
        EntitySpec entitySpec = EntitySpec.builder()
                .businessIdentifier(sender.getBusinessIdentifier())
                .entityIdentifier(sender.getIdentifier())
                .build();
        MvcResult result = create(entitySpec, userDetails, status().isCreated())
                .andReturn();

        Message message = objectMapper.readValue(result.getResponse().getContentAsString(), Message.class);
        assertNotNull(message.getId());
        assertFalse(message.getLinks().isEmpty());

        return message;
    }

    private void should_send(Long messageId, SecurityUserDetails userDetails) throws Exception {
        SendMessageRequestSpec sendMessageRequestSpec = mockSendMessageRequest(
                mockMessageSummarySpec(recipientEntity1.getId()),
                mockMessageSummarySpec(recipientEntity2.getId())
        );

        send(messageId, sendMessageRequestSpec, userDetails, status().isOk()).andReturn();
    }

    private void should_not_create(String businessIdentifier, String entityIdentifier, SecurityUserDetails userDetails) throws Exception {
        EntitySpec entitySpec = EntitySpec.builder()
                .businessIdentifier(businessIdentifier)
                .entityIdentifier(entityIdentifier)
                .build();

        create(entitySpec, userDetails, status().isForbidden());
    }

    private void should_not_send(Long messageId, SecurityUserDetails userDetails) throws Exception {
        SendMessageRequestSpec sendMessageRequestSpec = mockSendMessageRequest(
                mockMessageSummarySpec(recipientEntity1.getId()),
                mockMessageSummarySpec(recipientEntity2.getId())
        );

        send(messageId, sendMessageRequestSpec, userDetails, status().isForbidden());
    }


    private void should_fail_validation_create_with_missing_params() throws Exception {
        EntitySpec entitySpec = EntitySpec.builder()
                .build();

        ApiError apiError = getApiErrorForCreate(entitySpec);

        assertTrue(apiError.getMessage().contains(BUSINESS_IDENTIFIER_MESSAGE_PREFIX + GROUP_IDENTIFIER_LENGTH_ERROR_MSG));
        assertTrue(apiError.getMessage().contains(ENTITY_IDENTIFIER_MESSAGE_PREFIX + GROUP_IDENTIFIER_LENGTH_ERROR_MSG));
    }

    private void should_fail_validation_create_with_not_trimmed_params() throws Exception {
        EntitySpec entitySpec = EntitySpec.builder()
                .businessIdentifier("business ")
                .entityIdentifier(" entity")
                .build();

        ApiError apiError = getApiErrorForCreate(entitySpec);

        assertTrue(apiError.getMessage().contains(BUSINESS_IDENTIFIER_MESSAGE_PREFIX + GROUP_IDENTIFIER_TRIM_ERROR_MSG));
        assertTrue(apiError.getMessage().contains(ENTITY_IDENTIFIER_MESSAGE_PREFIX + GROUP_IDENTIFIER_TRIM_ERROR_MSG));
    }

    private void should_fail_validation_create_with_wrong_pattern_params() throws Exception {
        EntitySpec entitySpec = EntitySpec.builder()
                .businessIdentifier("A Business")
                .entityIdentifier("OTHER?ENTITY")
                .build();

        ApiError apiError = getApiErrorForCreate(entitySpec);

        assertTrue(apiError.getMessage().contains(BUSINESS_IDENTIFIER_MESSAGE_PREFIX + GROUP_IDENTIFIER_VALID_ERROR_MSG));
        assertTrue(apiError.getMessage().contains(ENTITY_IDENTIFIER_MESSAGE_PREFIX + GROUP_IDENTIFIER_VALID_ERROR_MSG));
    }

    private void should_fail_validation_create_with_empty_params() throws Exception {
        EntitySpec entitySpec = EntitySpec.builder()
                .businessIdentifier("")
                .entityIdentifier("")
                .build();

        ApiError apiError = getApiErrorForCreate(entitySpec);

        assertTrue(apiError.getMessage().contains(BUSINESS_IDENTIFIER_MESSAGE_PREFIX + GROUP_IDENTIFIER_LENGTH_ERROR_MSG));
        assertTrue(apiError.getMessage().contains(ENTITY_IDENTIFIER_MESSAGE_PREFIX + GROUP_IDENTIFIER_LENGTH_ERROR_MSG));
    }


    private void should_fail_validation_create_with_too_long_params() throws Exception {
        String moreThan255Chars = RandomStringUtils.random(256, true, false);
        EntitySpec entitySpec = EntitySpec.builder()
                .businessIdentifier(moreThan255Chars)
                .entityIdentifier(moreThan255Chars)
                .build();

        ApiError apiError = getApiErrorForCreate(entitySpec);

        assertTrue(apiError.getMessage().contains(BUSINESS_IDENTIFIER_MESSAGE_PREFIX + GROUP_IDENTIFIER_LENGTH_ERROR_MSG));
        assertTrue(apiError.getMessage().contains(ENTITY_IDENTIFIER_MESSAGE_PREFIX + GROUP_IDENTIFIER_LENGTH_ERROR_MSG));
    }

    private void should_fail_validation_send_empty_fields() throws Exception {
        SendMessageRequestSpec sendMessageRequestSpec = SendMessageRequestSpec.builder().build();

        ApiError apiError = getApiErrorForSend(sendMessageRequestSpec);

        assertTrue(apiError.getMessage().contains("attachmentSpecs: " + NOT_EMPTY_ATTACHMENTS));
        assertTrue(apiError.getMessage().contains("recipients: " + NOT_EMPTY_RECIPIENTS));

    }


    private void should_fail_validation_send_subject_max_byte_length() throws Exception {
        SendMessageRequestSpec sendMessageRequestSpec = mockSendMessageRequest(mockMessageSummarySpec(recipientEntity2.getId()));
        sendMessageRequestSpec.setSubject(RandomStringUtils.random(MAX_SUBJECT_BYTE_LENGTH + 1, true, true));

        ApiError apiError = getApiErrorForSend(sendMessageRequestSpec);

        String interpolatedMessage = "subject: " + SUBJECT_MAX_BYTE_LENGTH_ERROR_MESSAGE.replace("{value}", String.valueOf(MAX_SUBJECT_BYTE_LENGTH));

        assertTrue(apiError.getMessage().contains(interpolatedMessage));
    }

    private void should_fail_validation_send_duplicate_names() throws Exception {
        SendMessageRequestSpec sendMessageRequestSpec = mockSendMessageRequest(mockMessageSummarySpec(recipientEntity2.getId()));
        List<AttachmentSpec> attachmentSpecs = Arrays.asList(
                AttachmentSpec.builder().name("name").build(),
                AttachmentSpec.builder().name("name").build()
        );
        sendMessageRequestSpec.setAttachmentSpecs(attachmentSpecs);

        ApiError apiError = getApiErrorForSend(sendMessageRequestSpec);

        assertTrue(apiError.getMessage().contains("sendMessageRequestSpec: " + FILE_NAME_REPEATED_ERROR_MSG));

    }

    private ApiError getApiErrorForCreate(EntitySpec entitySpec) throws Exception {
        MvcResult result = create(entitySpec, senderUserDetails, status().isBadRequest()).andReturn();
        ApiError apiError = objectMapper.readValue(result.getResponse().getContentAsString(), ApiError.class);

        assertEquals(API_ERROR_INSTANCE_PREFIX + MESSAGES, apiError.getType());
        assertEquals(HttpStatus.BAD_REQUEST.value(), apiError.getCode());
        assertTrue(StringUtils.isNotBlank(apiError.getMessage()));

        return apiError;
    }

    private ApiError getApiErrorForSend(SendMessageRequestSpec sendMessageRequestSpec) throws Exception {
        MvcResult result = send(1L, sendMessageRequestSpec, senderUserDetails, status().isBadRequest()).andReturn();
        ApiError apiError = objectMapper.readValue(result.getResponse().getContentAsString(), ApiError.class);

        String uri = API_ERROR_INSTANCE_PREFIX + LinkUtils.getUri(MESSAGE, Pair.of("messageId", String.valueOf( 1L)));

        assertEquals(uri, apiError.getType());
        assertEquals(HttpStatus.BAD_REQUEST.value(), apiError.getCode());
        assertTrue(StringUtils.isNotBlank(apiError.getMessage()));

        return apiError;
    }

    private void should_fail_to_send_a_message_when_status_is_not_null(Message message) throws Exception {
        SendMessageRequestSpec sendMessageRequestSpec = mockSendMessageRequest(
                mockMessageSummarySpec(recipientEntity1.getId()),
                mockMessageSummarySpec(recipientEntity2.getId()));

        sendMessageRequestSpec.setAttachmentSpecs(Arrays.asList(
                AttachmentSpec.builder().name("/a/name").build(),
                AttachmentSpec.builder().name("/b/name").build())
        );

        for (Status status : Status.values()) {
            message.setStatus(status);
            runSendMessageTest(message.getId(), sendMessageRequestSpec, status().isForbidden());
        }
    }

    private void runSendMessageTest(Long messageId, SendMessageRequestSpec sendMessageRequestSpec, ResultMatcher expectedResult) throws Exception {
        mockMvc.perform(
                put(UrlTemplates.MESSAGE, String.valueOf(messageId))
                                .with(user(senderUserDetails))
                                .content(objectMapper.writeValueAsBytes(sendMessageRequestSpec))
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(expectedResult);
    }

    private ResultActions create(EntitySpec entitySpec, SecurityUserDetails userDetails, ResultMatcher expectedStatus) throws Exception {
        return mockMvc.perform(post(MESSAGES)
                        .with(user(userDetails))
                        .content(objectMapper.writeValueAsBytes(entitySpec))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(expectedStatus);
    }

    private ResultActions send(Long messageId, SendMessageRequestSpec sendMessageRequestSpec, SecurityUserDetails userDetails, ResultMatcher expectedStatus) throws Exception {
        return mockMvc.perform(
                        put(MESSAGE, String.valueOf(messageId))
                                .with(user(userDetails))
                                .content(objectMapper.writeValueAsBytes(sendMessageRequestSpec))
                                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(expectedStatus);
    }
}