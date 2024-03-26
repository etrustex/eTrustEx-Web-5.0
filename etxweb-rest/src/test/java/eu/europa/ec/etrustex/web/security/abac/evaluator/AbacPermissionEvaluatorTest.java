package eu.europa.ec.etrustex.web.security.abac.evaluator;

import eu.europa.ec.etrustex.web.common.UserAction;
import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.security.abac.enforcement.implementation.BasicPolicyEnforcement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static eu.europa.ec.etrustex.web.security.SecurityTestUtils.mockUserDetails;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AbacPermissionEvaluatorTest {
    @Mock
    private BasicPolicyEnforcement basicPolicyEnforcement;
    private PermissionEvaluator permissionEvaluator;


    @BeforeEach
    public void setUp() {
        permissionEvaluator = new AbacPermissionEvaluator(basicPolicyEnforcement);
    }

    @Test
    void should_evaluate_basic_policy() {
        SecurityUserDetails userDetails = mockUserDetails();
        Class<Message> domainType = Message.class;
        UserAction action = UserAction.CREATE;

        given(basicPolicyEnforcement.check(userDetails, domainType.getSimpleName(), action)).willReturn(true);
        assertTrue(permissionEvaluator.hasPermission(userDetails, domainType, action));

        given(basicPolicyEnforcement.check(userDetails, domainType.getSimpleName(), action)).willReturn(false);
        assertFalse(permissionEvaluator.hasPermission(userDetails, domainType, action));
    }
}
