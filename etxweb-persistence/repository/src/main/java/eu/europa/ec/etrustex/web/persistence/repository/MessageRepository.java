package eu.europa.ec.etrustex.web.persistence.repository;

import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.util.exchange.model.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    Optional<Message> findByIdAndSenderGroup(Long id, Group sender);

    @EntityGraph(attributePaths = {"messageSummaries", "senderGroup", "draftRecipients"})
    Optional<Message> findById(Long messageId);

    @EntityGraph(attributePaths = {"messageSummaries", "senderGroup", "draftRecipients"})
    Optional<Message> findByIdAndSenderGroupId(Long messageId, Long senderId);

    Page<Message> findBySenderGroupAndStatusNotAndSentOnIsNotNull(Group sender, Status status, Pageable pageable);

    @Query(value = "SELECT m1 FROM Message m1 WHERE m1.id in (SELECT distinct(m.id) from Message m " +
            "LEFT JOIN m.messageUserStatuses us ON us.user = :user " +
            "LEFT JOIN MessageSummary ms on ms.message = m " +
            "LEFT JOIN Group g on ms.recipient = g or g MEMBER OF m.draftRecipients " +
            "where m.senderGroup = :sender and m.status <> :status and (m.status = 'DRAFT' or ms.isValidPublicKey = true) " +
            "and (lower(m.subject) LIKE lower(concat('%', :subjectOrRecipient,'%')) " +
            "       or lower(g.name) LIKE lower(concat('%', :subjectOrRecipient,'%'))" +
            "       or lower(g.identifier) LIKE lower(concat('%', :subjectOrRecipient,'%'))" +
            ") and (:unread = false or us IS NULL) " +
            "and (COALESCE(m.sentOn, m.auditingEntity.modifiedDate) >= :startDate) " +
            "and (COALESCE(m.sentOn, m.auditingEntity.modifiedDate) <= :endDate))",
            countQuery = "SELECT count(distinct m.id) from Message m " +
                    "LEFT JOIN m.messageUserStatuses us ON us.user = :user " +
                    "LEFT JOIN MessageSummary ms on ms.message = m " +
                    "LEFT JOIN Group g on ms.recipient = g or g MEMBER OF m.draftRecipients " +
                    "where m.senderGroup = :sender and m.status <> :status and (m.status = 'DRAFT' or ms.isValidPublicKey = true) " +
                    "and (lower(m.subject) LIKE lower(concat('%', :subjectOrRecipient,'%')) " +
                    "       or lower(g.name) LIKE lower(concat('%', :subjectOrRecipient,'%'))" +
                    "       or lower(g.identifier) LIKE lower(concat('%', :subjectOrRecipient,'%'))" +
                    ") and (:unread = false or us IS NULL) " +
                    "and (COALESCE(m.sentOn, m.auditingEntity.modifiedDate) >= :startDate) " +
                    "and (COALESCE(m.sentOn, m.auditingEntity.modifiedDate) <= :endDate)")
    Page<Message> findBySenderGroupAndStatusNotAndSubjectContainingIgnoreCaseOrSenderGroupIdentifierContainingIgnoreCase(
            Group sender, User user, Status status, String subjectOrRecipient, Boolean unread, Date startDate, Date endDate, Pageable pageable
    );

    @Query(value = "SELECT m1 FROM Message m1 WHERE m1.id in (SELECT m.id from Message m " +
            "LEFT JOIN m.messageUserStatuses us ON us.user = :user " +
            "LEFT JOIN MessageSummary ms on ms.message = m " +
            "LEFT JOIN Group g on ms.recipient = g or g MEMBER OF m.draftRecipients " +
            "where m.senderGroup = :sender and m.status = :status and (m.status = 'DRAFT' or ms.isValidPublicKey = true) " +
            "and (lower(m.subject) LIKE lower(concat('%', :subjectOrRecipient,'%')) " +
            "       or lower(g.name) LIKE lower(concat('%', :subjectOrRecipient,'%'))" +
            "       or lower(g.identifier) LIKE lower(concat('%', :subjectOrRecipient,'%'))" +
            ") and (:unread = false or us IS NULL) " +
            "and (COALESCE(m.sentOn, m.auditingEntity.modifiedDate) >= :startDate) " +
            "and (COALESCE(m.sentOn, m.auditingEntity.modifiedDate) <= :endDate))",
            countQuery = "SELECT count(distinct m.id) from Message m " +
                    "LEFT JOIN m.messageUserStatuses us ON us.user = :user " +
                    "LEFT JOIN MessageSummary ms on ms.message = m " +
                    "LEFT JOIN Group g on ms.recipient = g or g MEMBER OF m.draftRecipients " +
                    "where m.senderGroup = :sender and m.status = :status and (m.status = 'DRAFT' or ms.isValidPublicKey = true) " +
                    "and (lower(m.subject) LIKE lower(concat('%', :subjectOrRecipient,'%')) " +
                    "       or lower(g.name) LIKE lower(concat('%', :subjectOrRecipient,'%'))" +
                    "       or lower(g.identifier) LIKE lower(concat('%', :subjectOrRecipient,'%'))" +
                    ") and (:unread = false or us IS NULL) " +
                    "and (COALESCE(m.sentOn, m.auditingEntity.modifiedDate) >= :startDate) " +
                    "and (COALESCE(m.sentOn, m.auditingEntity.modifiedDate) <= :endDate)")
    Page<Message> findBySenderGroupAndStatusAndSubjectContainingIgnoreCaseOrSenderGroupIdentifierContainingIgnoreCase(
            Group sender, User user, Status status, String subjectOrRecipient, Boolean unread, Date startDate, Date endDate, Pageable pageable
    );

    @Query(value = "SELECT m1 FROM Message m1 WHERE m1.id in (SELECT m.id from Message m " +
            "LEFT JOIN MessageSummary ms on ms.message = m " +
            "LEFT JOIN Group g on ms.recipient = g or g MEMBER OF m.draftRecipients " +
            "where m.senderGroup = :sender " +
            "and m.status = 'DRAFT' " +
            "and (lower(m.subject) LIKE lower(concat('%', :subjectOrRecipient,'%')) " +
            "       or lower(g.name) LIKE lower(concat('%', :subjectOrRecipient,'%')) " +
            "       or lower(g.identifier) LIKE lower(concat('%', :subjectOrRecipient,'%'))" +
            ") " +
            "and (COALESCE(m.sentOn, m.auditingEntity.modifiedDate) >= :startDate) " +
            "and (COALESCE(m.sentOn, m.auditingEntity.modifiedDate) <= :endDate))",
            countQuery = "SELECT count(distinct m.id) from Message m " +
                    "LEFT JOIN MessageSummary ms on ms.message = m " +
                    "LEFT JOIN Group g on ms.recipient = g or g MEMBER OF m.draftRecipients " +
                    "where m.senderGroup = :sender " +
                    "and m.status = 'DRAFT' " +
                    "and (lower(m.subject) LIKE lower(concat('%', :subjectOrRecipient,'%')) " +
                    "       or lower(g.name) LIKE lower(concat('%', :subjectOrRecipient,'%')) " +
                    "       or lower(g.identifier) LIKE lower(concat('%', :subjectOrRecipient,'%'))" +
                    ") " +
                    "and (COALESCE(m.sentOn, m.auditingEntity.modifiedDate) >= :startDate) " +
                    "and (COALESCE(m.sentOn, m.auditingEntity.modifiedDate) <= :endDate)")
    Page<Message> findDraftMessages(
            Group sender, String subjectOrRecipient, Date startDate, Date endDate, Pageable pageable
    );

    List<Message> findBySenderGroupId(Long senderGroupId);

    int countBySenderGroupIdAndSentOnGreaterThan(Long senderGroupId, Date sentOn);

    List<Message> findBySenderGroupAndSentOnBeforeAndSentOnNotNull(Group group, Date expiryDate);

    List<Message> findBySenderGroupAndSentOnBefore(Group group, Date expiryDate);

    List<Message> findBySenderGroupAndSentOnBetween(Group group, Date sentOnStart, Date sentOnEnd);

    Stream<Message> findBySenderGroupAndAttachmentTotalNumberIsNull(Group group);

    Page<Message> findByStatusIsNullAndAuditingEntityModifiedDateLessThan(Date modifiedDate, Pageable pageable);

    @Query("SELECT COUNT(distinct m.id) " +
            "FROM Message m LEFT JOIN m.messageUserStatuses us ON us.user = :user " +
            "WHERE m.subject IS NOT NULL " +
            "AND m.senderGroup.id = :senderGroupId " +
            "AND m.status <> 'DRAFT' " +
            "AND us IS NULL")
    int countUnread(Long senderGroupId, User user);

    void deleteById(Long id);

    @Modifying
    @Query("update Message m set m.updatedWithNewCertificate = :value")
    void setUpdatedWithNewCertificate(boolean value);

    @Modifying
    @Query("update Message m set m.updatedWithNewCertificate = true where m.processed = true")
    void setUpdatedWithNewCertificateToTrueForProcessedMessages();

    @Modifying
    @Query("update Message m set m.processed = :value")
    void setProcessed(boolean value);

    long countBySymmetricKeyIsNotNullAndIvIsNotNullAndUpdatedWithNewCertificateIsFalse();

}
