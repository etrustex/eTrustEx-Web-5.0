package eu.europa.ec.etrustex.web.persistence.entity;

import eu.europa.ec.etrustex.web.common.exchange.ExchangeMode;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.Channel;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.ExchangeRule;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.RecipientPreferences;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.SenderPreferences;
import eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.impl.*;
import eu.europa.ec.etrustex.web.persistence.entity.redirect.InboxMessageDetailsRedirect;
import eu.europa.ec.etrustex.web.persistence.entity.security.GrantedAuthority;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.Role;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.util.exchange.model.AttachmentSpec;
import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import eu.europa.ec.etrustex.web.util.exchange.model.Status;
import eu.europa.ec.etrustex.web.util.exchange.model.keys.SymmetricKey;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static eu.europa.ec.etrustex.web.persistence.entity.NewServerCertificate.NEW_SERVER_CERTIFICATE_ID;

public class EntityTestUtils {
    private static final Random RANDOM = new SecureRandom();
    public static final String PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----\n" +
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAl0RG0bO26Ha1k0Db2xMl\n" +
            "0xIGnAMKSff0XWn6DnLi7TjfBjj+38bSpgNXV3ZitOYMy8HLJB723Bj03Ii7ARmF\n" +
            "YWz/3F80fK8ujgoU10+OtuT0XpS3NE4EkOIV4zxb1+ziTb4iZHV0nMsGN9BBUuNj\n" +
            "w3QUFbKASROrFiIYwJV2l3F3+Ho6m9z9E6XCOi1PjzszXKkeP2TDVHJuUZ1041pT\n" +
            "Y9K3+itf6uWm1An1ma3nleQwzRTnHd78XxemEFpp6UI7O/8zThi7npjn1eM7oFV9\n" +
            "QMHR6BSqG6XoPZmM7OGi5ELIxnV7Q0NLu/9Y4vtcZ82mAaRcG5P6j6CYXqmUoNdw\n" +
            "PQIDAQAB\n" +
            "-----END PUBLIC KEY-----\n";
    public static final String TEST_ROOT_GROUP_IDENTIFIER = "ROOT";
    public static final Long TEST_BUSINESS_ID = RANDOM.nextLong();
    public static final String TEST_BUSINESS_IDENTIFIER = "BUSINESS_ID";
    public static final String TEST_BUSINESS_NAME = "TEST_BUSINESS_NAME";
    public static final Long TEST_ENTITY_ID = RANDOM.nextLong();
    public static final String TEST_ENTITY_IDENTIFIER = "TEST_ENTITY_ID";
    public static final String TEST_ENTITY_NAME = "TEST_ENTITY_NAME";
    public static final String TEST_USER_ID = "testuser";
    private static final String TEST_USER_NAME = "testUserName";
    private static final String SYMM_KEY_RANDOM_BITS = "R/uQvLOiv9phaCrZvx98Y6fezsavnirkZAkWpj2fMriR/SMgFa3vaoAbdPKBu997J9fIpUgRY2Vc5AVxPmAH43W0+LwavXQHcBX848PdOINMcvhLEDnwEfTIS10GugtqektzRF4dw60j13rbwi/nyKyOdLbVBcUgrYO96fZqoRQa/ZZ+vYnuEEF2ZlpsOOL6nGE7JwLR7tu9UM+gVrhXgFPxlmEvbFSDcKXlIwRFicm615OkEECNTVPwjFzPg9TwxxXbTL24Z15vi9avXeg6NOqxt1MYZqPE0EJjfchwmyEDDBHJzNdal24XdpK5ST7NQPvcJk0EXp2wZSp0+aZZtg==";
    private static final String IV = "CAYjcAHgja0HGhOpQ1L9Tg==";


    private EntityTestUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static User mockUser() {
        return mockUser(TEST_USER_ID, TEST_USER_NAME);
    }

    public static User mockUser(String ecasId, String name) {
        return User.builder()
                .id(RANDOM.nextLong())
                .ecasId(ecasId)
                .name(name)
                .euLoginEmailAddress("")
                .build();
    }

    public static List<User> mockUsers(int size) {
        List<User> userList = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            userList.add(User.builder()
                    .ecasId("ecasId_" + i)
                    .euLoginEmailAddress("")
                    .build());
        }

