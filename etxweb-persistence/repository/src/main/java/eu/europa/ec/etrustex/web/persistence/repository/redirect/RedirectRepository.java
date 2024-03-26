package eu.europa.ec.etrustex.web.persistence.repository.redirect;

import eu.europa.ec.etrustex.web.persistence.entity.redirect.Redirect;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.UUID;

public interface RedirectRepository extends JpaRepository<Redirect, UUID> {
    Collection<Redirect> findByGroupId(Long groupId);
    void delete(Redirect redirect);
}
