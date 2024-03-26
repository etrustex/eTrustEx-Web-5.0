package eu.europa.ec.etrustex.web.security;

import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.Role;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.util.Collections;
import java.util.HashSet;

import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.*;

public class SecurityTestUtils {
    private SecurityTestUtils() {
        throw new EtxWebException("Utility class");
    }

    static PreAuthenticatedAuthenticationToken mockAuthentication(String userName) {
        EtrustexPrincipal etrustexPrincipal = EtrustexPrincipal.builder()
                .euLoginId(userName)
                .build();

        return new PreAuthenticatedAuthenticationToken(etrustexPrincipal, null);
    }

    public static SecurityUserDetails mockUserDetails() {
        return mockUserDetails(TEST_USER_ID, TEST_ENTITY_IDENTIFIER, TEST_ENTITY_NAME, RoleName.OPERATOR);
    }

    public static SecurityUserDetails mockUserDetailsWithRole(RoleName role) {
        return mockUserDetails(TEST_USER_ID, TEST_ENTITY_IDENTIFIER, TEST_ENTITY_NAME, role);
    }

    public static SecurityUserDetails mockUserDetails(String userId, String groupId, String groupName, RoleName role) {
        return buildUserDetails(userId, mockGroup(role, groupId, groupName), role);
    }

    public static SecurityUserDetails mockUserDetails(User user, Group group, RoleName role) {
        return buildUserDetails(user.getEcasId(), group, role);
    }

    private static SecurityUserDetails buildUserDetails(String userId, Group group, RoleName role) {
        User testUser = mockUser(userId, "user_name");
        Role testRole = mockRole(role);
        SecurityGrantedAuthority securityGrantedAuthority = new SecurityGrantedAuthority(testUser, group, testRole);
        SecurityUserDetails securityUserDetails = new SecurityUserDetails();
        securityUserDetails.setUser(testUser);
        securityUserDetails.setRoles(new HashSet<>(Collections.singletonList(testRole)));
        securityUserDetails.setAuthorities(Collections.singletonList(securityGrantedAuthority));

        return securityUserDetails;
    }
}
