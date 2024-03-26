package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.persistence.entity.UserProfile;
import eu.europa.ec.etrustex.web.persistence.entity.UserRegistrationRequest;
import eu.europa.ec.etrustex.web.persistence.entity.redirect.UserRegistrationRedirect;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.persistence.repository.UserRegistrationRequestRepository;
import eu.europa.ec.etrustex.web.persistence.repository.redirect.UserRegistrationRedirectRepository;
import eu.europa.ec.etrustex.web.service.notification.NotificationService;
import eu.europa.ec.etrustex.web.service.redirect.RedirectService;
import eu.europa.ec.etrustex.web.service.security.UserProfileService;
import eu.europa.ec.etrustex.web.service.validation.model.BaseUserRegistrationRequestSpec;
import eu.europa.ec.etrustex.web.service.validation.model.CreateUserProfileSpec;
import eu.europa.ec.etrustex.web.service.validation.model.NotificationEmailSpec;
import eu.europa.ec.etrustex.web.service.validation.model.UserRegistrationRequestSpec;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
@Validated
public class UserRegistrationRequestServiceImpl implements UserRegistrationRequestService {
    private static final String REGISTRATION_LINK = "registrationLink";

    private final UserRegistrationRequestRepository userRegistrationRequestRepository;
    private final UserProfileService userProfileService;
    private final RedirectService redirectService;
    private final UserRegistrationRedirectRepository userRegistrationRedirectRepository;
    private final MailService mailService;
    private final NotificationService notificationService;

    @Override
    public UserRegistrationRequest create(User user, UserRegistrationRequestSpec userRegistrationRequestSpec) {
        UserProfile userProfile = userProfileService.create(
                CreateUserProfileSpec.builder()
                        .ecasId(userRegistrationRequestSpec.getEcasId())
                        .name(user.getName())
                        .euLoginEmailAddress(user.getEuLoginEmailAddress())
                        .alternativeEmail(userRegistrationRequestSpec.getNotificationEmail())
                        .alternativeEmailUsed(StringUtils.isNotEmpty(userRegistrationRequestSpec.getNotificationEmail()))
                        .groupId(userRegistrationRequestSpec.getGroupId())
                        .statusNotification(true)
                        .newMessageNotification(true)
                        .newMessageNotification(true)
                        .statusNotification(true)
                        .build()
        );
        UserRegistrationRequest userRegistrationRequest = userRegistrationRequestRepository.save(
                UserRegistrationRequest.builder()
                        .user(userProfile.getUser())
                        .group(userProfile.getGroup())
                        .isAdmin(userRegistrationRequestSpec.isAdmin())
                        .isOperator(userRegistrationRequestSpec.isOperator())
                        .build()
        );

        notificationService.notifyAdminsWithPendingUserRegistrationRequest(userRegistrationRequest);

        return userRegistrationRequest;
    }

    @Override
    public void sendNotificationEmail(NotificationEmailSpec notificationEmailSpec) {
        String body = notificationEmailSpec.getBody();
        HashSet<String> emails = notificationEmailSpec.getCc();
        emails.addAll(notificationEmailSpec.getBcc());
        notificationEmailSpec.setCc(new HashSet<>());
        notificationEmailSpec.setBcc(new HashSet<>());

        emails.forEach(email -> {
            String generatedLink = redirectService.createPermalink(UserRegistrationRedirect.builder()
                    .emailAddress(email)
                    .groupId(notificationEmailSpec.getGroupId())
                    .groupIdentifier(notificationEmailSpec.getGroupIdentifier())
                    .build());

            notificationEmailSpec.setBody(body.replace(REGISTRATION_LINK, generatedLink));
            notificationEmailSpec.setTo(email);
            mailService.send(notificationEmailSpec);
        });
    }

    @Override
    @Transactional
    public void deleteUserRegistrationRequestAndCleanUp(List<BaseUserRegistrationRequestSpec> baseUserRegistrationRequestSpecs) {
        baseUserRegistrationRequestSpecs.forEach(baseUserRegistrationRequestSpec -> {
            UserRegistrationRequest userRegistrationRequest = findByUserEcasIdAndGroupId(baseUserRegistrationRequestSpec.getEcasId(), baseUserRegistrationRequestSpec.getGroupId());

            User user = userRegistrationRequest.getUser();
            Group group = userRegistrationRequest.getGroup();
            UserProfile userProfile = userProfileService.findUserProfileByUserAndGroup(user, group);
            deleteByUserAndGroup(user, group);
            userProfileService.delete(user.getEcasId(), group.getId());
            notificationService.notifyOfUserRejected(userProfile);
        });

    }

    @Override
    public UserRegistrationRequest findByUserEcasIdAndGroupId(String ecasId, Long groupId) {
        return userRegistrationRequestRepository.findByUserEcasIdAndGroupId(ecasId, groupId)
                .orElseThrow(() -> new EtxWebException(String.format("Cannot find UserRegistrationRequest with ecasId: %s and groupId: %s ", ecasId, groupId)));
    }

    @Override
    @Transactional
    public void deleteByUserAndGroup(User user, Group group) {
        userRegistrationRequestRepository.deleteByUserIdAndGroupId(user.getId(), group.getId());
        userRegistrationRedirectRepository.deleteByGroupIdAndEmailAddress(group.getId(), user.getEuLoginEmailAddress());

    }

    @Override
    public UserRegistrationRequest update(UserRegistrationRequestSpec userRegistrationRequestSpec) {
        return userRegistrationRequestRepository.save(toUserRegistrationRequest(userRegistrationRequestSpec));
    }

    private UserRegistrationRequest toUserRegistrationRequest(UserRegistrationRequestSpec userRegistrationRequestSpec) {
        UserRegistrationRequest userRegistrationRequest = findByUserEcasIdAndGroupId(
                userRegistrationRequestSpec.getEcasId(), userRegistrationRequestSpec.getGroupId());
        userRegistrationRequest.setOperator(userRegistrationRequestSpec.isOperator());
        userRegistrationRequest.setAdmin(userRegistrationRequestSpec.isAdmin());
        return userRegistrationRequest;
    }

    @Override
    @Transactional
    public void deleteExpiredLinks(){
        Date deleteBefore = Date.from(Instant.now().minus(7, ChronoUnit.DAYS));
        userRegistrationRedirectRepository.deleteByAuditingEntityCreatedDateBefore(deleteBefore);
    }
}
