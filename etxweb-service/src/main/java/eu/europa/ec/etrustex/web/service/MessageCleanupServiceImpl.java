package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.persistence.entity.Attachment;
import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.repository.AttachmentRepository;
import eu.europa.ec.etrustex.web.persistence.repository.MessageRepository;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageCleanupServiceImpl implements MessageCleanupService {

    private final MessageRepository messageRepository;
    private final AttachmentRepository attachmentRepository;
    private final AttachmentService attachmentService;

    @Override
    public Page<Message> getCleanupPage() {
        Date deleteBefore = Date.from(Instant.now().minus(7, ChronoUnit.DAYS));
        return messageRepository.findByStatusIsNullAndAuditingEntityModifiedDateLessThan(deleteBefore, PageRequest.of(0, 100));
    }

    @Override
    public void cleanupMessage(Message message) {
        try {
            List<Attachment> attachmentList = attachmentService.findByMessage(message);

            attachmentRepository.deleteAll(attachmentList);
            messageRepository.delete(message);

            attachmentList.forEach(attachment -> attachmentService.deleteFileIfNotReferenced(attachment.getServerStorePath()));

        } catch (Exception e) {
            throw new EtxWebException(e);
        }
    }
}
