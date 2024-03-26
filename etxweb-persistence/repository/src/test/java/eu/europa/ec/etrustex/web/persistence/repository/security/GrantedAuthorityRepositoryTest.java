package eu.europa.ec.etrustex.web.persistence.repository.security;

import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import eu.europa.ec.etrustex.web.persistence.entity.security.GrantedAuthority;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.Role;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.mockGroup;
import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.mockUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class GrantedAuthorityRepositoryTest {

    private static final String TEST1 = "test1";

    @Autowired
    private GrantedAuthorityRepository grantedAuthorityRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GroupRepository groupRepository;

    private Role adminRole;
    private Role userRole;
    private Iterable<User> users;
    private Iterable<Group> groups;

    @BeforeEach
    public void init() {
        adminRole = roleRepository.save(Role.builder()
                .name(RoleName.GROUP_ADMIN)
                .description("GROUP_ADMIN")
                .build());
        userRole = roleRepository.save(Role.builder()
                .name(RoleName.OPERATOR)
                .description("OPERATOR")
                .build());

        users = userRepository.saveAll(Arrays.asList(
                userRepository.save(mockUser(TEST1,"user1")),
                userRepository.save(mockUser("test2", "user2")),
                userRepository.save(mockUser("test3", "user3")),
                userRepository.save(mockUser("test4", "user4"))
        ));

        groups = groupRepository.saveAll(Arrays.asList(
                groupRepository.save(mockGroup("TEST_PARTY", "TEST_PARTY", null)),
                groupRepository.save(mockGroup("EDMA", "EDMA", null)),
                groupRepository.save(mockGroup("DecideM1", "DecideM1", null)),
                groupRepository.save(mockGroup("DecideM2", "DecideM2", null))
        ));

    }

    @AfterEach
    public void cleanup() {
        grantedAuthorityRepository.deleteAll();
        roleRepository.deleteAll();
        userRepository.deleteAll();
        groupRepository.deleteAll();
    }


    @Test
    void should_save_all_granted_authorities() {
        users.forEach(user -> {
            Collection<GrantedAuthority> grantedAuthorities = StreamSupport.stream(groups.spliterator(), false)
                    .flatMap(group ->
                            Stream.of(
                                    GrantedAuthority.builder()
                                            .user(user)
                                            .group(group)
                                            .role(userRole)
                                            .build(),
                                    GrantedAuthority.builder()
                                            .user(user)
                                            .group(group)
                                            .role(adminRole)
                                            .build()
                            )
                    )
                    .collect(Collectors.toSet());

            grantedAuthorityRepository.saveAll(grantedAuthorities);
        });

        assertThat(grantedAuthorityRepository.count()).isEqualTo(32L); // 4 groups x 4 users x 2 authorities = 32
    }

    @Test
    void should_save_granted_authority() {
        User nodeFunctionalUser = userRepository.findByEcasIdIgnoreCase(TEST1).orElse(null);
        Role role = Role.builder()
                .name(RoleName.OPERATOR)
                .description("OPERATOR")
                .build();

        roleRepository.save(role);
        Group parent = groupRepository.save(mockGroup("parent", "parent", null, GroupType.BUSINESS));
        Group group = groupRepository.save(mockGroup("test", "test", parent, GroupType.ENTITY));


        GrantedAuthority nodeToDecideM1 = GrantedAuthority.builder()
                .user(nodeFunctionalUser)
                .group(group)
                .role(role)
                .build();

        assert nodeFunctionalUser != null;
        userRepository.save(nodeFunctionalUser);
        grantedAuthorityRepository.save(nodeToDecideM1);

        assertThat(grantedAuthorityRepository.findByUserAndGroup(nodeFunctionalUser, group)).hasSize(1);
        assertThat(grantedAuthorityRepository.findByUserAndGroupId(nodeFunctionalUser, group.getId())).hasSize(1);
    }

    @Test
    void should_find_by_user_ecas_id() {
        should_save_all_granted_authorities();
        assertThat(grantedAuthorityRepository.findByUserEcasId(TEST1)).hasSize(8);
    }

    @Test
    void should_delete_by_user_ecas_id_and_group_id() {
        should_save_all_granted_authorities();
        grantedAuthorityRepository.deleteByUserEcasIdAndGroupId(TEST1, 1L);
        assertTrue(grantedAuthorityRepository.findByUserEcasIdAndGroupId(TEST1, 1L).isEmpty());
    }

    @Test
    void should_exist_by_user_ecas_id_and_group_id() {
        Group group = groups.iterator().next();
        assertFalse(grantedAuthorityRepository.existsByGroupIdAndRoleNameAndEnabledTrue(group.getId(), userRole.getName()));
        should_save_all_granted_authorities();
        assertTrue(grantedAuthorityRepository.existsByGroupIdAndRoleNameAndEnabledTrue(group.getId(), userRole.getName()));
    }

    @Test
    void should_should_find_an_existing_granted_authority_by_user_and_group() {
        should_save_all_granted_authorities();
        User user = users.iterator().next();
        Group group = groups.iterator().next();
        assertTrue(grantedAuthorityRepository.existsByUserAndGroup(user, group));
    }

    @Test
    void should_exist_by_group() {
        should_save_all_granted_authorities();
        Group group = groups.iterator().next();
        assertTrue(grantedAuthorityRepository.existsByGroup(group));
    }
}
