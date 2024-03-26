package eu.europa.ec.etrustex.web.integration;

import eu.europa.ec.etrustex.web.common.EtrustexWebProperties;
import eu.europa.ec.etrustex.web.persistence.entity.NewServerCertificate;
import eu.europa.ec.etrustex.web.persistence.repository.NewServerCertificateRepository;
import eu.europa.ec.etrustex.web.service.EncryptionService;
import eu.europa.ec.etrustex.web.service.MailService;
import eu.europa.ec.etrustex.web.service.jobs.certificate.NewServerCertificateJob;
import eu.europa.ec.etrustex.web.service.validation.model.NotificationEmailSpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.mockNewServerCertificate;
import static eu.europa.ec.etrustex.web.persistence.entity.NewServerCertificate.NEW_SERVER_CERTIFICATE_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NewServerCertificateJobTest {
    @Spy
    private NewServerCertificateRepository newServerCertificateRepository;
    @Spy
    private MailService mailService;

    @Mock
    private EtrustexWebProperties etrustexWebProperties;
    @Mock
    private JobLauncher jobLauncher;
    @Mock
    private Job encryptWithNewCertificateJob;
    @Mock
    private EncryptionService encryptionService;

    @Captor
    private ArgumentCaptor<NotificationEmailSpec> notificationEmailSpecArgumentCaptor;

    @Captor
    private ArgumentCaptor<NewServerCertificate> newServerCertificateArgumentCaptor;

    private NewServerCertificateJob newServerCertificateJob;

    private static final String FUNCTIONAL_MAILBOX = "DIGIT-EU-SEND@ec.europa.eu";
    private static final String ENVIRONMENT = "test";


    @BeforeEach
    public void setUp() {
        newServerCertificateJob = new NewServerCertificateJob(newServerCertificateRepository, mailService, jobLauncher, encryptWithNewCertificateJob, etrustexWebProperties, encryptionService);
    }


    @Test
    void should_not_send_notification_if_more_than_60_days() {
        LocalDateTime inMoreThan2Months = LocalDateTime.now().plusDays(61);
        NewServerCertificate newServerCertificate = mockNewServerCertificate(false, false);

        given(newServerCertificateRepository.findById(NEW_SERVER_CERTIFICATE_ID)).willReturn(Optional.of(newServerCertificate));
        given(encryptionService.getServerCertificateExpirationDate()).willReturn(inMoreThan2Months);

        newServerCertificateJob.runNewCertificateTask();

        verify(mailService, times(0)).send(notificationEmailSpecArgumentCaptor.capture());
        verify(newServerCertificateRepository, times(0)).save(newServerCertificateArgumentCaptor.capture());
    }

    @Test
    void should_not_send_first_notification_if_less_than_60_days_and_first_notification_already_sent() {
        LocalDateTime inLessThan2Months = LocalDateTime.now().plusDays(61);
        NewServerCertificate newServerCertificate = mockNewServerCertificate(true, false);

        given(newServerCertificateRepository.findById(NEW_SERVER_CERTIFICATE_ID)).willReturn(Optional.of(newServerCertificate));
        given(encryptionService.getServerCertificateExpirationDate()).willReturn(inLessThan2Months);

        newServerCertificateJob.runNewCertificateTask();

        verify(mailService, times(0)).send(notificationEmailSpecArgumentCaptor.capture());
        verify(newServerCertificateRepository, times(0)).save(newServerCertificateArgumentCaptor.capture());
    }

    @Test
    void should_not_send_second_notification_if_less_than_30_days_and_second_notification_already_sent() {
        LocalDateTime inLessThan1Month = LocalDateTime.now().plusDays(59);
        NewServerCertificate newServerCertificate = mockNewServerCertificate(true, true);

        given(newServerCertificateRepository.findById(NEW_SERVER_CERTIFICATE_ID)).willReturn(Optional.of(newServerCertificate));
        given(encryptionService.getServerCertificateExpirationDate()).willReturn(inLessThan1Month);

        newServerCertificateJob.runNewCertificateTask();

        verify(mailService, times(0)).send(notificationEmailSpecArgumentCaptor.capture());
        verify(newServerCertificateRepository, times(0)).save(newServerCertificateArgumentCaptor.capture());
    }

    @Test
    void should_send_first_notification_if_less_than_60_days() {
        LocalDateTime inLessThan2Months = LocalDateTime.now().plusDays(59);
        NewServerCertificate newServerCertificate = mockNewServerCertificate(false, false);
        long daysToExpire = ChronoUnit.DAYS.between(LocalDate.now(), inLessThan2Months);
        String emailSubject = ENVIRONMENT + " - Server certificate is about to expire in " + daysToExpire + " days";
        String emailBody = String.format("The Sever certificate expiration date is %1$s. Please upload the new certificate and deploy application ",
                inLessThan2Months);


        given(newServerCertificateRepository.findById(NEW_SERVER_CERTIFICATE_ID)).willReturn(Optional.of(newServerCertificate));
        given(etrustexWebProperties.getEnvironment()).willReturn(ENVIRONMENT);
        given(etrustexWebProperties.getFunctionalMailbox()).willReturn(FUNCTIONAL_MAILBOX);
        given(etrustexWebProperties.getNewCertificateJobFirstNotificationDays()).willReturn(60);
        given(encryptionService.getServerCertificateExpirationDate()).willReturn(inLessThan2Months);
        newServerCertificateJob.runNewCertificateTask();


        verify(mailService, times(1)).send(notificationEmailSpecArgumentCaptor.capture());
        verify(newServerCertificateRepository, times(1)).save(newServerCertificateArgumentCaptor.capture());

        NotificationEmailSpec notificationEmailSpec = notificationEmailSpecArgumentCaptor.getValue();
        NewServerCertificate updatedServerCertificate = newServerCertificateArgumentCaptor.getValue();

        assertEquals(emailSubject, notificationEmailSpec.getSubject());
        assertEquals(emailBody, notificationEmailSpec.getBody());
        assertEquals(FUNCTIONAL_MAILBOX, notificationEmailSpec.getTo());

        assertTrue(updatedServerCertificate.isFirstNotificationSent());
    }

    @Test
    void should_send_second_notification_if_less_than_30_days() {
        LocalDateTime inLessThan1Month = LocalDateTime.now().plusDays(29);
        NewServerCertificate newServerCertificate = mockNewServerCertificate(true, false);
        long daysToExpire = ChronoUnit.DAYS.between(LocalDate.now(), inLessThan1Month);
        String emailSubject = ENVIRONMENT + " - Server certificate is about to expire in " + daysToExpire + " days";
        String emailBody = String.format("The Sever certificate expiration date is %1$s. Please upload the new certificate and deploy application ",
                inLessThan1Month);


        given(newServerCertificateRepository.findById(NEW_SERVER_CERTIFICATE_ID)).willReturn(Optional.of(newServerCertificate));
        given(etrustexWebProperties.getEnvironment()).willReturn(ENVIRONMENT);
        given(etrustexWebProperties.getFunctionalMailbox()).willReturn(FUNCTIONAL_MAILBOX);
        given(etrustexWebProperties.getNewCertificateJobFirstNotificationDays()).willReturn(60);
        given(etrustexWebProperties.getNewCertificateJobSecondNotificationDays()).willReturn(30);
        given(encryptionService.getServerCertificateExpirationDate()).willReturn(inLessThan1Month);


        newServerCertificateJob.runNewCertificateTask();


        verify(mailService, times(1)).send(notificationEmailSpecArgumentCaptor.capture());
        verify(newServerCertificateRepository, times(1)).save(newServerCertificateArgumentCaptor.capture());

        NotificationEmailSpec notificationEmailSpec = notificationEmailSpecArgumentCaptor.getValue();
        NewServerCertificate updatedServerCertificate = newServerCertificateArgumentCaptor.getValue();

        assertEquals(emailSubject, notificationEmailSpec.getSubject());
        assertEquals(emailBody, notificationEmailSpec.getBody());
        assertEquals(FUNCTIONAL_MAILBOX, notificationEmailSpec.getTo());

        assertTrue(updatedServerCertificate.isSecondNotificationSent());
    }
}