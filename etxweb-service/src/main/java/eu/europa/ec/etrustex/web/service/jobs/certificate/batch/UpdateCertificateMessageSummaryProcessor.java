package eu.europa.ec.etrustex.web.service.jobs.certificate.batch;

import eu.europa.ec.etrustex.web.util.exchange.model.keys.SymmetricKey;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummary;
import eu.europa.ec.etrustex.web.service.EncryptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateCertificateMessageSummaryProcessor implements ItemProcessor<MessageSummary, MessageSummary> {
    private final EncryptionService encryptionService;

    @Override
    public MessageSummary process(@NonNull final MessageSummary messageSummary) {
        try {
            if (messageSummary.getSymmetricKey() != null
                    && StringUtils.isNotBlank(messageSummary.getSymmetricKey().getRandomBits())
                    && ! Objects.equals(SymmetricKey.EncryptionMethod.RSA_OAEP_E2E, messageSummary.getSymmetricKey().getEncryptionMethod())) {
                messageSummary.setSymmetricKey(encryptionService.encryptWithNewCertificate(messageSummary.getSymmetricKey()));
                messageSummary.setProcessed(true);
            }
        } catch (Exception e) {
            log.error(String.format("Error updating encrypting symmetric key with new certificate for MessageSummary with id: %s and recipient id: %s",
                            messageSummary.getMessage().getId(), messageSummary.getRecipient().getId()),
                    e);
        }

        return messageSummary;
    }
}
