package eu.europa.ec.etrustex.web.service.notification;

import eu.europa.ec.etrustex.web.persistence.entity.MessageSummary;
import eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.impl.WelcomeEmailGroupConfiguration;
import eu.europa.ec.etrustex.web.persistence.entity.security.GrantedAuthority;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.persistence.repository.security.UserProfileRepository;
import eu.europa.ec.etrustex.web.service.MessageSummaryService;
import eu.europa.ec.etrustex.web.service.MessageSummaryUserStatusService;
import eu.europa.ec.etrustex.web.service.groupconfiguration.GroupConfigurationService;
import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import eu.europa.ec.etrustex.web.util.exchange.model.Status;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.List;

import static eu.europa.ec.etrustex.web.util.exchange.model.Status.*;

@Component
@Aspect
@Slf4j
@AllArgsConstructor
@SuppressWarnings({"squid:S1186"})
public class NotificationAspect {
    private final NotificationService notificationService;
    private final MessageSummaryService messageSummaryService;
    private final MessageSummaryUserStatusService messageSummaryUserStatusService;
    private final GroupConfigurationService groupConfigurationService;
    private final UserProfileRepository userProfileRepository;

    /*
     * Methods calling MessageSummaryService#save()
     *
     * DocumentBundleServiceImpl#processSendDocumentBundle()
     *  ?
     *
     * ApplicationResponseWebServiceEndpoint#updateMessageSummaryStatus
     *  READ
     *
     * ErrorQueueMessageListener#onMessage()
     *  FAILED
     *
     * ApplicationResponseWebServiceEndpoint#updateMessageSummaryStatus
     *  FAILED
     *
     * NodeAspect#sendToNodeAndUpdateMessageSummary()
     *  PROCESSING
     *
     * StatusAspect##setMessageSummaryStatusAfterCreate()
     *  PROCESSING | SENT
     *
     * StatusAspect##setMessageSummaryUserStatusRead()
     *  READ
     */
    @Around("execution(* eu.europa.ec.etrustex.web.service.MessageSummaryService.save(eu.europa.ec.etrustex.web.persistence.entity.MessageSummary)) && args(messageSummary)")
    public Object notifyOfMessageSummaryStatusChange(ProceedingJoinPoint joinPoint, MessageSummary messageSummary) throws Throwable {

        if (messageSummary.getStatus() == null) {
            return joinPoint.proceed();
        }

        MessageSummary currentMessageSummary = messageSummaryService.getCurrent(messageSummary);

        Object result = joinPoint.proceed();

        Status newStatus = messageSummary.getStatus();

        if (SENT.equals(newStatus) || DELIVERED.equals(newStatus)) {
            notificationService.notifyOfNewMessageSummary(messageSummary);
            if (SENT.equals(newStatus)) {
                notificationService.notifyOfNewMessageSummaryStatus(messageSummary);
            }
        }

        if (!READ.equals(newStatus) && !newStatus.equals(currentMessageSummary.getStatus())) {
            notificationService.notifyOfNewMessageSummaryStatus(messageSummary);
        }

        return result;
    }

    /*
     * StatusAspect#setMessageSummaryUserStatusRead()
     */
    @Around(value = "execution(* eu.europa.ec.etrustex.web.service.MessageSummaryUserStatusService.updateMessageSummaryUserStatus(..)) && args(messageSummary, user, status)", argNames = "joinPoint,messageSummary,user,status")
    public Object notifyOfReadStatus(ProceedingJoinPoint joinPoint, MessageSummary messageSummary, User user, Status status) throws Throwable {
        if (messageSummary.getStatus() == null || !READ.equals(status)) {
            return joinPoint.proceed();
        }

        MessageSummary currentMessageSummary = messageSummaryService.getCurrent(messageSummary);

        boolean statusChangedToRead = !currentMessageSummary.getStatus().equals(status);
        boolean sendIndividualStatusNotifications = messageSummary.getMessage().getSenderGroup().isIndividualStatusNotifications()
                && messageSummaryUserStatusService.findByMessageSummaryAndUser(messageSummary, user)
                .map(messageSummaryUserStatus -> !messageSummaryUserStatus.getStatus().equals(READ))
                .orElse(true);

        Object result = joinPoint.proceed();

        if (sendIndividualStatusNotifications || statusChangedToRead) {
            notificationService.notifyOfNewMessageSummaryStatus(messageSummary, user);
            notificationService.httpNotifyOfNewMessageSummaryStatus(messageSummary, user);
        }

        return result;
    }

    @AfterReturning(value = "execution(* eu.europa.ec.etrustex.web.service.security.GrantedAuthorityService.create(..)) && args(user, roleName, groupId)", argNames = "user, roleName, groupId, grantedAuthority", returning = "grantedAuthority")
    public void notifyOfUserConfigured(User user, RoleName roleName, Long groupId, GrantedAuthority grantedAuthority) {
        notifyUser(user, grantedAuthority);

    }

    @AfterReturning(value = "execution(* eu.europa.ec.etrustex.web.service.security.GrantedAuthorityService.create(..)) && args(user, roleNames, groupId)", argNames = "user, roleNames, groupId, grantedAuthorities", returning = "grantedAuthorities")
    public void notifyOfUserConfigured(User user, List<RoleName> roleNames, Long groupId, List<GrantedAuthority> grantedAuthorities) {
        notifyUser(user, grantedAuthorities.get(0));

    }

    @AfterReturning(value = "execution(* eu.europa.ec.etrustex.web.service.security.GroupService.deleteBusiness(..)) && args(group)")
    public void notifyOfficialsAfterBusinessDeletion(Group group) {
        if (group.getType().equals(GroupType.BUSINESS) && group.isPendingDeletion()) {
            notificationService.notifyOfBusinessPendingDeletion(userProfileRepository.findByRoleName(RoleName.OFFICIAL_IN_CHARGE), group);
        }
    }

    private void notifyUser(User user, GrantedAuthority grantedAuthority) {
        if (grantedAuthority.getGroup().getParent() != null) {
            groupConfigurationService.findByGroupIdAndType(grantedAuthority.getGroup().getParent().getId(), WelcomeEmailGroupConfiguration.class)
                    .filter(WelcomeEmailGroupConfiguration::isActive)
                    .ifPresent(welcomeEmailGroupConfiguration -> {
                        log.info("About to send notification to {} for entity {}", user.getName(), grantedAuthority.getGroup().getIdentifier());
                        notificationService.notifyOfUserConfigured(user, grantedAuthority.getGroup());
                    });
        }
    }

}
