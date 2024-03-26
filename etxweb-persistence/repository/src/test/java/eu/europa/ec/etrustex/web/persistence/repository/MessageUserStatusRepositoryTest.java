package eu.europa.ec.etrustex.web.persistence.repository;


import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.entity.MessageUserStatus;
import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.UserProfileRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.UserRepository;
import eu.europa.ec.etrustex.web.util.exchange.model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MessageUserStatusRepositoryTest {

    @Autowired
    private MessageUserStatusRepository messageUserStatusRepository;

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
    void should_find_by_message_and_user_and_sender_group_id() {
        Message message = messageRepository.findAll().get(0);
        MessageUserStatus messageUserStatus = messageUserStatusRepository.save(MessageUserStatus.builder()
                .user(inboxAndSentTestHelper.recipientUser)
                .status(Status.READ)
                .message(message)
                .senderGroupId(message.getSenderGroup().getId())
                .build());

        assertTrue(messageUserStatusRepository.existsByMessageAndUserAndSenderGroupId(messageUserStatus.getMessage(), messageUserStatus.getUser(), messageUserStatus.getSenderGroupId()));
    }
}
