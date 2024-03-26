package eu.europa.ec.etrustex.web.service.security;

import eu.europa.ec.etrustex.web.persistence.entity.UserProfile;
import eu.europa.ec.etrustex.web.persistence.entity.security.GrantedAuthority;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.Role;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.persistence.repository.security.GrantedAuthorityRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.UserRepository;
import eu.europa.ec.etrustex.web.service.validation.model.GrantedAuthoritySpec;
import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Service
@RequiredArgsConstructor
public class GrantedAuthorityServiceImpl implements GrantedAuthorityService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final GroupService groupService;
    private final GrantedAuthorityRepository grantedAuthorityRepository;

    @Override
    public GrantedAuthority create(User user, RoleName roleName, Long groupId) {
        /*
        Changes to this method signature should update #NotificationAspect.notifyOfUserConfigured
         */
        return grantedAuthorityRepository.save(toGrantedAuthority(user, roleName, groupId));
    }

    @Override
    public List<GrantedAuthority> create(User user, List<RoleName> roleNames, Long groupId) {
        /*
        Changes to this method signature should update #NotificationAspect.notifyOfUserConfigured
         */
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        roleNames.forEach(roleName -> grantedAuthorities.add(grantedAuthorityRepository.save(toGrantedAuthority(user, roleName, groupId))));
        return grantedAuthorities;
    }

    @Override
    public GrantedAuthority create(GrantedAuthoritySpec grantedAuthoritySpec) {
        User user = userRepository.findByEcasIdIgnoreCase(grantedAuthoritySpec.getUserName().toLowerCase()).orElse(null);
        GrantedAuthority grantedAuthority = create(user, grantedAuthoritySpec.getRoleName(), grantedAuthoritySpec.getGroupId());

        grantedAuthority.setEnabled(grantedAuthoritySpec.isEnabled());
        return grantedAuthorityRepository.save(grantedAuthority);
    }

    @Override
    @Transactional
    public void delete(GrantedAuthoritySpec grantedAuthoritySpec) {
        this.grantedAuthorityRepository.findByUserEcasIdAndGroupIdAndRoleName(grantedAuthoritySpec.getUserName(),
                        grantedAuthoritySpec.getGroupId(), grantedAuthoritySpec.getRoleName())
                .ifPresent(this.grantedAuthorityRepository::delete);
    }

    @Override
    public void deleteByUserEcasIdAndGroupID(String ecasId, Long groupId) {
        grantedAuthorityRepository.deleteByUserEcasIdAndGroupId(ecasId, groupId);
    }

    @Override
    public Collection<GrantedAuthority> findByUserEcasIdAndGroupId(String ecasId, Long groupId) {
        return grantedAuthorityRepository.findByUserEcasIdAndGroupId(ecasId, groupId);
    }

    @Override
    public boolean existsByUserAndGroup(User user, Group group) {
        return grantedAuthorityRepository.existsByUserAndGroup(user, group);
    }

    @Override
    @Transactional
    public void updateGroup(GrantedAuthoritySpec grantedAuthoritySpec, User currentUser) {
        if (StringUtils.isBlank(grantedAuthoritySpec.getUserName())) {
            // enable/disable all the users for the given group
            grantedAuthorityRepository.updateEnabled(grantedAuthoritySpec.getGroupId(), grantedAuthoritySpec.isEnabled());
        } else {
            userRepository.findByEcasIdIgnoreCase(grantedAuthoritySpec.getUserName())
                    .ifPresent(user -> grantedAuthorityRepository.updateEnabled(user.getId(), grantedAuthoritySpec.getGroupId(), grantedAuthoritySpec.isEnabled()));
        }

        if (!isCurrentUserSystemAdminOrBusinessAdmin(grantedAuthoritySpec, currentUser)
                /* you can't remove yourself as GROUP_ADMIN if no other GROUP_ADMIN is present, or you have a higher role */
                && !grantedAuthorityRepository.existsByGroupIdAndRoleNameAndEnabledTrue(grantedAuthoritySpec.getGroupId(), RoleName.GROUP_ADMIN)) {
            grantedAuthorityRepository.updateEnabled(currentUser.getId(), grantedAuthoritySpec.getGroupId(), true);
        }
    }

    @Override
    @Transactional
    public void updateGroups(List<GrantedAuthoritySpec> grantedAuthoritySpecs, User currentUser) {
        grantedAuthoritySpecs.forEach(grantedAuthoritySpec -> updateGroup(grantedAuthoritySpec, currentUser));
    }

    @Override
    public boolean isUserActiveWithinGroup(UserProfile userProfile) {
        Collection<GrantedAuthority> grantedAuthorities = grantedAuthorityRepository.findByUserEcasIdAndGroupId(userProfile.getUser().getEcasId(), userProfile.getGroup().getId());
        return !grantedAuthorities.isEmpty() && grantedAuthorities.stream().allMatch(GrantedAuthority::isEnabled);
    }

    @Override
    public boolean existsByGroup(Group group) {
        return grantedAuthorityRepository.existsByGroup(group);
    }

    @Override
    public GrantedAuthority findLastUpdatedGrantedAuthority(String user) {
        return grantedAuthorityRepository.findFirstByAuditingEntityModifiedByOrderByAuditingEntityModifiedDateDesc(user);
    }

    private boolean isCurrentUserSystemAdminOrBusinessAdmin(GrantedAuthoritySpec grantedAuthoritySpec, User currentUser) {
        Group root = groupService.getRoot();
        Group group = groupService.findById(grantedAuthoritySpec.getGroupId());
        return grantedAuthorityRepository.existsByUserEcasIdAndGroupIdAndRoleNameAndEnabledTrue(currentUser.getEcasId(), root.getId(), RoleName.SYS_ADMIN)
                || grantedAuthorityRepository.existsByUserEcasIdAndGroupIdAndRoleNameAndEnabledTrue(currentUser.getEcasId(), group.getBusinessId(), RoleName.GROUP_ADMIN);
    }

    private GrantedAuthority toGrantedAuthority(User user, RoleName roleName, Long groupId) {
        Group group = groupService.findById(groupId);
        Role role = roleService.getRole(roleName);
        return GrantedAuthority.builder()
                .user(user)
                .group(group)
                .role(role)
                .build();
    }
}
