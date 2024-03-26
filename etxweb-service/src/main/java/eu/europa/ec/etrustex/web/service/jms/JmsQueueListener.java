package eu.europa.ec.etrustex.web.service.jms;

import eu.europa.ec.etrustex.web.service.MailService;
import eu.europa.ec.etrustex.web.service.jms.notification.Notification;
import eu.europa.ec.etrustex.web.service.validation.model.NotificationEmailSpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import static eu.europa.ec.etrustex.web.service.jms.config.JmsConfig.RETRIEVE_NOTIFICATION_QUEUE;

@Component
@RequiredArgsConstructor
@Slf4j
public class JmsQueueListener {

    private final MailService mailService;

    @JmsListener(destination = RETRIEVE_NOTIFICATION_QUEUE, containerFactory = "nonTransactedListenerFactory")
    public void onMessage(Notification notification) {
        log.info("About sending email to: {}, subject: {}, body: {}",notification.getEmailAddress(), notification.getSubject(), notification.getEmailBody());
        mailService.send(NotificationEmailSpec.builder().to(notification.getEmailAddress()).bcc(null)
                .subject(notification.getSubject())
                .businessId(notification.getBusinessId())
                .body(notification.getEmailBody())
                .priority(notification.getPriority())
                .build());
    }

}

