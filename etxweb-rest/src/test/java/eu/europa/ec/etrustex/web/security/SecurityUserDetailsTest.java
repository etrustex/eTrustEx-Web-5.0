package eu.europa.ec.etrustex.web.security;

import eu.europa.ec.etrustex.web.exchange.model.UserDetails;
import eu.europa.ec.etrustex.web.exchange.model.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static eu.europa.ec.etrustex.web.util.exchange.model.RoleName.OPERATOR;
import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SecurityUserDetailsTest {

    @Test
    void overridden_methods_return_values() {
        SecurityUserDetails userDetails = new SecurityUserDetails();

        assertNull(userDetails.getPassword());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
    }

    @Test
    void should_return_user_details_to_exchange_model() {
        SecurityUserDetails securityUserDetails = SecurityTestUtils.mockUserDetails(TEST_USER_ID, TEST_ENTITY_IDENTIFIER, TEST_ENTITY_NAME, OPERATOR);
        SecurityGrantedAuthority grantedAuthority = securityUserDetails.getAuthorities().iterator().next();

        UserDetails userDetails = securityUserDetails.toExchangeModel();
        UserRole userRole = userDetails.getRoles()[0];

        assertEquals(securityUserDetails.getUser().getEcasId(), userDetails.getUsername());
        assertEquals(grantedAuthority.getGroup().getId(), userRole.getGroupId());
        assertEquals(grantedAuthority.getGroup().getName(), userRole.getGroupName());
        assertEquals(grantedAuthority.getGroup().getDescription(), userRole.getGroupDescription());
        assertEquals(grantedAuthority.getGroup().getType(), userRole.getGroupType());
        assertEquals(grantedAuthority.getRole().getName(), userRole.getRole());
    }
}
