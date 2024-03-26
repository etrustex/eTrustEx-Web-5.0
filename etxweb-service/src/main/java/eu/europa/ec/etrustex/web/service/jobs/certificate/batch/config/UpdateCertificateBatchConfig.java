package eu.europa.ec.etrustex.web.service.jobs.certificate.batch.config;

import eu.europa.ec.etrustex.web.common.EtrustexWebProperties;
import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummary;
import eu.europa.ec.etrustex.web.service.jobs.certificate.batch.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.persistence.EntityManagerFactory;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
@Slf4j
public class UpdateCertificateBatchConfig {
    public static final String UPDATE_CERTIFICATE_JOB_NAME = "newCertificateUpdateJob";
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private final UpdateCertificateMessageWriter updateCertificateMessageWriter;
    private final UpdateCertificateMessageSummaryWriter updateCertificateMessageSummaryWriter;
    private final UpdateCertificateMessageProcessor updateCertificateMessageProcessor;
    private final UpdateCertificateMessageSummaryProcessor updateCertificateMessageSummaryProcessor;
    private final JobCompletionNotificationListener listener;


    private final EtrustexWebProperties etrustexWebProperties;


    @Bean
    public Job encryptWithNewCertificateJob() {
        return jobBuilderFactory
                .get(UPDATE_CERTIFICATE_JOB_NAME)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(getMessageStep())
                .next(getMessageSummaryStep())
                .build();
    }

    @Bean
    protected Step getMessageStep() {
        return stepBuilderFactory.get("Multi-threaded get Message, decrypt with old certificate and encrypt with new one")
                .<Message, Message>chunk(etrustexWebProperties.getNewCertificateJobStepChunkSize())
                .reader(messageReader())
                .processor(updateCertificateMessageProcessor)
                .writer(updateCertificateMessageWriter)
                .allowStartIfComplete(true)
                .taskExecutor(newCertificateJobTaskExecutor())
                .build();
    }

    @Bean
    protected Step getMessageSummaryStep() {
        return stepBuilderFactory.get("Multi-threaded get MessageSummary, decrypt with old certificate and encrypt with new one")
                .<MessageSummary, MessageSummary>chunk(etrustexWebProperties.getNewCertificateJobStepChunkSize())
                .reader(messageSummaryReader())
                .processor(updateCertificateMessageSummaryProcessor)
                .writer(updateCertificateMessageSummaryWriter)
                .allowStartIfComplete(true)
                .taskExecutor(newCertificateJobTaskExecutor())
                .build();
    }


    @Bean
    public JpaPagingItemReader<Message> messageReader() {
        return new JpaPagingItemReaderBuilder<Message>()
                .name("messageItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select m from Message m where m.symmetricKey is not null and m.iv is not null and m.updatedWithNewCertificate = false")
                .pageSize(etrustexWebProperties.getNewCertificateJobJpaPageSize())
                .saveState(true)
                .build();
    }

    @Bean
    public JpaPagingItemReader<MessageSummary> messageSummaryReader() {
        return new JpaPagingItemReaderBuilder<MessageSummary>()
                .name("messageSummaryItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select ms from MessageSummary ms where ms.updatedWithNewCertificate = false")
                .pageSize(etrustexWebProperties.getNewCertificateJobJpaPageSize())
                .saveState(true)
                .build();
    }


    @Bean(name = "newCertificateJobTaskExecutor")
    public TaskExecutor newCertificateJobTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(etrustexWebProperties.getNewCertificateJobCorePoolSize());
        executor.setMaxPoolSize(etrustexWebProperties.getNewCertificateJobMaxPoolSize());
        executor.setQueueCapacity(etrustexWebProperties.getNewCertificateJobQueueSize());
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setThreadNamePrefix("NewCertificateJobTaskExecutor-");
        return executor;
    }

}
