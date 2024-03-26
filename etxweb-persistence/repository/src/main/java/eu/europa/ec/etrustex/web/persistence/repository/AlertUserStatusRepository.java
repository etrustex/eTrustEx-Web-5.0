package eu.europa.ec.etrustex.web.persistence.repository;

import eu.europa.ec.etrustex.web.persistence.entity.AlertUserStatus;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlertUserStatusRepository extends CrudRepository<AlertUserStatus, Long> {


    Optional<AlertUserStatus> findByUserIdAndAlertIdAndGroupId(Long userId, Long alertId, Long businessId);
    void deleteByGroupId(Long groupId);
    void deleteByUser(User user);
}
