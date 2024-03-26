package eu.europa.ec.etrustex.web.service.jobs;

import eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.impl.UnreadMessageReminderConfiguration;
import eu.europa.ec.etrustex.web.service.groupconfiguration.GroupConfigurationService;
import eu.europa.ec.etrustex.web.service.security.GroupService;
import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UnreadMessageReminderJob {

    @Autowired
    private final GroupService groupService;

    @Autowired
    private final GroupConfigurationService groupConfigurationService;

    private final UnreadMessageReminderService unreadMessageReminderService;

//    @Scheduled(fixedRate = 600000) //Every 10min
    @Scheduled(cron = "0 0 5 * * *") // 5am
    @SchedulerLock(name = "UnreadMessageReminderJob_runUnreadMessageReminderJobTask")
    public void runUnreadMessageReminderTask() {
        log.info("----Unread message reminder Job Launched");
        groupService.findByType(GroupType.BUSINESS)
                .forEach(business -> groupConfigurationService.findActiveByGroupIdAndType(business.getId(), UnreadMessageReminderConfiguration.class)
                        .ifPresent(unreadMessageReminderConfiguration -> {
                            log.info("Applied to Business: " + business.getIdentifier());
                            if ((unreadMessageReminderConfiguration.getIntegerValue() > 50 * 365)) {
                                log.error("Configuration Error for the Business: " + business.getIdentifier() + ". One of the values is over 50 years.");
                            } else {
                                groupService.findByParentId(business.getId())
                                        .forEach(group -> unreadMessageReminderService.handleUnreadMessageReminderForGroup(group, unreadMessageReminderConfiguration.getIntegerValue()));
                            }
                        }));
        log.info("----Unread message reminder Job Finished");
    }
}
