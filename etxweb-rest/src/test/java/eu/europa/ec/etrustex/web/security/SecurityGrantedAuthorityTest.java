package eu.europa.ec.etrustex.web.security;

import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import eu.europa.ec.etrustex.web.persistence.entity.security.Role;
import org.junit.jupiter.api.Test;

import static eu.europa.ec.etrustex.web.util.exchange.model.RoleName.OPERATOR;
import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.mockGroup;
import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.mockUser;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SecurityGrantedAuthorityTest {
    @Test
    void getAuthority() {
        Role role = Role.builder().name(OPERATOR).build();
        SecurityGrantedAuthority securityGrantedAuthority = new SecurityGrantedAuthority(mockUser(), mockGroup(), role);

        assertEquals(role.getName(), RoleName.valueOf(securityGrantedAuthority.getAuthority()));
    }
}
