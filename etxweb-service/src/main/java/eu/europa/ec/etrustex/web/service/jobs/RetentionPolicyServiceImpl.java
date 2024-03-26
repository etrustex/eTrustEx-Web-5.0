package eu.europa.ec.etrustex.web.service.jobs;

import eu.europa.ec.etrustex.web.common.EtrustexWebProperties;
import eu.europa.ec.etrustex.web.common.template.TemplateType;
import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummary;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.repository.MessageSummaryRepository;
import eu.europa.ec.etrustex.web.service.MailService;
import eu.europa.ec.etrustex.web.service.MessageService;
import eu.europa.ec.etrustex.web.service.MessageSummaryService;
import eu.europa.ec.etrustex.web.service.security.UserProfileService;
import eu.europa.ec.etrustex.web.service.template.TemplateService;
import eu.europa.ec.etrustex.web.service.validation.model.NotificationEmailSpec;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

import static eu.europa.ec.etrustex.web.common.DbStringListsSeparators.DB_STRING_LIST_SEPARATOR;
import static eu.europa.ec.etrustex.web.common.template.TemplateContextConstants.*;


@Service
@Slf4j
@AllArgsConstructor
public class RetentionPolicyServiceImpl implements RetentionPolicyService {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private static final String SUBJECT_FOR_WARNING_GENERIC = "Expiration warning - eTrustEx Web";
    private static final String SUBJECT_FOR_WARNING_EGREFFE = "Expiration warning - %1$s";
    private static final String EGREFFE = "EGREFFE_BD";
    private final MessageService messageService;
    private final MessageSummaryService messageSummaryService;
    private final MessageSummaryRepository messageSummaryRepository;
    private final MailService mailService;
    private final TemplateService templateService;
    private final UserProfileService userProfileService;

    private final EtrustexWebProperties etrustexWebProperties;


    /**
     * @param group                 (If a retention policy is not configured for the group we look it up through the parents)
     * @param retentionPolicyInDays The retention policy in days
     */
    public void deleteMessagesForGroup(Group group, Integer retentionPolicyInDays) {
        List<Message> messages = messageService.findBySenderGroupAndSentOnBefore(group, expiryDate(retentionPolicyInDays), true);
        if (!messages.isEmpty()) {
            log.info("Deleting " + messages.size() + " expired Message(s) for the group: " + group.getIdentifier());
            messages.forEach(messageService::delete);
        }
    }

    @Override
    public void deactivateMessagesForGroup(Group group, Integer retentionPolicyDays) {
        List<MessageSummary> messageSummaries = messageSummaryService.findByRecipientGroupAndSentOnBefore(group, expiryDate(retentionPolicyDays));
        if (!messageSummaries.isEmpty()) {
            log.info("Deactivating " + messageSummaries.size() + " expired Message(s) for the group: " + group.getIdentifier());
            messageSummaries.forEach(messageSummary -> {
                messageSummary.setActive(false);
                messageSummary.setDisabledByRetentionPolicy(true);
            });
            messageSummaryService.saveAll(messageSummaries);
        }
    }

    @Override
    public void handleRetentionPolicyNotificationsForGroup(Group group, Integer entityRetentionPolicyDays, Integer businessRetentionPolicyDays, Integer notificationInDays) {
        Set<Long> sentMessages = new HashSet<>();

        retrieveNotifiableMessageSummaries(group, entityRetentionPolicyDays, notificationInDays).stream()
                .filter(messageSummary -> messageSummary.isActive() && !messageSummary.isNotifiedOfRetentionPolicy())
                .forEach(messageSummary -> {
                    processMessageSummary(messageSummary.getMessage(), messageSummary, notificationInDays);
                    sentMessages.add(messageSummary.getMessage().getId());
                });


        if (businessRetentionPolicyDays != null && !Objects.equals(businessRetentionPolicyDays, entityRetentionPolicyDays)) {
            retrieveNotifiableMessages(group, businessRetentionPolicyDays, notificationInDays).stream()
                    .filter(message -> !sentMessages.contains(message.getId()))
                    .forEach(message -> {
                        log.info("-Handling message with ID: " + message.getId());
                        message.getMessageSummaries().stream()
                                .filter(messageSummary -> messageSummary.isActive() && !messageSummary.isNotifiedOfRetentionPolicy())
                                .forEach(messageSummary -> processMessageSummary(message, messageSummary, notificationInDays));

                        sentMessages.add(message.getId());
                    });
        }
    }

    private void processMessageSummary(Message message, MessageSummary messageSummary, Integer notificationInDays) {
        log.info("----Sent 'Business Retention' message to " + Pair.of(message.getId(), messageSummary.getRecipient().getId()));
        sendMessage(message, messageSummary, notificationInDays);

        messageSummary.setNotifiedOfRetentionPolicy(true);
        messageSummaryRepository.save(messageSummary);  // need to use the repository to avoid calling the NotificationAspect
    }

