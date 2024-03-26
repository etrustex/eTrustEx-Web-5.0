package eu.europa.ec.etrustex.web.service.jobs;

import eu.europa.ec.etrustex.web.persistence.entity.security.Group;

public interface RetentionPolicyService {
    void deleteMessagesForGroup(Group group, Integer retentionPolicyInDays);

    void deactivateMessagesForGroup(Group group, Integer retentionPolicyDays);

    void handleRetentionPolicyNotificationsForGroup(Group group, Integer entityRetentionPolicyDays, Integer businessRetentionPolicyDays, Integer notificationInDays);
}
