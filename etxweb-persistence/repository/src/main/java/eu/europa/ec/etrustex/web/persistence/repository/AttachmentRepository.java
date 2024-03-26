package eu.europa.ec.etrustex.web.persistence.repository;

import eu.europa.ec.etrustex.web.persistence.entity.Attachment;
import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttachmentRepository extends CrudRepository<Attachment, Long> {

    Optional<Attachment> findById(Long id);

    List<Attachment> findByIdIn(List<Long> ids);

    List<Attachment> findByMessage(Message message);

    List<Attachment> findByMessageId(Long messageId);

    void deleteByMessageIdAndServerStorePathIsNull(Long messageId);

    List<Attachment> findByClientReferenceAndMessageId(String clientReference, Long messageId);

    List<Attachment> findByMessageIdAndIdNotIn(Long messageId, Collection<Long> attachmentIds);

    List<Attachment> findByClientReferenceAndMessageSenderGroupAndServerStorePathIsNotNull(String clientReference, Group senderGroup);

    boolean existsByServerStorePath(String serverStorePath);

    void deleteByMessage(Message message);

    List<Attachment> findByMessageStatusIsNull();
}
