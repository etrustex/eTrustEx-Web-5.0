package eu.europa.ec.etrustex.web.security.abac.enforcement.checker;

import eu.europa.ec.etrustex.web.security.abac.PoliciesProperties;
import eu.europa.ec.etrustex.web.security.abac.enforcement.context.PolicyRuleContext;
import eu.europa.ec.etrustex.web.service.security.abac.PolicyRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PolicyCheckerImpl implements PolicyChecker {
    private final PoliciesProperties policiesProperties;


    @Override
    public boolean check(PolicyRuleContext context) {
        if (!validateContext(context))
            return false;

        List<PolicyRule> allRules = policiesProperties.getPolicyRules();

        List<PolicyRule> matchedRules = filterRules(allRules, context);

        return matchedRules.stream()
                .anyMatch(policyRule -> policyRule.getCondition().getValue(context, Boolean.class));
    }


    protected boolean validateContext(PolicyRuleContext context) {
        return context.getPrincipal() != null
                && context.getAction() != null
                && context.getTarget() != null;
    }

    protected List<PolicyRule> filterRules(List<PolicyRule> allRules, PolicyRuleContext context) {
        return allRules.stream()
                .filter(policyRule -> policyRule.getTarget().getValue(context, Boolean.class))
                .collect(Collectors.toList());
    }
}
