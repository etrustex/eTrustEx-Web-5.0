package eu.europa.ec.etrustex.web.persistence.repository;


import eu.europa.ec.etrustex.web.persistence.entity.MessageSummary;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummaryUserStatus;
import eu.europa.ec.etrustex.web.util.exchange.model.Status;
import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.UserProfileRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MessageSummaryUserStatusRepositoryTest {

    @Autowired
    private MessageSummaryUserStatusRepository messageSummaryUserStatusRepository;

    @Autowired
    private MessageSummaryRepository messageSummaryRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserProfileRepository userProfileRepository;

    private InboxAndSentTestHelper inboxAndSentTestHelper;

    @BeforeEach
    public void init() {
        this.inboxAndSentTestHelper = new InboxAndSentTestHelper(groupRepository, userRepository, messageRepository, messageSummaryRepository, userProfileRepository);
        inboxAndSentTestHelper.setRead();
    }

    @Test
    void should_find_by_message_summary_and_user() {
        MessageSummary messageSummary = messageSummaryRepository.findAll().get(0);
        MessageSummaryUserStatus messageSummaryUserStatusForRecipientUser = messageSummaryUserStatusRepository.save(MessageSummaryUserStatus.builder()
                .user(inboxAndSentTestHelper.recipientUser)
                .status(Status.DELIVERED)
                .messageSummary(messageSummary)
                .build());

        assertTrue(messageSummaryUserStatusRepository.findByMessageSummaryAndUser(messageSummaryUserStatusForRecipientUser.getMessageSummary(), messageSummaryUserStatusForRecipientUser.getUser()).isPresent());
    }


    @Test
    void should_delete_by_user() {
        MessageSummary messageSummary = messageSummaryRepository.findAll().get(0);
        MessageSummaryUserStatus messageSummaryUserStatusForRecipientUser = messageSummaryUserStatusRepository.save(MessageSummaryUserStatus.builder()
                .user(inboxAndSentTestHelper.recipientUser)
                .status(Status.DELIVERED)
                .messageSummary(messageSummary)
                .build());

        messageSummaryUserStatusRepository.deleteByUser(messageSummaryUserStatusForRecipientUser.getUser());
        assertThat(messageSummaryUserStatusRepository.findByMessageSummaryAndUser(messageSummaryUserStatusForRecipientUser.getMessageSummary(), messageSummaryUserStatusForRecipientUser.getUser())).isEmpty();
    }

    @Test
    void should_find_by_sender_group_id_and_message_id_and_subject_containing() {
        MessageSummary messageSummary = messageSummaryRepository.findAll().get(0);
        MessageSummaryUserStatus messageSummaryUserStatus = messageSummaryUserStatusRepository.save(MessageSummaryUserStatus.builder()
                .user(inboxAndSentTestHelper.recipientUser)
                .status(Status.READ)
                .messageSummary(messageSummary)
                .build());

        assertFalse(messageSummaryUserStatusRepository.findReadMessageSummaryUserStatusByRecipientIdAndMessageIdOrSubjectContainingIgnoreCase(messageSummaryUserStatus.getMessageSummary().getRecipient().getId(), "", PageRequest.of(0, 10, Sort.by("auditingEntity.modifiedDate"))).isEmpty());
    }

}
