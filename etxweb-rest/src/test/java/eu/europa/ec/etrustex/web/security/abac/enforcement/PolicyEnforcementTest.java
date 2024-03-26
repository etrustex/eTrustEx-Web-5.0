package eu.europa.ec.etrustex.web.security.abac.enforcement;

import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static eu.europa.ec.etrustex.web.common.UserAction.CREATE;
import static eu.europa.ec.etrustex.web.security.SecurityTestUtils.mockUserDetails;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
class PolicyEnforcementTest {
    private static final PolicyEnforcement POLICY_ENFORCEMENT = () -> null;

    @Test
    void default_implementations_should_return_false() {
        SecurityUserDetails userDetails = mockUserDetails();

        assertFalse(POLICY_ENFORCEMENT.check(userDetails, User.builder().build(), CREATE));
        assertFalse(POLICY_ENFORCEMENT.check(userDetails, 1L, CREATE));
        assertFalse(POLICY_ENFORCEMENT.check(userDetails, User.builder().build(), 1L, CREATE));
    }
}
