package eu.europa.ec.etrustex.web.service.api;

import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummary;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.persistence.repository.MessageSummaryRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import eu.europa.ec.etrustex.web.service.ExchangeRuleService;
import eu.europa.ec.etrustex.web.service.MessageSummaryService;
import eu.europa.ec.etrustex.web.service.MessageSummaryUserStatusService;
import eu.europa.ec.etrustex.web.service.StatusService;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.util.exchange.model.EntitySpec;
import eu.europa.ec.etrustex.web.util.exchange.model.MessageSummarySpec;
import eu.europa.ec.etrustex.web.util.exchange.model.RecipientStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.*;
import static eu.europa.ec.etrustex.web.util.exchange.model.Ack.*;
import static eu.europa.ec.etrustex.web.util.exchange.model.GroupType.ROOT;
import static eu.europa.ec.etrustex.web.util.exchange.model.Status.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"java:S100"})
class ApiMessageSummaryServiceImplTest {

    private ApiMessageSummaryService apiMessageSummaryService;
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private MessageSummaryRepository messageSummaryRepository;

    @Mock
    private MessageSummaryService messageSummaryService;

    @Mock
    private MessageSummaryUserStatusService messageSummaryUserStatusService;

    @Mock
    private StatusService statusService;

    @Mock
    private ExchangeRuleService exchangeRuleService;

    private SecureRandom random;


    @BeforeEach
    void setUp() {
        apiMessageSummaryService = new ApiMessageSummaryServiceImpl(messageSummaryRepository, groupRepository, messageSummaryUserStatusService, messageSummaryService, exchangeRuleService, statusService);
        random = new SecureRandom();
    }

    @Test
    void should_findByMessageIdAndRecipientIdForCurrentUser() {
        MessageSummary ms = mockMessageSummary(DELIVERED);
        Group recipient = ms.getRecipient();
        User user = User.builder().id(1L).build();
        EntitySpec entitySpec = EntitySpec.builder()
                .businessIdentifier(recipient.getBusinessIdentifier())
                .entityIdentifier(recipient.getIdentifier())
                .build();
        String clientPublicKey = "key";

        given(groupRepository.findFirstByIdentifierAndParentIdentifier(entitySpec.getEntityIdentifier(), entitySpec.getBusinessIdentifier())).willReturn(recipient);
        given(messageSummaryService.findByMessageIdAndRecipientIdForCurrentUser(ms.getMessage().getId(), clientPublicKey, recipient.getId(), user)).willReturn(ms);

        MessageSummary retrieved = apiMessageSummaryService.findByMessageIdAndRecipientIdForCurrentUser(ms.getMessage().getId(), clientPublicKey, entitySpec, user);

        assertEquals(ms, retrieved);
    }

    @Test
    void should_create_message_summaries() {
        Group recipientGroup1 = mockGroup("recipientGroup1");
        Group recipientGroup2 = mockGroup("recipientGroup2");

        Message message = Message.builder().id(random.nextLong()).build();
        List<MessageSummarySpec> specList = Arrays.asList(
                mockApiMessageSummarySpec(recipientGroup1.getBusinessIdentifier(), recipientGroup1.getIdentifier()),
                mockApiMessageSummarySpec(recipientGroup2.getBusinessIdentifier(), recipientGroup2.getIdentifier())
        );

        apiMessageSummaryService.createMessageSummaries(message, specList);

        verify(messageSummaryService, times(1)).createMessageSummaries(message, specList);
    }


    @Test
    void should_retrieve_unread_messages() {
        MessageSummary ms = mockMessageSummary(DELIVERED);
        Group recipient = ms.getRecipient();
        User user = User.builder().id(1L).build();
        String clientPublicKey = "aa";

        given(groupRepository.findFirstByIdentifierAndParentIdentifier(recipient.getIdentifier(), recipient.getBusinessIdentifier())).willReturn(recipient);
        given(messageSummaryRepository.findUnreadMessageSummariesByRecipientAndStatus(recipient.getId(), SENT)).willReturn(Stream.of(ms));
        given(messageSummaryService.decryptAndEncryptSymmetricKeysAndFilterUserStatuses(ms, user, clientPublicKey)).willReturn(ms);

        EntitySpec entitySpec = EntitySpec.builder()
                .businessIdentifier(recipient.getBusinessIdentifier())
                .entityIdentifier(recipient.getIdentifier())
                .build();

        List<MessageSummary> messageSummaries = apiMessageSummaryService.findUnreadByRecipientIdForCurrentUser(entitySpec, user, clientPublicKey);

        assertThat(messageSummaries.size()).isOne();

        MessageSummary messageSummary = messageSummaries.get(0);

        assertThat(messageSummary).isEqualTo(ms);
    }

    @Test
    void should_throw_if_clientPublicKey_is_empty_for_retrieve_unread_messages() {
        User user = User.builder().build();
        EntitySpec entitySpec = EntitySpec.builder().build();

        assertThrows(EtxWebException.class, () -> apiMessageSummaryService.findUnreadByRecipientIdForCurrentUser(entitySpec, user, ""));
    }

