package eu.europa.ec.etrustex.web.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.SignedJWT;
import eu.europa.ec.etrustex.web.common.exchange.view.InboxListViewFilter;
import eu.europa.ec.etrustex.web.exchange.model.MessageSummaryListItem;
import eu.europa.ec.etrustex.web.exchange.model.MessageSummaryUserStatusItem;
import eu.europa.ec.etrustex.web.exchange.model.SearchItem;
import eu.europa.ec.etrustex.web.exchange.util.UrlTemplates;
import eu.europa.ec.etrustex.web.integration.utils.MessageResult;
import eu.europa.ec.etrustex.web.integration.utils.MessageUtils;
import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummary;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummaryUserStatus;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.service.MessageSummaryService;
import eu.europa.ec.etrustex.web.service.pagination.MessageSummaryPage;
import eu.europa.ec.etrustex.web.service.pagination.RestResponsePage;
import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import eu.europa.ec.etrustex.web.util.exchange.model.Status;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.security.interfaces.RSAPublicKey;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static eu.europa.ec.etrustex.web.util.crypto.Rsa.CLIENT_PUBLIC_KEY_HEADER_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SuppressWarnings({"java:S112", "java:S100"}) /* Suppress Define and throw a dedicated exception instead of using a generic one & method names false positive. */
class MessageSummaryControllerTest extends AbstractControllerTest {

    private static final String CLIENT_PUBLIC_KEY_BASE64 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwB3nZzmkaQZgTM2UyZOFfqHRhAWEcvTu2ka0RN4vpHxxMFzCaZMeLykxLpyZBRg3dILGGeNaL+MjPaAJEWcL1CEUxmxFJ9vPiCifG2sBynY5YTUYv64H2DfI76n5TVD+lugZMblWb3FQI2PZwQfhkLjNIXgdGVrrj0y88seUC85ZJqQQiBy3BuCQDC1j+yBp0/tWYEe8Cvlai2E/N0ltnTuQXw4a7CROUhiMJPJYcOaJWrArCGCCBrPix4pkiGcYFrqiThpjil8NbWvyVhFNAdvIiOcxkLc2SWheqOXU1yM1H07dntYUNw580lJcMFpkrhRLCzEO8wAOmpAQw/h38wIDAQAB";
    private static final String RECIPIENT_ENTITY_ID_PARAM = "recipientEntityId";
    private static final String BUSINESS_ID_PARAM = "businessId";
    private static final String MESSAGE_ID_PARAM = "messageId";
    private static final String RECIPIENT_IDENTIFIER_PARAM = "recipientIdentifier";
    private static final String IS_ACTIVE_PARAM = "isActive";
    private static final String MESSAGE_ID_OR_SUBJECT = "messageIdOrSubject";
    private SecurityUserDetails senderUserDetails;
    private SecurityUserDetails recipientUserDetails;
    private Group recipientEntity1;
    private Group recipientEntity2;
    private SecurityUserDetails otherEntityUserDetails;

    @Autowired
    private MessageUtils messageUtils;
    @Autowired
    private MessageSummaryService messageSummaryService;


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
        MessageResult messageResult = messageUtils.createMessageWithNewConfigurations(businessAdminUserDetails, abstractTestUser, 2, false, true);

        recipientEntity1 = messageResult.getRecipientEntities().get(0);
        recipientEntity2 = messageResult.getRecipientEntities().get(1);

        senderUserDetails = messageResult.getSenderUserDetails();

        recipientUserDetails = userUtils.buildUserDetails(abstractTestUser, recipientEntity1, RoleName.OPERATOR);

