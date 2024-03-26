package eu.europa.ec.etrustex.web.service.notification;

import eu.europa.ec.etrustex.web.persistence.entity.MessageSummary;
import eu.europa.ec.etrustex.web.persistence.entity.UserProfile;
import eu.europa.ec.etrustex.web.persistence.entity.UserRegistrationRequest;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;

import java.util.List;

public interface NotificationService {
    void notifyOfNewMessageSummary(MessageSummary messageSummary);
    void notifyOfNewMessageSummaryStatus(MessageSummary messageSummary);
    void notifyOfNewMessageSummaryStatus(MessageSummary messageSummary, User user);

    void httpNotifyOfNewMessageSummaryStatus(MessageSummary messageSummary, User recipientUser);

    void notifyOfUserConfigured(User user, Group group);
    void notifyOfRetentionPolicyChanged(User user, Group group, int previousValue, int newValue);
    void notifyOfBusinessPendingDeletion(List<UserProfile> officials, Group business);
    void notifyOfUserRejected(UserProfile userProfile);
    void notifyAdminsWithPendingUserRegistrationRequest(UserRegistrationRequest userRegistrationRequest);
}
