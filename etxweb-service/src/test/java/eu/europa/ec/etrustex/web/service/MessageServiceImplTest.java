package eu.europa.ec.etrustex.web.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.etrustex.web.persistence.entity.Attachment;
import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.entity.UserProfile;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.persistence.repository.MessageRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import eu.europa.ec.etrustex.web.service.dto.FindMessageDto;
import eu.europa.ec.etrustex.web.service.pagination.MessagePage;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.util.exchange.model.MessageSummarySpec;
import eu.europa.ec.etrustex.web.util.exchange.model.SendMessageRequestSpec;
import eu.europa.ec.etrustex.web.util.exchange.model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.security.SecureRandom;
import java.util.*;

import static eu.europa.ec.etrustex.web.common.template.TemplateContextConstants.TPL_VARIABLES_MESSAGE_SUBJECT;
import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.*;
import static eu.europa.ec.etrustex.web.service.InboxAndSentTestHelper.mockSentMessage;
import static eu.europa.ec.etrustex.web.service.ServiceTestUtils.mockSendMessageRequest;
import static eu.europa.ec.etrustex.web.util.exchange.model.GroupType.ENTITY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"java:S100"}) /* method names false positive. */
class MessageServiceImplTest {

    private static final String SENDER_GROUP_ID = "senderGroup";
    private static final String SENT_ON_SORT = "sentOn";
    private static final Random RANDOM = new SecureRandom();

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private MessageSummaryService messageSummaryService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private GroupRepository groupRepository;

    private MessageService messageService;

    @Mock
    private AttachmentService attachmentService;

    @Mock
    private EncryptionService encryptionService;

    @BeforeEach
    public void setUp() {
        this.objectMapper = new ObjectMapper();
        this.messageService = new MessageServiceImpl(messageRepository, groupRepository, messageSummaryService, attachmentService, encryptionService, objectMapper);
    }

    @Test
    void should_find_by_id() {
        Message message = mockMessage();
        given(messageRepository.findById(anyLong())).willReturn(Optional.of(message));
        assertEquals(message, messageService.findById(message.getId()));
    }

    @Test
    void should_find_optional_by_id() {
        Message message = mockMessage();
        given(messageRepository.findById(anyLong())).willReturn(Optional.of(message));
        assertEquals(Optional.of(message), messageService.findOptionalById(message.getId()));
    }

    @Test
    void should_save() {
        Message message = mockMessage();
        given(messageRepository.save(message)).willReturn(message);
        assertEquals(message, messageService.save(message));
    }

    @Test
    void should_create_new_message_from_user_and_group_id() {

        Group senderGroup = mockGroup(SENDER_GROUP_ID);
        User user = mockUser();
        UserProfile senderUserProfile = mockUserProfile(user, senderGroup);
        Message mockMessage = mockMessage(senderUserProfile);

        given(groupRepository.findById(senderGroup.getId())).willReturn(Optional.of(senderGroup));
        given(messageRepository.save(any(Message.class))).willReturn(mockMessage);

        Message message = messageService.create(senderGroup.getId(), senderUserProfile.getUser().getName());

        assertEquals(senderGroup, message.getSenderGroup());
        assertEquals(user.getName(), message.getSenderUserName());
    }

    @Test
    void should_throw_an_exception_when_create_new_message_from_user_and_group_id() {

        Group senderGroup = mockGroup(SENDER_GROUP_ID);
        User user = mockUser();
        UserProfile senderUserProfile = mockUserProfile(user, senderGroup);
        given(groupRepository.findById(senderGroup.getId())).willReturn(Optional.of(senderGroup));

        given(messageRepository.save(any(Message.class))).willThrow(EtxWebException.class);
        assertThrows(EtxWebException.class, () -> messageService.create(senderGroup.getId(), senderUserProfile.getUser().getName()));

    }

    @Test
    void should_create_a_new_message() {

        Group senderGroup = mockGroup(SENDER_GROUP_ID);
        User user = mockUser();
        UserProfile senderUserProfile = mockUserProfile(user, senderGroup);
        Message mockMessage = mockMessage(senderUserProfile);


        given(messageRepository.save(any(Message.class))).willReturn(mockMessage);

        Message message = messageService.create(senderUserProfile);

        assertEquals(senderGroup, message.getSenderGroup());
        assertEquals(user.getName(), message.getSenderUserName());
    }

    @Test
    void should_throw_an_exception_when_create_a_new_message() {

        Group senderGroup = mockGroup(SENDER_GROUP_ID);
        User user = mockUser();
        UserProfile senderUserProfile = mockUserProfile(user, senderGroup);

        given(messageRepository.save(any(Message.class))).willThrow(EtxWebException.class);
        assertThrows(EtxWebException.class, () -> messageService.create(senderUserProfile));

    }

