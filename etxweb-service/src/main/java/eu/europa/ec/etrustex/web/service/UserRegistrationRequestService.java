package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.persistence.entity.UserRegistrationRequest;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.service.validation.model.BaseUserRegistrationRequestSpec;
import eu.europa.ec.etrustex.web.service.validation.model.NotificationEmailSpec;
import eu.europa.ec.etrustex.web.service.validation.model.UserRegistrationRequestSpec;

import java.util.List;

public interface UserRegistrationRequestService {

    UserRegistrationRequest create(User user, UserRegistrationRequestSpec userRegistrationRequestSpec);
    void sendNotificationEmail(NotificationEmailSpec notificationEmailSpec);
    void deleteUserRegistrationRequestAndCleanUp(List<BaseUserRegistrationRequestSpec> baseUserRegistrationRequestSpecs);
    UserRegistrationRequest findByUserEcasIdAndGroupId(String ecasID, Long groupId);
    void deleteByUserAndGroup(User user, Group group);
    UserRegistrationRequest update(UserRegistrationRequestSpec userRegistrationRequestSpec);
    void deleteExpiredLinks();

}
