package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.exchange.model.MessageSummaryListItem;
import eu.europa.ec.etrustex.web.exchange.model.SearchItem;
import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummary;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummaryId;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummaryUserStatus;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.persistence.repository.MessageSummaryRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import eu.europa.ec.etrustex.web.service.dto.FindMessageSummaryDto;
import eu.europa.ec.etrustex.web.service.pagination.MessageSummaryPage;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.util.exchange.model.EntitySpec;
import eu.europa.ec.etrustex.web.util.exchange.model.MessageSummarySpec;
import eu.europa.ec.etrustex.web.util.exchange.model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.security.SecureRandom;
import java.util.*;

import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.*;
import static eu.europa.ec.etrustex.web.service.ServiceTestUtils.mockMessageSummarySpec;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SuppressWarnings({"java:S112", "java:S100"})
@ExtendWith(MockitoExtension.class)
class MessageSummaryServiceImplTest {

    private static final String RECIPIENT = "recipient";

    private MessageSummaryService messageSummaryService;
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private MessageSummaryRepository messageSummaryRepository;

    @Mock
    private EncryptionService encryptionService;

    private static final Random RANDOM = new SecureRandom();

    @Captor
    private ArgumentCaptor<MessageSummary> messageSummaryArgumentCaptor;

    @BeforeEach
    void setUp() {
        messageSummaryService = new MessageSummaryServiceImpl(messageSummaryRepository, groupRepository, encryptionService);
    }

    @Test
    void should_throw_AssertionError_when_recipient_cannot_be_found() {
        MessageSummarySpec messageSummarySpec = mockMessageSummarySpec(RANDOM.nextLong());
        given(groupRepository.findById(messageSummarySpec.getRecipientId())).willReturn(Optional.empty());

        Message message = Message.builder().id(RANDOM.nextLong()).build();
        Collection<MessageSummarySpec> singleton = Collections.singleton(messageSummarySpec);
        assertThrows(RuntimeException.class,
                () -> messageSummaryService.createMessageSummaries(message, singleton));
    }

    @Test
    void should_save() {
        MessageSummary ms = mockMessageSummary(Status.DELIVERED);

        given(messageSummaryRepository.save(ms)).willReturn(ms);

        assertEquals(ms, messageSummaryService.save(ms));
    }

    @Test
    void should_save_all() {
        List<MessageSummary> ms = Collections.singletonList(mockMessageSummary(Status.DELIVERED));

        given(messageSummaryRepository.saveAll(ms)).willReturn(ms);

        assertEquals(ms, messageSummaryService.saveAll(ms));
    }

    @Test
    void should_create_message_summaries() {
        Group recipientGroup1 = mockGroup("recipientGroup1");
        Group recipientGroup2 = mockGroup("recipientGroup2");
        Group recipientGroup3 = mockGroup("recipientGroup3");
        MessageSummarySpec msSpec1 = mockMessageSummarySpec(recipientGroup1.getId());
        MessageSummarySpec msSpec2 = mockMessageSummarySpec(recipientGroup2.getId());
        MessageSummarySpec msSpec3 = mockMessageSummarySpec(recipientGroup3.getId());
        MessageSummarySpec msSpec4 = mockMessageSummarySpec();
        msSpec4.setEntitySpec(EntitySpec.builder().entityIdentifier(recipientGroup3.getIdentifier()).businessIdentifier(recipientGroup1.getParent().getIdentifier()).build());

        given(groupRepository.findById(recipientGroup1.getId())).willReturn(Optional.of(recipientGroup1));
        given(groupRepository.findById(recipientGroup2.getId())).willReturn(Optional.of(recipientGroup2));
        given(groupRepository.findById(recipientGroup3.getId())).willReturn(Optional.of(recipientGroup3));
        given(groupRepository.findFirstByIdentifierAndParentIdentifier(any(), any())).willReturn(recipientGroup3);

        messageSummaryService.createMessageSummaries(Message.builder().id(RANDOM.nextLong()).build(), Arrays.asList(msSpec1, msSpec2, msSpec3, msSpec4));

        verify(messageSummaryRepository, times(4)).save(messageSummaryArgumentCaptor.capture());

        List<MessageSummary> messageSummaries = messageSummaryArgumentCaptor.getAllValues();

        assertEquals(recipientGroup1.getIdentifier(), messageSummaries.get(0).getRecipient().getIdentifier());
        assertEquals(recipientGroup2.getIdentifier(), messageSummaries.get(1).getRecipient().getIdentifier());
        assertEquals(recipientGroup3.getIdentifier(), messageSummaries.get(2).getRecipient().getIdentifier());
        assertEquals(recipientGroup3.getIdentifier(), messageSummaries.get(3).getRecipient().getIdentifier());
    }

