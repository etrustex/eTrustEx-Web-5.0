package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.persistence.entity.Message;
import org.springframework.data.domain.Page;

public interface MessageCleanupService {

    Page<Message> getCleanupPage();
    void cleanupMessage(Message message);
}
