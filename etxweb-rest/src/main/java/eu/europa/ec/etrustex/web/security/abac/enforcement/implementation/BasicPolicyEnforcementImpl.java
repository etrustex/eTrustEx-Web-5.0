package eu.europa.ec.etrustex.web.security.abac.enforcement.implementation;

import eu.europa.ec.etrustex.web.common.UserAction;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.security.abac.enforcement.checker.PolicyChecker;
import eu.europa.ec.etrustex.web.security.abac.enforcement.context.PolicyRuleContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicPolicyEnforcementImpl implements BasicPolicyEnforcement {
    private final PolicyChecker policyChecker;

    @Override
    public boolean check(SecurityUserDetails userDetails, String type, UserAction action) {
        PolicyRuleContext context = PolicyRuleContext.builder()
                .principal(userDetails)
                .target(type)
                .action(String.valueOf(action))
                .build();

        return policyChecker.check(context);
    }
}
