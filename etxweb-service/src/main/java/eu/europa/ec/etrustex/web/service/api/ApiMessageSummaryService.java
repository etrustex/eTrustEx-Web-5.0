package eu.europa.ec.etrustex.web.service.api;

import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummary;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.util.exchange.model.Ack;
import eu.europa.ec.etrustex.web.util.exchange.model.EntitySpec;
import eu.europa.ec.etrustex.web.util.exchange.model.MessageSummarySpec;
import eu.europa.ec.etrustex.web.util.exchange.model.RecipientStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

public interface ApiMessageSummaryService {
    void createMessageSummaries(Message message, Collection<MessageSummarySpec> messageSummarySpecs);

    @Transactional(readOnly = true)
    MessageSummary findByMessageIdAndRecipientIdForCurrentUser(Long messageId, String clientPublicKey, EntitySpec entitySpec, User user);

    @Transactional(readOnly = true)
    List<RecipientStatus> recipientStatusByMessageId(Long messageId);

    @Transactional(readOnly = true)
    List<MessageSummary> findUnreadByRecipientIdForCurrentUser(EntitySpec entitySpec, User user, String clientPublicKey);

    @Transactional(readOnly = true)
    List<Group> getValidRecipients(String businessIdentifier, String entityIdentifier);

    void ack(User user, Long messageId, EntitySpec entitySpec, Ack ack);
}