    private void sendMessage(Message message, MessageSummary messageSummary, Integer notificationInDays) {
        boolean egreffeBusinessDomain = EGREFFE.equals(messageSummary.getMessage().getSenderGroup().getParent().getIdentifier());
        String subject = egreffeBusinessDomain ? String.format(SUBJECT_FOR_WARNING_EGREFFE, messageSummary.getMessage().getSubject()) : SUBJECT_FOR_WARNING_GENERIC;

        final Map<String, Object> templateVariables = templateService.getTemplateVariables(messageSummary);
        templateVariables.put(TPL_VARIABLES_DELETION_IN_DAYS.getValue(), notificationInDays);
        templateVariables.put(TPL_VARIABLES_MESSAGE_EXPIRATION_DATE.getValue(), dateFormat.format(expiryDate(-notificationInDays)));
        templateVariables.put(TPL_VARIABLES_SUBJECT.getValue(), message.getSubject());

        templateVariables.put(TPL_VARIABLES_APPLICATION_URL.getValue(), etrustexWebProperties.getApplicationUrl());

        final Long templateId = templateService.getByGroupType(message.getSenderGroup(), TemplateType.RETENTION_POLICY_NOTIFICATION).getId();
        final String emailBody = templateService.process(templateId, templateVariables);

        userProfileService.findByRetentionWarningNotificationsIsTrueAndGroup(messageSummary.getRecipient()).forEach(userProfile -> {
            String mailBox = (userProfile.isAlternativeEmailUsed() && !StringUtils.isEmpty(userProfile.getAlternativeEmail())) ? userProfile.getAlternativeEmail() : userProfile.getUser().getEuLoginEmailAddress();
            if (StringUtils.isNotEmpty(mailBox)) {
                NotificationEmailSpec notificationEmailSpec = NotificationEmailSpec.builder()
                        .to(mailBox)
                        .subject(subject)
                        .body(emailBody)
                        .businessId(message.getSenderGroup().getParent().getBusinessId())
                        .build();
                mailService.send(notificationEmailSpec);
            }
        });

        if (StringUtils.isNotEmpty(messageSummary.getRecipient().getRetentionWarningNotificationEmailAddresses())) {
            Arrays.asList(messageSummary.getRecipient().getRetentionWarningNotificationEmailAddresses().split(DB_STRING_LIST_SEPARATOR.toString()))
                    .forEach(retentionWarningNotificationEmail -> {
                        NotificationEmailSpec notificationEmailSpec = NotificationEmailSpec.builder()
                                .to(retentionWarningNotificationEmail)
                                .subject(subject)
                                .body(emailBody)
                                .businessId(message.getSenderGroup().getParent().getBusinessId())
                                .build();
                        mailService.send(notificationEmailSpec);
                    });
        }
    }

    /**
     * @param group                 Group
     * @param retentionPolicyInDays number of days for the retention policy
     * @param notificationInDays    number of days to notify before the message expires
     * @return Messages for which we need to send a notification today
     */
    private List<Message> retrieveNotifiableMessages(Group group, Integer retentionPolicyInDays, Integer notificationInDays) {
        Date startDate = notificationDate(retentionPolicyInDays, notificationInDays, true);
        Date endDate = notificationDate(retentionPolicyInDays, notificationInDays, false);
        return messageService.findBySenderGroupAndSentOnBetween(group, startDate, endDate);
    }

    /**
     * @param group                 Group
     * @param retentionPolicyInDays number of days for the retention policy
     * @param notificationInDays    number of days to notify before the message expires
     * @return Messages for which we need to send a notification today
     */
    private List<MessageSummary> retrieveNotifiableMessageSummaries(Group group, Integer retentionPolicyInDays, Integer notificationInDays) {
        Date startDate = notificationDate(retentionPolicyInDays, notificationInDays, true);
        Date endDate = notificationDate(retentionPolicyInDays, notificationInDays, false);
        return messageSummaryService.findByRecipientGroupAndMessageSentOnBetween(group, startDate, endDate);
    }

    private Date notificationDate(Integer retentionPolicyInDays, Integer notificationInDays, boolean startOfDay) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -retentionPolicyInDays);
        calendar.add(Calendar.DAY_OF_YEAR, notificationInDays);
        if (startOfDay) {
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
        } else {
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
        }
        return calendar.getTime();
    }

    /**
     * @param days expiry date in days
     * @return returns a Date Object after subtracting the retention policy's weeks from the current time
     */
    private Date expiryDate(Integer days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -days);
        return calendar.getTime();
    }

}
