package eu.europa.ec.etrustex.web.integration;

import eu.europa.ec.etrustex.web.security.abac.PoliciesProperties;
import eu.europa.ec.etrustex.web.security.abac.PolicyDefinition;
import eu.europa.ec.etrustex.web.service.security.abac.PolicyRule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PoliciesPropertiesIntegrationTest extends AbstractControllerTest {
    private final ExpressionParser expressionParser = new SpelExpressionParser();
    @Autowired
    protected PoliciesProperties policiesProperties;

    @Test
    void integrationTest() {
        happyFlows();
    }

    private void happyFlows() {
        should_return_policy_rules();
    }

    void should_return_policy_rules() {
        PolicyDefinition policyDefinition = policiesProperties.getDefinitions().get(0);
        PolicyRule policyRule = policiesProperties.getPolicyRules().get(0);

        assertEquals(policyDefinition.getName(), policyRule.getName());
        assertEquals(policyDefinition.getDescription(), policyRule.getDescription());
        assertEquals(expressionParser.parseExpression(policyDefinition.getTarget()).getExpressionString(), policyRule.getTarget().getExpressionString());
        assertEquals(expressionParser.parseExpression(policyDefinition.getCondition()).getExpressionString(), policyRule.getCondition().getExpressionString());
    }
}
