package eu.europa.ec.etrustex.web.service.api;

import eu.europa.ec.etrustex.web.persistence.entity.Attachment;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import org.springframework.core.io.InputStreamResource;

public interface ApiAttachmentService {
    Attachment save(Long messageId, String clientReference);
    Attachment findById(Long attachmentId);

    InputStreamResource getResource(Long attachmentId);

    byte[] appendChunkAndChecksum(byte[] content, Long attachmentId, Group sender);
}