    @Test
    void should_create_message_summaries_with_client_reference() {
        Group recipientGroup1 = mockGroup("recipientGroup1");
        Group recipientGroup2 = mockGroup("recipientGroup2");
        Group recipientGroup3 = mockGroup("recipientGroup3");
        MessageSummarySpec msSpec1 = mockMessageSummarySpec(recipientGroup1.getId());
        msSpec1.setClientReference("clientRef1");
        MessageSummarySpec msSpec2 = mockMessageSummarySpec(recipientGroup2.getId());
        msSpec2.setClientReference("clientRef2");
        MessageSummarySpec msSpec3 = mockMessageSummarySpec(recipientGroup3.getId());
        msSpec3.setClientReference("clientRef3");

        given(groupRepository.findById(recipientGroup1.getId())).willReturn(Optional.of(recipientGroup1));
        given(groupRepository.findById(recipientGroup2.getId())).willReturn(Optional.of(recipientGroup2));
        given(groupRepository.findById(recipientGroup3.getId())).willReturn(Optional.of(recipientGroup3));

        messageSummaryService.createMessageSummaries(Message.builder().id(RANDOM.nextLong()).build(), Arrays.asList(msSpec1, msSpec2, msSpec3));

        verify(messageSummaryRepository, times(3)).save(messageSummaryArgumentCaptor.capture());

        List<MessageSummary> messageSummaries = messageSummaryArgumentCaptor.getAllValues();

        assertEquals(recipientGroup1.getIdentifier(), messageSummaries.get(0).getRecipient().getIdentifier());
        assertEquals(recipientGroup2.getIdentifier(), messageSummaries.get(1).getRecipient().getIdentifier());
        assertEquals(recipientGroup3.getIdentifier(), messageSummaries.get(2).getRecipient().getIdentifier());
    }

