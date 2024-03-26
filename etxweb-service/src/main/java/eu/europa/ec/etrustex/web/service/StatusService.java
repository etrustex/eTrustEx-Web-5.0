package eu.europa.ec.etrustex.web.service;
import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.util.exchange.model.Status;

public interface StatusService {

    void markMessageSummaryRead(Long[] messageIds, Long recipientEntityId, User user);

    void markMessageRead(Long[] messageIds, User user);

    void updateMessageAndSummariesStatus(Long messageId, Long recipientId, Status status);

    void updateMessageAndSummariesStatus(Long messageId, Long recipientId, String clientReference, Status status);

    void updateWithMultipleStatus(Message message, Status status);
}