    @Test
    void should_retrieve_message_status() {
        MessageSummary messageSummary = mockMessageSummary(DELIVERED);
        Message message = messageSummary.getMessage();

        given(messageSummaryRepository.findByMessageIdAndIsActiveTrue(message.getId())).willReturn(Stream.of(messageSummary));
        List<RecipientStatus> recipientStatuses = apiMessageSummaryService.recipientStatusByMessageId(message.getId());
        assertEquals(DELIVERED, recipientStatuses.get(0).getStatus());
    }

    @Test
    void should_retrieve_sender_exchange_rules() {
        Group sender = mockGroupWithRecipientAndSenderPreferences();

        given(groupRepository.findFirstByIdentifierAndParentIdentifier(sender.getIdentifier(), sender.getBusinessIdentifier())).willReturn(sender);

        List<Group> recipients = apiMessageSummaryService.getValidRecipients(sender.getBusinessIdentifier(), sender.getIdentifier());

        assertEquals(recipients.size(), 0);
    }

    @Test
    void should_raise_404() {
        Group recipient = mockGroup();

        lenient().when(groupRepository.findFirstByIdentifierAndParentIdentifier(recipient.getIdentifier(), recipient.getBusinessIdentifier())).thenReturn(null);

        assertThrows(ResponseStatusException.class, () -> apiMessageSummaryService.getValidRecipients(recipient.getBusinessIdentifier(), recipient.getIdentifier()));
        assertThrows(ResponseStatusException.class, () -> apiMessageSummaryService.getValidRecipients("InvalidBusiness", recipient.getIdentifier()));

        lenient().when(groupRepository.findFirstByIdentifierAndParentIdentifier(recipient.getBusinessIdentifier(), ROOT.name())).thenReturn(recipient.getParent());
        assertThrows(ResponseStatusException.class, () -> apiMessageSummaryService.getValidRecipients(recipient.getBusinessIdentifier(), "Invalid Entity"));
    }


    @Test
    void should_change_status_if_ack_error() {
        User user = mockUser();
        MessageSummary ms = mockMessageSummary(DELIVERED);
        Group recipient = ms.getRecipient();
        EntitySpec entitySpec = EntitySpec.builder()
                .businessIdentifier(recipient.getBusinessIdentifier())
                .entityIdentifier(recipient.getIdentifier())
                .build();

        given(groupRepository.findFirstByIdentifierAndParentIdentifier(recipient.getIdentifier(), recipient.getBusinessIdentifier())).willReturn(recipient);
        given(messageSummaryService.findByMessageIdAndRecipientId(ms.getMessage().getId(), recipient.getId())).willReturn(ms);

        apiMessageSummaryService.ack(user, ms.getMessage().getId(), entitySpec, MESSAGE_FAILED);

        assertEquals(FAILED, ms.getStatus());
    }


    @Test
    void should_change_status_if_ack_read() {
        User user = mockUser();
        MessageSummary ms = mockMessageSummary(DELIVERED);
        Group recipient = ms.getRecipient();
        EntitySpec entitySpec = EntitySpec.builder()
                .businessIdentifier(recipient.getBusinessIdentifier())
                .entityIdentifier(recipient.getIdentifier())
                .build();

        given(groupRepository.findFirstByIdentifierAndParentIdentifier(recipient.getIdentifier(), recipient.getBusinessIdentifier())).willReturn(recipient);
        given(messageSummaryService.findByMessageIdAndRecipientId(ms.getMessage().getId(), recipient.getId())).willReturn(ms);

        apiMessageSummaryService.ack(user, ms.getMessage().getId(), entitySpec, MESSAGE_READ);

        assertEquals(READ, ms.getStatus());
    }

    @Test
    void should_change_status_if_ack_ok() {
        User user = mockUser();
        MessageSummary ms = mockMessageSummary(SENT);
        Group recipient = ms.getRecipient();
        EntitySpec entitySpec = EntitySpec.builder()
                .businessIdentifier(recipient.getBusinessIdentifier())
                .entityIdentifier(recipient.getIdentifier())
                .build();

        given(groupRepository.findFirstByIdentifierAndParentIdentifier(recipient.getIdentifier(), recipient.getBusinessIdentifier())).willReturn(recipient);
        given(messageSummaryService.findByMessageIdAndRecipientId(ms.getMessage().getId(), recipient.getId())).willReturn(ms);

        apiMessageSummaryService.ack(user, ms.getMessage().getId(), entitySpec, MESSAGE_RETRIEVED_OK);

        assertEquals(DELIVERED, ms.getStatus());
        assertEquals(DELIVERED, ms.getMessage().getStatus());
    }

    private MessageSummarySpec mockApiMessageSummarySpec(String businessIdentifier, String entityIdentifier) {
        return MessageSummarySpec.builder()
                .entitySpec(EntitySpec.builder()
                        .businessIdentifier(businessIdentifier)
                        .entityIdentifier(entityIdentifier)
                        .build())
                .build();
    }
}
