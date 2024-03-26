package eu.europa.ec.etrustex.web.service.jobs;

import eu.europa.ec.etrustex.web.common.EtrustexWebProperties;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.service.MailService;
import eu.europa.ec.etrustex.web.service.validation.model.NotificationEmailSpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
@Slf4j
@RequiredArgsConstructor
public class FileSystemReportingJob {

    private final EtrustexWebProperties etrustexWebProperties;
    private final MailService mailService;

    @Scheduled(cron = "0 0 5 * * *") //every day at 05:00
    @SchedulerLock(name = "FileSystemReportingJob_checkCriticalDiskSizeTask")
    public void checkCriticalDiskSizeTask() {
        if(etrustexWebProperties.isTestEnvironment()){
            log.info("--File System Reporting Job Launched");

            log.info("Path:"+etrustexWebProperties.getFileUploadDirPath().toString());
            try {
                Path dir = etrustexWebProperties.getFileUploadDirPath().toRealPath();
                FileStore fs = Files.getFileStore(dir);
                double freeSpace = (double) fs.getUsableSpace() /(1024*1024);
                double totalSpace = (double) fs.getTotalSpace() /(1024*1024);
                double freePercentage = (freeSpace/totalSpace)*100;

                log.info("name:"+fs.name());
                log.info("freeSpace:"+freeSpace+"MB");
                log.info("totalSpace:"+totalSpace+"MB");
                log.info("Percentage Free:"+ String.format("%.2f", freePercentage));

                if(freePercentage < 10) {
                    NotificationEmailSpec notificationEmailSpec = NotificationEmailSpec.builder()
                            .to(etrustexWebProperties.getFunctionalMailbox())
                            .subject("Alert: Critical Disk Size Reached")
                            .body("The file system space is :" + String.format("%.2f", freePercentage) + "% Free.  Please expand it or clean it up.")
                            .build();
                    mailService.send(notificationEmailSpec);
                }
            } catch (IOException e) {
                throw new EtxWebException("Problem checking the file system",e);
            }

            log.info("--File System Reporting Job Finished");
        }
    }
}
