package eu.europa.ec.etrustex.web.security.abac.enforcement.implementation;

import eu.europa.ec.etrustex.web.persistence.entity.exchange.RecipientPreferences;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.security.abac.enforcement.checker.PolicyChecker;
import eu.europa.ec.etrustex.web.security.abac.enforcement.context.PolicyRuleContext;
import eu.europa.ec.etrustex.web.service.security.GroupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static eu.europa.ec.etrustex.web.common.UserAction.CREATE;
import static eu.europa.ec.etrustex.web.util.exchange.model.RoleName.SYS_ADMIN;
import static eu.europa.ec.etrustex.web.security.SecurityTestUtils.mockUserDetails;
import static eu.europa.ec.etrustex.web.security.SecurityTestUtils.mockUserDetailsWithRole;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RecipientPreferencesPolicyEnforcementTest {
    @Mock
    private GroupService groupService;
    @Mock
    private PolicyChecker policyChecker;

    @Captor
    private ArgumentCaptor<PolicyRuleContext> policyRuleContextArgumentCaptor;

    private RecipientPreferencesPolicyEnforcement policyEnforcement;

    @BeforeEach
    void setUp() {
        policyEnforcement = new RecipientPreferencesPolicyEnforcement(policyChecker, groupService);
    }

    @Test
    void should_get_domain_type() {
        assertEquals(RecipientPreferences.class.getSimpleName(), policyEnforcement.getDomainType());
    }

    @Test
    void should_evaluate_targetId_policy() {
        SecurityUserDetails userDetails = mockUserDetails();
        Long targetId = 1L;
        Group entity = userDetails.getAuthorities().iterator().next().getGroup();

        given(groupService.findByRecipientPreferencesId(targetId)).willReturn(entity);
        given(policyChecker.check(any(PolicyRuleContext.class))).willReturn(true);

        assertTrue(policyEnforcement.check(userDetails, targetId, CREATE));

        verify(policyChecker).check(policyRuleContextArgumentCaptor.capture());

        Map<String, Object> environment = policyRuleContextArgumentCaptor.getValue().getEnvironment();
        assertEquals(entity.getId(), environment.get("groupId"));

        given(policyChecker.check(any(PolicyRuleContext.class))).willReturn(false);
        assertFalse(policyEnforcement.check(userDetails, targetId, CREATE));
    }

    @Test
    void should_set_groupId_null_in_environment_if_group_not_found() {
        SecurityUserDetails userDetails = mockUserDetails();
        Long targetId = 1L;

        given(groupService.findByRecipientPreferencesId(targetId)).willReturn(null);

        given(policyChecker.check(any(PolicyRuleContext.class))).willReturn(true);
        assertTrue(policyEnforcement.check(userDetails, targetId, CREATE));

        verify(policyChecker).check(policyRuleContextArgumentCaptor.capture());

        Map<String, Object> environment = policyRuleContextArgumentCaptor.getValue().getEnvironment();
        assertNull(environment.get("groupId"));
    }


    @Test
    void should_evaluate_targetId_policy_for_SYS_ADMIN() {
        SecurityUserDetails userDetails = mockUserDetailsWithRole(SYS_ADMIN);
        Long targetId = 1L;

        given(policyChecker.check(any(PolicyRuleContext.class))).willReturn(true);
        assertTrue(policyEnforcement.check(userDetails, targetId, CREATE));

        given(policyChecker.check(any(PolicyRuleContext.class))).willReturn(false);
        assertFalse(policyEnforcement.check(userDetails, targetId, CREATE));
    }
}
