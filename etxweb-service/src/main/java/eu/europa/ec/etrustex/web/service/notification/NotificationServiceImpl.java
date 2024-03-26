package eu.europa.ec.etrustex.web.service.notification;

import eu.europa.ec.etrustex.web.common.EtrustexWebProperties;
import eu.europa.ec.etrustex.web.common.template.TemplateType;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummary;
import eu.europa.ec.etrustex.web.persistence.entity.UserProfile;
import eu.europa.ec.etrustex.web.persistence.entity.UserRegistrationRequest;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.service.jms.JmsSender;
import eu.europa.ec.etrustex.web.service.jms.notification.Notification;
import eu.europa.ec.etrustex.web.service.rest.RestClient;
import eu.europa.ec.etrustex.web.service.security.GrantedAuthorityService;
import eu.europa.ec.etrustex.web.service.security.UserProfileService;
import eu.europa.ec.etrustex.web.service.template.TemplateService;
import eu.europa.ec.etrustex.web.service.validation.model.HttpNotificationSpec;
import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static eu.europa.ec.etrustex.web.common.DbStringListsSeparators.DB_STRING_LIST_SEPARATOR;
import static eu.europa.ec.etrustex.web.common.template.TemplateContextConstants.*;
import static eu.europa.ec.etrustex.web.util.exchange.LinkUtils.getUri;


