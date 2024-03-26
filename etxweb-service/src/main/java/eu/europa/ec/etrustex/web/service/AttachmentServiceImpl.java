package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.persistence.entity.Attachment;
import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummary;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.repository.AttachmentRepository;
import eu.europa.ec.etrustex.web.persistence.repository.MessageRepository;
import eu.europa.ec.etrustex.web.persistence.repository.MessageSummaryRepository;
import eu.europa.ec.etrustex.web.util.crypto.Aes;
import eu.europa.ec.etrustex.web.util.crypto.Md;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.spec.SecretKeySpec;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {
    public static final String CANNOT_FIND_ATTACHMENT_WITH_ID_S = "Cannot find attachment with id %s";
    private final MessageRepository messageRepository;
    private final MessageSummaryRepository messageSummaryRepository;
    private final AttachmentRepository attachmentRepository;
    private final StorageService storageService;
    private final EncryptionService encryptionService;

    @Override
    @Transactional
    public Attachment save(Long messageId) {
        return this.save(messageId, null);
    }

    @Override
    @Transactional
    public Attachment save(Long messageId, String clientReference) {
        return messageRepository.findById(messageId)
                .map(message -> this.save(message, clientReference))
                .orElseThrow(() -> new EtxWebException(String.format("Cannot save the attachment for message %s. Message not found", messageId)));
    }

    @Override
    @Transactional
    public Attachment save(Message message) {
        return this.save(message, null);
    }

    @Override
    @Transactional
    public Attachment save(Message message, String clientReference) {
        log.trace("about to save attachment for message {}", message.getId());
        Attachment savedAttachment = attachmentRepository.save(Attachment.builder()
                .message(message)
                .clientReference(clientReference)
                .build());
        log.trace("Attachment successfully saved {}", savedAttachment);
        return savedAttachment;
    }

    @Override
    @Transactional
    public Attachment save(Attachment attachment) {
        return attachmentRepository.save(attachment);
    }

    @Override
    @Transactional(readOnly = true)
    public Attachment findById(Long attachmentId) {
        return attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new EtxWebException(String.format(CANNOT_FIND_ATTACHMENT_WITH_ID_S, attachmentId)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Attachment> findOptionalById(Long attachmentId) {
        Optional<Attachment> response = attachmentRepository.findById(attachmentId);
        if (!response.isPresent()) {
            log.trace("Cannot retrieve attachment with id {}", attachmentId);
        }
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Attachment> findByClientReferenceAndMessageId(String clientReference, Long messageId) {
        return attachmentRepository.findByClientReferenceAndMessageId(clientReference, messageId);
    }

    @Override
    @Transactional
    public byte[] appendChunkAndChecksum(byte[] content, Long attachmentId, Group sender) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new EtxWebException(String.format(CANNOT_FIND_ATTACHMENT_WITH_ID_S, attachmentId)));

        return appendChunkAndChecksum(content, attachment, sender);
    }

    @Override
    @Transactional
    public byte[] appendChunkAndChecksum(byte[] content, Attachment attachment, Group sender) {
        appendChunk(content, attachment, sender);

        return Md.getSha512MdInstance().digest(content);
    }

    @Override
    @Transactional
    public void appendChunk(byte[] content, Attachment attachment, Group sender) {
        Path target;
        if (StringUtils.isEmpty(attachment.getServerStorePath())) {
            attachment.setServerStorePath(createRelativePath(sender, attachment.getId()));
            save(attachment);
        }

        target = storageService.getAbsolutePath(attachment.getServerStorePath());

        storageService.appendChunkToFile(content, target);
    }

    @Override
    @Transactional(readOnly = true)
    public InputStreamResource getResource(Long attachmentId) {
        Attachment attachment = findById(attachmentId);
        Path storePath = getStorePath(attachment);

        return storageService.loadFile(storePath);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] getResourceDecrypted(Long attachmentId, final Long recipientId) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new EtxWebException(String.format("Cannot find attachment with id %s.", attachmentId)));
        Message message = attachment.getMessage();
        Long messageId = message.getId();
        MessageSummary messageSummary = messageSummaryRepository.findByMessageIdAndRecipientId(messageId, recipientId)
                .orElseThrow(() -> new EtxWebException(String.format("Cannot find MessageSummary for messageId: %s and recipientId: %s.", messageId, recipientId)));
        byte[] decryptedSymmetricKey = encryptionService.decryptWithServerPrivateKey(messageSummary.getSymmetricKey().getRandomBits());
        SecretKeySpec key = new SecretKeySpec(decryptedSymmetricKey, "AES");

        try {
            return Aes.gcmDecryptFile(key, storageService.getAbsolutePath(attachment.getServerStorePath()));
        } catch (IOException e) {
            throw new EtxWebException("Error decrypting attachment", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Attachment> findByMessage(Message message) {
        return attachmentRepository.findByMessage(message);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Attachment> findByMessageId(Long messageId) {
        return attachmentRepository.findByMessageId(messageId);
    }

    @Override
    public void deleteAll(List<Attachment> attachments) {
        attachmentRepository.deleteAll(attachments);
        attachments.forEach(attachment -> this.deleteFileIfNotReferenced(attachment.getServerStorePath()));
    }

    @Override
    public void delete(List<Long> attachmentsIds) {
        List<Attachment> attachments = attachmentRepository.findByIdIn(attachmentsIds);
        deleteAll(attachments);
    }

    @Override
    @Transactional
    public void delete(Long attachmentId) {
        attachmentRepository.findById(attachmentId).ifPresent(this::delete);
    }

    @Override
    @Transactional
    public void deleteAttachmentsIfEmptyStorePath(Long messageId) {
        attachmentRepository.deleteByMessageIdAndServerStorePathIsNull(messageId);
    }

    @Override
    public void delete(@NotNull Attachment attachment) {
        attachmentRepository.delete(attachment);
        this.deleteFileIfNotReferenced(attachment.getServerStorePath());
    }

    @Override
    public Optional<Attachment> findByClientReferenceAndSenderId(String clientReference, Group senderGroup) {
        return attachmentRepository.findByClientReferenceAndMessageSenderGroupAndServerStorePathIsNotNull(clientReference, senderGroup).stream().findFirst();
    }

    @Override
    public void deleteFileIfNotReferenced(String serverStorePath) {
        if (StringUtils.isNotBlank(serverStorePath) && !attachmentRepository.existsByServerStorePath(serverStorePath)) {
            storageService.delete(storageService.getAbsolutePath(serverStorePath));
        }
    }

    @Override
    public boolean fileExists(Attachment attachment) {
        return Files.exists(storageService.getAbsolutePath(attachment.getServerStorePath()));
    }

    @Override
    public Path getStorePath(String storePath) {
        return storageService.getFileUploadDir().resolve(storePath);
    }

    private Path getStorePath(Attachment attachment) {
        return storageService.getFileUploadDir().resolve(attachment.getServerStorePath());
    }

    private String createRelativePath(Group sender, Long attachmentId) {
        return sender.getParent().getIdentifier() + "/" + sender.getIdentifier() + "/" + attachmentId;
    }

}
