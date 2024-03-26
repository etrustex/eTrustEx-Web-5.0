package eu.europa.ec.etrustex.web.service.jobs.certificate.batch;

import eu.europa.ec.etrustex.web.common.EtrustexWebProperties;
import eu.europa.ec.etrustex.web.persistence.entity.NewServerCertificate;
import eu.europa.ec.etrustex.web.persistence.repository.NewServerCertificateRepository;
import eu.europa.ec.etrustex.web.service.CredentialService;
import eu.europa.ec.etrustex.web.service.MailService;
import eu.europa.ec.etrustex.web.service.MessageService;
import eu.europa.ec.etrustex.web.service.MessageSummaryService;
import eu.europa.ec.etrustex.web.service.validation.model.NotificationEmailSpec;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;

import static eu.europa.ec.etrustex.web.persistence.entity.NewServerCertificate.NEW_SERVER_CERTIFICATE_ID;
import static org.springframework.batch.core.BatchStatus.COMPLETED;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {
    private final EtrustexWebProperties etrustexWebProperties;
    private final MailService mailService;
    private final NewServerCertificateRepository newServerCertificateRepository;
    private final MessageService messageService;
    private final MessageSummaryService messageSummaryService;
    private final CredentialService credentialService;

    @Getter
    private JobExecution jobExecution;


    @Override
    public void beforeJob(JobExecution jobExecution) {
        this.jobExecution = jobExecution;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == COMPLETED) {
            credentialService.encryptCredentialPasswordsWithNewCertificate();

            newServerCertificateRepository.findById(NEW_SERVER_CERTIFICATE_ID)
                    .ifPresent(newServerCertificate -> {
                        saveNewServerCertificateDefaults(newServerCertificate);

                        messageService.setUpdatedWithNewCertificateToTrueForProcessedMessages();
                        messageService.setProcessed(false);

                        messageSummaryService.setUpdatedWithNewCertificateToTrueForProcessedMessages();
                        messageSummaryService.setProcessed(false);

                        long msgFailures = messageService.countUpdatedWithNewCertificateFailures();
                        long msgSummaryFailures = messageSummaryService.countUpdatedWithNewCertificateFailures();

                        notifyResult(msgFailures, msgSummaryFailures);
                    });
        }
    }

    private void saveNewServerCertificateDefaults(NewServerCertificate newServerCertificate) {
        newServerCertificate.setFirstNotificationSent(false);
        newServerCertificate.setSecondNotificationSent(false);
        newServerCertificate.setReady(false);

        newServerCertificateRepository.save(newServerCertificate);
    }

    private void notifyResult(long failures, long msgSummaryFailures) {
        String environment = etrustexWebProperties.getEnvironment();
        String subject;
        String body;

        if (failures + msgSummaryFailures > 0) {
            subject = environment + " - Server certificate update result FAILURE!";
            body = "Symmetric key has not been updated with the new certificate for " + failures + " Messages and " + msgSummaryFailures + "Message Summaries. Check the logs from new-certificate-update-failures.log file";
        } else {
            subject = environment + " - The messages text have been updated successfully after new Server certificate upload";
            body = "All messages text have been encrypted successfully after new Server certificate upload";
        }


        NotificationEmailSpec notificationEmailSpec = NotificationEmailSpec.builder()
                .to(etrustexWebProperties.getFunctionalMailbox())
                .subject(subject)
                .body(body)
                .build();

        mailService.send(notificationEmailSpec);
    }
}
