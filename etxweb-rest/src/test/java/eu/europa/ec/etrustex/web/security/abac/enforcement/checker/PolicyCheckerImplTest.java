package eu.europa.ec.etrustex.web.security.abac.enforcement.checker;

import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.security.abac.PoliciesProperties;
import eu.europa.ec.etrustex.web.security.abac.enforcement.context.PolicyRuleContext;
import eu.europa.ec.etrustex.web.service.security.abac.PolicyRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.Collections;
import java.util.List;

import static eu.europa.ec.etrustex.web.common.UserAction.CREATE;
import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.*;
import static eu.europa.ec.etrustex.web.security.SecurityTestUtils.mockUserDetails;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PolicyCheckerImplTest {
    private static final ExpressionParser expressionParser = new SpelExpressionParser();
    @Mock
    private PoliciesProperties policiesProperties;
    private PolicyChecker policyChecker;

    @BeforeEach
    void setUp() {
        policyChecker = new PolicyCheckerImpl(policiesProperties);
    }


    /****** Validate context ***********/
    @Test
    void should_return_false_if_authentication_is_null() {
        PolicyRuleContext context = PolicyRuleContext.builder()
                .principal(null)
                .action(CREATE.toString())
                .target(Message.class.getSimpleName())
                .build();

        assertFalse(policyChecker.check(context));
    }

    @Test
    void should_return_false_if_target_is_null() {
        SecurityUserDetails userDetails = mockUserDetails(TEST_USER_ID, TEST_ENTITY_IDENTIFIER, TEST_ENTITY_NAME, RoleName.SYS_ADMIN);
        PolicyRuleContext context = PolicyRuleContext.builder()
                .principal(userDetails)
                .action(CREATE.toString())
                .target(null)
                .build();

        assertFalse(policyChecker.check(context));
    }

    @Test
    void should_return_false_if_action_is_null() {
        SecurityUserDetails userDetails = mockUserDetails(TEST_USER_ID, TEST_ENTITY_IDENTIFIER, TEST_ENTITY_NAME, RoleName.SYS_ADMIN);
        PolicyRuleContext context = PolicyRuleContext.builder()
                .principal(userDetails)
                .action(null)
                .target(Message.class.getSimpleName())
                .build();

        assertFalse(policyChecker.check(context));
    }


    /********* check Policy Rules **********/
    @Test
    void should_check() {
        SecurityUserDetails userDetails = mockUserDetails(TEST_USER_ID, TEST_ENTITY_IDENTIFIER, TEST_ENTITY_NAME, RoleName.SYS_ADMIN);
        List<PolicyRule> policyRules = Collections.singletonList(PolicyRule.builder()
                .name("Allow SYS_ADMIN to create user")
                .description("User with SYS_ADMIN role can create users")
                .target(expressionParser.parseExpression("principal.hasAuthority('SYS_ADMIN') && action == 'CREATE' && target == 'User'"))
                .condition(expressionParser.parseExpression("true"))
                .build());

        given(policiesProperties.getPolicyRules()).willReturn(policyRules);

        PolicyRuleContext context = PolicyRuleContext.builder()
                .principal(userDetails)
                .action(CREATE.toString())
                .target("User")
                .build();

        assertTrue(policyChecker.check(context));

        context = PolicyRuleContext.builder()
                .principal(userDetails)
                .action(CREATE.toString())
                .target("Message")
                .build();

        assertFalse(policyChecker.check(context));
    }
}
