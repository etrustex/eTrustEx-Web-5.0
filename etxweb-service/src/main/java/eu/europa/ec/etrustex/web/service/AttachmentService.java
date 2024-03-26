package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.persistence.entity.Attachment;
import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import org.springframework.core.io.InputStreamResource;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public interface AttachmentService {
    Attachment save(Long messageId);

    Attachment save(Long messageId, String clientReference);

    Attachment save(Message message);

    Attachment save(Message message, String clientReference);

    Attachment save(Attachment attachment);

    Attachment findById(Long attachmentId);

    Optional<Attachment> findOptionalById(Long attachmentId);

    List<Attachment> findByClientReferenceAndMessageId(String clientReference, Long messageId);

    void deleteAll(List<Attachment> attachments);

    void delete(List<Long> attachmentsIds);

    void delete(Long attachmentId);

    @Transactional
    void deleteAttachmentsIfEmptyStorePath(Long messageId);

    void delete(@NotNull Attachment attachment);

    byte[] appendChunkAndChecksum(byte[] content, Long attachmentId, Group sender);

    @Transactional
    byte[] appendChunkAndChecksum(byte[] content, Attachment attachment, Group sender);

    @Transactional
    void appendChunk(byte[] content, Attachment attachment, Group sender);

    @Transactional(readOnly = true)
    InputStreamResource getResource(Long attachmentId);

    @Transactional(readOnly = true)
    byte[] getResourceDecrypted(Long attachmentId, Long recipientId);

    List<Attachment> findByMessage(Message message);

    List<Attachment> findByMessageId(Long messageId);

    Optional<Attachment> findByClientReferenceAndSenderId(String clientReference, Group senderGroup);

    void deleteFileIfNotReferenced(String serverStorePath);

    boolean fileExists(Attachment attachment);

    Path getStorePath(String storePath);
}
