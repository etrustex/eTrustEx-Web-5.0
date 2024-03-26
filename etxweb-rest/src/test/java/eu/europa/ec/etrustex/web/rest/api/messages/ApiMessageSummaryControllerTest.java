/*
 * Copyright (c) 2024. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package eu.europa.ec.etrustex.web.rest.api.messages;

import com.fasterxml.jackson.core.type.TypeReference;
import eu.europa.ec.etrustex.web.integration.AbstractControllerTest;
import eu.europa.ec.etrustex.web.integration.utils.MessageResult;
import eu.europa.ec.etrustex.web.integration.utils.MessageUtils;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummary;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.util.exception.ApiError;
import eu.europa.ec.etrustex.web.util.exchange.LinkUtils;
import eu.europa.ec.etrustex.web.util.exchange.model.EntitySpec;
import eu.europa.ec.etrustex.web.util.exchange.model.RecipientStatus;
import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import eu.europa.ec.etrustex.web.util.exchange.model.Status;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.List;

import static eu.europa.ec.etrustex.web.exchange.api.ApiUrlTemplates.*;
import static eu.europa.ec.etrustex.web.util.crypto.Rsa.CLIENT_PUBLIC_KEY_HEADER_NAME;
import static eu.europa.ec.etrustex.web.util.exchange.model.Ack.MESSAGE_RETRIEVED_OK;
import static eu.europa.ec.etrustex.web.util.validation.ValidationMessage.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SuppressWarnings({"java:S112", "java:S100"}) /* Suppress Define and throw a dedicated exception instead of using a generic one & method names false positive. */
class ApiMessageSummaryControllerTest extends AbstractControllerTest {

    private static final String CLIENT_PUBLIC_KEY_BASE64 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwB3nZzmkaQZgTM2UyZOFfqHRhAWEcvTu2ka0RN4vpHxxMFzCaZMeLykxLpyZBRg3dILGGeNaL+MjPaAJEWcL1CEUxmxFJ9vPiCifG2sBynY5YTUYv64H2DfI76n5TVD+lugZMblWb3FQI2PZwQfhkLjNIXgdGVrrj0y88seUC85ZJqQQiBy3BuCQDC1j+yBp0/tWYEe8Cvlai2E/N0ltnTuQXw4a7CROUhiMJPJYcOaJWrArCGCCBrPix4pkiGcYFrqiThpjil8NbWvyVhFNAdvIiOcxkLc2SWheqOXU1yM1H07dntYUNw580lJcMFpkrhRLCzEO8wAOmpAQw/h38wIDAQAB";
    private static final String BUSINESS_IDENTIFIER_PARAM = "businessIdentifier";
    private static final String ENTITY_IDENTIFIER_PARAM = "entityIdentifier";
    private static final String BUSINESS_IDENTIFIER_MESSAGE_PREFIX = "businessIdentifier: ";
    private static final String ENTITY_IDENTIFIER_MESSAGE_PREFIX = "entityIdentifier: ";
    private SecurityUserDetails senderUserDetails;
    private SecurityUserDetails recipientUserDetails;
    private Group recipientEntity1;
    private Group recipientEntity2;
    private SecurityUserDetails otherEntityUserDetails;

    @Autowired
    private MessageUtils messageUtils;


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
        MessageResult messageResult = messageUtils.createMessageWithNewConfigurations(businessAdminUserDetails, abstractTestUser, 2, false, true, true);

        recipientEntity1 = messageResult.getRecipientEntities().get(0);
        recipientEntity2 = messageResult.getRecipientEntities().get(1);

        senderUserDetails = messageResult.getSenderUserDetails();

        recipientUserDetails = userUtils.buildUserDetails(abstractTestUser, recipientEntity1, RoleName.OPERATOR);

