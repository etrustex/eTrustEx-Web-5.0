package eu.europa.ec.etrustex.web.persistence.repository;

import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import eu.europa.ec.etrustex.web.persistence.entity.Attachment;
import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.entity.UserProfile;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.UserProfileRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Random;
import java.util.stream.StreamSupport;

import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AttachmentRepositoryTest {
    private static final String CLIENT_REFERENCE = "client-reference";
    private static final Random RANDOM = new SecureRandom();

    @Autowired
    private AttachmentRepository attachmentRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserProfileRepository userProfileRepositoryRepository;

    private Message message1;
    private Message message2;

    private Attachment attachment1;
    private UserProfile userProfile;

    @BeforeEach
    public void init() {
        Group group = groupRepository.save(mockGroup(GroupType.BUSINESS, "aGroup_id", "aGroup"));
        userProfile = userProfileRepositoryRepository.findByUserEcasIdAndGroup("aUser", group).orElse(null);

        if (userProfile == null) {
            User user = userRepository.save(mockUser("aUser","name"));
            userProfile = userProfileRepositoryRepository.save(mockUserProfile(user, group));
        }

        this.message1 = messageRepository.save(mockMessage(userProfile, new Date()));
        this.message2 = messageRepository.save(mockMessage(userProfile, new Date()));

        this.attachment1 = attachmentRepository.save(mockAttachment(message1));
        attachmentRepository.save(mockAttachment(message1));
        attachmentRepository.save(mockAttachment(message2));

        Attachment attachmentWithClientReference = attachmentRepository.save(mockAttachment(message2));
        attachmentWithClientReference.setClientReference(CLIENT_REFERENCE);
    }

    @Test
    void should_find_by_id_and_message_id() {
        assertThat(attachmentRepository.findById(attachment1.getId())).isPresent();
    }

    @Test
    void should_find_by_message() {
        assertThat(attachmentRepository.findByMessage(message1).size()).isEqualTo(2);
    }

    @Test
    void should_find_by_message_id() {
        assertThat(attachmentRepository.findByMessageId(message1.getId()).size()).isEqualTo(2);
    }

    @Test
    void should_find_by_client_reference_and_message_id() {
        assertThat(attachmentRepository.findByClientReferenceAndMessageId(CLIENT_REFERENCE, message2.getId()).size()).isOne();
    }

    @Test
    void should_delete_by_message() {
        attachmentRepository.deleteByMessage(message1);
        assertThat(attachmentRepository.findByMessage(message1).size()).isZero();
    }

    @Test
    void should_find_by_message_status_is_null() {
        assertThat(attachmentRepository.findByMessageStatusIsNull().size()).isZero();
    }


    @Test
    void should_find_by_client_reference_and_sender_group() {
        Message messageWithoutServerStorePath = messageRepository.save(mockMessage(userProfile, new Date()));
        attachmentRepository.save(Attachment.builder()
                .id(RANDOM.nextLong())
                .clientReference(CLIENT_REFERENCE)
                .message(messageWithoutServerStorePath)
                .build());


        assertThat(StreamSupport.stream(attachmentRepository.findAll().spliterator(), false).count()).isEqualTo(5);
        assertThat(attachmentRepository.findByClientReferenceAndMessageSenderGroupAndServerStorePathIsNotNull(CLIENT_REFERENCE, messageWithoutServerStorePath.getSenderGroup()).size())
                .isEqualTo(1);
    }
}
