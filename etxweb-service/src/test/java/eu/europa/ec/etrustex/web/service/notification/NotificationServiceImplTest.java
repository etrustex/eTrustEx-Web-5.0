package eu.europa.ec.etrustex.web.service.notification;

import eu.europa.ec.etrustex.web.common.EtrustexWebProperties;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummary;
import eu.europa.ec.etrustex.web.persistence.entity.UserProfile;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.service.jms.JmsSender;
import eu.europa.ec.etrustex.web.service.rest.RestClient;
import eu.europa.ec.etrustex.web.service.security.GrantedAuthorityService;
import eu.europa.ec.etrustex.web.service.security.UserProfileService;
import eu.europa.ec.etrustex.web.service.template.TemplateService;
import eu.europa.ec.etrustex.web.util.exchange.model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;

import static eu.europa.ec.etrustex.web.common.DbStringListsSeparators.DB_STRING_LIST_SEPARATOR;
import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"java:S112", "java:S100"}) /* Suppress Define and throw a dedicated exception instead of using a generic one & method names false positive. */
class NotificationServiceImplTest {

    public static final String FIRST_NEW_EMAIL = "first@new.email";
    public static final String FIRST_STATUS_EMAIL = "first@status.email";
    private static final String CANNOT_FIND_USER_PROFILE = "Cannot find the user profile!";
    private static final String CANNOT_RETRIEVE_PROFILE_TO_GET_EMAIL_ADDRESS = "Cannot retrieve profile to get the emailAddress";
    @Mock
    private JmsSender jmsSender;
    @Mock
    private RestClient restClient;
    @Mock
    private TemplateService templateService;
    @Mock
    private UserProfileService userProfileService;
    @Mock
    private GrantedAuthorityService grantedAuthorityService;
    @Mock
    private EtrustexWebProperties etrustexWebProperties;
    private NotificationService notificationService;

    @BeforeEach
    public void setUp() {
        this.notificationService = new NotificationServiceImpl(jmsSender, restClient, templateService, userProfileService, grantedAuthorityService, etrustexWebProperties);
    }


    @Test
    void should_notify_of_new_msg_summary() {
        User user = buildUser();
        UserProfile userProfile = user.getUserProfiles().stream().findFirst().orElseThrow(() -> new Error(CANNOT_FIND_USER_PROFILE));
        userProfile.setNewMessageNotifications(true);
        MessageSummary messageSummary = mockMessageSummary(Status.DELIVERED);
        Group recipient = messageSummary.getRecipient();

        recipient.setNewMessageNotificationEmailAddresses(FIRST_NEW_EMAIL + ",second@new.email");

        given(userProfileService.findByNewMessageNotificationsIsTrueAndGroup(recipient)).willReturn(Collections.singletonList(userProfile));
        given(grantedAuthorityService.isUserActiveWithinGroup(any())).willReturn(true);
        given(templateService.getByGroupType(any(Group.class), any())).willReturn(mockTemplate(100L));

        notificationService.notifyOfNewMessageSummary(messageSummary);

        String emailAddress = user.getUserProfiles().stream().findFirst()
                .orElseThrow(() -> new Error(CANNOT_RETRIEVE_PROFILE_TO_GET_EMAIL_ADDRESS))
                .getAlternativeEmail();
        verify(jmsSender, times(1)).send(argThat(notification -> notification.getEmailAddress().equals(emailAddress)));

        Arrays.stream(messageSummary.getRecipient().getNewMessageNotificationEmailAddresses().split(DB_STRING_LIST_SEPARATOR.toString()))
                .forEach(functionalEmail -> verify(jmsSender, times(1)).send(argThat(notification -> notification.getEmailAddress().equals(functionalEmail))));
    }

