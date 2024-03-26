package eu.europa.ec.etrustex.web.service.jobs;


import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.service.MessageCleanupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class DeleteNotSentMessageJob {

    private final MessageCleanupService messageCleanupService;

    //    @Scheduled(fixedRate = 450000) //every 7min
    @Scheduled(cron = "0 0 2 * * *") //every night at 02:00
    @SchedulerLock(name = "DeleteNotSentMessageJob_runDeleteNotSentMessageTask")
    public void runDeleteNotSentMessageTask() {
        log.info("--Delete not sent message Job Launched");

        Page<Message> expiredIncompleteMessages;
        do {
            expiredIncompleteMessages = messageCleanupService.getCleanupPage();
            log.info("Deleting " + expiredIncompleteMessages.getContent().size() + " incomplete Message(s)");
            expiredIncompleteMessages.forEach(messageCleanupService::cleanupMessage);
        } while (expiredIncompleteMessages.getTotalElements() > expiredIncompleteMessages.getSize());

        log.info("--Delete not sent message Job Finished");
    }
}
