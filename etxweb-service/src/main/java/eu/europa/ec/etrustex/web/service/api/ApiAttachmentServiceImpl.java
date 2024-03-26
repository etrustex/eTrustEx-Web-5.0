package eu.europa.ec.etrustex.web.service.api;

import eu.europa.ec.etrustex.web.persistence.entity.Attachment;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.service.AttachmentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class ApiAttachmentServiceImpl implements ApiAttachmentService {
    private final AttachmentService attachmentService;

    @Override
    @Transactional
    public Attachment save(Long messageId, String clientReference) {
        return attachmentService.save(messageId, clientReference);
    }

    @Override
    @Transactional(readOnly = true)
    public Attachment findById(Long attachmentId) {
        return attachmentService.findById(attachmentId);
    }

    @Override
    @Transactional(readOnly = true)
    public InputStreamResource getResource(Long attachmentId) {
        return attachmentService.getResource(attachmentId);
    }

    @Override
    @Transactional
    public byte[] appendChunkAndChecksum(byte[] content, Long attachmentId, Group sender) {
        return attachmentService.appendChunkAndChecksum(content, attachmentId, sender);
    }
}