        return userList;
    }

    public static Group mockGroupWithRecipientAndSenderPreferences() {
        Group group = mockGroup(TEST_ENTITY_IDENTIFIER);
        group.setRecipientPreferences(RecipientPreferences.builder().id(RANDOM.nextLong())
                .publicKey("-----BEGIN PUBLIC KEY-----\n" +
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAl0RG0bO26Ha1k0Db2xMl\n" +
                "0xIGnAMKSff0XWn6DnLi7TjfBjj+38bSpgNXV3ZitOYMy8HLJB723Bj03Ii7ARmF\n" +
                "YWz/3F80fK8ujgoU10+OtuT0XpS3NE4EkOIV4zxb1+ziTb4iZHV0nMsGN9BBUuNj\n" +
                "w3QUFbKASROrFiIYwJV2l3F3+Ho6m9z9E6XCOi1PjzszXKkeP2TDVHJuUZ1041pT\n" +
                "Y9K3+itf6uWm1An1ma3nleQwzRTnHd78XxemEFpp6UI7O/8zThi7npjn1eM7oFV9\n" +
                "QMHR6BSqG6XoPZmM7OGi5ELIxnV7Q0NLu/9Y4vtcZ82mAaRcG5P6j6CYXqmUoNdw\n" +
                "PQIDAQAB\n" +
                "-----END PUBLIC KEY-----\n").build());
        group.setSenderPreferences(SenderPreferences.builder().id(RANDOM.nextLong()).build());

        return group;
    }

    public static Group mockGroup() {
        return mockGroup(TEST_ENTITY_IDENTIFIER);
    }

    public static Group mockGroup(String identifier) {
        return mockGroup(identifier, identifier + " NAME");
    }

    public static Group mockGroup(String identifier, String name) {
        return mockGroup(identifier, name, mockBusiness(), GroupType.ENTITY);
    }

    public static Group mockGroup(String identifier, String name, Group parent) {
        return mockGroup(identifier, name, parent, GroupType.ENTITY);
    }

    public static Group mockGroup(RoleName role, String id, String name) {
        boolean isSysAdmin = RoleName.SYS_ADMIN.equals(role) || RoleName.OFFICIAL_IN_CHARGE.equals(role);
        GroupType groupType = isSysAdmin ? GroupType.ROOT : GroupType.ENTITY;
        Group parent = isSysAdmin ? null : mockBusiness();
        return mockGroup(id, name, parent, groupType);
    }

    public static Group mockGroup(GroupType groupType, String identifier, String name) {
        Group parent;
        switch (groupType) {
            case BUSINESS:
                parent = mockRoot();
                break;
            case ENTITY:
                parent = mockBusiness();
                break;
            case ROOT:
                parent = null;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + groupType);
        }
        return mockGroup(identifier, name, parent, groupType);
    }


    public static Group mockGroup(String identifier, String name, Group parent, GroupType type) {
        return Group.builder()
                .id(RANDOM.nextLong())
                .identifier(identifier)
                .name(name)
                .parent(parent)
                .type(type)
                .build();
    }

    public static Group mockGroup(String id, String name, Group parent, GroupType type, String newNotification, String statusNotification, String registrationRequestNotification) {
        Group group = mockGroup(id, name, parent, type);
        group.setNewMessageNotificationEmailAddresses(StringUtils.isNotEmpty(newNotification) ? newNotification : null);
        group.setStatusNotificationEmailAddress(StringUtils.isNotEmpty(statusNotification) ? statusNotification : null);
        group.setRegistrationRequestNotificationEmailAddresses(StringUtils.isNotEmpty(registrationRequestNotification) ? registrationRequestNotification : null);
        return group;
    }

    @SuppressWarnings("SameParameterValue")
    private static List<Group> mockGroups(int numberOfGroups) {
        List<Group> groups = new ArrayList<>();

        for (int i = 0; i < numberOfGroups; i++) {
            groups.add(Group.builder()
                    .id(TEST_ENTITY_ID)
                    .identifier(TEST_ENTITY_IDENTIFIER)
                    .name(TEST_ENTITY_NAME + "_" + i)
                    .description("Testing Group " + i)
                    .build());
        }

        groups.add(Group.builder()
                .identifier("default_group_id")
                .name("DefaultGroup")
                .description("Default group from profile")
                .build());

        return groups;
    }

    public static Role mockRole(RoleName roleName) {
        return Role.builder()
                .name(roleName)
                .description(roleName + " description")
                .build();
    }

    public static Group mockBusiness() {
        return Group.builder()
                .id(RANDOM.nextLong())
                .identifier("BD_ID")
                .name("TEST_BD")
                .type(GroupType.BUSINESS)
                .parent(mockRoot())
                .build();
    }

    public static Group mockBusiness(Group root, List<Template> templates) {
        return Group.builder()
                .identifier(TEST_BUSINESS_IDENTIFIER)
                .name(TEST_BUSINESS_NAME)
                .templates(templates)
                .type(GroupType.BUSINESS)
                .parent(root)
                .build();
    }

    public static Group mockRoot() {
        return Group.builder()
                .identifier(TEST_ROOT_GROUP_IDENTIFIER)
                .name("Root group")
                .type(GroupType.ROOT)
                .build();
    }

    public static List<GrantedAuthority> mockGrantedAuthorities(User user) {
        User u = user != null ? user : mockUser();

        Role role = Role.builder()
                .name(RoleName.OPERATOR)
                .description("Test role")
                .build();

        return mockGroups(3).stream()
                .flatMap(group ->
                        Stream.of(new GrantedAuthority(u, group, role))
                )
                .collect(Collectors.toList());
    }


    public static Template mockTemplate(Long templateId) {
        Template template = new Template();
        template.setId(templateId);
        template.setContent("Content...");

        return template;
    }

    public static Message mockMessage() {
        return mockMessage(mockGroup("senderGroup"));
    }

    public static Message mockMessage(Group senderGroup) {
        User senderUser = mockUser("senderUser", "senderUserName");
        UserProfile senderUserProfile = mockUserProfile(senderUser, senderGroup);
        return mockMessage(senderUserProfile);
    }

    public static Message mockMessage(UserProfile senderUserProfile) {
        return mockMessage(senderUserProfile, new Date());
    }

    public static Message mockIncompleteMessage(UserProfile senderUserProfile, Date lastModified) {
        Message message = mockMessage(senderUserProfile, lastModified);
        message.setSentOn(null);
        message.setStatus(null);
        message.getAuditingEntity().setModifiedDate(lastModified);
        return message;
    }

    public static Message mockMessage(UserProfile senderUserProfile, Date sentOn) {
        return Message.builder()
                .id(RANDOM.nextLong())
                .symmetricKey(new SymmetricKey(SYMM_KEY_RANDOM_BITS, SymmetricKey.EncryptionMethod.RSA_OAEP_E2E))
                .iv(IV)
                .subject(RandomStringUtils.randomAlphabetic(10))
                .text(RandomStringUtils.randomAlphabetic(500))
                .senderUserProfile(senderUserProfile)
                .attachmentSpecs(Arrays.asList(
                        AttachmentSpec.builder()
                                .id(509L)
                                .iv(null)
                                .name("Saisine - Parlement européen.docx")
                                .path(null)
                                .type("application/octet-stream")
                                .byteLength(63202)
                                .checkSum("82e74245ca6663f42736250575bc17775152041602c7fb3ac68569b727361162663a83036f994e446b6217c66431a28961efd78333a3ce0c8c5e60c971ba67aa")
                                .clientRefId("6920679")
                                .build(),
                        AttachmentSpec.builder()
                                .id(510L)
                                .iv(null)
                                .name("ACT_428_ELL.docx")
                                .path(null)
                                .type("application/octet-stream")
                                .byteLength(63202)
                                .checkSum("82e74245ca6663f42736250575bc17775152041602c7fb3ac68569b727361162663a83036f994e446b6217c66431a28961efd78333a3ce0c8c5e60c971ba67aa")
                                .clientRefId("6920679")
                                .build()))
                .status(Status.DELIVERED)
                .sentOn(sentOn)
                .build();
    }


    public static Attachment mockAttachment(Message message) {
        return Attachment.builder()
                .id(RANDOM.nextLong())
                .message(message)
                .serverStorePath("/test/server/store/path/")
                .build();
    }

    public static MessageSummary mockMessageSummary(Status status) {

        AuditingEntity auditingEntity = new AuditingEntity();
        auditingEntity.setModifiedDate(new Date());

        return MessageSummary.builder()
                .auditingEntity(auditingEntity)
                .recipient(mockGroup())
                .status(status)
                .message(mockMessage())
                .isValidPublicKey(true)
                .isActive(true)
                .build();
    }

    public static MessageSummary mockMessageSummary(Status status, String clientReference) {
        return MessageSummary.builder()
                .recipient(mockGroup())
                .status(status)
                .message(mockMessage())
                .clientReference(clientReference)
                .isValidPublicKey(true)
                .build();
    }

    public static MessageSummary mockMessageSummary(Message message) {
        return MessageSummary.builder()
                .message(message)
                .recipient(mockGroup())
                .isValidPublicKey(true)
                .build();
    }

    public static List<MessageSummary> mockMessageSummaries(int size) {
        MessageSummary[] messageSummaries = new MessageSummary[size];

        for (int i = 0; i < size; i++) {
            messageSummaries[i] = mockMessageSummary(i);
        }

        return Arrays.asList(messageSummaries);
    }

    public static MessageSummary mockMessageSummary(long messageId) {
        Group senderGroup = Group.builder().id(RANDOM.nextLong()).identifier("SenderGroup").name("Sender group").build();
        Group recipient = Group.builder().id(RANDOM.nextLong()).identifier("RecipientGroup").name("Recipient group").type(GroupType.ENTITY).build();
        User senderUser = User.builder().id(RANDOM.nextLong()).ecasId("SERVICE_USER_ID").name("name").build();
        UserProfile senderUserProfile = UserProfile.builder().user(senderUser).group(senderGroup).build();
        Message m = mockSentMessage(messageId);
        m.setSenderUserProfile(senderUserProfile);
        MessageSummary ms = m.getMessageSummaries().iterator().next();
        ms.setRecipient(recipient);

        return ms;
    }

    public static MessageSummary mockMessageSummaryWithUserStatus(User currentUser) {
        Group sender = Group.builder().name("Sender group").build();
        Group recipient = Group.builder().name("Recipient group").build();
        User senderUser = User.builder().id(1L).ecasId("SERVICE_USER_ID").name("name").build();
        UserProfile senderUserProfile = UserProfile.builder().user(senderUser).group(sender).build();
        Message m = mockSentMessage(1234L);
        m.setSenderUserProfile(senderUserProfile);
        MessageSummary ms = m.getMessageSummaries().iterator().next();
        ms.setRecipient(recipient);

        ms.getMessageSummaryUserStatuses().add(MessageSummaryUserStatus.builder()
                .user(User.builder()
                        .ecasId("another_user")
                        .build())
                .status(Status.DELIVERED)
                .messageSummary(ms)
                .build());

        ms.getMessageSummaryUserStatuses().add(MessageSummaryUserStatus.builder()
                .user(currentUser)
                .status(Status.DELIVERED)
                .messageSummary(ms)
                .build());

        return ms;
    }

    public static Message mockSentMessage(long messageId) {
        Date sentOn = new Date();
        Long attachmentTotalByteLength = 1234L;
        int attachmentTotalNumber = 2;
        String subject = "Subject";
        String recipientName = "Recipient";

        Message m = Message.builder()
                .id(messageId)
                .senderGroup(mockGroup())
                .symmetricKey(new SymmetricKey(SYMM_KEY_RANDOM_BITS, SymmetricKey.EncryptionMethod.RSA_OAEP_E2E))
                .iv(IV)
                .attachmentsTotalByteLength(attachmentTotalByteLength)
                .attachmentTotalNumber(attachmentTotalNumber)
                .subject(subject)
                .sentOn(sentOn)
                .build();
        MessageSummary summary = MessageSummary.builder()
                .recipient(Group.builder().name(recipientName).build())
                .message(m)
                .build();
        m.getMessageSummaries().add(summary); // needs to be added by hand since the summary is not persisted!
        return m;
    }

    public static Message mockSentMessage(long messageId, Status status) {
        Message message = mockSentMessage(messageId);
        message.setStatus(status);
        return message;
    }

    public static MessageSummary mockMessageSummary(String recipient) {
        return MessageSummary.builder()
                .recipient(Group.builder().name(recipient).build())
                .isValidPublicKey(true)
                .build();
    }


    public static MessageSummary mockMessageSummary(Message message, Group recipient) {
        AuditingEntity auditingEntity = new AuditingEntity();
        auditingEntity.setModifiedBy("System");
        auditingEntity.setModifiedDate(Calendar.getInstance().getTime());
        return MessageSummary.builder()
                .message(message)
                .recipient(recipient)
                .clientReference(UUID.randomUUID().toString())
                .status(Status.DELIVERED)
                .auditingEntity(auditingEntity)
                .isValidPublicKey(true)
                .build();
    }


    public static MessageSummaryUserStatus mockMsgUserStatus(Status status) {
        return MessageSummaryUserStatus.builder()
                .messageSummary(mockMessageSummary(status))
                .user(mockUser())
                .status(status)
                .build();
    }

    public static Channel mockChannel() {
        Group sender = mockGroup();
        Group receiver = mockGroup();

        String channelName = String.format("%sTo%s", sender.getIdentifier(), receiver.getIdentifier());

        return Channel.builder()
                .id(1L)
                .name(channelName)
                .defaultChannel(false)
                .defaultExchangeMode(null)
                .build();
    }

    public static InboxMessageDetailsRedirect mockMessageDetailsRedirect(Long messageId) {
        return InboxMessageDetailsRedirect.builder()
                .messageId(messageId)
                .groupId(TEST_ENTITY_ID)
                .build();
    }

    public static UserProfile mockUserProfile() {
        return mockUserProfile(mockUser(), mockGroup());
    }

    public static UserProfile mockUserProfile(User user, Group group) {
        return UserProfile.builder()
                .user(user)
                .group(group)
                .alternativeEmail(user.getEcasId() + "@domain.eu")
                .build();
    }

    public static ExchangeRule mockExchangeRule(ExchangeMode exchangeMode) {

        return ExchangeRule.builder()
                .exchangeMode(exchangeMode)
                .channel(mockChannel())
                .member(mockGroup())
                .build();
    }

    public static WindowsCompatibleFilenamesGroupConfiguration mockWindowsCompatibleFilenamesGroupConfiguration(Group group, Boolean value) {
        return WindowsCompatibleFilenamesGroupConfiguration.builder()
                .id(1L)
                .group(group)
                .active(value)
                .build();
    }

    public static RetentionPolicyGroupConfiguration mockRetentionPolicyGroupConfiguration(Group group, Integer value) {
        return RetentionPolicyGroupConfiguration.builder()
                .id(1L)
                .group(group)
                .active(true)
                .integerValue(value)
                .build();
    }

    public static RetentionPolicyEntityConfiguration mockRetentionPolicyEntityConfiguration(Group group, Integer value) {
        return RetentionPolicyEntityConfiguration.builder()
                .id(1L)
                .group(group)
                .active(true)
                .integerValue(value)
                .build();
    }

    public static RetentionPolicyNotificationGroupConfiguration mockRetentionPolicyNotificationGroupConfiguration(Group group, Integer value) {
        return RetentionPolicyNotificationGroupConfiguration.builder()
                .id(1L)
                .group(group)
                .active(true)
                .integerValue(value)
                .build();
    }

    public static ForbiddenExtensionsGroupConfiguration mockForbiddenExtensionsGroupConfiguration(Group group, String value) {
        return ForbiddenExtensionsGroupConfiguration.builder()
                .id(1L)
                .group(group)
                .active(false)
                .stringValue(value)
                .build();
    }

    public static SplashScreenGroupConfiguration mockSplashScreenGroupConfiguration(Group group, String value) {
        return SplashScreenGroupConfiguration.builder()
                .id(1L)
                .group(group)
                .active(false)
                .stringValue(value)
                .build();
    }

    public static UnreadMessageReminderConfiguration mockUnreadMessageReminderConfiguration(Group group, Integer value) {
        return UnreadMessageReminderConfiguration.builder()
                .id(1L)
                .group(group)
                .active(true)
                .integerValue(value)
                .build();
    }

    public static SignatureGroupConfiguration mocksignatureGroupConfiguration(Group group, Boolean value) {
        return SignatureGroupConfiguration.builder()
                .id(1L)
                .group(group)
                .active(value)
                .build();
    }

    public static NewServerCertificate mockNewServerCertificate(boolean firstNotificationSent, boolean secondNotificationSent) {
        return mockNewServerCertificate(false, firstNotificationSent, secondNotificationSent);
    }

    public static NewServerCertificate mockNewServerCertificate(boolean ready, boolean firstNotificationSent, boolean secondNotificationSent) {
        return NewServerCertificate.builder()
                .id(NEW_SERVER_CERTIFICATE_ID)
                .ready(ready)
                .firstNotificationSent(firstNotificationSent)
                .secondNotificationSent(secondNotificationSent)
                .build();
    }

}
