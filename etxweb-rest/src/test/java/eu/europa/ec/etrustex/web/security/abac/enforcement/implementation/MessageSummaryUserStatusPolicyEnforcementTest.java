package eu.europa.ec.etrustex.web.security.abac.enforcement.implementation;

import eu.europa.ec.etrustex.web.common.UserAction;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummaryUserStatus;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.security.abac.enforcement.checker.PolicyChecker;
import eu.europa.ec.etrustex.web.security.abac.enforcement.context.PolicyRuleContext;
import eu.europa.ec.etrustex.web.service.security.GroupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static eu.europa.ec.etrustex.web.util.exchange.model.RoleName.GROUP_ADMIN;
import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.mockGroup;
import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.mockUser;
import static eu.europa.ec.etrustex.web.security.SecurityTestUtils.mockUserDetails;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MessageSummaryUserStatusPolicyEnforcementTest {
    @Mock
    private GroupService groupService;
    @Mock
    private PolicyChecker policyChecker;

    private MessageSummaryUserStatusPolicyEnforcement policyEnforcement;

    @BeforeEach
    void setUp() {
        policyEnforcement = new MessageSummaryUserStatusPolicyEnforcement(policyChecker, groupService);
    }

    @Test
    void should_get_domain_type() {
        assertEquals(MessageSummaryUserStatus.class.getSimpleName(), policyEnforcement.getDomainType());
    }

    @Test
    void should_check() {
        User user = mockUser();
        Group group = mockGroup(GROUP_ADMIN, "TEST_GROUP", "TEST_GROUP");
        SecurityUserDetails userDetails = mockUserDetails(user, group, GROUP_ADMIN);

        given(policyChecker.check(any(PolicyRuleContext.class))).willReturn(true);
        given(groupService.findById(any())).willReturn(group);

        assertTrue(policyEnforcement.check(userDetails, 1L, UserAction.RETRIEVE));

        given(policyChecker.check(any(PolicyRuleContext.class))).willReturn(false);

        assertFalse(policyEnforcement.check(userDetails, 1L, UserAction.RETRIEVE));
    }
}
