package eu.europa.ec.etrustex.web.persistence.repository;

import eu.europa.ec.etrustex.web.persistence.entity.Alert;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface AlertRepository extends CrudRepository<Alert, Long> {
    @Query(value = "select a from Alert a " +
            "where a.isActive = true " +
            "and a.startDate <= :date and (a.endDate is null or a.endDate >= :date) " +
            "and (a.group.identifier = 'ROOT' or a.group.id = :groupId )")
    List<Alert> findActiveByGroupIdAndDate(Long groupId, Date date);

    Alert findFirstByAuditingEntityModifiedByOrderByAuditingEntityModifiedDateDesc(String modifiedBy);

    void deleteByGroup(Group group);

    Optional<Alert> findByGroupId(Long id);
}