        otherEntityUserDetails = createEntityAndOperatorUserDetails(abstractTestBusiness);
    }

    private void happyFlows() throws Exception {
        List<MessageSummary> messageSummaries = should_get_the_inbox(recipientUserDetails);
        MessageSummary messageSummary = messageSummaries.get(0);
        int unread = should_count_unread(recipientUserDetails, recipientEntity1.getId());
        assertEquals(1, unread);

        should_mark_as_read(messageSummary, recipientUserDetails);
        should_monitor_messages(messageSummary);

        should_get_message_summary(messageSummary.getMessage().getId(), recipientUserDetails);

        should_verify_signature_of_message();

        should_get_message_summary_display_items(recipientEntity1.getBusinessId(), sysAdminUserDetails);
        should_get_message_summary_display_items(recipientEntity1.getBusinessId(), businessAdminUserDetails);

        should_get_message_summary_search_items(recipientEntity1.getBusinessId(), sysAdminUserDetails);
        should_get_message_summary_search_items(recipientEntity1.getBusinessId(), businessAdminUserDetails);

        should_update_active_status(recipientEntity1.getBusinessId(), messageSummary.getMessage().getId(), recipientEntity1.getIdentifier(), sysAdminUserDetails);
        should_update_active_status(recipientEntity1.getBusinessId(), messageSummary.getMessage().getId(), recipientEntity1.getIdentifier(), businessAdminUserDetails);

        should_update_multiple_active_status(recipientEntity1.getBusinessId(), sysAdminUserDetails, messageSummaries);
        should_update_multiple_active_status(recipientEntity1.getBusinessId(), businessAdminUserDetails, messageSummaries);
    }

    private void securityAccessTests() throws Exception {
        messageUtils.createAndSendMessage(senderUserDetails, recipientEntity1.getId(), recipientEntity2.getId());

        should_not_get_the_message_summaries(senderUserDetails, RANDOM.nextLong());
        should_not_get_the_message_summaries(otherEntityUserDetails, recipientEntity1.getId());

        should_not_count_unread(otherEntityUserDetails, recipientEntity1.getId());

        List<MessageSummary> messageSummaries = should_get_the_inbox(recipientUserDetails);
        MessageSummary messageSummary = messageSummaries.get(0);

        should_not_mark_as_read(messageSummary, otherEntityUserDetails);

        should_not_get_message_summary(messageSummary.getMessage().getId(), recipientEntity1.getId(), otherEntityUserDetails);
        Long otherEntityId = otherEntityUserDetails.getAuthorities().iterator().next().getGroup().getId();
        should_not_get_message_summary(messageSummary.getMessage().getId(), otherEntityId, otherEntityUserDetails);

        should_not_get_message_summary_display_items(recipientEntity1.getBusinessId(), recipientUserDetails);

        should_not_get_message_summary_search_items(recipientEntity1.getBusinessId(), recipientUserDetails);

        should_not_update_active_status(recipientEntity1.getBusinessId(), recipientUserDetails);

        should_not_update_multiple_active_status(recipientEntity1.getBusinessId(), recipientUserDetails);
    }

    private List<MessageSummary> should_get_the_inbox(SecurityUserDetails userDetails) throws Exception {
        MvcResult result = mockMvc.perform(
                        get(UrlTemplates.MESSAGE_SUMMARIES)
                                .param("page", String.valueOf(0))
                                .param("size", String.valueOf(10))
                                .param("sortBy", "message.sentOn")
                                .param("sortOrder", Sort.Direction.DESC.toString())
                                .param(RECIPIENT_ENTITY_ID_PARAM, String.valueOf(userDetails.getAuthorities().iterator().next().getGroup().getId()))
                                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andReturn();

        objectMapper.setConfig(objectMapper.getSerializationConfig()
                .withView(InboxListViewFilter.class));

        MessageSummaryPage messageSummaryPage = objectMapper.readValue(result.getResponse().getContentAsString(), MessageSummaryPage.class);
        List<MessageSummary> resultList = objectMapper.convertValue(messageSummaryPage.getContent(), new TypeReference<List<MessageSummary>>() {});
        MessageSummary messageSummary = resultList.get(0);

        assertEquals(1, messageSummaryPage.getTotalPages());
        assertNull(messageSummary.getSymmetricKey());
        assertEquals(2, messageSummary.getLinks().size());

        return resultList;
    }

    private void should_get_message_summary(Long messageId, SecurityUserDetails userDetails) throws Exception {
        MvcResult result = mockMvc.perform(
                        get(UrlTemplates.MESSAGE_SUMMARY, messageId)
                                .param(RECIPIENT_ENTITY_ID_PARAM, String.valueOf(recipientEntity1.getId()))
                                .with(user(userDetails))
                                .header(CLIENT_PUBLIC_KEY_HEADER_NAME, CLIENT_PUBLIC_KEY_BASE64))
                .andExpect(status().isOk())
                .andReturn();

        MessageSummary messageSummary = objectMapper.readValue(result.getResponse().getContentAsString(), MessageSummary.class);
        assertNotNull(messageSummary);
        assertNotNull(messageSummary.getMessage());
        assertNotNull(messageSummary.getMessage().getId());
        assertThat(messageSummary.getMessage().getAttachmentSpecs()).isNotEmpty();
        assertThat(messageSummary.getLinks()).isNotEmpty();
    }

    private int should_count_unread(SecurityUserDetails userDetails, Long recipientId) throws Exception {
        MvcResult result = mockMvc.perform(
                        get(UrlTemplates.COUNT_UNREAD_MESSAGE_SUMMARIES)
                                .param(RECIPIENT_ENTITY_ID_PARAM, String.valueOf(recipientId))
                                .with(user(userDetails))
                                .header(CLIENT_PUBLIC_KEY_HEADER_NAME, CLIENT_PUBLIC_KEY_BASE64))
                .andExpect(status().isOk())
                .andReturn();

        return Integer.parseInt(result.getResponse().getContentAsString());
    }

    private void should_mark_as_read(MessageSummary messageSummary, SecurityUserDetails userDetails) throws Exception {
        mockMvc.perform(
                        put(UrlTemplates.MESSAGE_SUMMARIES)
                                .param(RECIPIENT_ENTITY_ID_PARAM, String.valueOf(recipientEntity1.getId()))
                                .with(user(userDetails))
                                .header(CLIENT_PUBLIC_KEY_HEADER_NAME, CLIENT_PUBLIC_KEY_BASE64)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(Collections.singletonList(messageSummary.getMessage().getId()))
                                ))
                .andExpect(status().isOk())
                .andReturn();
        messageSummary = messageSummaryService.findByMessageIdAndRecipientId(messageSummary.getMessage().getId(), recipientEntity1.getId());
        Optional<MessageSummaryUserStatus> messageSummaryUserStatus = messageSummaryUserStatusRepository.findByMessageSummaryAndUser(messageSummary, userDetails.getUser());
        assertThat(messageSummaryUserStatus).isPresent();
        assertEquals(Status.READ, messageSummaryUserStatus.get().getStatus());
    }

    private void should_monitor_messages(MessageSummary messageSummary) throws Exception {
        MvcResult result = mockMvc.perform(
                        get(UrlTemplates.MESSAGE_MONITORING)
                                .param(RECIPIENT_ENTITY_ID_PARAM, String.valueOf(recipientEntity1.getId()))
                                .with(user(businessAdminUserDetails)))
                .andExpect(status().isOk())
                .andReturn();

        RestResponsePage<MessageSummaryUserStatusItem> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<RestResponsePage<MessageSummaryUserStatusItem>>() {});
        assertEquals(messageSummary.getMessage().getId(), response.getContent().get(0).getMessageId());
    }

    private void should_verify_signature_of_message() throws Exception {
        Message signedMessage = messageUtils.createAndSendMessage(senderUserDetails, true, recipientEntity1.getId(), recipientEntity2.getId());

        MvcResult result = mockMvc.perform(
                        get(UrlTemplates.MESSAGE_SUMMARY, signedMessage.getId())
                                .param(RECIPIENT_ENTITY_ID_PARAM, String.valueOf(recipientEntity1.getId()))
                                .with(user(recipientUserDetails))
                                .header(CLIENT_PUBLIC_KEY_HEADER_NAME, CLIENT_PUBLIC_KEY_BASE64))
                .andExpect(status().isOk())
                .andReturn();


        MessageSummary MessageSummaryWithLinks = objectMapper.readValue(result.getResponse().getContentAsString(), MessageSummary.class);
        String signature = MessageSummaryWithLinks.getSignature();

        RSAPublicKey publicKey = (RSAPublicKey) messageUtils.getServerPublicKey();
        SignedJWT signedJWT = SignedJWT.parse(signature);

        JWSVerifier signatureVerifier = new RSASSAVerifier(publicKey);
        Assertions.assertTrue(signedJWT.verify(signatureVerifier));
    }

    private void should_get_message_summary_display_items(Long businessId, SecurityUserDetails userDetails) throws Exception {
        MvcResult result = mockMvc.perform(
                        get(UrlTemplates.MESSAGE_SUMMARY_LIST_ITEMS)
                                .param("page", String.valueOf(0))
                                .param("size", String.valueOf(10))
                                .param("filterValue", "Test")
                                .param(BUSINESS_ID_PARAM, String.valueOf(businessId))
                                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andReturn();

        RestResponsePage<MessageSummaryListItem> messageSummaryPage = objectMapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<RestResponsePage<MessageSummaryListItem>>() {});
        MessageSummaryListItem messageSummaryListItem = messageSummaryPage.getContent().get(0);

        assertNotNull(messageSummaryListItem.getMessageId());
        assertThat(messageSummaryListItem.getSubject()).isNotEmpty();
        assertThat(messageSummaryListItem.getRecipientEntity()).isNotEmpty();
        assertNotNull(messageSummaryListItem.getDate());
        assertTrue(messageSummaryListItem.isActive());
    }

    private void should_get_message_summary_search_items(Long businessId, SecurityUserDetails userDetails) throws Exception {
        MvcResult result = mockMvc.perform(
                        get(UrlTemplates.MESSAGE_SUMMARY_SEARCH_ITEM)
                                .param(BUSINESS_ID_PARAM, String.valueOf(businessId))
                                .param(MESSAGE_ID_OR_SUBJECT, "1")
                                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andReturn();

        List<SearchItem> searchItems = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<SearchItem>>() {});

        SearchItem searchItem = searchItems.get(0);

        assertThat(searchItem.getSearchValue()).isNotEmpty();
        assertThat(searchItem.getValue()).isNotEmpty();
    }

    private void should_update_active_status(Long businessId, Long messageId, String recipientIdentifier, SecurityUserDetails userDetails) throws Exception {
        mockMvc.perform(
                        put(UrlTemplates.MESSAGE_SUMMARY_UPDATE)
                                .param(BUSINESS_ID_PARAM, String.valueOf(businessId))
                                .param(MESSAGE_ID_PARAM, String.valueOf(messageId))
                                .param(RECIPIENT_IDENTIFIER_PARAM, recipientIdentifier)
                                .param(IS_ACTIVE_PARAM, String.valueOf(true))
                                .with(user(userDetails))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();


    }

    private void should_update_multiple_active_status(Long businessId, SecurityUserDetails userDetails, List<MessageSummary> messageSummaries) throws Exception {
        List<MessageSummaryListItem> messageSummaryListItems = messageSummaries.stream()
                        .map(ms -> MessageSummaryListItem.builder()
                                .messageId(ms.getMessage().getId())
                                .subject(ms.getMessage().getSubject())
                                .recipientEntity(recipientEntity1.getIdentifier())
                                .isActive(ms.isActive())
                                .build())
                                .collect(Collectors.toList());


        mockMvc.perform(
                        put(UrlTemplates.MESSAGE_SUMMARIES_UPDATE)
                                .param(BUSINESS_ID_PARAM, String.valueOf(businessId))
                                .param("activate", String.valueOf(true))
                                .with(user(userDetails))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(messageSummaryListItems))
                )
                .andExpect(status().isOk())
                .andReturn();


    }

    private void should_not_get_the_message_summaries(SecurityUserDetails userDetails, Long recipientEntityId) throws Exception {
        mockMvc.perform(
                        get(UrlTemplates.MESSAGE_SUMMARIES)
                                .param("page", String.valueOf(0))
                                .param("size", String.valueOf(10))
                                .param("sortBy", "message.sentOn")
                                .param("sortOrder", Sort.Direction.DESC.toString())
                                .param(RECIPIENT_ENTITY_ID_PARAM, String.valueOf(recipientEntityId))
                                .with(user(userDetails)))
                .andExpect(status().isForbidden());
    }

    private void should_not_get_message_summary(Long messageId, Long recipientEntityId, SecurityUserDetails userDetails) throws Exception {
        mockMvc.perform(
                        get(UrlTemplates.MESSAGE_SUMMARY, String.valueOf(messageId))
                                .param(RECIPIENT_ENTITY_ID_PARAM, String.valueOf(recipientEntityId))
                                .with(user(userDetails))
                                .header(CLIENT_PUBLIC_KEY_HEADER_NAME, CLIENT_PUBLIC_KEY_BASE64)
                )
                .andExpect(status().isForbidden());
    }

    private void should_not_count_unread(SecurityUserDetails userDetails, Long recipientId) throws Exception {
        mockMvc.perform(
                        get(UrlTemplates.COUNT_UNREAD_MESSAGE_SUMMARIES)
                                .param(RECIPIENT_ENTITY_ID_PARAM, String.valueOf(recipientId))
                                .with(user(userDetails))
                                .header(CLIENT_PUBLIC_KEY_HEADER_NAME, CLIENT_PUBLIC_KEY_BASE64))
                .andExpect(status().isForbidden());
    }

    private void should_not_mark_as_read(MessageSummary messageSummary, SecurityUserDetails userDetails) throws Exception {
        mockMvc.perform(
                        put(UrlTemplates.MESSAGE_SUMMARIES)
                                .param(RECIPIENT_ENTITY_ID_PARAM, String.valueOf(recipientEntity1.getId()))
                                .with(user(userDetails))
                                .header(CLIENT_PUBLIC_KEY_HEADER_NAME, CLIENT_PUBLIC_KEY_BASE64)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(Collections.singletonList(messageSummary.getMessage().getId()))
                                ))
                .andExpect(status().isForbidden());
    }

    private void should_not_get_message_summary_display_items(Long businessId, SecurityUserDetails userDetails) throws Exception {
        mockMvc.perform(
                        get(UrlTemplates.MESSAGE_SUMMARY_LIST_ITEMS)
                                .param(BUSINESS_ID_PARAM, String.valueOf(businessId))
                                .with(user(userDetails)))
                .andExpect(status().isForbidden());
    }

    private void should_not_get_message_summary_search_items(Long businessId, SecurityUserDetails userDetails) throws Exception {
        mockMvc.perform(
                        get(UrlTemplates.MESSAGE_SUMMARY_SEARCH_ITEM)
                                .param(BUSINESS_ID_PARAM, String.valueOf(businessId))
                                .param(MESSAGE_ID_OR_SUBJECT, "1")
                                .with(user(userDetails)))
                .andExpect(status().isForbidden());
    }

    private void should_not_update_active_status(Long businessId, SecurityUserDetails userDetails) throws Exception {
        mockMvc.perform(
                        put(UrlTemplates.MESSAGE_SUMMARY_UPDATE)
                                .param(BUSINESS_ID_PARAM, String.valueOf(businessId))
                                .param(MESSAGE_ID_PARAM, "1")
                                .param(RECIPIENT_IDENTIFIER_PARAM, RECIPIENT_IDENTIFIER_PARAM)
                                .param(IS_ACTIVE_PARAM, String.valueOf(true))
                                .with(user(userDetails))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden());
    }

    private void should_not_update_multiple_active_status(Long businessId, SecurityUserDetails userDetails) throws Exception {
        mockMvc.perform(
                        put(UrlTemplates.MESSAGE_SUMMARIES_UPDATE)
                                .param(BUSINESS_ID_PARAM, String.valueOf(businessId))
                                .param("activate", String.valueOf(true))
                                .with(user(userDetails))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Collections.singletonList(
                                        MessageSummaryListItem.builder()
                                                .messageId(1L)
                                                .recipientEntity("r")
                                                .subject("s")
                                                .date(new Date())
                                                .build())
                                        )

                                )
                )
                .andExpect(status().isForbidden());
    }
}
