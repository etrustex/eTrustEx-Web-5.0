package eu.europa.ec.etrustex.web.security.abac;

import eu.europa.ec.etrustex.web.service.security.abac.PolicyRule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(value = PoliciesProperties.class)
class PoliciesPropertiesTest {
    private final ExpressionParser expressionParser = new SpelExpressionParser();
    @Autowired
    private PoliciesProperties policiesProperties;

    @Test
    void should_return_policy_rules() {
        List<PolicyDefinition> policyDefinitions = policiesProperties.getDefinitions();
        List<PolicyRule> policyRules = policiesProperties.getPolicyRules();

        assertFalse(policyRules.isEmpty());
        assertEquals(policyDefinitions.size(), policyRules.size());
        assertEquals(policyDefinitions.get(0).getName(), policyRules.get(0).getName());
        assertEquals(policyDefinitions.get(0).getDescription(), policyRules.get(0).getDescription());
        assertEquals(expressionParser.parseExpression(policyDefinitions.get(0).getTarget()).getExpressionString(), policyRules.get(0).getTarget().getExpressionString());
        assertEquals(expressionParser.parseExpression(policyDefinitions.get(0).getCondition()).getExpressionString(), policyRules.get(0).getCondition().getExpressionString());
    }
}
