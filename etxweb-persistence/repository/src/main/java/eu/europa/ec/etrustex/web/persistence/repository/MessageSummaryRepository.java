package eu.europa.ec.etrustex.web.persistence.repository;

import eu.europa.ec.etrustex.web.exchange.model.MessageSummaryListItem;
import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummary;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummaryId;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.util.cia.Confidentiality;
import eu.europa.ec.etrustex.web.util.exchange.model.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface MessageSummaryRepository extends JpaRepository<MessageSummary, MessageSummaryId> {
    Optional<MessageSummary> findByMessageIdAndRecipientId(Long messageId, Long recipientId);

    List<MessageSummary> findByRecipientIdAndMessageIdIn(Long recipientId, List<Long> messageIds);

    Optional<MessageSummary> findByMessageIdAndRecipientIdentifier(Long messageId, String recipientIdentifier);

    Optional<MessageSummary> findByMessageIdAndRecipientIdentifierAndRecipientParentIdentifier(Long messageId, String recipientIdentifier, String recipientParentIdentifier);

    Optional<MessageSummary> findByClientReferenceAndRecipientIdentifier(String clientReference, String recipientIdentifier);

    boolean existsByClientReferenceAndRecipientId(String clientReference, Long recipientId);


    int countByMessage(Message message);

    List<MessageSummary> findByRecipientAndMessageSentOnBefore(Group recipient, Date expiryDate);
    List<MessageSummary> findByRecipient(Group recipient);

    Stream<MessageSummary> findByRecipientAndMessageSentOnBetweenAndIsActiveTrue(Group recipient, Date from, Date to);

    @Query(value = "SELECT ms1 FROM MessageSummary ms1 WHERE ms1.recipient.id = :recipientId AND ms1.message.id IN (" +
            "SELECT ms.message.id FROM MessageSummary ms " +
            "   LEFT JOIN ms.messageSummaryUserStatuses us ON us.user = :user " +
            "WHERE ms.recipient.id = :recipientId and ms.isValidPublicKey = true and ms.isActive = true " +
            "and (lower(ms.message.subject) LIKE lower(concat('%', :subjectOrSender,'%'))" +
            "       or lower(ms.message.senderUserName) LIKE lower(concat('%', :subjectOrSender,'%'))" +
            "       or lower(ms.message.senderGroup.name) LIKE lower(concat('%', :subjectOrSender,'%'))" +
            "       or lower(ms.message.senderGroup.identifier) LIKE lower(concat('%', :subjectOrSender,'%'))" +
            ") and (:unread = false or us IS NULL) " +
            "and (:startDate is null or ms.message.sentOn >= :startDate) " +
            "and (:endDate is null or ms.message.sentOn <= :endDate))",
            countQuery = "SELECT count(distinct(ms.message.id)) " +
                    "FROM MessageSummary ms " +
                    "   LEFT JOIN ms.messageSummaryUserStatuses us ON us.user = :user " +
                    "WHERE ms.recipient.id = :recipientId and ms.isValidPublicKey = true and ms.isActive = true " +
                    "and (lower(ms.message.subject) LIKE lower(concat('%', :subjectOrSender,'%')) " +
                    "   or lower(ms.message.senderUserName) LIKE lower(concat('%', :subjectOrSender,'%')) " +
                    "   or lower(ms.message.senderGroup.name) LIKE lower(concat('%', :subjectOrSender,'%'))" +
                    "   or lower(ms.message.senderGroup.identifier) LIKE lower(concat('%', :subjectOrSender,'%'))" +
                    ") and (:unread = false or us IS NULL) " +
                    "and (:startDate is null or ms.message.sentOn >= :startDate) " +
                    "and (:endDate is null or ms.message.sentOn <= :endDate)")
    Page<MessageSummary> findByRecipientGroupIdAndSubjectContainingIgnoreCaseOrMessageSenderGroupIdentifierContainingIgnoreCase(
            Long recipientId, User user, String subjectOrSender, Boolean unread, Date startDate, Date endDate, Pageable pageable
    );

    Page<MessageSummary> findByRecipientIdAndIsValidPublicKeyTrueAndIsActiveTrue(Long recipientId, Pageable pageable);

    List<MessageSummary> findByRecipientIdAndAndPublicKeyHashValue(Long recipientId, String publicKeyHashValue);

    List<MessageSummary> findByRecipientId(Long recipientGroupId);

    int countByRecipientIdAndMessageSentOnGreaterThan(Long recipientId, Date sentOn);

    Stream<MessageSummary> findByMessageIdAndIsActiveTrue(Long messageId);

    @Query("SELECT ms " +
            "FROM MessageSummary ms " +
            "WHERE ms.recipient.id = :recipientId " +
            "AND ms.isActive = true " +
            "AND ms.isValidPublicKey = true " +
            "AND ms.status = :status")
    Stream<MessageSummary> findUnreadMessageSummariesByRecipientAndStatus(@Param("recipientId") Long recipientId, @Param("status") Status status);

    @Query("SELECT COUNT(distinct ms.message) " +
            "FROM MessageSummary ms LEFT JOIN ms.messageSummaryUserStatuses us ON us.user = :user " +
            "WHERE ms.recipient.id = :recipientId " +
            "AND ms.message.sentOn <= :to " +
            "AND ms.isValidPublicKey = true " +
            "AND us IS NULL AND ms.isActive = true")
    int countUnread(@Param("to") Date to, @Param("recipientId") Long recipientId, @Param("user") User user);

    @Query("SELECT COUNT(distinct ms.message) " +
            "FROM MessageSummary ms LEFT JOIN ms.messageSummaryUserStatuses us ON us.user = :user " +
            "WHERE ms.recipient.id = :recipientId and ms.isActive = true " +
            "AND ms.isValidPublicKey = true " +
            "AND us IS NULL AND ms.isActive = true")
    int countUnread(@Param("recipientId") Long recipientId, @Param("user") User user);

    @Modifying
    @Query("update MessageSummary ms set ms.updatedWithNewCertificate = :value")
    void setUpdatedWithNewCertificate(boolean value);

    @Modifying
    @Query("update MessageSummary ms set ms.updatedWithNewCertificate = true where ms.processed = true")
    void setUpdatedWithNewCertificateToTrueForProcessedMessages();

    @Modifying
    @Query("update MessageSummary ms set ms.processed = :value")
    void setProcessed(boolean value);

    Stream<MessageSummary> findByConfidentialityAndSymmetricKeyIsNotNullAndUpdatedWithNewCertificateIsFalse(Confidentiality confidentiality);

    @Modifying
    @Query("update MessageSummary ms set ms.isValidPublicKey = false where ms.publicKeyHashValue = :hashValue " +
            "and ms.recipient.id = :entityId")
    void disableByPublicKeyHashValueAndBusinessIdAndEntityId(String hashValue, Long entityId);

    int countByPublicKeyHashValueAndRecipientIdAndIsValidPublicKeyIsTrue(String hashValue, Long entityId);

    @Query(value = "select distinct new eu.europa.ec.etrustex.web.exchange.model.MessageSummaryListItem(ms.message.id, ms.message.subject, ms.recipient.identifier, ms.auditingEntity.createdDate, ms.isActive) " +
            "from MessageSummary ms " +
            "where ms.recipient.parent.id = :businessId and ms.disabledByRetentionPolicy = false and ms.recipient.isActive = true and ms.recipient.isPendingDeletion = false " +
            "and (lower(ms.message.subject) LIKE lower(concat('%', :messageIdOrSubject,'%')) or cast(ms.message.id as string) LIKE concat('%', :messageIdOrSubject,'%'))",
            countQuery = "SELECT count(distinct(ms.concatedId)) FROM MessageSummary ms " +
                    "where ms.recipient.parent.id = :businessId and ms.disabledByRetentionPolicy = false and ms.recipient.isActive = true and ms.recipient.isPendingDeletion = false " +
                    "and (lower(ms.message.subject) LIKE lower(concat('%', :messageIdOrSubject,'%')) or cast(ms.message.id as string) LIKE concat('%', :messageIdOrSubject,'%'))")
    Page<MessageSummaryListItem> findMessageSummaryListItemsByBusinessIdAndMessageIdOrSubject(@Param("businessId") Long businessId, @Param("messageIdOrSubject") String messageIdOrSubject, @Param("pageable") Pageable pageable);

    Optional<MessageSummary> findByMessageIdAndRecipientIdAndIsActiveIsTrue(Long messageId, Long recipientId);

    List<MessageSummary> findByRecipientAndMessageSentOnBetween(Group group, Date startDate, Date endDate);
}
