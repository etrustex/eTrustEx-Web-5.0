package eu.europa.ec.etrustex.web.service.security;

import eu.europa.ec.etrustex.web.exchange.model.UserPreferencesSpec;
import eu.europa.ec.etrustex.web.persistence.entity.UserPreferences;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.persistence.repository.security.UserRepository;
import eu.europa.ec.etrustex.web.service.dto.LdapUserDto;
import eu.europa.ec.etrustex.web.service.pagination.RestResponsePage;
import eu.europa.ec.etrustex.web.service.validation.post_auth_validation.PostAuthValidated;
import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import java.util.List;
import java.util.Optional;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final LdapTemplate ldapTemplate;

    @Override
    @Transactional(readOnly = true)
    public RestResponsePage<User> getUsersByRoleAndGroupId(RoleName role, @Nullable String groupId, Pageable pageable) {

        Page<User> userPage;
        Pageable pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort().and(Sort.by(Sort.Order.asc("id"))));

        if (StringUtils.isBlank(groupId)) {
            userPage = userRepository.getUsersByRoleName(role, pageRequest);
        } else {
            userPage = userRepository.getUsersByRoleNameAndGroupId(role, groupId, pageRequest);
        }
        return new RestResponsePage<>(userPage);
    }

    @Override
    public User create(String ecasId, String name, String euLoginEmailAddress) {
        return userRepository.findByEcasIdIgnoreCase(ecasId).orElseGet(() -> userRepository.save(User.builder()
                .ecasId(ecasId.toLowerCase())
                .name(name)
                .euLoginEmailAddress(euLoginEmailAddress)
                .build()));

    }

    @Override
    public void update(User user) {
        userRepository.save(user);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User findByEcasId(String ecasId) {
        return userRepository.findByEcasIdIgnoreCase(ecasId).orElse(null);
    }

    @Override
    public Optional<User> findOptionalByEcasId(String ecasId) {
        return userRepository.findByEcasIdIgnoreCase(ecasId);
    }

    @Override
    public User findLastUpdatedUser(String actorUser) {
        return userRepository.findFirstByAuditingEntityModifiedByOrderByAuditingEntityModifiedDateDesc(actorUser);
    }

    @Override
    public void delete(User user) {
        userRepository.delete(user);
    }

    @Override
    public User updateUserPreferences(User user, UserPreferencesSpec userPreferencesSpec) {
        user.setUserPreferences(toUserPreferences(userPreferencesSpec));
        return userRepository.save(user);
    }

    @Override
    public LdapUserDto getEuLoginProfileFromLdap(String mailOrEcasId) {
        LdapQuery query = query().where("uid").is(mailOrEcasId).or("mail").is(mailOrEcasId);
        List<LdapUserDto> ldapUsers = ldapTemplate.search(query, new LDAPUserAttributesMapper());

        return (ldapUsers.size() == 1) ? ldapUsers.get(0) : null;
    }

    @Override
    @PostAuthValidated
    public LdapUserDto getEuLoginProfileFromLdapWithValidation(String mailOrEcasId) {
        return getEuLoginProfileFromLdap(mailOrEcasId.toLowerCase());
    }

    private static class LDAPUserAttributesMapper implements AttributesMapper<LdapUserDto> {
        @Override
        public LdapUserDto mapFromAttributes(Attributes attributes) throws javax.naming.NamingException {
            NamingEnumeration<? extends Attribute> enumeration = attributes.getAll();
            while (enumeration.hasMoreElements()) {
                Attribute attribute = enumeration.next();
                log.info("Attribute: " + attribute.getID() + ". - Value:" + attribute.get());
            }


            return LdapUserDto.builder()
                    .euLogin((String) attributes.get("uid").get())
                    .fullName((String) attributes.get("cn").get())
                    .euLoginEmail(attributes.get("mail") == null ? null : (String) attributes.get("mail").get())
                    .build();
        }
    }

    private UserPreferences toUserPreferences(UserPreferencesSpec userPreferencesSpec) {
        return UserPreferences.builder()
                .paginationSize(userPreferencesSpec.getPaginationSize())
                .build();
    }
}
