package eu.europa.ec.etrustex.web.security.abac.enforcement.implementation;

import eu.europa.ec.etrustex.web.common.UserAction;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.security.abac.enforcement.checker.PolicyChecker;
import eu.europa.ec.etrustex.web.security.abac.enforcement.context.PolicyRuleContext;
import eu.europa.ec.etrustex.web.service.validation.model.GrantedAuthoritySpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static eu.europa.ec.etrustex.web.security.SecurityTestUtils.mockUserDetails;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class GrantedAuthorityPolicyEnforcementTest {
    @Mock
    private PolicyChecker policyChecker;
    @Mock
    private GroupRepository groupRepository;

    private GrantedAuthorityPolicyEnforcement policyEnforcement;

    @BeforeEach
    void setUp() {
        policyEnforcement = new GrantedAuthorityPolicyEnforcement(policyChecker, groupRepository);
    }


    @Test
    void should_get_domain_type() {
        assertEquals(GrantedAuthoritySpec.class.getSimpleName(), policyEnforcement.getDomainType());
    }


    @Test
    void should_evaluate_object_policy() {
        SecurityUserDetails userDetails = mockUserDetails();
        GrantedAuthoritySpec grantedAuthoritySpec = GrantedAuthoritySpec.builder().build();

        given(policyChecker.check(any(PolicyRuleContext.class))).willReturn(true);
        assertTrue(policyEnforcement.check(userDetails, grantedAuthoritySpec, UserAction.CREATE));

        given(policyChecker.check(any(PolicyRuleContext.class))).willReturn(false);
        assertFalse(policyEnforcement.check(userDetails, grantedAuthoritySpec, UserAction.CREATE));
    }

    @Test
    void should_throw_if_target_object_wrong_instance() {
        SecurityUserDetails securityUserDetails = mockUserDetails();
        Message message = Message.builder().build();
        assertThrows(EtxWebException.class, () -> policyEnforcement.check(securityUserDetails, message, UserAction.CREATE));
    }
}
