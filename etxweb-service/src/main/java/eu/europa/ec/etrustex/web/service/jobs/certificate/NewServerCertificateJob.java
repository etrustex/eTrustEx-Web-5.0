package eu.europa.ec.etrustex.web.service.jobs.certificate;

import eu.europa.ec.etrustex.web.common.EtrustexWebProperties;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.persistence.entity.NewServerCertificate;
import eu.europa.ec.etrustex.web.persistence.repository.NewServerCertificateRepository;
import eu.europa.ec.etrustex.web.service.EncryptionService;
import eu.europa.ec.etrustex.web.service.MailService;
import eu.europa.ec.etrustex.web.service.validation.model.NotificationEmailSpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static eu.europa.ec.etrustex.web.persistence.entity.NewServerCertificate.NEW_SERVER_CERTIFICATE_ID;

/**
 * This task contains a scheduled task that:
 * - if EW_NEW_SERVER_CERTIFICATE_PK.READY is true, we assume that 'server-certificate.properties file' has been updated
 * by setting etrustexweb.old-server-certificate... properties. A Batch Job will decrypt all Message.symmetricKey with the old
 * certificcate and encrypt with the new one.
 * <p>
 * At the end it will notify about the result and set EW_NEW_SERVER_CERTIFICATE_PK.READY flag to false.
 * See {@link eu.europa.ec.etrustex.web.service.jobs.certificate.batch.JobCompletionNotificationListener}
 * <p>
 * If EW_NEW_SERVER_CERTIFICATE_PK.READY is false it will check the server certificate expiration date and notify if less than x days
 * <p>
 * <p>
 * See activity diagram in doc/certificate-renewal/certificate-renewal.puml
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class NewServerCertificateJob {
    private final NewServerCertificateRepository newServerCertificateRepository;
    private final MailService mailService;
    private final JobLauncher jobLauncher;
    private final Job encryptWithNewCertificateJob;
    private final EtrustexWebProperties etrustexWebProperties;
    private final EncryptionService encryptionService;

    //    @Scheduled(cron = "0 * * * * *") // every minute
    @Scheduled(cron = "0 0 1 * * *") // every night at 01:00
    @SchedulerLock(name = "NewServerCertificateJob_runNewCertificateTask")
    public void runNewCertificateTask() {
        String environment = etrustexWebProperties.getEnvironment();
        NewServerCertificate newServerCertificate = newServerCertificateRepository.findById(NEW_SERVER_CERTIFICATE_ID)
                .orElseThrow(() -> new Error("NewServerCertificate not found with id " + NEW_SERVER_CERTIFICATE_ID));

        if (newServerCertificate.isReady()) {
            try {
                JobExecution jobExecution = jobLauncher.run(encryptWithNewCertificateJob, new JobParameters());

                ExitStatus status = jobExecution.getExitStatus();

                log.info("Finished job with status " + status);

            } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                     JobParametersInvalidException e) {
                throw new EtxWebException(e);
            }
        } else {
            LocalDateTime expirationDate = encryptionService.getServerCertificateExpirationDate();
            long daysToExpire = ChronoUnit.DAYS.between(LocalDate.now(), expirationDate);

            boolean sendNotification = false;

            if (daysToExpire <= etrustexWebProperties.getNewCertificateJobFirstNotificationDays() && !newServerCertificate.isFirstNotificationSent()) {
                sendNotification = true;
                newServerCertificate.setFirstNotificationSent(true);
            } else if (daysToExpire <= etrustexWebProperties.getNewCertificateJobSecondNotificationDays() && !newServerCertificate.isSecondNotificationSent()) {
                sendNotification = true;
                newServerCertificate.setSecondNotificationSent(true);
            }

            if (sendNotification) {
                String body = String.format("The Sever certificate expiration date is %1$s. Please upload the new certificate and deploy application ",
                        expirationDate);

                mailService.send(NotificationEmailSpec.builder()
                        .to(etrustexWebProperties.getFunctionalMailbox())
                        .subject(environment + " - Server certificate is about to expire in " + daysToExpire + " days")
                        .body(body)
                        .build());

                newServerCertificateRepository.save(newServerCertificate);
            }
        }
    }
}
