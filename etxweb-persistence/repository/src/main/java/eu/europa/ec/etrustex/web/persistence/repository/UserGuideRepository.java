package eu.europa.ec.etrustex.web.persistence.repository;

import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import eu.europa.ec.etrustex.web.persistence.entity.UserGuide;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserGuideRepository extends CrudRepository<UserGuide, Long> {
    Optional<UserGuide> findByBusinessIdAndRoleNameAndGroupType(Long businessOrRootId, RoleName roleName, GroupType groupType);

    Optional<UserGuide> findByBusinessIdentifierAndRoleNameAndGroupType(String businessOrRootId, RoleName roleName, GroupType groupType);

    List<UserGuide> findAllByBusinessId(Long businessOrRootId);

    void deleteByBusiness(Group business);
}
