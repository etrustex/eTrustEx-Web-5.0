package eu.europa.ec.etrustex.web.service.jobs;

import eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.impl.RetentionPolicyEntityConfiguration;
import eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.impl.RetentionPolicyGroupConfiguration;
import eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.impl.RetentionPolicyNotificationGroupConfiguration;
import eu.europa.ec.etrustex.web.service.groupconfiguration.GroupConfigurationService;
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
public class RetentionPolicyJob {

    private final RetentionPolicyService retentionPolicyService;
    private final GroupService groupService;
    private final GroupConfigurationService groupConfigurationService;

    //    @Scheduled(fixedRate = 900000) //Every half hour
    @Scheduled(cron = "0 0 1 * * *") //every night at 01:00
    @SchedulerLock(name = "RetentionPolicyJob_runRetentionPolicyTask")
    public void runRetentionPolicyTask() {
        log.info("--Retention Policy Job Launched");
        log.info("--Retention Policy Job Running for Business");
        groupService.findByType(GroupType.BUSINESS).forEach(business ->
                groupConfigurationService.findActiveByGroupIdAndType(business.getId(), RetentionPolicyGroupConfiguration.class)
                        .ifPresent(retentionPolicyGroupConfiguration -> {
                            log.info("Applied to Business: " + business.getIdentifier());
                            groupService.findByParentId(business.getId())
                                    .forEach(group -> retentionPolicyService.deleteMessagesForGroup(group, retentionPolicyGroupConfiguration.getIntegerValue()));
                        }));

        log.info("--Retention Policy Job Running for Entities");
        groupService.findByType(GroupType.ENTITY).forEach(entity ->
                groupConfigurationService.findActiveByGroupIdAndType(entity.getId(), RetentionPolicyEntityConfiguration.class)
                        .ifPresent(retentionPolicyEntityConfiguration -> {
                            log.info("Applied to Entity: " + entity.getIdentifier());
                            retentionPolicyService.deactivateMessagesForGroup(entity, retentionPolicyEntityConfiguration.getIntegerValue());
                        }));
        log.info("--Retention Policy Job Finished");
    }

    //    @Scheduled(fixedRate = 300000) //Every 5min
    @Scheduled(cron = "0 0 4 * * *") // 4am
    @SchedulerLock(name = "RetentionPolicyJob_runRetentionPolicyNotificationWarningTask")
    public void runRetentionPolicyNotificationWarningTask() {
        // TODO : ETRUSTEX-8479 Split BusinessRetention from EntityRetention as it's becoming harder than expected
        log.info("----Retention Policy Notification Warning Job Launched");

        groupService.findByType(GroupType.BUSINESS).forEach(business -> {
            RetentionPolicyGroupConfiguration retentionPolicyGroupConfiguration = groupConfigurationService.findActiveByGroupIdAndType(business.getId(), RetentionPolicyGroupConfiguration.class).orElse(null);
            groupService.findByParentId(business.getId()).forEach(entity -> {
                RetentionPolicyEntityConfiguration retentionPolicyEntityConfiguration = groupConfigurationService.findActiveByGroupIdAndType(entity.getId(), RetentionPolicyEntityConfiguration.class).orElse(null);

                if ((retentionPolicyGroupConfiguration == null || !retentionPolicyGroupConfiguration.isActive())
                        && (retentionPolicyEntityConfiguration == null || !retentionPolicyEntityConfiguration.isActive())) {
                    // no configurations
                    return;
                }

                int retentionPolicyValue = getRetentionPolicyValue(retentionPolicyGroupConfiguration, retentionPolicyEntityConfiguration);

                groupConfigurationService.findActiveByGroupIdAndType(business.getId(), RetentionPolicyNotificationGroupConfiguration.class).ifPresent(retentionPolicyNotificationGroupConfiguration -> {
                    if (retentionPolicyGroupConfiguration != null && retentionPolicyNotificationGroupConfiguration.getIntegerValue() > retentionPolicyGroupConfiguration.getIntegerValue()) {
                        log.error("Configuration Error for the Business: " + business.getIdentifier() + ". The configured notification comes after the configured Retention Policy.");
                    } else if (retentionPolicyEntityConfiguration != null && retentionPolicyNotificationGroupConfiguration.getIntegerValue() > retentionPolicyEntityConfiguration.getIntegerValue()) {
                        log.error("Configuration Error for the Business: " + business.getIdentifier() + " entity " + entity.getIdentifier() + ". The configured notification comes after the configured Entity Retention Policy.");
                        log.info("Trying to apply Retention Policy");
                        if (retentionPolicyGroupConfiguration != null && isValidValue(retentionPolicyNotificationGroupConfiguration.getIntegerValue()) && isValidValue(retentionPolicyGroupConfiguration.getIntegerValue())
                                && retentionPolicyNotificationGroupConfiguration.getIntegerValue() <= retentionPolicyGroupConfiguration.getIntegerValue()) {
                            log.info("Applied to Business: " + business.getIdentifier() + " entity: " + entity.getIdentifier());
                            retentionPolicyService.handleRetentionPolicyNotificationsForGroup(
                                    entity, null, retentionPolicyGroupConfiguration.getIntegerValue(), retentionPolicyNotificationGroupConfiguration.getIntegerValue());
                        }
                    } else if (!isValidValue(retentionPolicyNotificationGroupConfiguration.getIntegerValue()) || !isValidValue(retentionPolicyValue)) {
                        log.error("Configuration Error for the Business: " + business.getIdentifier() + " entity: " + entity.getIdentifier() + ". One of the values is over 50 years.");
                    } else {
                        log.info("Applied to Business: " + business.getIdentifier() + " entity: " + entity.getIdentifier());
                        Integer businessRetentionValue = (retentionPolicyGroupConfiguration != null) ? retentionPolicyGroupConfiguration.getIntegerValue() : null;
                        retentionPolicyService.handleRetentionPolicyNotificationsForGroup(
                                entity, retentionPolicyValue, businessRetentionValue, retentionPolicyNotificationGroupConfiguration.getIntegerValue());
                    }
                });
            });
        });
        log.info("----Retention Policy Notification Warning Job Finished");
    }


    private static boolean isValidValue(int value) {
        return value <= 50 * 365;
    }

    private static int getRetentionPolicyValue(RetentionPolicyGroupConfiguration retentionPolicyGroupConfiguration, RetentionPolicyEntityConfiguration retentionPolicyEntityConfiguration) {
        int retentionPolicyGroupConfigurationValue = (retentionPolicyGroupConfiguration != null && retentionPolicyGroupConfiguration.isActive()) ? retentionPolicyGroupConfiguration.getIntegerValue() : Integer.MAX_VALUE;
        int retentionPolicyEntityConfigurationValue = (retentionPolicyEntityConfiguration != null && retentionPolicyEntityConfiguration.isActive()) ? retentionPolicyEntityConfiguration.getIntegerValue() : Integer.MAX_VALUE;
        return Integer.min(retentionPolicyEntityConfigurationValue, retentionPolicyGroupConfigurationValue);
    }

}