        otherEntityUserDetails = createEntityAndOperatorUserDetails(abstractTestBusiness);
    }

    private void happyFlows() throws Exception {
        List<Long> messageSummaryIds = should_get_unread(recipientUserDetails);
        Long messageId = messageSummaryIds.get(0);

        should_get_message_summary(messageId, recipientEntity1, recipientUserDetails);

        List<RecipientStatus> recipientStatuses = should_get_status(messageId, senderUserDetails);


        for (RecipientStatus recipientStatus : recipientStatuses) {
            if (recipientStatus.getEntityIdentifier().equals(recipientEntity1.getIdentifier())) {
                assertEquals(Status.SENT, recipientStatus.getStatus());
            } else {
                assertEquals(Status.DELIVERED, recipientStatus.getStatus());
            }
        }

        should_retrieve_ack(messageId, recipientEntity1, recipientUserDetails);
    }

    private void securityAccessTests() throws Exception {
        messageUtils.createAndSendMessage(senderUserDetails, recipientEntity1.getId(), recipientEntity2.getId());

        List<Long> messageSummaryIds = should_get_unread(recipientUserDetails);
        Long messageId = messageSummaryIds.get(0);

        should_not_get_unread(otherEntityUserDetails);

        Group otherEntity = otherEntityUserDetails.getAuthorities().iterator().next().getGroup();
        should_not_get_message_summary(messageId, recipientEntity1, otherEntityUserDetails);
        should_not_get_message_summary(messageId, otherEntity, otherEntityUserDetails);

        should_not_get_status(messageId, otherEntityUserDetails);

        should_not_retrieve_ack(messageId, recipientEntity1, otherEntityUserDetails);
        should_not_retrieve_ack(messageId, otherEntity, otherEntityUserDetails);
    }

    private void validationTests() throws Exception {
        should_fail_validation_create_with_missing_params();
        should_fail_validation_get_with_empty_params();
        should_fail_validation_get_with_not_trimmed_params();
        should_fail_validation_get_with_wrong_pattern_params();
        should_fail_validation_get_with_too_long_params();

        should_fail_validation_get_with_wrong_type_param();
    }

    private List<Long> should_get_unread(SecurityUserDetails userDetails) throws Exception {
        EntitySpec entitySpec = EntitySpec.builder()
                .businessIdentifier(recipientEntity1.getBusinessIdentifier())
                .entityIdentifier(recipientEntity1.getIdentifier())
                .build();
        MvcResult result = getUnread(entitySpec, userDetails, status().isOk()).andReturn();

        List<Long> resultList = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<Long>>() {});
        Long messageSummaryId = resultList.get(0);

        assertNotNull(messageSummaryId);

        return resultList;
    }

    private void should_get_message_summary(Long messageId, Group recipient, SecurityUserDetails userDetails) throws Exception {
        EntitySpec entitySpec = EntitySpec.builder()
                .businessIdentifier(recipient.getBusinessIdentifier())
                .entityIdentifier(recipient.getIdentifier())
                .build();

        MvcResult result = getMessageSummary(messageId, entitySpec, userDetails, status().isOk())
                .andReturn();

        MessageSummary messageSummary = objectMapper.readValue(result.getResponse().getContentAsString(), MessageSummary.class);

        assertNotNull(messageSummary);
        assertNotNull(messageSummary.getMessage());
        assertNotNull(messageSummary.getMessage().getId());
        assertThat(messageSummary.getMessage().getAttachmentSpecs()).isNotEmpty();
        assertThat(messageSummary.getLinks()).isNotEmpty();
    }

    private List<RecipientStatus> should_get_status(Long messageId, SecurityUserDetails senderUserDetails) throws Exception {
        MvcResult result = mockMvc.perform(
                        get(MESSAGE_SUMMARY_STATUS, messageId)
                                .with(user(senderUserDetails))
                )
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<RecipientStatus>>() {});
    }

    private void should_retrieve_ack(Long messageId, Group recipient, SecurityUserDetails userDetails) throws Exception {
        EntitySpec entitySpec = EntitySpec.builder()
                .businessIdentifier(recipient.getBusinessIdentifier())
                .entityIdentifier(recipient.getIdentifier())
                .build();

        retrieveAck(messageId, entitySpec, userDetails, status().isOk());
    }

    private void should_not_get_unread(SecurityUserDetails userDetails) throws Exception {
        Group recipient = userDetails.getAuthorities().iterator().next().getGroup();

        EntitySpec entitySpec = EntitySpec.builder()
                .businessIdentifier(recipient.getBusinessIdentifier())
                .entityIdentifier(recipient.getIdentifier())
                .build();

        getUnread(entitySpec, senderUserDetails, status().isForbidden()).andReturn();
    }

    private void should_not_get_message_summary(Long messageId, Group recipient, SecurityUserDetails userDetails) throws Exception {
        EntitySpec entitySpec = EntitySpec.builder()
                .businessIdentifier(recipient.getBusinessIdentifier())
                .entityIdentifier(recipient.getIdentifier())
                .build();

        getMessageSummary(messageId, entitySpec, userDetails, status().isForbidden())
                .andReturn();
    }

    private void should_not_get_status(Long messageId, SecurityUserDetails userDetails) throws Exception {
        mockMvc.perform(
                        get(MESSAGE_SUMMARY_STATUS, messageId)
                                .with(user(userDetails))
                )
                .andExpect(status().isForbidden());
    }

    private void should_not_retrieve_ack(Long messageId, Group recipient, SecurityUserDetails userDetails) throws Exception {
        EntitySpec entitySpec = EntitySpec.builder()
                .businessIdentifier(recipient.getBusinessIdentifier())
                .entityIdentifier(recipient.getIdentifier())
                .build();

        retrieveAck(messageId, entitySpec, userDetails, status().isForbidden());
    }

    private void should_fail_validation_create_with_missing_params() throws Exception {
        EntitySpec entitySpec = EntitySpec.builder()
                .build();

        ApiError apiError = getApiErrorForGetUnread(entitySpec);
        assertTrue(apiError.getMessage().contains(BUSINESS_IDENTIFIER_MESSAGE_PREFIX + GROUP_IDENTIFIER_LENGTH_ERROR_MSG));
        assertTrue(apiError.getMessage().contains(ENTITY_IDENTIFIER_MESSAGE_PREFIX + GROUP_IDENTIFIER_LENGTH_ERROR_MSG));

        apiError = getApiErrorForGetMessageSummary(entitySpec);
        assertTrue(apiError.getMessage().contains(BUSINESS_IDENTIFIER_MESSAGE_PREFIX + GROUP_IDENTIFIER_LENGTH_ERROR_MSG));
        assertTrue(apiError.getMessage().contains(ENTITY_IDENTIFIER_MESSAGE_PREFIX + GROUP_IDENTIFIER_LENGTH_ERROR_MSG));
    }

    private void should_fail_validation_get_with_empty_params() throws Exception {
        EntitySpec entitySpec = EntitySpec.builder()
                .businessIdentifier("")
                .entityIdentifier("")
                .build();

        ApiError apiError = getApiErrorForGetUnread(entitySpec);
        assertTrue(apiError.getMessage().contains(BUSINESS_IDENTIFIER_MESSAGE_PREFIX + GROUP_IDENTIFIER_LENGTH_ERROR_MSG));
        assertTrue(apiError.getMessage().contains(ENTITY_IDENTIFIER_MESSAGE_PREFIX + GROUP_IDENTIFIER_LENGTH_ERROR_MSG));

        apiError = getApiErrorForGetMessageSummary(entitySpec);
        assertTrue(apiError.getMessage().contains(BUSINESS_IDENTIFIER_MESSAGE_PREFIX + GROUP_IDENTIFIER_LENGTH_ERROR_MSG));
        assertTrue(apiError.getMessage().contains(ENTITY_IDENTIFIER_MESSAGE_PREFIX + GROUP_IDENTIFIER_LENGTH_ERROR_MSG));
    }

    private void should_fail_validation_get_with_not_trimmed_params() throws Exception {
        EntitySpec entitySpec = EntitySpec.builder()
                .businessIdentifier("business ")
                .entityIdentifier(" entity")
                .build();

        ApiError apiError = getApiErrorForGetUnread(entitySpec);
        assertTrue(apiError.getMessage().contains(BUSINESS_IDENTIFIER_MESSAGE_PREFIX + GROUP_IDENTIFIER_TRIM_ERROR_MSG));
        assertTrue(apiError.getMessage().contains(ENTITY_IDENTIFIER_MESSAGE_PREFIX + GROUP_IDENTIFIER_TRIM_ERROR_MSG));

        apiError = getApiErrorForGetMessageSummary(entitySpec);
        assertTrue(apiError.getMessage().contains(BUSINESS_IDENTIFIER_MESSAGE_PREFIX + GROUP_IDENTIFIER_TRIM_ERROR_MSG));
        assertTrue(apiError.getMessage().contains(ENTITY_IDENTIFIER_MESSAGE_PREFIX + GROUP_IDENTIFIER_TRIM_ERROR_MSG));
    }

    private void should_fail_validation_get_with_wrong_pattern_params() throws Exception {
        EntitySpec entitySpec = EntitySpec.builder()
                .businessIdentifier("A Business")
                .entityIdentifier("OTHER?ENTITY")
                .build();

        ApiError apiError = getApiErrorForGetUnread(entitySpec);
        assertTrue(apiError.getMessage().contains(BUSINESS_IDENTIFIER_MESSAGE_PREFIX + GROUP_IDENTIFIER_VALID_ERROR_MSG));
        assertTrue(apiError.getMessage().contains(ENTITY_IDENTIFIER_MESSAGE_PREFIX + GROUP_IDENTIFIER_VALID_ERROR_MSG));

        apiError = getApiErrorForGetMessageSummary(entitySpec);
        assertTrue(apiError.getMessage().contains(BUSINESS_IDENTIFIER_MESSAGE_PREFIX + GROUP_IDENTIFIER_VALID_ERROR_MSG));
        assertTrue(apiError.getMessage().contains(ENTITY_IDENTIFIER_MESSAGE_PREFIX + GROUP_IDENTIFIER_VALID_ERROR_MSG));
    }

    private void should_fail_validation_get_with_too_long_params() throws Exception {
        String moreThan255Chars = RandomStringUtils.random(256, true, false);
        EntitySpec entitySpec = EntitySpec.builder()
                .businessIdentifier(moreThan255Chars)
                .entityIdentifier(moreThan255Chars)
                .build();

        ApiError apiError = getApiErrorForGetUnread(entitySpec);
        assertTrue(apiError.getMessage().contains(BUSINESS_IDENTIFIER_MESSAGE_PREFIX + GROUP_IDENTIFIER_LENGTH_ERROR_MSG));
        assertTrue(apiError.getMessage().contains(ENTITY_IDENTIFIER_MESSAGE_PREFIX + GROUP_IDENTIFIER_LENGTH_ERROR_MSG));

        apiError = getApiErrorForGetMessageSummary(entitySpec);
        assertTrue(apiError.getMessage().contains(BUSINESS_IDENTIFIER_MESSAGE_PREFIX + GROUP_IDENTIFIER_LENGTH_ERROR_MSG));
        assertTrue(apiError.getMessage().contains(ENTITY_IDENTIFIER_MESSAGE_PREFIX + GROUP_IDENTIFIER_LENGTH_ERROR_MSG));
    }

    private void should_fail_validation_get_with_wrong_type_param() throws Exception {
        String messageId = ENTITY_IDENTIFIER_PARAM;
        MvcResult result = mockMvc.perform(
                        get(MESSAGE_SUMMARY, messageId)
                                .param(BUSINESS_IDENTIFIER_PARAM, BUSINESS_IDENTIFIER_PARAM)
                                .param(ENTITY_IDENTIFIER_PARAM, ENTITY_IDENTIFIER_PARAM)
                                .with(user(recipientUserDetails))
                                .header(CLIENT_PUBLIC_KEY_HEADER_NAME, CLIENT_PUBLIC_KEY_BASE64)
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        ApiError apiError = objectMapper.readValue(result.getResponse().getContentAsString(), ApiError.class);

        String uri = API_ERROR_INSTANCE_PREFIX + LinkUtils.getUri(MESSAGE_SUMMARY, Pair.of("messageId", messageId));

        assertEquals(uri, apiError.getType());
        assertEquals(HttpStatus.BAD_REQUEST.value(), apiError.getCode());
        assertTrue(StringUtils.isNotBlank(apiError.getMessage()));
        assertEquals("messageId should be of type java.lang.Long", apiError.getMessage());
    }

    private ApiError getApiErrorForGetUnread(EntitySpec entitySpec) throws Exception {
        MvcResult result = getUnread(entitySpec, senderUserDetails, status().isBadRequest()).andReturn();
        ApiError apiError = objectMapper.readValue(result.getResponse().getContentAsString(), ApiError.class);

        assertEquals(API_ERROR_INSTANCE_PREFIX + UNREAD_MESSAGE_SUMMARIES, apiError.getType());
        assertEquals(HttpStatus.BAD_REQUEST.value(), apiError.getCode());
        assertTrue(StringUtils.isNotBlank(apiError.getMessage()));

        return apiError;
    }

    private ApiError getApiErrorForGetMessageSummary(EntitySpec entitySpec) throws Exception {
        MvcResult result = getMessageSummary(1L, entitySpec, senderUserDetails, status().isBadRequest()).andReturn();
        ApiError apiError = objectMapper.readValue(result.getResponse().getContentAsString(), ApiError.class);

        String uri = API_ERROR_INSTANCE_PREFIX + LinkUtils.getUri(MESSAGE_SUMMARY, Pair.of("messageId", String.valueOf(1L)));

        assertEquals(uri, apiError.getType());
        assertEquals(HttpStatus.BAD_REQUEST.value(), apiError.getCode());
        assertTrue(StringUtils.isNotBlank(apiError.getMessage()));

        return apiError;
    }

    private ResultActions getUnread(EntitySpec entitySpec, SecurityUserDetails userDetails, ResultMatcher expectedStatus) throws Exception {
        return mockMvc.perform(
                        get(UNREAD_MESSAGE_SUMMARIES)
                                .param(BUSINESS_IDENTIFIER_PARAM, entitySpec.getBusinessIdentifier())
                                .param(ENTITY_IDENTIFIER_PARAM, entitySpec.getEntityIdentifier())
                                .with(user(userDetails))
                                .header(CLIENT_PUBLIC_KEY_HEADER_NAME, CLIENT_PUBLIC_KEY_BASE64)
                )
                .andExpect(expectedStatus);
    }

    private ResultActions getMessageSummary(Long messageId, EntitySpec entitySpec, SecurityUserDetails userDetails, ResultMatcher expectedStatus) throws Exception {
        return mockMvc.perform(
                        get(MESSAGE_SUMMARY, messageId)
                                .param(BUSINESS_IDENTIFIER_PARAM, entitySpec.getBusinessIdentifier())
                                .param(ENTITY_IDENTIFIER_PARAM, entitySpec.getEntityIdentifier())
                                .with(user(userDetails))
                                .header(CLIENT_PUBLIC_KEY_HEADER_NAME, CLIENT_PUBLIC_KEY_BASE64)
                )
                .andExpect(expectedStatus);
    }

    private void retrieveAck(Long messageId, EntitySpec entitySpec, SecurityUserDetails userDetails, ResultMatcher expectedStatus) throws Exception {
        mockMvc.perform(
                        put(MESSAGE_SUMMARY_ACK, messageId, MESSAGE_RETRIEVED_OK)
                                .param(BUSINESS_IDENTIFIER_PARAM, entitySpec.getBusinessIdentifier())
                                .param(ENTITY_IDENTIFIER_PARAM, entitySpec.getEntityIdentifier())
                                .with(user(userDetails))
                )
                .andExpect(expectedStatus);
    }
}