    @Test
    void should_delete_message() {
        given(attachmentService.findByMessageId(any())).willReturn(new ArrayList<>());
        Message mockMessage = mockMessage();
        messageService.delete(mockMessage);
        verify(messageRepository, times(1)).delete(mockMessage);
    }

    @Test
    void should_throw_EWE_when_message_cannot_be_created() {

        doThrow(new EtxWebException("Test exception")).when(messageRepository).save(notNull());

        User senderUser = User.builder().ecasId("test").build();
        Group senderGroup = Group.builder().name("Test group").type(ENTITY).build();
        UserProfile senderUserProfile = UserProfile.builder()
                .user(senderUser)
                .group(senderGroup)
                .build();


        assertThrows(EtxWebException.class,
                () -> messageService.create(senderUserProfile));
    }

    @Test
    void should_send_message() {
        Message message = mockMessage();
        SendMessageRequestSpec sendMessageRequestSpec = mockSendMessageRequest("test", MessageSummarySpec.builder()
                .recipientId(RANDOM.nextLong())
                .build());

        given(messageRepository.findById(message.getId())).willReturn(Optional.of(message));
        given(messageRepository.save(message)).willReturn(message);


        messageService.update(message.getId(), sendMessageRequestSpec, message.getSenderUserName());

        verify(messageSummaryService, times(1)).createMessageSummaries(message, sendMessageRequestSpec.getRecipients());
    }

    @Test
    void should_throw_exception_when_send_message() {
        Message message = mockMessage();
        SendMessageRequestSpec sendMessageRequestSpec = mockSendMessageRequest("test", MessageSummarySpec.builder()
                .recipientId(RANDOM.nextLong())
                .build());

        given(messageRepository.findById(message.getId())).willReturn(Optional.of(message));
        given(messageRepository.save(message)).willReturn(message);
        doThrow(new EtxWebException("")).when(messageSummaryService).createMessageSummaries(message, sendMessageRequestSpec.getRecipients());

        assertThrows(EtxWebException.class,
                () -> messageService.update(message.getId(), sendMessageRequestSpec, message.getSenderUserName()));

    }

    @Test
    void should_check_is_ready_to_send_message() {
        Message message = mockMessage();
        List<Attachment> attachments = Collections.nCopies(3, mockAttachment(message));

        given(attachmentService.findByMessageId(message.getId())).willReturn(attachments);

        assertTrue(messageService.isReadyToSend(message.getId()));
    }

    @Test
    void isSender_should_return_false_with_null_args() {
        assertFalse(this.messageService.groupIsSender(null, null));
    }

    @Test
    void isRecipient_should_return_false_with_null_args() {
        assertFalse(this.messageSummaryService.groupIsRecipient(null, null));
    }


    @Test
    void should_retrieve_the_sent_messages() {
        Group sender = mockGroup(SENDER_GROUP_ID);
        Message m = mockSentMessage(messageRepository);

        given(messageRepository.countUnread(any(), any())).willReturn(1);
        given(groupRepository.findById(sender.getId())).willReturn(Optional.of(sender));

        MessagePage sentMessagesPage = messageService.getMessagesForUser(FindMessageDto.builder()
                .senderGroupId(sender.getId())
                .messageId(null)
                .pageable(PageRequest.of(0, 10, Sort.by(SENT_ON_SORT)))
                .filterBy(null)
                .filterValue(null)
                .build(), mockUser());

        assertThat(sentMessagesPage.getTotalElements()).isOne();
        sentMessagesPage.stream().findFirst()
                .ifPresent(message -> {
                    assertThat(message).isEqualTo(m);
                    assertEquals(message.getMessageSummaries(), m.getMessageSummaries());
                });
    }

    @Test
    void should_retrieve_the_sent_message() {
        Message m = mockSentMessage(messageRepository);

        given(messageRepository.findByIdAndSenderGroupId(any(), any())).willReturn(Optional.of(m));

        Message retrievedMessage = messageService.findByIdAndSenderGroupId(m.getId(), m.getSenderGroup().getId());
        assertThat(retrievedMessage.getId()).isEqualTo(m.getId());

    }

