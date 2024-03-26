package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.exchange.model.MessageSummaryListItem;
import eu.europa.ec.etrustex.web.exchange.model.SearchItem;
import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummary;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummaryUserStatus;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.service.dto.FindMessageSummaryDto;
import eu.europa.ec.etrustex.web.service.pagination.MessageSummaryPage;
import eu.europa.ec.etrustex.web.util.exchange.model.MessageSummarySpec;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import java.util.*;

public interface MessageSummaryService {

    MessageSummary save(MessageSummary messageSummary);

    List<MessageSummary> saveAll(List<MessageSummary> messageSummaries);

    MessageSummary update(MessageSummary messageSummary);

    List<MessageSummary> createMessageSummaries(Message message, Collection<MessageSummarySpec> messageSummarySpecs);

    MessageSummary findByMessageIdAndRecipientIdForCurrentUser(Long messageId, String clientPublicKey, Long recipientEntityId, User user);

    MessageSummary decryptAndEncryptSymmetricKeysAndFilterUserStatuses(MessageSummary messageSummary, User user, String clientPublicKey);

    MessageSummary findByMessageIdAndRecipientId(Long messageId, Long recipientEntityId);

    Optional<MessageSummary> findOptionalByMessageIdAndRecipientId(Long messageId, Long recipientId);

    /**
     * @param updated the updated and not yet persisted message summary
     * @return the messageSummary as currently saved in the database. Beware, the current value will always match
     * the current if invoked within a Transaction!
     */
    MessageSummary getCurrent(MessageSummary updated);

    MessageSummaryPage findByRecipientForUser(FindMessageSummaryDto findMessageSummaryDto, User user);

    List<MessageSummary> findByRecipientGroupAndSentOnBefore(Group group, Date expiryDate);

    List<MessageSummary> findByRecipientIdAndMessageIdIn(List<Long> messageIds, Long recipientId);

    boolean groupIsRecipient(Long messageId, Group recipient);

    int countUnreadMessages(@Nullable Long to, Long recipientId, User user);

    Optional<MessageSummary> findByClientReferenceAndRecipientIdentifier(String referenceId, String recipientIdentifier);

    boolean isMessageSummaryCreatedForClientReferenceAndRecipientId(String referenceId, Long recipientId);

    void deleteAll(Set<MessageSummary> messageSummaries);

    @Transactional
    void setUpdatedWithNewCertificateToTrueForProcessedMessages();

    @Transactional
    void setProcessed(boolean value);

    long countUpdatedWithNewCertificateFailures();

    void disableByPublicKeyAndBusinessIdAndEntityId(String hashValue, Long entityId);

    void filterByUser(Set<MessageSummaryUserStatus> messageSummaryUserStatuses, User user);

    int countByPublicKeyHashValueAndRecipientId(String hashValue, Long entityId);

    Page<MessageSummaryListItem> findMessageSummaryListItemsByBusinessIdAndMessageIdOrSubject(Long businessId, String filterValue, Pageable pageable);

    List<SearchItem> search(Long businessId, String messageIdOrSubject);

    void setActive(Long messageId, String recipientIdentifier, Boolean isActive);

    void activateOrInactivateMessageSummaries(List<MessageSummaryListItem> messageSummaryListItems, boolean activate);

    List<MessageSummary> findByRecipientGroupAndMessageSentOnBetween(Group group, Date startDate, Date endDate);
}
