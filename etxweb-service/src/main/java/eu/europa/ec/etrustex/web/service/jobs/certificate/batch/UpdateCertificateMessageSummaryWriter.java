package eu.europa.ec.etrustex.web.service.jobs.certificate.batch;

import eu.europa.ec.etrustex.web.persistence.entity.MessageSummary;
import eu.europa.ec.etrustex.web.persistence.repository.MessageSummaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateCertificateMessageSummaryWriter implements ItemWriter<MessageSummary> {
    private final MessageSummaryRepository messageSummaryRepository;

    @Override
    public void write(List<? extends MessageSummary> items) {
        log.info("Writing " + items.size() + " items");

        messageSummaryRepository.saveAll(items);
    }
}
