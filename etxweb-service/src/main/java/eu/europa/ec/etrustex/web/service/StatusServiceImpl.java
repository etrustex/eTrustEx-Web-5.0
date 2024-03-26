package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummary;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.util.exchange.model.Status;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

@AllArgsConstructor
@Service
@Slf4j
public class StatusServiceImpl implements StatusService {

    private final MessageSummaryUserStatusService messageSummaryUserStatusService;
    private final MessageUserStatusService messageUserStatusService;
    private final MessageSummaryService messageSummaryService;
    private final MessageService messageService;

    @Override
    public void markMessageSummaryRead(Long[] messageIds, Long recipientEntityId, User user) {
        List<MessageSummary> messageSummaries = messageSummaryService.findByRecipientIdAndMessageIdIn(Arrays.asList(messageIds), recipientEntityId);
        messageSummaries.forEach(messageSummary -> {
            if (!Status.READ.equals(messageSummary.getStatus())) {
                messageSummary.setStatusModifiedDate(new Date());
                messageSummary.setStatus(Status.READ);
            }

            // required to be called before messageSummaryService.save so #NotificationAspect.notifyOfMessageSummaryStatusChange is called
            messageSummaryUserStatusService.updateMessageSummaryUserStatus(messageSummary, user, Status.READ);

            messageSummaryService.save(messageSummary);
            updateWithMultipleStatus(messageSummary.getMessage(), Status.READ);
        });
    }

    @Override
    public void markMessageRead(Long[] messageIds, User user) {
        Arrays.asList(messageIds).forEach(messageId -> {
            Message message = messageService.findById(messageId);
            if (message.getStatus() != Status.DRAFT) {
                messageUserStatusService.createReadMessageUserStatusIfNotExists(message, user, Status.READ);
            }
        });
    }

    @Override
    public void updateMessageAndSummariesStatus(Long messageId, Long recipientId, Status status) {
        updateMessageAndSummariesStatus(messageId, recipientId, null, status);
    }

    @Override
    public void updateMessageAndSummariesStatus(Long messageId, Long recipientId, String clientReference, Status status) {
        MessageSummary messageSummary = messageSummaryService.findOptionalByMessageIdAndRecipientId(messageId, recipientId)
                .orElseThrow(() -> new EtxWebException(String.format("Message summary not found for SendDocumentBundle, messageId %s, and recipient id %s!", messageId, recipientId)));

        if (StringUtils.isNotBlank(clientReference)) {
            messageSummary.setClientReference(clientReference);
        }

        if (!status.equals(messageSummary.getStatus())) {
            messageSummary.setStatus(status);
            messageSummary.setStatusModifiedDate(new Date());
            messageSummaryService.save(messageSummary);
        }

        updateWithMultipleStatus(messageSummary.getMessage(), status);
    }

    @Override
    public void updateWithMultipleStatus(Message message, Status status) {
        int nbOfDifferentStatus = (int) message.getMessageSummaries().stream().filter(distinctByKey(ms -> ms.getStatus() != null ? ms.getStatus().name() : "")).count();
        message.setStatus(nbOfDifferentStatus > 1 ? Status.MULTIPLE : status);
        message.setStatusModifiedDate(new Date());
        messageService.save(message);
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}
