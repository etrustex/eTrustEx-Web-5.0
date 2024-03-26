package eu.europa.ec.etrustex.web.security.abac.enforcement.implementation;

import eu.europa.ec.etrustex.web.common.UserAction;
import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.security.abac.enforcement.checker.PolicyChecker;
import eu.europa.ec.etrustex.web.security.abac.enforcement.context.PolicyRuleContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static eu.europa.ec.etrustex.web.security.SecurityTestUtils.mockUserDetails;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BasicPolicyEnforcementTest {
    @Mock
    private PolicyChecker policyChecker;

    private BasicPolicyEnforcement policyEnforcement;

    @BeforeEach
    void setUp() {
        policyEnforcement = new BasicPolicyEnforcementImpl(policyChecker);
    }

    @Test
    void should_check() {
        SecurityUserDetails userDetails = mockUserDetails();

        given(policyChecker.check(any(PolicyRuleContext.class))).willReturn(true);
        assertTrue(policyEnforcement.check(userDetails, Message.class.getSimpleName(), UserAction.CREATE));

        given(policyChecker.check(any(PolicyRuleContext.class))).willReturn(false);
        assertFalse(policyEnforcement.check(userDetails, Message.class.getSimpleName(), UserAction.CREATE));
    }
}
