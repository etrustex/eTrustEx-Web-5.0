package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.persistence.entity.NewServerCertificate;
import eu.europa.ec.etrustex.web.persistence.repository.MessageRepository;
import eu.europa.ec.etrustex.web.persistence.repository.MessageSummaryRepository;
import eu.europa.ec.etrustex.web.persistence.repository.NewServerCertificateRepository;
import eu.europa.ec.etrustex.web.service.jobs.certificate.batch.JobCompletionNotificationListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static eu.europa.ec.etrustex.web.persistence.entity.NewServerCertificate.NEW_SERVER_CERTIFICATE_ID;

@Service
@Slf4j
public class NewServerCertificateServiceImpl implements NewServerCertificateService {
    private final EncryptionService encryptionService;
    private final NewServerCertificateRepository newServerCertificateRepository;
    private final MessageRepository messageRepository;
    private final MessageSummaryRepository messageSummaryRepository;
    private final JobLauncher jobLauncher;
    private final Job encryptWithNewCertificateJob;
    private final JobCompletionNotificationListener jobCompletionNotificationListener;


    public NewServerCertificateServiceImpl(EncryptionService encryptionService,
                                           NewServerCertificateRepository newServerCertificateRepository,
                                           MessageRepository messageRepository,
                                           MessageSummaryRepository messageSummaryRepository,
                                           JobLauncher jobLauncher,
                                           @Qualifier("encryptWithNewCertificateJob") Job encryptWithNewCertificateJob,
                                           JobCompletionNotificationListener jobCompletionNotificationListener) {
        this.encryptionService = encryptionService;
        this.newServerCertificateRepository = newServerCertificateRepository;
        this.messageRepository = messageRepository;
        this.messageSummaryRepository = messageSummaryRepository;
        this.jobLauncher = jobLauncher;
        this.encryptWithNewCertificateJob = encryptWithNewCertificateJob;
        this.jobCompletionNotificationListener = jobCompletionNotificationListener;
    }


    @Override
    @Async
    public void launchJob() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        NewServerCertificate newServerCertificate = newServerCertificate();
        newServerCertificate.setReady(true);
        newServerCertificateRepository.save(newServerCertificate);
        jobLauncher.run(encryptWithNewCertificateJob, new JobParameters());
    }

    @Override
    public NewServerCertificate getNewServerCertificate() {
        NewServerCertificate newServerCertificate = newServerCertificate();
        String status;
        JobExecution jobExecution = jobCompletionNotificationListener.getJobExecution();

        if (jobExecution == null) {
            status = BatchStatus.STOPPED.name();
        } else {
            int commited = jobExecution.getStepExecutions().iterator().next()
                    .getCommitCount();
            log.info("Step executions: " + commited);
            status = jobExecution.getStatus().name();
        }

        newServerCertificate.setStatus(status);
        newServerCertificate.setExpirationDate(encryptionService.getServerCertificateExpirationDate());
        newServerCertificate.setOldCertificateAlias(encryptionService.getServerCertificateAlias());

        return newServerCertificate;
    }

    @Override
    @Transactional
    public void resetUpdatedFlag() {
        messageRepository.setUpdatedWithNewCertificate(Boolean.FALSE);
        messageSummaryRepository.setUpdatedWithNewCertificate(Boolean.FALSE);
    }

    private NewServerCertificate newServerCertificate() {
        return newServerCertificateRepository.findById(NEW_SERVER_CERTIFICATE_ID)
                .orElseThrow(() -> new Error("NewServerCertificate not found with id " + NEW_SERVER_CERTIFICATE_ID));
    }
}
