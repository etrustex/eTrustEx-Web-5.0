package eu.europa.ec.etrustex.web.service.jobs;

import eu.europa.ec.etrustex.web.persistence.repository.redirect.CertificateUpdateRedirectRepository;
import eu.europa.ec.etrustex.web.service.UserRegistrationRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
@Slf4j
@RequiredArgsConstructor
public class DeleteExpiredRedirectLinksJob {

    private final UserRegistrationRequestService userRegistrationRequestService;
    private final CertificateUpdateRedirectRepository certificateUpdateRedirectRepository;

    @Scheduled(cron = "0 0 2 * * *") //every night at 02:00
    @SchedulerLock(name = "DeleteExpiredRedirectLinksJob_runDeleteExpiredLinksTask")
    public void runDeleteExpiredLinksTask() {
        log.info("--Delete expired redirect links older than 7 days - Job Launched");

        userRegistrationRequestService.deleteExpiredLinks();

        Date deleteBefore = Date.from(Instant.now().minus(1, ChronoUnit.DAYS));
        certificateUpdateRedirectRepository.deleteByAuditingEntityCreatedDateBefore(deleteBefore);

        log.info("--Delete expired redirect links Job Finished");
    }
}
