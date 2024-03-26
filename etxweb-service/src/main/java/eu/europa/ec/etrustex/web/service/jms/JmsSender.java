package eu.europa.ec.etrustex.web.service.jms;

import eu.europa.ec.etrustex.web.service.jms.notification.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

import static eu.europa.ec.etrustex.web.service.jms.config.JmsConfig.COMMAND_TOPIC;
import static eu.europa.ec.etrustex.web.service.jms.config.JmsConfig.RETRIEVE_NOTIFICATION_QUEUE;

@Component
@RequiredArgsConstructor
public class JmsSender {

    private final JmsTemplate jmsTemplate;

    public void send(Notification notification) {
        jmsTemplate.convertAndSend(RETRIEVE_NOTIFICATION_QUEUE, notification);
    }
    public void sendCommand(Map<String, String> commandProperties) {
        jmsTemplate.convertAndSend(COMMAND_TOPIC, commandProperties);
    }
}
