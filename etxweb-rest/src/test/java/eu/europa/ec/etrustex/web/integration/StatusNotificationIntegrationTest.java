package eu.europa.ec.etrustex.web.integration;

import eu.europa.ec.etrustex.web.common.exchange.ExchangeMode;
import eu.europa.ec.etrustex.web.exchange.model.groupconfiguration.RetentionPolicyGroupConfigurationSpec;
import eu.europa.ec.etrustex.web.exchange.util.UrlTemplates;
import eu.europa.ec.etrustex.web.integration.utils.GroupUtils;
import eu.europa.ec.etrustex.web.integration.utils.MessageUtils;
import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummary;
import eu.europa.ec.etrustex.web.util.exchange.model.Status;
import eu.europa.ec.etrustex.web.persistence.entity.UserProfile;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.Channel;
import eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.impl.RetentionPolicyGroupConfiguration;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.service.MessageSummaryService;
import eu.europa.ec.etrustex.web.service.MessageSummaryUserStatusService;
import eu.europa.ec.etrustex.web.service.validation.model.GrantedAuthoritySpec;
import eu.europa.ec.etrustex.web.service.validation.model.UserProfileSpec;
import eu.europa.ec.etrustex.web.util.exchange.model.AttachmentSpec;
import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import eu.europa.ec.etrustex.web.util.exchange.model.SendMessageRequestSpec;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;