    @Test
    void should_not_notify_of_new_msg_summary_if_user_disabled() {
        User user = buildUser();
        UserProfile userProfile = user.getUserProfiles().stream().findFirst().orElseThrow(() -> new Error(CANNOT_FIND_USER_PROFILE));
        userProfile.setNewMessageNotifications(true);
        MessageSummary messageSummary = mockMessageSummary(Status.DELIVERED);
        Group recipient = messageSummary.getRecipient();

        recipient.setNewMessageNotificationEmailAddresses(FIRST_NEW_EMAIL + ",second@new.email");

        given(userProfileService.findByNewMessageNotificationsIsTrueAndGroup(recipient)).willReturn(Collections.singletonList(userProfile));
        given(grantedAuthorityService.isUserActiveWithinGroup(any())).willReturn(false);
        given(templateService.getByGroupType(any(Group.class), any())).willReturn(mockTemplate(100L));

        notificationService.notifyOfNewMessageSummary(messageSummary);

        String emailAddress = user.getUserProfiles().stream().findFirst()
                .orElseThrow(() -> new Error(CANNOT_RETRIEVE_PROFILE_TO_GET_EMAIL_ADDRESS))
                .getAlternativeEmail();
        verify(jmsSender, times(0)).send(argThat(notification -> notification.getEmailAddress().equals(emailAddress)));
    }

    @Test
    void should_not_notify_of_new_msg_summary_when_list_is_empty() {
        MessageSummary messageSummary = mockMessageSummary(Status.DELIVERED);
        Group recipient = messageSummary.getRecipient();

        given(userProfileService.findByNewMessageNotificationsIsTrueAndGroup(recipient)).willReturn(Collections.emptyList());

        notificationService.notifyOfNewMessageSummary(messageSummary);

        verify(jmsSender, times(0)).send(any());
    }

    @Test
    void should_notify_of_new_msg_summary_status() {
        User user = buildUser();
        UserProfile userProfile = user.getUserProfiles().stream().findFirst().orElseThrow(() -> new Error(CANNOT_FIND_USER_PROFILE));
        userProfile.setMessageStatusForSenderNotifications(true);

        MessageSummary messageSummary = mockMessageSummary(Status.DELIVERED);
        Group sender = messageSummary.getMessage().getSenderGroup();
        sender.setStatusNotificationEmailAddress(FIRST_STATUS_EMAIL);


        given(userProfileService.findByMessageStatusForSenderNotificationsIsTrueAndGroup(sender)).willReturn(Collections.singletonList(userProfile));
        given(grantedAuthorityService.isUserActiveWithinGroup(any())).willReturn(true);
        given(templateService.getByGroupType(any(Group.class), any())).willReturn(mockTemplate(100L));
        notificationService.notifyOfNewMessageSummaryStatus(messageSummary);

        String emailAddress = user.getUserProfiles().stream().findFirst()
                .orElseThrow(() -> new Error(CANNOT_RETRIEVE_PROFILE_TO_GET_EMAIL_ADDRESS))
                .getAlternativeEmail();

        verify(jmsSender, times(1)).send(argThat(notification -> notification.getEmailAddress().equals(emailAddress)));
        verify(jmsSender, times(1)).send(argThat(notification -> notification.getEmailAddress().equals(sender.getStatusNotificationEmailAddress())));
    }

    @Test
    void should_notify_http_of_new_msg_summary_status() throws URISyntaxException {
        User user = buildUser();
        UserProfile userProfile = user.getUserProfiles().stream().findFirst().orElseThrow(() -> new Error(CANNOT_FIND_USER_PROFILE));
        userProfile.setMessageStatusForSenderNotifications(true);

        MessageSummary messageSummary = mockMessageSummary(Status.DELIVERED);
        Group sender = messageSummary.getMessage().getSenderGroup();
        sender.setSystemEndpoint("http://test-endpoint.com/");

        userProfile.setGroup(sender);

        given(userProfileService.findByMessageStatusForSenderNotificationsIsTrueAndGroup(sender)).willReturn(Collections.singletonList(userProfile));
        given(grantedAuthorityService.isUserActiveWithinGroup(any())).willReturn(true);

        notificationService.httpNotifyOfNewMessageSummaryStatus(messageSummary, user);

        verify(restClient, times(1)).put(anyString(), any());
    }

