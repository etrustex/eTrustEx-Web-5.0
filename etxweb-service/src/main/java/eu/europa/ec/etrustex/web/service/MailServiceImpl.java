package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.impl.NotificationsEmailFromGroupConfiguration;
import eu.europa.ec.etrustex.web.service.config.MailProperties;
import eu.europa.ec.etrustex.web.service.groupconfiguration.GroupConfigurationService;
import eu.europa.ec.etrustex.web.service.validation.model.NotificationEmailSpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailServiceImpl implements MailService {

    private static final int SUBJECT_MAX_LENGTH = 255;

    private final MailProperties mailProperties;
    private final JavaMailSender javaMailSender;
    private final GroupConfigurationService groupConfigurationService;

    @Override
    public void send(NotificationEmailSpec notificationEmailSpec) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            final MimeMessageHelper helper = new MimeMessageHelper(message, false, "utf-8");
            helper.setText(notificationEmailSpec.getBody(), true);
            helper.setSubject(StringUtils.abbreviate(notificationEmailSpec.getSubject(), SUBJECT_MAX_LENGTH));

            String configFrom = groupConfigurationService.findNonEmptyValueByGroupIdAndType(notificationEmailSpec.getBusinessId(), NotificationsEmailFromGroupConfiguration.class);
            helper.setFrom(StringUtils.isNotEmpty(configFrom) ? configFrom : mailProperties.getNotificationFrom());

            if (notificationEmailSpec.getPriority() != 0) {
                helper.setPriority(notificationEmailSpec.getPriority());
            }

            if (StringUtils.isNotEmpty(notificationEmailSpec.getTo())) {
                helper.setTo(notificationEmailSpec.getTo());
            }

            if (CollectionUtils.isNotEmpty(notificationEmailSpec.getBcc())) {
                helper.setBcc(notificationEmailSpec.getBcc().toArray(new String[0]));
            }

            if (CollectionUtils.isNotEmpty(notificationEmailSpec.getCc())) {
                helper.setCc(notificationEmailSpec.getCc().toArray(new String[0]));
            }
            javaMailSender.send(helper.getMimeMessage());
        } catch (MessagingException me) {
            throw new EtxWebException("Error sending mail message. ", me);
        }
    }
}