    @Test
    void should_retrieve_the_sent_from_page_2() {
        Group sender = mockGroup(SENDER_GROUP_ID);
        Message m = mockSentMessage(messageRepository);
        m.setSentOn(new Date());

        given(messageRepository.findById(1234L)).willReturn(Optional.of(m));
        given(messageRepository.countBySenderGroupIdAndSentOnGreaterThan(anyLong(), eq(m.getSentOn()))).willReturn(11);
        given(messageRepository.countUnread(any(), any())).willReturn(2);
        given(groupRepository.findById(sender.getId())).willReturn(Optional.of(sender));

        PageRequest passedPageRequest = PageRequest.of(0, 10, Sort.by(SENT_ON_SORT));
        PageRequest actualPageRequest = PageRequest.of(1, passedPageRequest.getPageSize(), passedPageRequest.getSort().and(Sort.by(Sort.Order.asc("id"))));

        messageService.getMessagesForUser(FindMessageDto.builder()
                .senderGroupId(sender.getId())
                .messageId(1234L)
                .pageable(PageRequest.of(0, 10, Sort.by(SENT_ON_SORT)))
                .filterBy(null)
                .filterValue(null)
                .build(), mockUser());

        verify(messageRepository).findBySenderGroupAndStatusNotAndSentOnIsNotNull(any(Group.class), any(), eq(actualPageRequest));

    }

    @Test
    void should_retrieve_the_sent_messages_by_status() {
        Group sender = mockGroup(SENDER_GROUP_ID);
        Message m = mockSentMessage(messageRepository);

        given(messageRepository.countUnread(any(), any())).willReturn(1);
        given(groupRepository.findById(sender.getId())).willReturn(Optional.of(sender));
        when(messageRepository.findBySenderGroupAndStatusAndSubjectContainingIgnoreCaseOrSenderGroupIdentifierContainingIgnoreCase(any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(new PageImpl<>(Collections.singletonList(m)));

        MessagePage sentMessagesPage = messageService.getMessagesForUser(FindMessageDto.builder()
                .senderGroupId(sender.getId())
                .messageId(null)
                .pageable(PageRequest.of(0, 10, Sort.by(SENT_ON_SORT)))
                .filterBy("status")
                .filterValue(Status.SENT.name())
                .build(), mockUser());

        assertThat(sentMessagesPage.getTotalElements()).isOne();
        sentMessagesPage.stream().findFirst()
                .ifPresent(message -> {
                    assertThat(message).isEqualTo(m);
                    assertEquals(message.getMessageSummaries(), m.getMessageSummaries());
                });
    }

    @Test
    void should_retrieve_the_sent_message_with_the_summaries() {
        String clientPublicKey = "bpIKYWJUgO24P1wa5EvqfdDHeOx4dsWUC9A0svJOnGA=";
        mockSentMessage(messageRepository);
        Message m = messageService.getMessage(1234L, 1L, clientPublicKey, mockUser());
        assertThat(m.getMessageSummaries().size()).isPositive();
    }

    @Test
    void should_throw_exception_when_the_message_is_not_found() {
        String clientPublicKey = "bpIKYWJUgO24P1wa5EvqfdDHeOx4dsWUC9A0svJOnGA=";

        try {
            messageService.getMessage(567L, 1L , clientPublicKey, mockUser());
        } catch (EtxWebException e) {
            assertEquals("Cannot retrieve a message with id: 567 for Sender id: 1", e.getMessage());
        }
    }

    @Test
    void should_get_tpl_vars_as_string() throws JsonProcessingException {
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put(TPL_VARIABLES_MESSAGE_SUBJECT.getValue(), "SUBJECT");
        templateVariables.put("key", "value");

        String tplVars = messageService.getTemplateVarsAsString(templateVariables);
        Map<String, Object> tplVarConverted = objectMapper.readValue(tplVars, new TypeReference<HashMap<String, Object>>() {
        });
        assertEquals(templateVariables, tplVarConverted);
    }

    @Test
    void should_find_by_sender_group_and_sent_on_before() {
        Group group = mockGroup();

        List<Message> messages = messageService.findBySenderGroupAndSentOnBefore(group, new Date(), false);
        assertEquals(0, messages.size());
    }

    @Test
    void should_find_by_sender_group_and_sent_on_before_and_sent_on_not_null() {
        Group group = mockGroup();
        Message message = mockMessage(group);

        when(messageRepository.findBySenderGroupAndSentOnBeforeAndSentOnNotNull(any(), any())).thenReturn(Collections.nCopies(3, message));

        List<Message> messages = messageService.findBySenderGroupAndSentOnBefore(group, new Date(), true);
        assertEquals(3, messages.size());
    }


    @Test
    void should_delete_by_id() {
        Message mockMessage = mockMessage();
        messageService.delete(mockMessage.getId());
        verify(messageRepository, times(1)).deleteById(mockMessage.getId());
    }
}
