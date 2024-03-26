package eu.europa.ec.etrustex.web.service.security;

import eu.europa.ec.etrustex.web.persistence.entity.security.GrantedAuthority;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.Role;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.persistence.repository.security.GrantedAuthorityRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.UserRepository;
import eu.europa.ec.etrustex.web.service.validation.model.GrantedAuthoritySpec;
import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"java:S1068", "java:S100"})
class GrantedAuthorityServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleService roleService;
    @Mock
    private GroupService groupService;
    @Mock
    private GrantedAuthorityRepository grantedAuthorityRepository;

    private GrantedAuthorityService grantedAuthorityService;


    @BeforeEach
    public void setUp() {
        this.grantedAuthorityService = new GrantedAuthorityServiceImpl(userRepository, roleService, groupService, grantedAuthorityRepository);
    }

    @Test
    void should_create_granted_authority() {
        User user = mockUser();
        Group sender = mockGroup();
        Role role = mockRole(RoleName.OPERATOR);

        GrantedAuthoritySpec grantedAuthoritySpec = GrantedAuthoritySpec.builder()
                .groupId(sender.getId())
                .roleName(role.getName())
                .userName(user.getEcasId())
                .build();

        GrantedAuthority grantedAuthority = new GrantedAuthority(user, sender, role);

        given(userRepository.findByEcasIdIgnoreCase(any())).willReturn(Optional.of(user));
        given(groupService.findById(any())).willReturn(sender);
        given(roleService.getRole(any())).willReturn(role);

        given(grantedAuthorityRepository.save(any())).willReturn(grantedAuthority);

        assertThat(grantedAuthorityService.create(grantedAuthoritySpec)).isEqualTo(grantedAuthority);
    }


    @Test
    void should_delete_granted_authority() {
        User user = mockUser();
        Group sender = mockGroup();
        Role role = mockRole(RoleName.OPERATOR);

        GrantedAuthoritySpec grantedAuthoritySpec = GrantedAuthoritySpec.builder()
                .groupId(sender.getId())
                .roleName(role.getName())
                .userName(user.getEcasId())
                .build();

        GrantedAuthority grantedAuthority = new GrantedAuthority(user, sender, role);

        given(grantedAuthorityRepository.findByUserEcasIdAndGroupIdAndRoleName(anyString(), anyLong(), any(RoleName.class))).willReturn(Optional.of(grantedAuthority));

        grantedAuthorityService.delete(grantedAuthoritySpec);

        verify(grantedAuthorityRepository).delete(grantedAuthority);
    }

    @Test
    void should_delete_by_user_ecas_id_and_group_id() {
        User user = mockUser();
        Group sender = mockGroup();
        Role role = mockRole(RoleName.OPERATOR);

        GrantedAuthoritySpec grantedAuthoritySpec = GrantedAuthoritySpec.builder()
                .groupId(sender.getId())
                .roleName(role.getName())
                .userName(user.getEcasId())
                .build();

        GrantedAuthority grantedAuthority = new GrantedAuthority(user, sender, role);


        grantedAuthorityService.deleteByUserEcasIdAndGroupID(grantedAuthoritySpec.getUserName(), grantedAuthoritySpec.getGroupId());

        verify(grantedAuthorityRepository).deleteByUserEcasIdAndGroupId(grantedAuthority.getUser().getEcasId(), grantedAuthority.getGroup().getId());
    }

    @Test
    void should_should_find_an_existing_granted_authority_by_user_and_group() {
        User user = mockUser();
        Group group = mockGroup();
        given(grantedAuthorityRepository.existsByUserAndGroup(user, group)).willReturn(true);
        assertTrue(grantedAuthorityService.existsByUserAndGroup(user, group));
    }

    @Test
    void should_update_enabled_per_group_and_user() {
        User user = mockUser();
        Group group = mockGroup();
        GrantedAuthoritySpec grantedAuthoritySpec = GrantedAuthoritySpec.builder()
                .groupId(group.getId())
                .userName(user.getEcasId())
                .enabled(true)
                .roleName(RoleName.OPERATOR)
                .build();

        given(userRepository.findByEcasIdIgnoreCase(any())).willReturn(Optional.of(user));
        given(groupService.getRoot()).willReturn(mockRoot());
        given(groupService.findById(any())).willReturn(group);
        grantedAuthorityService.updateGroup(grantedAuthoritySpec, user);

        verify(grantedAuthorityRepository, times(2)).updateEnabled(any(), any(), any());

    }

    @Test
    void should_update_enabled_multiple_per_group_and_user() {
        User user = mockUser();
        Group group = mockGroup();
        GrantedAuthoritySpec grantedAuthoritySpec = GrantedAuthoritySpec.builder()
                .groupId(group.getId())
                .userName(user.getEcasId())
                .enabled(true)
                .roleName(RoleName.OPERATOR)
                .build();

        given(userRepository.findByEcasIdIgnoreCase(any())).willReturn(Optional.of(user));
        given(groupService.getRoot()).willReturn(mockRoot());
        given(groupService.findById(any())).willReturn(group);
        grantedAuthorityService.updateGroups(Collections.singletonList(grantedAuthoritySpec), user);

        verify(grantedAuthorityRepository, times(2)).updateEnabled(any(), any(), any());

    }
}
