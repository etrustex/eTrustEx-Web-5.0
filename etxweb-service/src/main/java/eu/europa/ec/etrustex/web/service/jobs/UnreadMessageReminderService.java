package eu.europa.ec.etrustex.web.service.jobs;

import eu.europa.ec.etrustex.web.persistence.entity.security.Group;

public interface UnreadMessageReminderService {

    void handleUnreadMessageReminderForGroup(Group group, Integer reminderInDays);
}
