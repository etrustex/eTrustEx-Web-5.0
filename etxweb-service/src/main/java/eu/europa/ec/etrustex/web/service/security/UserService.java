package eu.europa.ec.etrustex.web.service.security;

import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import eu.europa.ec.etrustex.web.exchange.model.UserPreferencesSpec;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.service.dto.LdapUserDto;
import eu.europa.ec.etrustex.web.service.pagination.RestResponsePage;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;

import java.util.Optional;


public interface UserService {
    RestResponsePage<User> getUsersByRoleAndGroupId(RoleName role, @Nullable String groupId, Pageable pageable);

    User create(String ecasId, String name, String euLoginEmailAddress);

    void update(User user);

    User save(User user);

    User findByEcasId(String ecasId);

    Optional<User> findOptionalByEcasId(String ecasId);

    User findLastUpdatedUser(String actorUser);

    void delete(User user);

    LdapUserDto getEuLoginProfileFromLdap(String mailOrEcasId);
    LdapUserDto getEuLoginProfileFromLdapWithValidation(String mailOrEcasId);
    User updateUserPreferences(User user, UserPreferencesSpec userPreferencesSpec);
}
