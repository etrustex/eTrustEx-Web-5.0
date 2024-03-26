package eu.europa.ec.etrustex.web.service.jobs.certificate.batch;

import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateCertificateMessageWriter implements ItemWriter<Message> {
    private final MessageRepository messageRepository;

    @Override
    public void write(List<? extends Message> items) {
        log.info("Writing " + items.size() + " items");

        messageRepository.saveAll(items);
    }
}
