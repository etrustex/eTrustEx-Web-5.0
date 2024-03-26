package eu.europa.ec.etrustex.web.service.security;

import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import eu.europa.ec.etrustex.web.exchange.model.UserPreferencesSpec;
import eu.europa.ec.etrustex.web.persistence.entity.UserPreferences;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.persistence.repository.security.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.ldap.core.LdapTemplate;

import java.util.List;

import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.mockUser;
import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.mockUsers;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    private UserService userService;
    private LdapTemplate ldapTemplate;

    @BeforeEach
    public void setUp() {
        this.userService = new UserServiceImpl(userRepository, ldapTemplate);
    }


    @Test
    void should_get_users_by_role() {
        RoleName roleName = RoleName.SYS_ADMIN;
        List<User> users = mockUsers(5);

        PageRequest pageRequest = PageRequest.of(0, users.size());

        given(userRepository.getUsersByRoleName(eq(roleName), any(PageRequest.class))).willReturn(new PageImpl<>(users));

        assertEquals(users, userService.getUsersByRoleAndGroupId(roleName, null, pageRequest).getContent());
    }

    @Test
    void should_create_user() {
        User user = mockUser();

        given(userRepository.save(any())).willReturn(user);

        User savedUser = userService.create(user.getEcasId(), user.getName(), "");

        assertEquals(savedUser.getEcasId(), user.getEcasId());
    }

    @Test
    void should_update_user_references() {
        User user = mockUser();
        user.setUserPreferences(UserPreferences.builder().paginationSize(5).build());

        UserPreferencesSpec userPreferencesSpec = UserPreferencesSpec.builder()
                .paginationSize(25).build();

        given(userRepository.save(any())).willReturn(user);

        User updatedUserPreferences = userService.updateUserPreferences(user, userPreferencesSpec);
        assertEquals(userPreferencesSpec.getPaginationSize(), updatedUserPreferences.getUserPreferences().getPaginationSize());
    }
}
