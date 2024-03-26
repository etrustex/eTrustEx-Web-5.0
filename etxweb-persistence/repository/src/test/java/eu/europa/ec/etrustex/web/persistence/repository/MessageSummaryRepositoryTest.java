package eu.europa.ec.etrustex.web.persistence.repository;

import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummary;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummaryUserStatus;
import eu.europa.ec.etrustex.web.persistence.entity.UserProfile;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.UserProfileRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.UserRepository;
import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import eu.europa.ec.etrustex.web.util.exchange.model.Status;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.AssertionsForClassTypes;
import org.hamcrest.MatcherAssert;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.PUBLIC_KEY;
import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.mockMessage;
import static eu.europa.ec.etrustex.web.util.crypto.Base64.toHash;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MessageSummaryRepositoryTest {

    @Autowired
    private MessageSummaryRepository messageSummaryRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MessageSummaryUserStatusRepository messageSummaryUserStatusRepository;
    @Autowired
    private UserProfileRepository userProfileRepository;
    private InboxAndSentTestHelper inboxAndSentTestHelper;

    @BeforeEach
    public void init() {
        this.inboxAndSentTestHelper = new InboxAndSentTestHelper(groupRepository, userRepository, messageRepository, messageSummaryRepository, userProfileRepository);
        inboxAndSentTestHelper.setRead();
    }

    @Test
    void should_find_by_message_id_and_recipient_id() {
        assertTrue(messageSummaryRepository.findByMessageIdAndRecipientId(
                inboxAndSentTestHelper.message.getId(),
                inboxAndSentTestHelper.recipientGroup.getId()).isPresent());
    }

    @Test
    void should_find_by_message_id_and_recipient_id_in() {
        assertEquals(1, messageSummaryRepository.findByRecipientIdAndMessageIdIn(
                inboxAndSentTestHelper.recipientGroup.getId(),
                Collections.singletonList(inboxAndSentTestHelper.message.getId())).size());
    }

    @Test
    void should_find_by_recipient_id() {
        assertThat(messageSummaryRepository.findByRecipientId(
                inboxAndSentTestHelper.recipientGroup.getId()).size()).isPositive();
    }

    @Test
    void should_not_find_with_wrong_id() {
        assertFalse(messageSummaryRepository.findByMessageIdAndRecipientId(
                -1L,
                inboxAndSentTestHelper.recipientGroup.getId()).isPresent()
        );
    }

    @Test
    void should_count_the_message_recipients() {
        assertThat(messageSummaryRepository.countByMessage(inboxAndSentTestHelper.message)).isEqualTo(2);
    }

    @Test
    void should_retrieve_the_inbox() {
        Page<MessageSummary> messageSummaries = messageSummaryRepository.findByRecipientIdAndIsValidPublicKeyTrueAndIsActiveTrue(
                inboxAndSentTestHelper.recipientGroup.getId(),
                PageRequest.of(0, 10));

        assertThat(messageSummaries.getContent().size()).isEqualTo(5);
    }

    @Test
    void should_retrieve_message_summaries_sorted_by_message_subject_ascending() {
        Instant beforeFirstMessage = Instant.now();

        initMessageSummariesForSorting(beforeFirstMessage);

        Page<MessageSummary> messageSummaries = messageSummaryRepository.findByRecipientIdAndIsValidPublicKeyTrueAndIsActiveTrue(
                inboxAndSentTestHelper.recipientGroup.getId(),
                PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "message.subject")));

        List<MessageSummary> sortedMessageSummaries = new ArrayList<>(messageSummaries.getContent());
        sortedMessageSummaries.sort(Comparator.comparing(ms -> ms.getMessage().getSubject()));

        MatcherAssert.assertThat(messageSummaries, contains(sortedMessageSummaries.toArray()));
    }

    @Test
    void should_retrieve_message_summaries_sorted_by_message_subject_descending() {
        Instant beforeFirstMessage = Instant.now();

        initMessageSummariesForSorting(beforeFirstMessage);

        Page<MessageSummary> messageSummaries = messageSummaryRepository.findByRecipientIdAndIsValidPublicKeyTrueAndIsActiveTrue(
                inboxAndSentTestHelper.recipientGroup.getId(),
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "message.subject")));

        List<MessageSummary> sortedMessageSummaries = new ArrayList<>(messageSummaries.getContent());
        sortedMessageSummaries.sort(Comparator.comparing(ms -> ms.getMessage().getSubject(), Comparator.reverseOrder()));

        MatcherAssert.assertThat(messageSummaries, contains(sortedMessageSummaries.toArray()));
    }

    @Test
    void should_retrieve_message_summaries_sorted_by_senderGroup_name_ascending() {
        Instant beforeFirstMessage = Instant.now();

        initMessageSummariesForSorting(beforeFirstMessage);

        Page<MessageSummary> messageSummaries = messageSummaryRepository.findByRecipientIdAndIsValidPublicKeyTrueAndIsActiveTrue(
                inboxAndSentTestHelper.recipientGroup.getId(),
                PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "message.senderGroup.name")));

        List<MessageSummary> sortedMessageSummaries = new ArrayList<>(messageSummaries.getContent());
        sortedMessageSummaries.sort(Comparator.comparing(ms -> ms.getMessage().getSenderGroup().getName()));

        MatcherAssert.assertThat(messageSummaries, contains(sortedMessageSummaries.toArray()));
    }

    @Test
    void should_retrieve_message_summaries_sorted_by_senderGroup_name_descending() {
        Instant beforeFirstMessage = Instant.now();

        initMessageSummariesForSorting(beforeFirstMessage);

        Page<MessageSummary> messageSummaries = messageSummaryRepository.findByRecipientIdAndIsValidPublicKeyTrueAndIsActiveTrue(
                inboxAndSentTestHelper.recipientGroup.getId(),
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "message.senderGroup.name")));

        List<MessageSummary> sortedMessageSummaries = new ArrayList<>(messageSummaries.getContent());
        sortedMessageSummaries.sort(Comparator.comparing(ms -> ms.getMessage().getSenderGroup().getName(), Comparator.reverseOrder()));

        MatcherAssert.assertThat(messageSummaries, contains(sortedMessageSummaries.toArray()));
    }

    @Test
    void should_retrieve_message_filtered_by_subject_on_exact_match() {
        runMatchTest("Hello there, I've been expecting you");
    }

    @Test
    void should_retrieve_message_filtered_by_subject_on_beginning_partial_match() {
        runMatchTest("Hello there");
    }

    @Test
    void should_retrieve_message_filtered_by_subject_on_end_partial_match() {
        runMatchTest("expecting you");
    }

    @Test
    void should_retrieve_message_filtered_by_subject_on_middle_partial_match() {
        runMatchTest("I've been");
    }

    @Test
    void should_retrieve_message_filtered_by_subject_with_case_sensitivity() {
        runMatchTest("Expecting");
    }

    @Test
    void should_retrieve_the_received_messages_up_to_ten_minutes_ago() {
        List<MessageSummary> messageSummaries = messageSummaryRepository.findByRecipientAndMessageSentOnBefore(
                inboxAndSentTestHelper.recipientGroup,
                Date.from(inboxAndSentTestHelper.tenMinutesAgo.plus(1, ChronoUnit.MILLIS)));

        AssertionsForClassTypes.assertThat(messageSummaries.size()).isEqualTo(4);
    }

    @Test
    void should_retrieve_the_new_message() {
        List<MessageSummary> messageSummaries = messageSummaryRepository.findByRecipientAndMessageSentOnBefore(
                inboxAndSentTestHelper.recipientGroup,
                Date.from(inboxAndSentTestHelper.now.plus(1, ChronoUnit.MILLIS)));

        AssertionsForClassTypes.assertThat(messageSummaries.size()).isEqualTo(5);
    }

    @Test
    void should_count_unread_messages_1() {
        assertThat(
                this.messageSummaryRepository.countUnread(
                        this.inboxAndSentTestHelper.lateMessage.getSentOn(),
                        this.inboxAndSentTestHelper.recipientGroup.getId(),
                        this.inboxAndSentTestHelper.recipientUser)
        ).isEqualTo(4);
    }

    @Test
    void should_count_only_old_unread_messages() {
        Instant minusOneMillis = this.inboxAndSentTestHelper.lateMessage.getSentOn().toInstant().minus(1, ChronoUnit.MILLIS);
        assertThat(
                this.messageSummaryRepository.countUnread(
                        Date.from(minusOneMillis),
                        this.inboxAndSentTestHelper.recipientGroup.getId(),
                        this.inboxAndSentTestHelper.recipientUser)
        ).isEqualTo(3);
    }

    @Test
    void should_count_user_unread_messages() {
        assertThat(
                this.messageSummaryRepository.countUnread(
                        this.inboxAndSentTestHelper.recipientGroup.getId(),
                        this.inboxAndSentTestHelper.recipientUser)
        ).isEqualTo(4);
    }

    @Test
    void should_retrieve_message_summaries_with_msg_user_statuses() {
        Instant beforeFirstMessage = Instant.now();
        initMessageSummariesForSorting(beforeFirstMessage);

        Page<MessageSummary> messageSummaries = messageSummaryRepository.findByRecipientIdAndIsValidPublicKeyTrueAndIsActiveTrue(
                inboxAndSentTestHelper.recipientGroup.getId(),
                PageRequest.of(0, 1));


        assertThat(messageSummaries.getContent().get(0).getMessageSummaryUserStatuses().size()).isOne();
        assertNotNull(messageSummaries.getContent().get(0).getMessageSummaryUserStatuses().iterator().next().getStatus());
    }

    @Test
    void should_retrieve_message_summaries_page_containing_message() {
        Group recipient = inboxAndSentTestHelper.recipientGroup;
        Instant now = Instant.now();
        int pageSize = 5;
        long totalElements = 30;
        Message message = null;

        for (int i = 0; i < totalElements; i++) {
            MessageSummary ms = messageSummaryRepository.save(MessageSummary.builder()
                    .message(messageRepository.save(mockMessage(inboxAndSentTestHelper.senderUserProfile,
                            Date.from(now.plus(i, ChronoUnit.MILLIS)))))
                    .recipient(recipient)
                    .isValidPublicKey(true)
                    .build());

            if (i == 17) {
                message = ms.getMessage();
            }
        }

        assert message != null;
        int messageSummaryPosition = messageSummaryRepository.countByRecipientIdAndMessageSentOnGreaterThan(
                inboxAndSentTestHelper.recipientGroup.getId(), message.getSentOn());

        int pageIndex = messageSummaryPosition / pageSize;

        Page<MessageSummary> messageSummaryPage = messageSummaryRepository.findByRecipientIdAndIsValidPublicKeyTrueAndIsActiveTrue(
                inboxAndSentTestHelper.recipientGroup.getId(),
                PageRequest.of(pageIndex, pageSize, Sort.by(Sort.Direction.DESC, "message.sentOn")));

        assertThat(messageSummaryPage.getContent().size()).isEqualTo(pageSize);

        final long messageId = message.getId();
        assertTrue(messageSummaryPage.getContent().stream()
                .map(MessageSummary::getMessage)
                .map(Message::getId)
                .anyMatch(id -> id.equals(messageId)));
    }

    @Test
    void should_find_by_client_reference_and_recipient_identifier() {
        Group recipient = inboxAndSentTestHelper.recipientGroup;
        inboxAndSentTestHelper.message.getMessageSummaries()
                .stream().filter(messageSummary -> messageSummary.getRecipient().equals(recipient))
                .findFirst().ifPresent(messageSummary -> {
                    MessageSummary retrieved = this.messageSummaryRepository.findByClientReferenceAndRecipientIdentifier(messageSummary.getClientReference(), recipient.getIdentifier())
                            .orElseThrow(() -> new Error("Message summary not found!"));
                    assertEquals(messageSummary, retrieved);
                });
    }

    @Test
    void should_find_by_client_reference_and_recipient_id() {
        Group recipient = inboxAndSentTestHelper.recipientGroup;
        inboxAndSentTestHelper.message.getMessageSummaries()
                .stream().filter(messageSummary -> messageSummary.getRecipient().equals(recipient))
                .findFirst().ifPresent(messageSummary -> {
                    boolean exists = this.messageSummaryRepository.existsByClientReferenceAndRecipientId(messageSummary.getClientReference(), recipient.getId());

                    assertTrue(exists);
                });
    }

    @Test
    void should_findByRecipientAndMessageSentOnGreaterThan() {
        Group sender = inboxAndSentTestHelper.senderGroup;
        Instant now = Instant.now();

        for (int i = 0; i < 5; i++) {

            if (i % 2 == 0) {
                messageRepository.save(Message.builder()
                        .sentOn(Date.from(now.minus(i, ChronoUnit.MILLIS)))
                        .senderGroup(sender)
                        .senderUserName("testuser")
                        .build());
            } else {
                messageRepository.save(Message.builder()
                        .sentOn(new Date())
                        .senderGroup(sender)
                        .senderUserName("testuser")
                        .build());
            }
        }

        assertEquals(4, messageRepository.countBySenderGroupIdAndSentOnGreaterThan(sender.getId(), Date.from(now.minus(3L, ChronoUnit.MILLIS))));


    }

    @Test
    void should_find_by_recipientId_and_publicKey_hashValue(){
        Group recipient = inboxAndSentTestHelper.recipientGroup;
        String publicHAshedValue = toHash(PUBLIC_KEY);

        List<MessageSummary> messageSummaries = messageSummaryRepository.findByRecipientId(recipient.getId());
        messageSummaries.forEach(messageSummary -> {
            messageSummary.setPublicKeyHashValue(publicHAshedValue);
            messageSummaryRepository.save(messageSummary);
        });


        assertEquals(5, messageSummaryRepository.findByRecipientIdAndAndPublicKeyHashValue(recipient.getId(), publicHAshedValue).size());

    }

    private void runMatchTest(String subject) {
        List<Message> messages = inboxAndSentTestHelper.allMessages;
        messages.get(0).setSubject("Hello there, I've been expecting you");
        messageRepository.save(messages.get(0));

        Page<MessageSummary> messageSummaries = messageSummaryRepository.findByRecipientGroupIdAndSubjectContainingIgnoreCaseOrMessageSenderGroupIdentifierContainingIgnoreCase(
                inboxAndSentTestHelper.recipientGroup.getId(),
                inboxAndSentTestHelper.recipientUser,
                subject,
                false,
                new Date(0),
                new Date(),
                PageRequest.of(0, 10));
        assertThat(messageSummaries.getContent().size()).isOne();
    }

    private void initMessageSummariesForSorting(Instant beforeFirstMessage) {
        Instant oneMinuteAfter = beforeFirstMessage.plus(1, ChronoUnit.MINUTES);

        for (int i = 0; i < 10; i++) {
            oneMinuteAfter = oneMinuteAfter.plus(1, ChronoUnit.MINUTES);

            Group senderGroup = groupRepository.save(Group.builder()
                    .identifier("sender_group_id_" + i)
                    .name("sender group " + i)
                    .type(GroupType.BUSINESS)
                    .build());

            User senderUser = userRepository.save(User.builder()
                    .ecasId("ecasId_" + i)
                    .name("name_" + i)
                    .build());

            UserProfile senderUserProfile = userProfileRepository.save(UserProfile.builder()
                    .group(senderGroup)
                    .user(senderUser)
                    .build()
            );

            Message message = messageRepository.save(Message.builder()
                    .senderUserProfile(senderUserProfile)
                    .subject(RandomStringUtils.randomAlphabetic(10))
                    .sentOn(Date.from(beforeFirstMessage))
                    .build());

            MessageSummary messageSummary = messageSummaryRepository.save(MessageSummary.builder()
                    .message(message)
                    .recipient(inboxAndSentTestHelper.recipientGroup)
                    .build());

            User anotherUser = userRepository.save(User.builder()
                    .ecasId("another_user_" + i)
                    .name("user_name_" + i)
                    .build());

            MessageSummaryUserStatus messageSummaryUserStatusForAnotherUser = messageSummaryUserStatusRepository.save(MessageSummaryUserStatus.builder()
                    .user(anotherUser)
                    .status(Status.DELIVERED)
                    .messageSummary(messageSummary)
                    .build());

            MessageSummaryUserStatus messageSummaryUserStatusForRecipientUser = messageSummaryUserStatusRepository.save(MessageSummaryUserStatus.builder()
                    .user(inboxAndSentTestHelper.recipientUser)
                    .status(Status.DELIVERED)
                    .messageSummary(messageSummary)
                    .build());


            messageSummary.getMessageSummaryUserStatuses().addAll(Stream.of(messageSummaryUserStatusForAnotherUser, messageSummaryUserStatusForRecipientUser)
                    .collect(Collectors.toList()));

            messageSummaryRepository.save(messageSummary);
        }
    }
}
