package eu.europa.ec.etrustex.web.persistence.repository.security;

import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import eu.europa.ec.etrustex.web.persistence.entity.security.GrantedAuthority;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface GrantedAuthorityRepository extends CrudRepository<GrantedAuthority, Long> {
    Collection<GrantedAuthority> findByUserAndGroup(User user, Group group);
    Collection<GrantedAuthority> findByUserAndGroupId(User user, Long groupId);
    Collection<GrantedAuthority> findByUserEcasIdAndGroupId(String ecasId, Long groupId);
    Collection<GrantedAuthority> findByUser(User user);
    Collection<GrantedAuthority> findByUserEcasId(String ecasId);
    Stream<GrantedAuthority> findByGroupParent(Group group);
    boolean existsByUserEcasIdAndGroupIdAndRoleNameAndEnabledTrue(String ecasId, Long groupId, RoleName roleName);
    boolean existsByGroupIdAndRoleNameAndEnabledTrue(Long groupId, RoleName roleName);
    Optional<GrantedAuthority> findByUserEcasIdAndGroupIdAndRoleName(String ecasId, Long groupId, RoleName roleName);
    void deleteByUserEcasIdAndGroupId(String ecasId, Long groupId);
    void deleteByGroup(Group group);
    boolean existsByUserAndGroup(User user, Group group);
    boolean existsByGroup(Group group);

    @Modifying
    @Query("update GrantedAuthority ga set ga.enabled = :enabled where ga.group.id = :groupId")
    void updateEnabled(Long groupId, Boolean enabled);

    @Modifying
    @Query("update GrantedAuthority ga set ga.enabled = :enabled where ga.user.id = :userId and ga.group.id = :groupId")
    void updateEnabled(Long userId, Long groupId, Boolean enabled);

    GrantedAuthority findFirstByAuditingEntityModifiedByOrderByAuditingEntityModifiedDateDesc(String modifiedBy);
}
