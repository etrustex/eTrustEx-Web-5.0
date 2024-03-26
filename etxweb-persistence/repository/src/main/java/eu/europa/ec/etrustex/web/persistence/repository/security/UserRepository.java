package eu.europa.ec.etrustex.web.persistence.repository.security;

import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    @EntityGraph(attributePaths = {"userProfiles"})
    Optional<User> findByEcasIdIgnoreCase(String ecasID);

    @Query("select distinct u from User u join GrantedAuthority ga on u.id = ga.user.id where ga.role.name = :roleName")
    Page<User> getUsersByRoleName(@Param("roleName") RoleName roleName, Pageable pageable);

    @Query("select distinct u from User u join GrantedAuthority ga on u.id = ga.user.id where ga.role.name = :roleName and ga.group.identifier = :groupId")
    Page<User> getUsersByRoleNameAndGroupId(@Param("roleName") RoleName roleName, @Param("groupId") String groupId, Pageable pageable);

    User findFirstByAuditingEntityModifiedByOrderByAuditingEntityModifiedDateDesc(String modifiedBy);
}
