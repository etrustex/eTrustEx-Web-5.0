package eu.europa.ec.etrustex.web.rest.status;

import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummary;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.service.*;
import eu.europa.ec.etrustex.web.util.exchange.model.Status;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;

@Component
@Aspect
@Slf4j
@AllArgsConstructor
@SuppressWarnings({"squid:S1186"})
public class StatusAspect {
    private final MessageSummaryUserStatusService messageSummaryUserStatusService;
    private final MessageUserStatusService messageUserStatusService;
    private final MessageSummaryService messageSummaryService;
    private final MessageService messageService;
    private final StatusService statusService;

    @Pointcut(value = "execution(* eu.europa.ec.etrustex.web.rest.MessageController.update(..)) " +
            "&& args(userDetails, messageId, ..)",
            argNames = "userDetails, messageId")
    public void updateMessageFromUI(SecurityUserDetails userDetails, Long messageId) {
    }

    @Pointcut(value = "execution(* eu.europa.ec.etrustex.web.rest.api.messages.ApiMessageController.update(..)) " +
            "&& args(userDetails, messageId, ..)",
            argNames = "userDetails, messageId")
    public void updateMessageFromApi(SecurityUserDetails userDetails, Long messageId) {
    }

    @AfterReturning(pointcut = "updateMessageFromUI(userDetails, messageId) || updateMessageFromApi(userDetails, messageId)", argNames = "userDetails, messageId")
    public void createReadMessageUserStatusAfterSent(SecurityUserDetails userDetails, Long messageId) {
        Message message = messageService.findById(messageId);
        if (message.getStatus() != Status.DRAFT) {
            messageUserStatusService.createReadMessageUserStatusIfNotExists(message, userDetails.getUser(), Status.READ);
        }
    }

    @AfterReturning(value = "execution(* eu.europa.ec.etrustex.web.service.MessageSummaryServiceImpl.createMessageSummaries(..)) && args(message, ..)", argNames = "message, messageSummaries", returning = "messageSummaries")
    public void initMessageSummaryStatusAfterCreate(Message message, Collection<MessageSummary> messageSummaries) {
        messageSummaries.forEach(messageSummary -> {
            messageSummary.setStatus(messageSummary.getRecipient().isSystem() ? Status.SENT : Status.DELIVERED);
            messageSummary.setStatusModifiedDate(new Date());
            messageSummaryService.save(messageSummary);
            statusService.updateWithMultipleStatus(message, messageSummary.getStatus());
        });
    }

    @AfterReturning(value = "execution(* eu.europa.ec.etrustex.web.rest.MessageSummaryController.get(..)) && args(messageId, .., userDetails)", argNames = "messageId, userDetails, messageSummaryWithLinks", returning = "messageSummaryWithLinks")
    public void setMessageSummaryStatusAfterRead(Long messageId, SecurityUserDetails userDetails, MessageSummary messageSummaryWithLinks) {
        Group recipient = messageSummaryWithLinks.getRecipient();
        MessageSummary messageSummary = messageSummaryService.findByMessageIdAndRecipientId(messageId, recipient.getId());

        if (!messageSummary.getStatus().equals(Status.READ)) {
            messageSummary.setStatusModifiedDate(new Date());
            messageSummary.setStatus(Status.READ);
        }

        // required to be called before messageSummaryService.save so #NotificationAspect.notifyOfMessageSummaryStatusChange is called
        messageSummaryUserStatusService.updateMessageSummaryUserStatus(messageSummary, userDetails.getUser(), Status.READ);

        messageSummaryService.save(messageSummary);
        statusService.updateWithMultipleStatus(messageSummary.getMessage(), Status.READ);
    }

    @AfterReturning(value = "execution(* eu.europa.ec.etrustex.web.rest.MessageController.get(..)) && args(messageId, senderEntityId, clientPublicKeyPem, userDetails)", argNames = "messageId, senderEntityId, clientPublicKeyPem, userDetails")
    public void createReadMessageUserStatusAfterRetrieve(Long messageId, Long senderEntityId, String clientPublicKeyPem, SecurityUserDetails userDetails) {
        Message message = messageService.findById(messageId);
        if (message.getStatus() != Status.DRAFT) {
            messageUserStatusService.createReadMessageUserStatusIfNotExists(message, userDetails.getUser(), Status.READ);
        }
    }
}
