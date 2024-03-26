package eu.europa.ec.etrustex.web.persistence.repository.redirect;

import eu.europa.ec.etrustex.web.persistence.entity.redirect.MessageRedirect;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRedirectRepository<T extends MessageRedirect> extends CrudRepository<T, UUID> {
    Optional<T> findFirstByMessageId(long messageId);
    List<T> findByMessageId(long messageId);
    void deleteByMessageId(long messageId);
}
