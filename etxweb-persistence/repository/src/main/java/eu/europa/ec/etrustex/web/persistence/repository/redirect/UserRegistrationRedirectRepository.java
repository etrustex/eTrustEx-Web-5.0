package eu.europa.ec.etrustex.web.persistence.repository.redirect;

import eu.europa.ec.etrustex.web.persistence.entity.redirect.UserRegistrationRedirect;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface UserRegistrationRedirectRepository extends CrudRepository<UserRegistrationRedirect, UUID> {
    List<UserRegistrationRedirect> findByEmailAddress(String emailAddress);
    List<UserRegistrationRedirect> findByEmailAddressIgnoreCaseAndGroupId(String emailAddress, Long groupId);
    void deleteByGroupIdAndEmailAddress(Long groupId, String emailAddress);
    void deleteByAuditingEntityCreatedDateBefore(Date expirationDate);
    void deleteByGroupId(Long groupId);
}
