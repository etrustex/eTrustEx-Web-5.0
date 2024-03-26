package eu.europa.ec.etrustex.web.persistence.repository;

import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import eu.europa.ec.etrustex.web.persistence.entity.*;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.UserProfileRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.UserRepository;
import eu.europa.ec.etrustex.web.util.exchange.model.Status;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.*;

public class InboxAndSentTestHelper {

    protected Group senderGroup;
    protected Group recipientGroup;
    protected User senderUser;
    protected UserProfile senderUserProfile;
    protected User recipientUser;
    protected Message message;
    protected Message draftMessage;
    protected Message earlyMessage;
    protected Message lateMessage;
    protected Message fiveWeeksMessage;
    protected Message readedMessage;
    protected List<Message> allMessages = new ArrayList<>();
    protected List<Group> recipients = new ArrayList<>();

    protected Instant yesterday;
    protected Instant now;
    protected Instant tenMinutesAgo;
    protected Date fiveWeeks;

    private final MessageSummaryRepository messageSummaryRepository;
    private final MessageRepository messageRepository;


    public InboxAndSentTestHelper(GroupRepository groupRepository, UserRepository userRepository, MessageRepository messageRepository, MessageSummaryRepository messageSummaryRepository, UserProfileRepository userProfileRepository) {
        this.messageSummaryRepository = messageSummaryRepository;
        this.messageRepository = messageRepository;

        this.now = Instant.now();
        this.yesterday = now.minus(1, ChronoUnit.DAYS);
        this.tenMinutesAgo = now.minus(10, ChronoUnit.MINUTES);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.WEEK_OF_YEAR, -5);
        this.fiveWeeks = calendar.getTime();

        this.senderGroup = groupRepository.save(mockGroup("SENDER", "SENDER", null, GroupType.BUSINESS));
        this.recipientGroup = groupRepository.save(mockGroup("RECIPIENT", "RECIPIENT", null, GroupType.BUSINESS));
        this.senderUser = userRepository.save(mockUser("SENDER_USER", "SENDER_NAME"));
        this.senderUserProfile = userProfileRepository.save(mockUserProfile(senderUser, senderGroup));
        this.recipientUser = userRepository.save(mockUser("RECIPIENT_USER", "RECIPIENT_NAME"));

        this.message = messageRepository.save(mockMessage(senderUserProfile, Date.from(tenMinutesAgo)));
        this.allMessages.add(message);
        setDelivered(message);

        this.draftMessage = messageRepository.save(mockMessage(senderUserProfile, Date.from(tenMinutesAgo)));
        this.allMessages.add(draftMessage);
        setDraft(draftMessage);

        this.earlyMessage = messageRepository.save(mockMessage(senderUserProfile, Date.from(yesterday)));
        this.allMessages.add(earlyMessage);

        this.lateMessage = messageRepository.save(mockMessage(senderUserProfile, Date.from(now)));
        this.allMessages.add(lateMessage);

        this.fiveWeeksMessage = messageRepository.save(mockMessage(senderUserProfile, fiveWeeks));
        this.allMessages.add(lateMessage);

        this.readedMessage = messageRepository.save(mockMessage(senderUserProfile, Date.from(tenMinutesAgo)));
        this.allMessages.add(readedMessage);
        setRead(readedMessage, senderGroup, senderUser);

        this.recipients.add(recipientGroup);
        this.recipients.add(groupRepository.save(mockGroup("RECIPIENT_2", "RECIPIENT_2", null, GroupType.BUSINESS)));

        this.allMessages.forEach(m -> recipients.forEach(r -> messageSummaryRepository.save(mockMessageSummary(m, r))));
    }

    public void setRead() {
        setRead(earlyMessage, recipientGroup, recipientUser);
        setRead(message, recipientGroup, senderUser);
    }

    public void setRead(Message message, Group recipientGroup, User recipientUser) {
        message.getMessageSummaries()
                .stream()
                .filter(messageSummary -> messageSummary.getRecipient().equals(recipientGroup))
                .forEach(messageSummary -> {
                    messageSummary.getMessageSummaryUserStatuses().add(MessageSummaryUserStatus.builder()
                            .messageSummary(messageSummary)
                            .user(recipientUser)
                            .status(Status.READ)
                            .build());
                    messageSummaryRepository.save(messageSummary);
                });

        message.setMessageUserStatuses(new HashSet<>(Collections.singletonList(MessageUserStatus.builder()
                .message(message)
                .senderGroupId(this.senderGroup.getId())
                .user(recipientUser)
                .status(Status.READ)
                .build())));
        messageRepository.save(message);
    }

    public void saveIncompleteMessages() {
        messageRepository.saveAll(
                Arrays.asList(
                        mockIncompleteMessage(senderUserProfile, Date.from(yesterday)),
                        mockIncompleteMessage(senderUserProfile, fiveWeeks),
                        mockIncompleteMessage(senderUserProfile, Date.from(tenMinutesAgo))
                )
        );
    }

    private void setDelivered(Message message) {
        message.setStatus(Status.DELIVERED);
        messageRepository.save(message);
    }

    private void setDraft(Message message) {
        message.setStatus(Status.DRAFT);
        messageRepository.save(message);
    }
}
