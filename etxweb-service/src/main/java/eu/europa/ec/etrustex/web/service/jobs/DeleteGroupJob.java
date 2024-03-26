package eu.europa.ec.etrustex.web.service.jobs;

import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import eu.europa.ec.etrustex.web.service.security.GroupService;
import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class DeleteGroupJob {

    private final GroupRepository groupRepository;
    private final DeleteGroupService deleteGroupService;
    private final GroupService groupService;

    @Scheduled(cron = "0 0 3 * * *") //every night at 03:00
    @SchedulerLock(name = "DeleteGroupJob_runDeleteBusinessTask")
    public void runDeleteBusinessTask() {
        log.info("--Business Deletion Job Launched");
        groupRepository.findByIsPendingDeletionIsTrueAndRemovedDateIsNotNullAndType(GroupType.BUSINESS)
                .forEach(deleteGroupService::deleteBusiness);
        log.info("--Business Deletion Job Finished");
    }

//    @Scheduled(cron = "0 0 3 * * *") //every night at 03:00
    @Scheduled(fixedRate = 900000) //every 15min
    @SchedulerLock(name = "DeleteGroupJob_runDeleteGroupTask")
    public void runDeleteGroupTask() {
        log.info("--Group Deletion Job Launched");
        groupRepository.findByIsPendingDeletionIsTrueAndIsActiveIsFalseAndType(GroupType.ENTITY)
                .stream().filter(group -> !groupService.hasMessages(group.getId()))
                .forEach(groupService::deleteGroup);
        log.info("--Group Deletion Job Finished");
    }
}