@Service
@RequiredArgsConstructor
@Getter
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private final JmsSender jmsSender;
    private final RestClient restClient;
    private final TemplateService templateService;
    private final UserProfileService userProfileService;
    private final GrantedAuthorityService grantedAuthorityService;
    private final EtrustexWebProperties etrustexWebProperties;
    private static final String SUBJECT_FOR_NEW_MESSAGE = "New message available in Etrustex Web";
    private static final String SUBJECT_FOR_NEW_STATUS = "New %1$s status in Etrustex Web for message %2$s";

    @Override
    public void notifyOfNewMessageSummary(MessageSummary messageSummary) {
        log.info("Send new message email notification");

        final List<String> newMessageNotificationEmailAddresses = StringUtils.isEmpty(messageSummary.getRecipient().getNewMessageNotificationEmailAddresses())
                ? Collections.emptyList()
                : Arrays.asList(messageSummary.getRecipient().getNewMessageNotificationEmailAddresses().split(DB_STRING_LIST_SEPARATOR.toString()));

        final List<UserProfile> userProfiles = userProfileService.findByNewMessageNotificationsIsTrueAndGroup(messageSummary.getRecipient())
                .stream()
                .filter(grantedAuthorityService::isUserActiveWithinGroup)
                .collect(Collectors.toList());

        if (userProfiles.isEmpty() && newMessageNotificationEmailAddresses.isEmpty()) {
            return;
        }

        final Map<String, Object> templateVariables = templateService.getTemplateVariables(messageSummary);
        final String subject = templateService.getSubject(messageSummary, templateVariables, SUBJECT_FOR_NEW_MESSAGE);
        final Long templateId = templateService.getByGroupType(messageSummary.getRecipient(), TemplateType.NEW_MESSAGE_NOTIFICATION).getId();
        final String emailBody = templateService.process(templateId, templateVariables);

        emailAddressesToNotify(userProfiles, newMessageNotificationEmailAddresses)
                .forEach(emailAddress -> jmsSender.send(Notification.builder()
                        .businessId(messageSummary.getRecipient().getBusinessId())
                        .emailAddress(emailAddress)
                        .emailBody(emailBody)
                        .subject(subject)
                        .priority(Boolean.TRUE.equals(messageSummary.getMessage().getHighImportance()) ? 1 : 0)
                        .build()));
    }

    @Override
    public void notifyOfNewMessageSummaryStatus(MessageSummary messageSummary) {
        notifyOfNewMessageSummaryStatus(messageSummary, null);
        httpNotifyOfNewMessageSummaryStatus(messageSummary, null);
    }

    @Override
    public void notifyOfNewMessageSummaryStatus(MessageSummary messageSummary, User recipientUser) {
        log.info("Send new status email notification");

        final String statusNotificationEmailAddress = messageSummary.getMessage().getSenderGroup().getStatusNotificationEmailAddress();

        final List<UserProfile> userProfiles = getSenderUserProfilesConfiguredToReceiveStatusNotifications(messageSummary)
                .stream()
                .filter(grantedAuthorityService::isUserActiveWithinGroup)
                .collect(Collectors.toList());

        if (userProfiles.isEmpty() && StringUtils.isEmpty(statusNotificationEmailAddress)) {
            return;
        }

        final Map<String, Object> templateVariables = templateService.getTemplateVariables(messageSummary);
        final String subject = templateService.getSubject(messageSummary, templateVariables, SUBJECT_FOR_NEW_STATUS);
        final String emailBody = emailBody(messageSummary, recipientUser);

        emailAddressesToNotify(userProfiles, Collections.singletonList(statusNotificationEmailAddress))
                .forEach(emailAddress -> jmsSender.send(Notification.builder()
                        .businessId(messageSummary.getRecipient().getBusinessId())
                        .emailAddress(emailAddress)
                        .emailBody(emailBody)
                        .subject(subject)
                        .priority(Boolean.TRUE.equals(messageSummary.getMessage().getHighImportance()) ? 1 : 0)
                        .build()));
    }

    @Override
    public void httpNotifyOfNewMessageSummaryStatus(MessageSummary messageSummary, User recipientUser) {
        log.info("Send new status http notification");

        final List<UserProfile> userProfiles = getSenderUserProfilesConfiguredToReceiveStatusNotifications(messageSummary)
                .stream()
                .filter(grantedAuthorityService::isUserActiveWithinGroup)
                .collect(Collectors.toList());

        if (userProfiles.isEmpty()) {
            return;
        }

        endpointsToNotify(userProfiles).forEach(endpoint -> {
            String url = getUri(endpoint + "/message-api/{messageId}/new-status",
                    Pair.of("messageId", "" + messageSummary.getMessage().getId()));

            HttpNotificationSpec notificationSpec = HttpNotificationSpec.builder()
                    .messageId(messageSummary.getMessage().getId())
                    .entityIdentifier(messageSummary.getRecipient().getIdentifier())
                    .businessIdentifier(messageSummary.getRecipient().getBusinessIdentifier())
                    .ecasId(recipientUser != null ? recipientUser.getEcasId() : null)
                    .status(messageSummary.getStatus())
                    .modifiedDate(messageSummary.getAuditingEntity().getModifiedDate())
                    .build();

            log.info("Sending new status http notification to {}", url);
            try {
                restClient.put(url, notificationSpec);
            } catch (Exception e) {
                log.error("Error sending new status http notification to {}", url, e);
            }
        });
    }

    @Override
    public void notifyOfUserConfigured(User user, Group group) {
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put(TPL_VARIABLES_USER_NAME.getValue(), user.getName());
        templateVariables.put(TPL_VARIABLES_GROUP_NAME.getValue(), group.getName());
        templateVariables.put(TPL_VARIABLES_APPLICATION_URL.getValue(), etrustexWebProperties.getApplicationUrl());
        templateVariables.put(TPL_VARIABLES_FUNCTIONAL_MAILBOX.getValue(), etrustexWebProperties.getFunctionalMailbox());

        if (!group.getParent().getTemplates().isEmpty()) {
            final String subject = "Welcome to eTrustEx Web!";
            final Long templateId = templateService.getByGroupType(group, TemplateType.USER_CONFIGURED_NOTIFICATION).getId();
            final String emailBody = templateService.process(templateId, templateVariables);

            UserProfile userProfile = userProfileService.findUserProfileByUserAndGroup(user, group);
            if (group.getType().equals(GroupType.ENTITY) && grantedAuthorityService.isUserActiveWithinGroup(userProfile)) {
                String emailAddress = userProfile.isAlternativeEmailUsed() ? userProfile.getAlternativeEmail() : userProfile.getUser().getEuLoginEmailAddress();
                if (Strings.isNotEmpty(emailAddress)) {
                    jmsSender.send(Notification.builder()
                            .businessId(group.getBusinessId())
                            .emailAddress(emailAddress)
                            .emailBody(emailBody)
                            .subject(subject)
                            .build());
                }
            }
        }
    }

    @Override
    public void notifyOfRetentionPolicyChanged(User user, Group group, int previousValue, int newValue) {
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put(TPL_VARIABLES_USER_NAME.getValue(), user.getEcasId());
        templateVariables.put(TPL_VARIABLES_GROUP_NAME.getValue(), String.format("%s - %s", group.getIdentifier(), group.getName()));
        templateVariables.put(TPL_VARIABLES_ENVIRONMENT.getValue(), etrustexWebProperties.getEnvironment());
        templateVariables.put(TPL_VARIABLES_PREVIOUS_VALUE.getValue(), previousValue);
        templateVariables.put(TPL_VARIABLES_NEW_VALUE.getValue(), newValue);

        final String subject = "Retention policy update - eTrustEx Web";
        final Long templateId = templateService.getByGroupType(group, TemplateType.UPDATE_RETENTION_POLICY_NOTIFICATION).getId();
        final String emailBody = templateService.process(templateId, templateVariables);

        jmsSender.send(Notification.builder()
                .businessId(group.getBusinessId())
                .emailAddress(etrustexWebProperties.getFunctionalMailbox())
                .emailBody(emailBody)
                .subject(subject)
                .build());
    }

    @Override
    public void notifyOfBusinessPendingDeletion(List<UserProfile> officials, Group business) {
        Map<String, Object> templateVariables = new HashMap<>();
        String adminUrl = etrustexWebProperties.getApplicationUrl() + "#/admin";
        templateVariables.put(TPL_VARIABLES_APPLICATION_URL.getValue(), adminUrl);
        templateVariables.put(TPL_VARIABLES_GROUP_NAME.getValue(), String.format("%s - %s", business.getIdentifier(), business.getName()));

        final String subject = "Business deletion request - eTrustEx Web";
        final Long templateId = templateService.getDefaultTemplateByType(TemplateType.BUSINESS_DELETION_NOTIFICATION).getId();
        final String emailBody = templateService.process(templateId, templateVariables);

        officials.stream()
                .filter(grantedAuthorityService::isUserActiveWithinGroup)
                .map(o -> o.isAlternativeEmailUsed() ? o.getAlternativeEmail() : o.getUser().getEuLoginEmailAddress())
                .filter(StringUtils::isNotEmpty)
                .distinct()
                .forEach(email -> jmsSender.send(Notification.builder()
                        .businessId(business.getId())
                        .emailAddress(email)
                        .emailBody(emailBody)
                        .subject(subject)
                        .build()));
    }

    @Override
    public void notifyOfUserRejected(UserProfile userProfile) {
        log.trace("Send a rejected request email notification");
        Group group = userProfile.getGroup();
        Map<String, Object> templateVariables = buildTemplateVariables(userProfile.getUser(), group);

        final String subject = String.format("Your request to be added to the entity %s in eTrustEx Web was rejected.", group.getIdentifier());
        final Long templateId = templateService.getByGroupType(group, TemplateType.USER_REJECTED_NOTIFICATION).getId();
        final String emailBody = templateService.process(templateId, templateVariables);

        String emailAddress = userProfile.isAlternativeEmailUsed() ? userProfile.getAlternativeEmail() : userProfile.getUser().getEuLoginEmailAddress();
        if (Strings.isNotEmpty(emailAddress)) {
            jmsSender.send(Notification.builder()
                    .businessId(group.getBusinessId())
                    .emailAddress(emailAddress)
                    .emailBody(emailBody)
                    .subject(subject)
                    .build());
        }
    }

    @Override
    public void notifyAdminsWithPendingUserRegistrationRequest(UserRegistrationRequest userRegistrationRequest) {
        log.trace("Send a pending UserRegistrationRequest email notification");

        Group group = userRegistrationRequest.getGroup();

        Map<String, Object> templateVariables = buildTemplateVariables(userRegistrationRequest.getUser(), group);
        String adminUrl = etrustexWebProperties.getApplicationUrl() + "#/admin";
        templateVariables.put(TPL_VARIABLES_APPLICATION_URL.getValue(), adminUrl);

        final String subject = "New registration request in eTrustEx Web";
        final Long templateId = templateService.getByGroupType(group, TemplateType.PENDING_USER_REGISTRATION_REQUEST_NOTIFICATION).getId();
        final String emailBody = templateService.process(templateId, templateVariables);

        if (StringUtils.isNotEmpty(group.getRegistrationRequestNotificationEmailAddresses())) {
            Arrays.asList(group.getRegistrationRequestNotificationEmailAddresses().split(DB_STRING_LIST_SEPARATOR.toString()))
                    .forEach(emailAddress ->
                            jmsSender.send(Notification.builder()
                                    .businessId(group.getBusinessId())
                                    .emailAddress(emailAddress)
                                    .emailBody(emailBody)
                                    .subject(subject)
                                    .build())
                    );
        }
    }

    private Map<String, Object> buildTemplateVariables(User user, Group group) {
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put(TPL_VARIABLES_USER_NAME.getValue(), user.getName());
        templateVariables.put(TPL_VARIABLES_GROUP_NAME.getValue(), group.getName());

        return templateVariables;
    }

    private List<UserProfile> getSenderUserProfilesConfiguredToReceiveStatusNotifications(MessageSummary messageSummary) {
        return userProfileService.findByMessageStatusForSenderNotificationsIsTrueAndGroup(messageSummary.getMessage().getSenderGroup())
                .stream()
                .filter(grantedAuthorityService::isUserActiveWithinGroup)
                .collect(Collectors.toList());
    }

    private Stream<String> emailAddressesToNotify(List<UserProfile> userProfiles, List<String> statusNotificationEmailAddress) {
        return Stream.concat(
                userProfiles.stream().map(userProfile -> userProfile.isAlternativeEmailUsed() ? userProfile.getAlternativeEmail() : userProfile.getUser().getEuLoginEmailAddress()),
                statusNotificationEmailAddress.stream()
        ).filter(Strings::isNotEmpty);
    }

    private Stream<String> endpointsToNotify(List<UserProfile> userProfiles) {
        return userProfiles.stream()
                .filter(userProfile -> !StringUtils.isEmpty(userProfile.getGroup().getSystemEndpoint()))
                .map(userProfile -> userProfile.getGroup().getSystemEndpoint());
    }

    private String emailBody(MessageSummary messageSummary, User recipientUser) {
        final Map<String, Object> templateVariables = templateService.getTemplateVariables(messageSummary, recipientUser);

        final Long templateId = templateService.getByGroupType(messageSummary.getMessage().getSenderGroup(), TemplateType.MESSAGE_STATUS_NOTIFICATION).getId();

        return templateService.process(templateId, templateVariables);
    }
}
