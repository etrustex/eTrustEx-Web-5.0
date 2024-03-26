package eu.europa.ec.etrustex.web.persistence.entity;

import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.Channel;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.EncryptedPassword;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.ExchangeRule;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.SenderPreferences;
import eu.europa.ec.etrustex.web.persistence.entity.redirect.AttachmentDownloadRedirect;
import eu.europa.ec.etrustex.web.persistence.entity.redirect.AttachmentsDownloadRedirect;
import eu.europa.ec.etrustex.web.persistence.entity.redirect.InboxMessageDetailsRedirect;
import eu.europa.ec.etrustex.web.persistence.entity.redirect.SentMessageDetailsRedirect;
import eu.europa.ec.etrustex.web.persistence.entity.security.GrantedAuthority;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.Role;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.util.exchange.model.Status;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ValidationTest {
    private static Validator validator;

    private static final Random RANDOM = new SecureRandom();

    private static final String NAME = "name";
    private static final String SERVER_STORE_PATH = "serverStorePath";
    private static final String CLIENT_REFERENCE = "clientReference";
    private static final String USER = "user";
    private static final String GROUP = "group";
    private static final String ROLE = "role";
    private static final String ATTACHMENT_ID = "attachmentId";
    private static final String GROUP_ID = "groupId";
    private static final String MESSAGE_ID = "messageId";
    private static final String ID = "ecasId";
    private static final String PASSWORD_B64 = "passwordB64";
    private static final String IV_B64 = "ivB64";
    private static final String EXCHANGE_MODE = "exchangeMode";
    private static final String MESSAGE = "message";
    private static final String STATUS = "status";
    private static final String MESSAGE_SUMMARY = "messageSummary";

    @BeforeAll
    public static void createValidator() {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.getValidator();
        }
    }

    @Test
    void channel_validation_ok() {
        Channel c = Channel.builder().name(getRandomString(255)).build();
        checkValidationResult(c);
    }

    @Test
    void channel_validation_should_fail_due_to_null_name() {
        Channel c = Channel.builder().build();
        checkValidationResult(c, NAME);
    }

    @Test
    void channel_validation_should_fail_due_to_too_short_name() {
        Channel c = Channel.builder().name("").build();
        checkValidationResult(c, NAME);
    }

    @Test
    void channel_validation_should_fail_due_to_too_long_name() {
        Channel c = Channel.builder().name(getRandomString(256)).build();
        checkValidationResult(c, NAME);
    }

    @Test
    void attachment_validation_ok() {
        Attachment a = Attachment.builder()
                .clientReference(getRandomString(1))
                .serverStorePath(getRandomString(255))
                .message(Message.builder().build())
                .build();
        checkValidationResult(a);
    }

    @Test
    void attachment_validation_should_fail_due_to_null_and_too_short_values() {
        Attachment attachment = Attachment.builder().serverStorePath("").build();
        checkValidationResult(attachment, MESSAGE, SERVER_STORE_PATH);
    }

    @Test
    void attachment_validation_should_fail_due_to_too_long_values() {
        Attachment a = Attachment.builder()
                .clientReference(getRandomString(256))
                .serverStorePath(getRandomString(256))
                .message(Message.builder().build())
                .build();
        checkValidationResult(a, CLIENT_REFERENCE, SERVER_STORE_PATH);
    }

    @Test
    void group_validation_ok() {
        Group group = Group.builder()
                .name(getRandomString(1))
                .build();
        checkValidationResult(group);
    }

    @Test
    void group_validation_should_fail_due_to_null_name() {
        Group group = Group.builder().build();
        checkValidationResult(group, NAME);
    }

    @Test
    void group_validation_should_fail_due_to_too_short_name() {
        Group group = Group.builder().name("").build();
        checkValidationResult(group, NAME);
    }

    @Test
    void group_validation_should_fail_due_to_too_long_name() {
        Group group = Group.builder()
                .name(getRandomString(101))
                .build();
        checkValidationResult(group, NAME);
    }

    @Test
    void msgUserStatus_validation_ok() {
        MessageSummaryUserStatus messageSummaryUserStatus = MessageSummaryUserStatus.builder()
                .user(User.builder().build())
                .status(Status.DELIVERED)
                .messageSummary(MessageSummary.builder().build())
                .build();
        checkValidationResult(messageSummaryUserStatus);
    }

    @Test
    void msgUSerStatus_should_fail_due_to_null_values() {
        MessageSummaryUserStatus messageSummaryUserStatus = MessageSummaryUserStatus.builder().build();
        checkValidationResult(messageSummaryUserStatus, STATUS, USER, MESSAGE_SUMMARY);
    }


    @Test
    void attachmentDownloadRedirect_validation_ok() {
        AttachmentDownloadRedirect attachmentDownloadRedirect = AttachmentDownloadRedirect.builder()
                .attachmentId(RANDOM.nextLong())
                .groupId(RANDOM.nextLong())
                .messageId(567L)
                .build();
        checkValidationResult(attachmentDownloadRedirect);
    }

    @Test
    void attachmentDownloadRedirect_validation_should_fail_due_to_null_values() {
        AttachmentDownloadRedirect attachmentDownloadRedirect = AttachmentDownloadRedirect.builder().build();
        checkValidationResult(attachmentDownloadRedirect, ATTACHMENT_ID, GROUP_ID, MESSAGE_ID);
    }

    @Test
    void attachmentsDownloadRedirect_validation_ok() {
        AttachmentsDownloadRedirect attachmentsDownloadRedirect = AttachmentsDownloadRedirect.builder()
                .groupId(RANDOM.nextLong())
                .messageId(567L)
                .build();
        checkValidationResult(attachmentsDownloadRedirect);
    }

    @Test
    void attachmentsDownloadRedirect_validation_should_fail_due_to_null_values() {
        AttachmentsDownloadRedirect attachmentsDownloadRedirect = AttachmentsDownloadRedirect.builder().build();
        checkValidationResult(attachmentsDownloadRedirect, GROUP_ID, MESSAGE_ID);
    }

    @Test
    void inboxMessageDetailsRedirect_validation_ok() {
        InboxMessageDetailsRedirect inboxMessageDetailsRedirect = InboxMessageDetailsRedirect.builder()
                .groupId(RANDOM.nextLong())
                .messageId(RANDOM.nextLong())
                .build();
        checkValidationResult(inboxMessageDetailsRedirect);
    }

    @Test
    void inboxMessageDetailsRedirect_validation_should_fail_due_to_null_values() {
        InboxMessageDetailsRedirect inboxMessageDetailsRedirect = InboxMessageDetailsRedirect.builder().build();
        checkValidationResult(inboxMessageDetailsRedirect, GROUP_ID, MESSAGE_ID);
    }

    @Test
    void sentMessageDetailsRedirect_validation_ok() {
        SentMessageDetailsRedirect sentMessageDetailsRedirect = SentMessageDetailsRedirect.builder()
                .groupId(RANDOM.nextLong())
                .messageId(RANDOM.nextLong())
                .build();
        checkValidationResult(sentMessageDetailsRedirect);
    }

    @Test
    void sentMessageDetailsRedirect_validation_should_fail_due_to_null_values() {
        SentMessageDetailsRedirect sentMessageDetailsRedirect = SentMessageDetailsRedirect.builder().build();
        checkValidationResult(sentMessageDetailsRedirect, GROUP_ID, MESSAGE_ID);
    }

    @Test
    void role_validation_ok() {
        Role role = Role.builder()
                .name(RoleName.OPERATOR)
                .build();
        checkValidationResult(role);
    }

    @Test
    void role_validation_should_fail_for_null() {
        Role role = Role.builder()
                .build();
        checkValidationResult(role, NAME);
    }



    @Test
    void senderPreferences_validation_ok() {
        SenderPreferences senderPreferences = SenderPreferences.builder().build();
        checkValidationResult(senderPreferences);
    }

    @Test
    void senderPreferences_validation_should_fail_for_null() {
        SenderPreferences senderPreferences = SenderPreferences.builder().build();
        senderPreferences.setEncryptionImplementation(null);
        checkValidationResult(senderPreferences, "encryptionImplementation");
    }

    @Test
    void user_validation_ok() {
        User user = User.builder()
                .id(11L)
                .ecasId(ID)
                .name("name_name")
                .build();
        checkValidationResult(user);
    }

    @Test
    void user_should_fail_for_non_valid_email_address() {
        User user = User.builder()
                .id(1L)
                .ecasId("anId")
                .name("name")
                .build();
        checkValidationResult(user);
    }

    @Test
    void user_should_fail_for_null_values() {
        User user = User.builder().build();
        checkValidationResult(user, ID, NAME);
    }

    @Test
    void user_should_fail_for_too_short_values() {
        User user = User.builder()
                .ecasId("")
                .name("name")
                .build();
        checkValidationResult(user, ID);
    }

    @Test
    void user_should_fail_for_too_long_values() {
        User user = User.builder()
                .ecasId(getRandomString(51))
                .name("name")
                .build();
        checkValidationResult(user, ID);
    }

    @Test
    void user_profile_should_validation_ok() {
        UserProfile userProfile = UserProfile.builder().build();
        checkValidationResult(userProfile, USER, GROUP);
    }

    @Test
    void template_validation_ok() {
        Template template = Template.builder().build();
        checkValidationResult(template);
    }

    @Test
    void granted_authority_validation_ok() {
        GrantedAuthority ga = GrantedAuthority.builder().build();
        checkValidationResult(ga, USER, GROUP, ROLE);
    }

    @Test
    void encrypted_password_validation_ok() {
        EncryptedPassword encryptedPassword = EncryptedPassword.builder().passwordB64(" ").ivB64(" ").build();
        checkValidationResult(encryptedPassword);
    }

    @Test
    void encrypted_password_should_not_allow_null_password() {
        EncryptedPassword encryptedPassword = EncryptedPassword.builder().passwordB64(null).ivB64(" ").build();
        checkValidationResult(encryptedPassword, PASSWORD_B64);
    }

    @Test
    void encrypted_password_should_fail_for_too_long_values() {
        EncryptedPassword encryptedPassword = EncryptedPassword.builder()
                .passwordB64(null)
                .ivB64(getRandomString(256))
                .build();
        checkValidationResult(encryptedPassword, PASSWORD_B64, IV_B64);
    }


    @Test
    void exchange_rule_should_not_allow_null_exchange_mode() {
        ExchangeRule er = ExchangeRule.builder().exchangeMode(null).build();
        checkValidationResult(er, EXCHANGE_MODE);
    }

    private <T> void checkValidationResult(T entity, String... propertyPaths) {
        Set<ConstraintViolation<T>> violations = validator.validate(entity);
        assertThat(violations).hasSize(propertyPaths.length);

        // too short a list to make it worth to create an HashSet (unless we create entities with thousands of fields)
        Collection<String> propertyPathsSet = Arrays.asList(propertyPaths);

        for (ConstraintViolation<T> constraintViolation : violations) {
            assertTrue(propertyPathsSet.contains(constraintViolation.getPropertyPath().toString()));
        }
    }

    private String getRandomString(int length) {
        return RandomStringUtils.random(length, true, false);
    }
}
