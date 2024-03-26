package eu.europa.ec.etrustex.web.persistence.repository;

import eu.europa.ec.etrustex.web.exchange.model.MessageSummaryUserStatusItem;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummary;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummaryUserStatus;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MessageSummaryUserStatusRepository extends CrudRepository<MessageSummaryUserStatus, Long> {
    Optional<MessageSummaryUserStatus> findByMessageSummaryAndUser(MessageSummary messageSummary, User user);

    void deleteByUser(User user);

    @Query(value = "SELECT new eu.europa.ec.etrustex.web.exchange.model.MessageSummaryUserStatusItem(msus.user.ecasId, m.id, m.subject, msus.user.name, msus.auditingEntity.modifiedDate) " +
            "FROM MessageSummaryUserStatus msus " +
            "JOIN Message m on msus.messageSummary.message = m " +
            "JOIN Group g on msus.messageSummary.recipient = g " +
            "WHERE g.id = :recipientId " +
            "and msus.status = 'READ' " +
            "and (" +
            "   :subjectOrMessageIdText is null " +
            "   or to_char(m.id) = :subjectOrMessageIdText " +
            "   or lower(m.subject) LIKE lower(concat('%', :subjectOrMessageIdText,'%'))" +
            ") ",
            countQuery = "SELECT count(*) FROM MessageSummaryUserStatus msus " +
                    "JOIN Message m on msus.messageSummary.message = m " +
                    "JOIN Group g on msus.messageSummary.recipient = g " +
                    "WHERE g.id = :recipientId " +
                    "and msus.status = 'READ' " +
                    "and (" +
                    "   :subjectOrMessageIdText is null " +
                    "   or to_char(m.id) = :subjectOrMessageIdText " +
                    "   or lower(m.subject) LIKE lower(concat('%', :subjectOrMessageIdText,'%'))" +
                    ") ")
    Page<MessageSummaryUserStatusItem> findReadMessageSummaryUserStatusByRecipientIdAndMessageIdOrSubjectContainingIgnoreCase(
            Long recipientId, String subjectOrMessageIdText, Pageable pageable
    );
}
