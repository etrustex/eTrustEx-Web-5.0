package eu.europa.ec.etrustex.web.persistence.repository.security;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.mockGroup;
import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.mockUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private GrantedAuthorityRepository grantedAuthorityRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GroupRepository groupRepository;

    private Role groupAdminRole;
    private Role systemAdminRole;

    private List<User> users;
    private List<Group> groups;
    private Validator validator;

    @BeforeEach
    public void init() {
        groupAdminRole = roleRepository.save(Role.builder()
                .name(RoleName.GROUP_ADMIN)
                .description("Group Admin")
                .build());
        Role userRole = roleRepository.save(Role.builder()
                .name(RoleName.OPERATOR)
                .description("Operator")
                .build());
        systemAdminRole = roleRepository.save(Role.builder()
                .name(RoleName.SYS_ADMIN)
                .description("System Admin")
                .build());

        users = Arrays.asList(
                userRepository.save(mockUser("test1", "user1")),
                userRepository.save(mockUser("test2", "user2")),
                userRepository.save(mockUser("test3", "user3")),
                userRepository.save(mockUser("test4", "user4"))
        );

        groups = Arrays.asList(
                groupRepository.save(mockGroup("TEST_PARTY", "TEST_PARTY", null)),
                groupRepository.save(mockGroup("EDMA", "EDMA", null)),
                groupRepository.save(mockGroup("DecideM1", "DecideM1", null)),
                groupRepository.save(mockGroup("DecideM2", "DecideM2", null))
        );

        users.forEach(user -> {
            Collection<GrantedAuthority> grantedAuthorities = groups.stream()
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
                                            .role(groupAdminRole)
                                            .build()
                            )
                    )
                    .collect(Collectors.toSet());

            grantedAuthorityRepository.saveAll(grantedAuthorities);
        });

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @AfterEach
    public void cleanup() {
        grantedAuthorityRepository.deleteAll();
        roleRepository.deleteAll();
        userRepository.deleteAll();
        groupRepository.deleteAll();
    }

    @Test
    void should_return_0_system_administrator() {
        Page<User> userPage = userRepository.getUsersByRoleName(RoleName.SYS_ADMIN, PageRequest.of(0, 10));

        assertThat(userPage.getContent().size()).isZero();
    }

    @Test
    void should_return_one_system_administrator() {
        GrantedAuthority grantedAuthority = GrantedAuthority.builder()
                .user(users.iterator().next())
                .group(groups.iterator().next())
                .role(systemAdminRole)
                .build();

        grantedAuthorityRepository.save(grantedAuthority);

        Page<User> userPage = userRepository.getUsersByRoleName(RoleName.SYS_ADMIN, PageRequest.of(0, 10));

        assertThat(userPage.getContent().size()).isOne();
    }

    @Test
    void should_return_the_group_admins_of_a_group() {

        User user = users.stream().findFirst().orElseThrow(() -> new Error("Empty list of users"));
        Group group = groups.stream().findFirst().orElseThrow(() -> new Error("Empty list of groups"));

        grantedAuthorityRepository.delete(
                grantedAuthorityRepository.findByUserAndGroupId(user, group.getId()).stream().filter(grantedAuthority -> grantedAuthority.getRole().equals(groupAdminRole))
                        .findFirst().orElseThrow(() -> new Error("Granted authority not found"))
        );

        Page<User> groupAdmins = userRepository.getUsersByRoleNameAndGroupId(RoleName.GROUP_ADMIN, group.getIdentifier(), PageRequest.of(0, 10));

        assertThat(groupAdmins.getContent().size()).isEqualTo(users.size() - 1);
    }

    @Test
    void should_fail_validation_with_invalid_email_address() {
        User user = User.builder()
                .id(1L)
                .ecasId("ecasId")
                .name("name")
                .euLoginEmailAddress("asdfasdf")
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }
}
