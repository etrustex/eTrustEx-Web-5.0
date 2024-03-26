package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.service.validation.model.NotificationEmailSpec;

public interface MailService {
    void send(NotificationEmailSpec notificationEmailSpec);
}