    @Test
    void should_not_notify_of_new_msg_summary_status_if_user_disabled() {
        User user = buildUser();
        UserProfile userProfile = user.getUserProfiles().stream().findFirst().orElseThrow(() -> new Error(CANNOT_FIND_USER_PROFILE));
        userProfile.setMessageStatusForSenderNotifications(true);

        MessageSummary messageSummary = mockMessageSummary(Status.DELIVERED);
        Group sender = messageSummary.getMessage().getSenderGroup();

        sender.setStatusNotificationEmailAddress(FIRST_STATUS_EMAIL);


        given(userProfileService.findByMessageStatusForSenderNotificationsIsTrueAndGroup(sender)).willReturn(Collections.singletonList(userProfile));
        given(grantedAuthorityService.isUserActiveWithinGroup(any())).willReturn(false);
        given(templateService.getByGroupType(any(Group.class), any())).willReturn(mockTemplate(100L));
        notificationService.notifyOfNewMessageSummaryStatus(messageSummary);

        String emailAddress = user.getUserProfiles().stream().findFirst()
                .orElseThrow(() -> new Error(CANNOT_RETRIEVE_PROFILE_TO_GET_EMAIL_ADDRESS))
                .getAlternativeEmail();
        verify(jmsSender, times(0)).send(argThat(notification -> notification.getEmailAddress().equals(emailAddress)));
    }

    @Test
    void should_not_notify_of_new_msg_summary_status() {
        MessageSummary messageSummary = mockMessageSummary(Status.DELIVERED);
        Group sender = messageSummary.getMessage().getSenderGroup();

        given(userProfileService.findByMessageStatusForSenderNotificationsIsTrueAndGroup(sender)).willReturn(Collections.emptyList());
        notificationService.notifyOfNewMessageSummaryStatus(messageSummary);

        verify(jmsSender, times(0)).send(any());
    }

    @Test
    void should_notify_when_a_user_is_added_to_an_entity() {
        User user = buildUser();
        Group group = mockGroup();
        UserProfile userProfile = user.getUserProfiles().stream().findFirst().orElseThrow(() -> new Error(CANNOT_FIND_USER_PROFILE));
        group.getParent().setTemplates(Collections.singletonList(mockTemplate(100L)));

        when(userProfileService.findUserProfileByUserAndGroup(any(), any())).thenReturn(userProfile);
        when(grantedAuthorityService.isUserActiveWithinGroup(any())).thenReturn(true);
        given(templateService.getByGroupType(any(Group.class), any())).willReturn(mockTemplate(100L));
        notificationService.notifyOfUserConfigured(user, group);

        String emailAddress = user.getUserProfiles().stream().findFirst()
                .orElseThrow(() -> new Error(CANNOT_RETRIEVE_PROFILE_TO_GET_EMAIL_ADDRESS))
                .getAlternativeEmail();
        verify(jmsSender, times(1)).send(argThat(notification -> notification.getEmailAddress().equals(emailAddress)));
    }

    @Test
    void should_notify_of_privacy_policy_changes() {
        User user = buildUser();
        Group group = mockBusiness();

        when(etrustexWebProperties.getFunctionalMailbox()).thenReturn(FIRST_STATUS_EMAIL);
        given(templateService.getByGroupType(any(Group.class), any())).willReturn(mockTemplate(100L));

        notificationService.notifyOfRetentionPolicyChanged(user, group, 10, 30);

        verify(jmsSender, times(1)).send(argThat(notification -> notification.getEmailAddress().equals(etrustexWebProperties.getFunctionalMailbox())));
    }

    @Test
    void should_notify_when_a_user_is_rejected_by_an_admin() {
        User user = buildUser();
        Group group = mockGroup();
        group.setTemplates(Collections.singletonList(mockTemplate(100L)));

        UserProfile userProfile = mockUserProfile(user, group);
        given(templateService.getByGroupType(any(Group.class), any())).willReturn(mockTemplate(100L));

        notificationService.notifyOfUserRejected(userProfile);

        verify(jmsSender, times(1)).send(any());
    }

    private User buildUser() {
        return User.builder().ecasId("testuser").euLoginEmailAddress("test@eulogin.mail").userProfiles(
                Collections.singleton(UserProfile.builder().group(mockGroup()).alternativeEmail("test@user.foo").alternativeEmailUsed(true).build())).build();
    }
}