    @Test
    void should_retrieve_the_inbox_message() {
        MessageSummary ms = mockMessageSummary(Status.DELIVERED);

        User user = User.builder().id(1L).build();
        Set<MessageSummaryUserStatus> messageSummaryUserStatuses = ms.getMessageSummaryUserStatuses();
        messageSummaryUserStatuses.add(MessageSummaryUserStatus.builder()
                .status(Status.FAILED).build());
        messageSummaryUserStatuses.add(MessageSummaryUserStatus.builder()
                .user(user)
                .status(Status.READ).build());

        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "message.sentOn"));

        given(messageSummaryRepository.findByRecipientIdAndIsValidPublicKeyTrueAndIsActiveTrue(anyLong(), any(PageRequest.class)))
                .willReturn(new PageImpl<>(Collections.singletonList(ms), pageRequest, 1));

        given(messageSummaryRepository.countUnread(any(), any())).willReturn(1);

        FindMessageSummaryDto findMessageSummaryDto = FindMessageSummaryDto.builder()
                .recipientGroupId(ms.getRecipient().getId())
                .messageId(null)
                .pageable(pageRequest)
                .filterBy(null)
                .filterValue(null)
                .build();

        MessageSummaryPage messageSummaryPage = messageSummaryService.findByRecipientForUser(findMessageSummaryDto, user);
        List<MessageSummary> messageSummaries = messageSummaryPage.getContent();

        assertThat(messageSummaries.size()).isOne();
        assertThat(messageSummaryPage.getUnreadMessages()).isOne();
        MessageSummary messageSummary = messageSummaries.get(0);
        assertThat(messageSummary).isEqualTo(ms);

        assertEquals(1, messageSummary.getMessageSummaryUserStatuses().size());
        messageSummary.getMessageSummaryUserStatuses().forEach(msgUserStatus -> assertEquals(user, msgUserStatus.getUser()));
    }

    @Test
    void should_retrieve_message_summary_with_msg_usr_status_only_for_current_user() {
        User currentUser = User.builder()
                .id(1L)
                .ecasId("current_user_")
                .build();
        MessageSummary ms = mockMessageSummaryWithUserStatus(currentUser);
        ms.getRecipient().setId(0L);

        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "message.sentOn"));

        given(messageSummaryRepository.findByRecipientIdAndIsValidPublicKeyTrueAndIsActiveTrue(anyLong(), any(PageRequest.class)))
                .willReturn(new PageImpl<>(Collections.singletonList(ms), pageRequest, 2));

        FindMessageSummaryDto findMessageSummaryDto = FindMessageSummaryDto.builder()
                .recipientGroupId(ms.getRecipient().getId())
                .messageId(null)
                .pageable(pageRequest)
                .filterBy(null)
                .filterValue(null)
                .build();

        MessageSummaryPage messageSummaryPage = messageSummaryService.findByRecipientForUser(findMessageSummaryDto, currentUser);
        List<MessageSummary> messageSummaries = messageSummaryPage.getContent();

        assertThat(messageSummaries.get(0).getMessageSummaryUserStatuses().size()).isOne();
        MessageSummaryUserStatus messageSummaryUserStatus = messageSummaries.get(0).getMessageSummaryUserStatuses().iterator().next();

        assertThat(messageSummaryUserStatus.getUser()).isEqualTo(currentUser);
    }

    @Test
    void should_get_the_page_for_the_selected_message_summary() {
        int pageSize = 10;
        int messageSummaryPosition = 22;
        List<MessageSummary> mockMessageSummaries = mockMessageSummaries(45);
        MessageSummary messageSummary = mockMessageSummaries.get(messageSummaryPosition);
        int pageIndex = messageSummaryPosition / pageSize;
        PageRequest pageRequest = PageRequest.of(pageIndex, pageSize);

        given(messageSummaryRepository.countByRecipientIdAndMessageSentOnGreaterThan(anyLong(), any(Date.class)))
                .willReturn(messageSummaryPosition);
        given(messageSummaryRepository.countUnread(any(), any())).willReturn(messageSummaryPosition);
        given(messageSummaryRepository.findByMessageIdAndRecipientIdAndIsActiveIsTrue(any(), any())).willReturn(Optional.of(messageSummary));
        given(messageSummaryRepository.findByRecipientIdAndIsValidPublicKeyTrueAndIsActiveTrue(anyLong(), any(PageRequest.class)))
                .willReturn(new PageImpl<>(mockMessageSummaries, pageRequest, 2));

        FindMessageSummaryDto findMessageSummaryDto = FindMessageSummaryDto.builder()
                .recipientGroupId(messageSummary.getRecipient().getId())
                .messageId(messageSummary.getMessage().getId())
                .pageable(pageRequest)
                .filterBy(null)
                .filterValue(null)
                .build();

        MessageSummaryPage messageSummaryPage = messageSummaryService.findByRecipientForUser(findMessageSummaryDto, User.builder().id(1L).build());

        assertTrue(messageSummaryPage.getContent().contains(messageSummary));
        assertEquals(pageSize, messageSummaryPage.getSize());
    }

    @Test
    void should_find_by_message_id_and_recipient_id() {
        Long recipientId = RANDOM.nextLong();
        Long messageId = RANDOM.nextLong();
        MessageSummary messageSummaryMock = mockMessageSummary(Status.DELIVERED);

        given(messageSummaryRepository.findByMessageIdAndRecipientIdAndIsActiveIsTrue(messageId, recipientId))
                .willReturn(Optional.of(messageSummaryMock));

        MessageSummary messageSummary = messageSummaryService.findByMessageIdAndRecipientId(messageId, recipientId);

        assertEquals(messageSummaryMock, messageSummary);
    }

    @Test
    void should_find_by_message_id_and_recipient_id_for_current_user() {
        Long recipientId = RANDOM.nextLong();
        Long messageId = RANDOM.nextLong();
        User user = mockUser();
        MessageSummary ms = mockMessageSummaryWithUserStatus(user);
        String clientPublicKey = "bpIKYWJUgO24P1wa5EvqfdDHeOx4dsWUC9A0svJOnGA=";

        given(messageSummaryRepository.findByMessageIdAndRecipientIdAndIsActiveIsTrue(messageId, recipientId))
                .willReturn(Optional.of(ms));

        given(encryptionService.decryptSymmetricKeyAndEncryptWithClientPublicKey(any(), anyString())).willReturn(ms.getMessage().getSymmetricKey());

        MessageSummary messageSummary = messageSummaryService.findByMessageIdAndRecipientIdForCurrentUser(messageId, clientPublicKey, recipientId, user);

        assertThat(messageSummary.getMessageSummaryUserStatuses().size()).isOne();
        MessageSummaryUserStatus messageSummaryUserStatus = messageSummary.getMessageSummaryUserStatuses().iterator().next();

        assertThat(messageSummaryUserStatus.getUser()).isEqualTo(user);
    }

    @Test
    void should_get_current() {
        MessageSummary updated = mockMessageSummary(Status.DELIVERED);
        MessageSummary persisted = mockMessageSummary(Status.DELIVERED);
        MessageSummaryId messageSummaryId = new MessageSummaryId(updated.getRecipient().getId(), updated.getMessage().getId());

        given(messageSummaryRepository.findById(messageSummaryId)).willReturn(Optional.ofNullable(persisted));

        assertEquals(persisted, messageSummaryService.getCurrent(updated));
    }

    @Test
    void should_throw_get_current_if_not_found() {
        MessageSummary updated = mockMessageSummary(Status.DELIVERED);
        MessageSummaryId messageSummaryId = new MessageSummaryId(updated.getRecipient().getId(), updated.getMessage().getId());

        given(messageSummaryRepository.findById(messageSummaryId)).willReturn(Optional.empty());

        assertThrows(EtxWebException.class, () -> messageSummaryService.getCurrent(updated));
    }


    @Test
    void should_find_optional_by_message_id_and_recipient_id() {
        Long recipientId = RANDOM.nextLong();
        Long messageId = RANDOM.nextLong();
        MessageSummary messageSummaryMock = mockMessageSummary(Status.DELIVERED);

        given(messageSummaryRepository.findByMessageIdAndRecipientIdAndIsActiveIsTrue(messageId, recipientId))
                .willReturn(Optional.of(messageSummaryMock));

        MessageSummary messageSummary = messageSummaryService.findOptionalByMessageIdAndRecipientId(messageId, recipientId)
                .orElseThrow(() -> new Error("Message summary not found!"));

        assertEquals(messageSummaryMock, messageSummary);
    }

    @Test
    void should_return_true_if_group_is_recipient() {
        Group recipientGroup = mockGroup(RECIPIENT);
        Long messageId = RANDOM.nextLong();

        given(messageSummaryRepository.findByMessageIdAndRecipientId(messageId, recipientGroup.getId()))
                .willReturn(Optional.of(mockMessageSummary(Status.DELIVERED)));

        assertTrue(messageSummaryService.groupIsRecipient(messageId, recipientGroup));
    }


    @Test
    void should_return_false_if_group_is_recipient() {
        Group recipientGroup = mockGroup(RECIPIENT);
        Long messageId = RANDOM.nextLong();

        given(messageSummaryRepository.findByMessageIdAndRecipientId(messageId, recipientGroup.getId()))
                .willReturn(Optional.empty());

        assertFalse(messageSummaryService.groupIsRecipient(messageId, recipientGroup));
    }

    @Test
    void should_return_false_group_is_recipient_if_recipient_is_null() {
        Long messageId = RANDOM.nextLong();

        assertFalse(messageSummaryService.groupIsRecipient(messageId, null));
    }

    @Test
    void should_find_by_client_reference_and_recipient_identifier() {
        String clientRef = "ETRUSTEX_WEB_354fgfg";
        Group recipientGroup = mockGroup(RECIPIENT);

        MessageSummary messageSummary = mockMessageSummary(recipientGroup.getIdentifier());
        given(messageSummaryRepository.findByClientReferenceAndRecipientIdentifier(clientRef, recipientGroup.getIdentifier())).willReturn(Optional.of(messageSummary));

        Optional<MessageSummary> foundMessageSummary = messageSummaryService.findByClientReferenceAndRecipientIdentifier(clientRef, recipientGroup.getIdentifier());
        assertTrue(foundMessageSummary.isPresent());

        foundMessageSummary.ifPresent(summary -> assertEquals(messageSummary.getRecipient(), summary.getRecipient()));

    }

    @Test
    void should_find_by_client_reference_and_recipient_id() {
        String clientRef = "ETRUSTEX_WEB_354fgfg";
        Group recipientGroup = mockGroup(RECIPIENT);

        given(messageSummaryRepository.existsByClientReferenceAndRecipientId(clientRef, recipientGroup.getId())).willReturn(Boolean.TRUE);

        boolean exists = messageSummaryService.isMessageSummaryCreatedForClientReferenceAndRecipientId(clientRef, recipientGroup.getId());
        assertTrue(exists);

    }

    @Test
    void should_count_by_publicKeyValue_and_recipientId() {
        String publicKeyHashValue = "DJDF65DFLKNDF5D54DFKLNDKNDIZPSDKJNSDF52S68SDGFSDG5DFG";
        int numberOfMessageSummaries = RANDOM.nextInt();
        given(messageSummaryRepository.countByPublicKeyHashValueAndRecipientIdAndIsValidPublicKeyIsTrue(any(), any())).willReturn(numberOfMessageSummaries);
        assertEquals(numberOfMessageSummaries, messageSummaryService.countByPublicKeyHashValueAndRecipientId(publicKeyHashValue, 2L));
    }

    @Test
    void should_get_messages_display() {
        Group recipientGroup = mockGroup(RECIPIENT);

        List<MessageSummaryListItem> messageSummaryListItems = new ArrayList<>();
        mockMessageSummaries(30).forEach(messageSummary -> messageSummaryListItems.add(new MessageSummaryListItem(
                messageSummary.getMessage().getId(),
                messageSummary.getMessage().getSubject(),
                messageSummary.getRecipient().getIdentifier(),
                messageSummary.getAuditingEntity().getCreatedDate(),
                messageSummary.isActive())));
        PageRequest pageRequest = PageRequest.of(2, 10);

        given(messageSummaryRepository.findMessageSummaryListItemsByBusinessIdAndMessageIdOrSubject(any(), any(), any())).willReturn(new PageImpl<>(messageSummaryListItems, pageRequest, 10));

        Page<MessageSummaryListItem> messageSummaryListItemPage = messageSummaryService.findMessageSummaryListItemsByBusinessIdAndMessageIdOrSubject(recipientGroup.getBusinessId(), "flk", pageRequest);
        assertEquals(50, messageSummaryListItemPage.getTotalElements());
    }

    @Test
    void should_search_by_messageId_or_subject() {
        Group recipientGroup = mockGroup(RECIPIENT);

        List<MessageSummaryListItem> messageSummaryListItems = new ArrayList<>();
        mockMessageSummaries(30).forEach(messageSummary -> {
            messageSummaryListItems.add(new MessageSummaryListItem(
                    messageSummary.getMessage().getId(),
                    messageSummary.getMessage().getSubject(),
                    messageSummary.getRecipient().getIdentifier(),
                    messageSummary.getAuditingEntity().getCreatedDate(),
                    messageSummary.isActive()));
        });
        PageRequest pageRequest = PageRequest.of(2, 10);
        given(messageSummaryRepository.findMessageSummaryListItemsByBusinessIdAndMessageIdOrSubject(any(), any(), any())).willReturn(new PageImpl<>(messageSummaryListItems, pageRequest, 10));

        List<SearchItem> searchItems = messageSummaryService.search(recipientGroup.getBusinessId(), "rea");
        assertEquals(30, searchItems.size());
    }

    @Test
    void should_set_active_status() {
        Group recipientGroup = mockGroup(RECIPIENT);
        MessageSummary messageSummary = mockMessageSummary(recipientGroup.getIdentifier());
        given(messageSummaryRepository.findByMessageIdAndRecipientIdentifier(any(), any())).willReturn(Optional.of(messageSummary));
        messageSummaryService.setActive(2L, recipientGroup.getIdentifier(), true);

        verify(messageSummaryRepository, times(1)).save(messageSummaryArgumentCaptor.capture());

    }

    @Test
    void should_active_or_inactive_message_summaries() {
        List<MessageSummary> messageSummaries = mockMessageSummaries(30);
        List<MessageSummaryListItem> messageSummaryListItems = new ArrayList<>();
        messageSummaries.forEach(messageSummary -> {
            messageSummaryListItems.add(new MessageSummaryListItem(
                    messageSummary.getMessage().getId(),
                    messageSummary.getMessage().getSubject(),
                    messageSummary.getRecipient().getIdentifier(),
                    messageSummary.getAuditingEntity().getCreatedDate(),
                    messageSummary.isActive()));
            given(messageSummaryRepository.findByMessageIdAndRecipientIdentifier(any(), any())).willReturn(Optional.of(messageSummary));
        });

        messageSummaryService.activateOrInactivateMessageSummaries(messageSummaryListItems, true);

        verify(messageSummaryRepository, times(30)).save(any());
    }
}
