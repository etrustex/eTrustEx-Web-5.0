package eu.europa.ec.etrustex.web.persistence.repository;

import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummary;
import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.UserProfileRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.UserRepository;
import eu.europa.ec.etrustex.web.util.exchange.model.Status;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MessageRepositoryTest {

    public static final String SENT_ON = "sentOn";
    @Autowired
    MessageRepository messageRepository;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    MessageSummaryRepository messageSummaryRepository;
    @Autowired
    UserProfileRepository userProfileRepository;

    private InboxAndSentTestHelper inboxAndSentTestHelper;

    @BeforeEach
    public void init() {
        this.inboxAndSentTestHelper = new InboxAndSentTestHelper(groupRepository, userRepository, messageRepository, messageSummaryRepository, userProfileRepository);
    }

    @Test
    void should_find_by_id_and_sender() {
        assertThat(messageRepository.findByIdAndSenderGroup(inboxAndSentTestHelper.message.getId(), inboxAndSentTestHelper.senderGroup)).isPresent();
    }

    @Test
    void should_find_by_sender_id() {
        assertThat(messageRepository.findBySenderGroupId(inboxAndSentTestHelper.senderGroup.getId()).size()).isPositive();
    }

    @Test
    void message_summary_deletions_should_update_message_summaries_in_message_but_not_delete_the_message() throws Exception {
        messageRepository.findByIdAndSenderGroup(inboxAndSentTestHelper.message.getId(), inboxAndSentTestHelper.senderGroup)
                .ifPresent(message -> {
                    assertThat(message.getMessageSummaries().size()).isPositive();
                    messageSummaryRepository.deleteAll(message.getMessageSummaries());
                });

        messageRepository.findByIdAndSenderGroup(inboxAndSentTestHelper.message.getId(), inboxAndSentTestHelper.senderGroup)
                .map(message -> assertThat(message.getMessageSummaries().size()).isZero())
                .orElseThrow(() -> new Exception("Failed to retrieve the message"));
    }

    @Test
    void should_cascade_delete_the_message_summaries() {
        Set<MessageSummary> summariesToDelete = new HashSet<>();
        messageRepository.findByIdAndSenderGroup(inboxAndSentTestHelper.message.getId(), inboxAndSentTestHelper.senderGroup)
                .ifPresent(message -> {
                    summariesToDelete.addAll(message.getMessageSummaries());
                    this.messageRepository.delete(message);
                });

        List<MessageSummary> remainingSummaries = this.messageSummaryRepository.findAll();

        assertThat(summariesToDelete.size()).isPositive();
        summariesToDelete.forEach(messageSummary -> assertThat(remainingSummaries.contains(messageSummary)).isFalse());
    }

    @Test
    void should_find_by_id_and_retrieve_the_summaries() {
        messageRepository.findById(inboxAndSentTestHelper.message.getId())
                .map(message1 -> assertThat(message1.getMessageSummaries().isEmpty()))
                .orElseThrow(() -> new Error("Message not found!"));
    }

    @Test
    void should_retrieve_the_sent_message_with_summaries() {
        Page<Message> messagePage = this.messageRepository.findBySenderGroupAndStatusNotAndSentOnIsNotNull(inboxAndSentTestHelper.senderGroup, Status.DELIVERED, PageRequest.of(0, 10, Sort.by(SENT_ON)));

        assertThat(messagePage.getTotalElements()).isEqualTo(1L);

        messagePage.forEach(m -> assertThat(m.getMessageSummaries().size()).isEqualTo(2));
    }

    @Test
    void should_retrieve_the_draft_message() {
        Page<Message> messagePage = this.messageRepository.findDraftMessages(inboxAndSentTestHelper.senderGroup, "", new Date(0), new Date(), PageRequest.of(0, 10, Sort.by(SENT_ON)));

        assertThat(messagePage.getTotalElements()).isEqualTo(1L);
        assertEquals(inboxAndSentTestHelper.draftMessage, messagePage.getContent().get(0));
        messagePage.forEach(m -> assertThat(m.getStatus()).isEqualTo(Status.DRAFT));
    }

    @Test
    void should_count_return_page_the_second_page() {
        assertEquals(4, this.messageRepository.countBySenderGroupIdAndSentOnGreaterThan(inboxAndSentTestHelper.senderGroup.getId(), inboxAndSentTestHelper.earlyMessage.getSentOn()));
        assertEquals(1, this.messageRepository.countBySenderGroupIdAndSentOnGreaterThan(inboxAndSentTestHelper.senderGroup.getId(), inboxAndSentTestHelper.message.getSentOn()));
        assertEquals(0, this.messageRepository.countBySenderGroupIdAndSentOnGreaterThan(inboxAndSentTestHelper.senderGroup.getId(), new Date()));
    }

    @Test
    void should_not_find_expired_message() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.WEEK_OF_YEAR, -6);

        assertEquals(0, messageRepository.findBySenderGroupAndSentOnBeforeAndSentOnNotNull(inboxAndSentTestHelper.senderGroup, calendar.getTime()).size());
    }

    @Test
    void should_return_one_expired_message() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.WEEK_OF_YEAR, -4);

        assertEquals(1, messageRepository.findBySenderGroupAndSentOnBeforeAndSentOnNotNull(inboxAndSentTestHelper.senderGroup, calendar.getTime()).size());
    }

    @Test
    void should_retrieve_the_sent_message_by_status() {
        Page<Message> messagePage = this.messageRepository.findBySenderGroupAndStatusNotAndSentOnIsNotNull(inboxAndSentTestHelper.senderGroup, Status.DELIVERED, PageRequest.of(0, 10, Sort.by(SENT_ON)));

        assertThat(messagePage.getTotalElements()).isOne();

    }

    @Test
    void should_retrieve_not_sent_message() {

        Date twoHoursBefore = Date.from(Instant.now().minus(2, ChronoUnit.HOURS));

        inboxAndSentTestHelper.saveIncompleteMessages();

        Page<Message> messagePage = this.messageRepository.findByStatusIsNullAndAuditingEntityModifiedDateLessThan(twoHoursBefore, PageRequest.of(0, 10));

        messagePage.forEach(message -> {
            assertTrue(message.getModifiedDate().before(twoHoursBefore));
            assertNull(message.getStatus());
        });

        assertThat(messagePage.getTotalElements()).isPositive();
    }

    @Test
    void should_count_user_unread_sent_messages() {
        Assertions.assertThat(
                this.messageRepository.countUnread(
                        this.inboxAndSentTestHelper.senderGroup.getId(),
                        this.inboxAndSentTestHelper.senderUser)
        ).isEqualTo(4);
        this.inboxAndSentTestHelper.setRead();
        Assertions.assertThat(
                this.messageRepository.countUnread(
                        this.inboxAndSentTestHelper.senderGroup.getId(),
                        this.inboxAndSentTestHelper.senderUser)
        ).isEqualTo(3);
    }

    @Test
    void should_find_the_sent_messages_by_status_and_subject_like() {
        Page<Message> messagePage = this.messageRepository.findBySenderGroupAndStatusNotAndSubjectContainingIgnoreCaseOrSenderGroupIdentifierContainingIgnoreCase(
                inboxAndSentTestHelper.senderGroup, inboxAndSentTestHelper.senderUser, Status.DRAFT, inboxAndSentTestHelper.message.getSubject(), false, new Date(0), new Date(), PageRequest.of(0, 10, Sort.by(SENT_ON))
        );

        assertThat(messagePage.getTotalElements()).isEqualTo(1L);
    }

    @Test
    void should_find_messages_by_status_and_subject_like() {
        Page<Message> messagePage = this.messageRepository.findBySenderGroupAndStatusAndSubjectContainingIgnoreCaseOrSenderGroupIdentifierContainingIgnoreCase(
                inboxAndSentTestHelper.senderGroup, inboxAndSentTestHelper.senderUser, Status.DELIVERED, inboxAndSentTestHelper.message.getSubject(), false, new Date(0), new Date(), PageRequest.of(0, 10, Sort.by(SENT_ON))
        );

        assertThat(messagePage.getTotalElements()).isEqualTo(1);
    }
}
