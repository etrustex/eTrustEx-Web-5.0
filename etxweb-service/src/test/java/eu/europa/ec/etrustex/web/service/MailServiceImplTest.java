package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.service.config.MailProperties;
import eu.europa.ec.etrustex.web.service.groupconfiguration.GroupConfigurationService;
import eu.europa.ec.etrustex.web.service.validation.model.NotificationEmailSpec;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class MailServiceImplTest {

    private MailService mailService;

    private JavaMailSender javaMailSender;
    private MimeMessage mimeMessage;
    private MailProperties mailProperties;

    private static final int SUBJECT_MAX_LENGTH = 255;

    @BeforeEach
    public void setUp() {
        mimeMessage = new MimeMessage((Session) null);
        javaMailSender = Mockito.mock(JavaMailSender.class);
        mailProperties = Mockito.mock(MailProperties.class);
        mailService = new MailServiceImpl(mailProperties, javaMailSender, Mockito.mock(GroupConfigurationService.class));
    }

    @Test
    void should_send_email() throws MessagingException {
        String subject = "Some subject";
        String emailBody = "Some contents.";
        String emailAddress = "test@test.com";

        String notificationForm = "notification_form";

        Mockito.when(mailProperties.getNotificationFrom()).thenReturn(notificationForm);

        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "utf-8");
        helper.setText(emailBody, true);
        helper.setSubject(StringUtils.abbreviate(subject, SUBJECT_MAX_LENGTH));
        helper.setFrom(mailProperties.getNotificationFrom());

        helper.setTo(emailAddress);

        Mockito.when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        mailService.send(NotificationEmailSpec.builder().to(emailAddress).bcc(null).subject(subject).body(emailBody).businessId(1L).build());

        assertEquals(emailAddress, mimeMessage.getRecipients(Message.RecipientType.TO)[0].toString());
    }
}
