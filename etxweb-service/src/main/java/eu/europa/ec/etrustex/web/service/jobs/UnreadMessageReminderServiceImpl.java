package eu.europa.ec.etrustex.web.service.jobs;

import eu.europa.ec.etrustex.web.common.template.TemplateType;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummary;
import eu.europa.ec.etrustex.web.persistence.entity.UserProfile;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.repository.MessageSummaryRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.UserProfileRepository;
import eu.europa.ec.etrustex.web.service.MailService;
import eu.europa.ec.etrustex.web.service.MessageSummaryUserStatusServiceImpl;
import eu.europa.ec.etrustex.web.service.security.GrantedAuthorityService;
import eu.europa.ec.etrustex.web.service.template.TemplateService;
import eu.europa.ec.etrustex.web.service.validation.model.NotificationEmailSpec;
import eu.europa.ec.etrustex.web.util.exchange.model.Status;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class UnreadMessageReminderServiceImpl implements UnreadMessageReminderService {

    private static final String SUBJECT = "New message reminder - eTrustEx Web";
    private final UserProfileRepository userProfileRepository;
    private final GrantedAuthorityService grantedAuthorityService;
    private final MessageSummaryRepository messageSummaryRepository;
    private final MailService mailService;
    private final TemplateService templateService;

    private final MessageSummaryUserStatusServiceImpl messageSummaryUserStatusService;

    @Override
    @Transactional
    public void handleUnreadMessageReminderForGroup(Group recipient, Integer reminderInDays) {
        LocalDate expiryDate = LocalDate.now().minusDays(reminderInDays);
        Date from = Date.from(expiryDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date to = Date.from(expiryDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        messageSummaryRepository.findByRecipientAndMessageSentOnBetweenAndIsActiveTrue(recipient, from, to)
                .forEach(messageSummary -> {
                    final List<UserProfile> userProfiles = userProfileRepository.findByNewMessageNotificationsIsTrueAndGroup(messageSummary.getRecipient())
                            .stream()
                            .filter(grantedAuthorityService::isUserActiveWithinGroup)
                            .filter(userProfile -> !messageSummaryUserStatusService.findByMessageSummaryAndUser(messageSummary, userProfile.getUser()).isPresent())
                            .collect(Collectors.toList());

                    final Long templateId = templateService.getByGroupType(messageSummary.getMessage().getSenderGroup(), TemplateType.UNREAD_MESSAGE_REMINDER_NOTIFICATION).getId();

                    userProfiles
                            .stream()
                            .map(userProfile -> userProfile.isAlternativeEmailUsed() ? userProfile.getAlternativeEmail() : userProfile.getUser().getEuLoginEmailAddress())
                            .filter(Strings::isNotEmpty)
                            .forEach(emailAddress -> mailService.send(NotificationEmailSpec.builder()
                                    .businessId(messageSummary.getRecipient().getBusinessId())
                                    .to(emailAddress)
                                    .body(toBody(templateId, messageSummary))
                                    .subject(SUBJECT)
                                    .build()));

                    if (StringUtils.isNotBlank(recipient.getNewMessageNotificationEmailAddresses()) && !messageSummary.getMessage().getStatus().equals(Status.READ)) {
                        mailService.send(NotificationEmailSpec.builder()
                                .businessId(messageSummary.getRecipient().getBusinessId())
                                .to(recipient.getNewMessageNotificationEmailAddresses())
                                .body(toBody(templateId, messageSummary))
                                .subject(SUBJECT)
                                .build());
                    }
                });
    }

    String toBody(Long templateId, MessageSummary messageSummary) {
        final Map<String, Object> templateVariables = templateService.getTemplateVariables(messageSummary);

        return templateService.process(templateId, templateVariables);
    }
}
