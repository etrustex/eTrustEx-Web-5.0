package eu.europa.ec.etrustex.web.service.jobs.certificate.batch;

import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.service.EncryptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateCertificateMessageProcessor implements ItemProcessor<Message, Message> {
    private final EncryptionService encryptionService;

    @Override
    public Message process(@NonNull final Message message) {
        try {
            message.setSymmetricKey(encryptionService.encryptWithNewCertificate(message.getSymmetricKey()));
            message.setProcessed(true);
        } catch (Exception e) {
            log.error("Error updating encrypting symmetric key with new certificate for Message: " + message, e);
        }

        return message;
    }
}
