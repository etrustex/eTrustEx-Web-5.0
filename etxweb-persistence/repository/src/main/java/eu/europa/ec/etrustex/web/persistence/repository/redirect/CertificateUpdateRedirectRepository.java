package eu.europa.ec.etrustex.web.persistence.repository.redirect;

import eu.europa.ec.etrustex.web.persistence.entity.redirect.CertificateUpdateRedirect;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

public interface CertificateUpdateRedirectRepository extends CrudRepository<CertificateUpdateRedirect, UUID> {
    void deleteByGroupIdAndAndUserId(Long groupId, Long userId);
    @Transactional
    void deleteByAuditingEntityCreatedDateBefore(Date expirationDate);

    boolean existsByGroupIdAndGroupIdentifierAndUserId(Long groupId, String identifier, Long userId);
}
