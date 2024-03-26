package eu.europa.ec.etrustex.web.service.security;

import eu.europa.ec.etrustex.web.persistence.entity.UserProfile;
import eu.europa.ec.etrustex.web.persistence.entity.security.GrantedAuthority;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.service.validation.model.GrantedAuthoritySpec;
import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;

import java.util.Collection;
import java.util.List;

public interface GrantedAuthorityService {
    GrantedAuthority create(User user, RoleName roleName, Long groupId);
    List<GrantedAuthority> create(User user, List<RoleName> roleNames, Long groupId);

    GrantedAuthority create(GrantedAuthoritySpec grantedAuthoritySpec);

    void delete(GrantedAuthoritySpec grantedAuthoritySpec);

    void deleteByUserEcasIdAndGroupID(String ecasId, Long groupId);

    Collection<GrantedAuthority> findByUserEcasIdAndGroupId(String ecasId, Long groupId);

    boolean existsByUserAndGroup(User user, Group group);

    void updateGroup(GrantedAuthoritySpec grantedAuthoritySpec, User currentUser);

    void updateGroups(List<GrantedAuthoritySpec> grantedAuthoritySpecs, User currentUser);

    boolean isUserActiveWithinGroup(UserProfile userProfile);

    boolean existsByGroup(Group group);

    GrantedAuthority findLastUpdatedGrantedAuthority(String user);
}
