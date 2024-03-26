package eu.europa.ec.etrustex.web.persistence.repository;

import eu.europa.ec.etrustex.web.exchange.model.UserListItem;
import eu.europa.ec.etrustex.web.persistence.entity.UserRegistrationRequest;
import eu.europa.ec.etrustex.web.persistence.entity.UserRegistrationRequestId;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRegistrationRequestRepository extends CrudRepository<UserRegistrationRequest, UserRegistrationRequestId> {

    boolean existsByUserEcasIdAndGroupId(String ecasId, Long groupId);
    Page<UserListItem> findByGroupId(Long groupId, String filterValue, PageRequest pageRequest);
    void deleteByUserIdAndGroupId(Long userId, Long groupId);
    Optional<UserRegistrationRequest> findByUserEcasIdAndGroupId(String ecasId, Long groupId);
    void deleteByGroup(Group group);
}
