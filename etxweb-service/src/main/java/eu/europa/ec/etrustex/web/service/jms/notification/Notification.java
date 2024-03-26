package eu.europa.ec.etrustex.web.service.jms.notification;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class Notification implements Serializable {
    String emailAddress;
    String subject;
    String emailBody;
    Long businessId;
    /**
     * The priority ("X-Priority" header) of the message.
     * typically between 1 (highest) and 5 (lowest)
     * See org.springframework.mail.javamail.MimeMessageHelper#setPriority(int)
     */
    private int priority;
}