import static eu.europa.ec.etrustex.web.integration.RestTestUtils.mockMessageSummarySpec;
import static eu.europa.ec.etrustex.web.integration.RestTestUtils.mockSendMessageRequest;
import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.TEST_USER_ID;
import static eu.europa.ec.etrustex.web.util.exchange.model.RoleName.SYS_ADMIN;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class StatusNotificationIntegrationTest extends AbstractControllerTest {
    private final ResponseFieldsSnippet messageResponseFields = relaxedResponseFields(
            fieldWithPath("id").description("The UUID of the message"),
            fieldWithPath("subject").description("The subject"),
            fieldWithPath("sentOn").description("The date and time when the message has been sent"),
            fieldWithPath("attachmentsTotalByteLength").description("The cumulative size of the attachments"),
            fieldWithPath("attachmentTotalNumber").description("The total number of attachments"),
            fieldWithPath("text").description("The body of the message"),
            fieldWithPath("attachmentSpecs").description("The list of attachments with metadata, business specific"),
            fieldWithPath("templateVariables").description("Business specific fields")
    );
    @Autowired
    private GroupUtils groupUtils;
    @Autowired
    private MessageUtils messageUtils;
    @Autowired
    private MessageSummaryUserStatusService messageSummaryUserStatusService;
    @Autowired
    private MessageSummaryService messageSummaryService;
    private SecurityUserDetails senderUserDetails;
    private SecurityUserDetails recipientUserDetails;
    private Group recipientEntity1;
    private Group recipientEntity2;

    private Message message;

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
        recipientUserDetails = userUtils.buildUserDetails(abstractTestUser, recipientEntity1, RoleName.OPERATOR);
    }

    private void happyFlows() throws Exception {
        should_notify_of_new_message_summary();
        should_notify_of_new_message_summary_status();
        should_notify_of_new_user_configured();
        should_notify_of_retention_policy_changed();
    }

    void should_notify_of_new_message_summary() throws Exception {
        message = createMessage(senderUserDetails);
        sendMessage(message, senderUserDetails);

        verify(mailService, atLeastOnce()).send(any());
    }

    void should_notify_of_new_message_summary_status() {
        MessageSummary messageSummary = messageSummaryService.findByMessageIdAndRecipientId(message.getId(), recipientEntity1.getId());
        messageSummaryUserStatusService.updateMessageSummaryUserStatus(messageSummary, recipientUserDetails.getUser(), Status.READ);

        verify(mailService, atLeastOnce()).send(any());
    }

    void should_notify_of_new_user_configured() throws Exception {
        GrantedAuthoritySpec grantedAuthoritySpec = GrantedAuthoritySpec.builder()
                .groupId(root.getId())
                .userName(createUser().getEcasId())
                .roleName(SYS_ADMIN)
                .build();

        mockMvc.perform(
                    post(UrlTemplates.GRANTED_AUTHORITIES)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(grantedAuthoritySpec))
                            .with(user(sysAdminUserDetails)))
            .andExpect(status().isCreated())
            .andReturn();

        verify(mailService, atLeastOnce()).send(any());
    }

    void should_notify_of_retention_policy_changed() {
        groupConfigurationService.updateValue(businessAdminUserDetails.getUser(), abstractTestBusiness.getId(), RetentionPolicyGroupConfiguration.class, RetentionPolicyGroupConfigurationSpec.builder().value(15).build());

        verify(mailService, atLeastOnce()).send(any());
    }

    private Message createMessage(SecurityUserDetails userDetails) throws Exception {
        ResultActions resultActions = mockMvc.perform(post(UrlTemplates.MESSAGES)
                        .param("senderEntityId", String.valueOf(userDetails.getAuthorities().iterator().next().getGroup().getId()))
                        .with(user(userDetails)))
                .andExpect(status().isCreated());

        resultActions.andDo(document(
                "should_create_a_message",
                messageResponseFields,
                RestTestUtils.messageHateoasLinks
        ));

        MvcResult result = resultActions.andReturn();

        Message resultMessage = objectMapper.readValue(result.getResponse().getContentAsString(), Message.class);
        assertNotNull(resultMessage.getId());
        assertFalse(resultMessage.getLinks().isEmpty());

        return resultMessage;
    }

    private void sendMessage(Message message, SecurityUserDetails userDetails) throws Exception {
        SendMessageRequestSpec sendMessageRequestSpec = mockSendMessageRequest(
                mockMessageSummarySpec(recipientEntity1.getId()),
                mockMessageSummarySpec(recipientEntity2.getId()));

        sendMessageRequestSpec.setAttachmentSpecs(Arrays.asList(
                AttachmentSpec.builder().name("/a/name").build(),
                AttachmentSpec.builder().name("/b/name").build()
        ));

        ResultActions resultActions = mockMvc.perform(
                        put(UrlTemplates.MESSAGE, message.getId().toString())
                                .with(user(userDetails))
                                .content(objectMapper.writeValueAsBytes(sendMessageRequestSpec))
                                .contentType("application/json"))
                .andExpect(status().isOk());

        resultActions.andDo(document("should_send_a_message",
                pathParameters(
                        parameterWithName("messageId").description("The UUID of the message")
                ),
                relaxedRequestFields(
                        fieldWithPath("recipients").description("The list of recipients of the message"),
                        fieldWithPath("subject").description("The subject"),
                        fieldWithPath("attachmentsTotalByteLength").description("The cumulative size of the attachments"),
                        fieldWithPath("attachmentTotalNumber").description("The total number of attachments"),
                        fieldWithPath("text").description("The body of the message"),
                        fieldWithPath("symmetricKey").description("The symmetric key used to encrypt the message text"),
                        fieldWithPath("iv").description("The Initialisation Vector used to encrypt the message text along the symmetric key"),
                        fieldWithPath("attachmentSpecs").description("The list of attachments with metadata, business specific"),
                        fieldWithPath("templateVariables").description("Business specific fields")
                )
        ));
        resultActions.andReturn();
    }

    private User createUser() throws Exception {
        UserProfileSpec userProfileSpec = UserProfileSpec.builder()
                .ecasId("otherUser")
                .groupId(root.getId())
                .alternativeEmail(GrantedAuthorityControllerTest.class.getSimpleName() + "@domain.com")
                .alternativeEmailUsed(true)
                .name(GrantedAuthorityControllerTest.class.getSimpleName())
                .build();
        MvcResult result = mockMvc.perform(
                        post(UrlTemplates.USER_PROFILES)
                                .with(user(sysAdminUserDetails))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userProfileSpec))
                )
                .andExpect(status().isCreated())
                .andReturn();

        UserProfile createdUserProfile = objectMapper.readValue(result.getResponse().getContentAsString(), UserProfile.class);

        return createdUserProfile.getUser();
    }
}
