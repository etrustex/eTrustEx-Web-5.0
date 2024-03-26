package eu.europa.ec.etrustex.web.service.redirect;

import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.entity.redirect.MessageRedirect;
import eu.europa.ec.etrustex.web.persistence.entity.security.GrantedAuthority;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.persistence.repository.redirect.MessageRedirectRepository;
import eu.europa.ec.etrustex.web.service.UserRegistrationRequestService;
import lombok.AllArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
@AllArgsConstructor
@SuppressWarnings({"squid:S1186"})
public class DeletionAspect {

    private final MessageRedirectRepository<MessageRedirect> messageRedirectRepository;
    private final UserRegistrationRequestService userRegistrationRequestService;

    @Pointcut(value = "execution(* eu.europa.ec.etrustex.web.service.MessageService.delete(..)) && args(message)", argNames = "message")
    public void deleteMessage(Message message) {
    }

    @AfterReturning(pointcut = "deleteMessage(message)", argNames = "message")
    public void deleteRedirects(Message message) {
        messageRedirectRepository.deleteByMessageId(message.getId());
    }

    @AfterReturning(value = "execution(* eu.europa.ec.etrustex.web.service.security.GrantedAuthorityService.create(..)) && args(user, roleName, groupId)", argNames = "user, roleName, groupId, grantedAuthority", returning = "grantedAuthority")
    public void cleanUpUserRegistrationRequest(User user, RoleName roleName, Long groupId, GrantedAuthority grantedAuthority) {
        Group group = grantedAuthority.getGroup();
        userRegistrationRequestService.deleteByUserAndGroup(user, group);

    }
}
