package eu.europa.ec.etrustex.web.service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Needed by spring boot to configure JavaMailSender
 */
@Configuration
@PropertySource({"classpath:mail.properties"})
@ConfigurationProperties("mail")
@Getter
@Setter
public class MailProperties {
    private String notificationFrom;
}
