package eu.europa.ec.etrustex.web.security.abac.enforcement.implementation;

import eu.europa.ec.etrustex.web.common.UserAction;
import eu.europa.ec.etrustex.web.persistence.entity.Alert;
import eu.europa.ec.etrustex.web.persistence.entity.security.GrantedAuthority;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.security.abac.enforcement.PolicyEnforcement;
import eu.europa.ec.etrustex.web.security.abac.enforcement.checker.PolicyChecker;
import eu.europa.ec.etrustex.web.security.abac.enforcement.context.PolicyRuleContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlertPolicyEnforcement implements PolicyEnforcement {
    private final PolicyChecker policyChecker;

    @Override
    public String getDomainType() {
        return Alert.class.getSimpleName();
    }

    @Override
    public boolean check(SecurityUserDetails userDetails, Long targetId, UserAction action) {
        Map<String, Object> environment = new HashMap<>();

        environment.put("businessId", targetId);
        environment.put("userBusinessesIds", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getGroup)
                .map(Group::getBusinessId)
                .collect(Collectors.toSet()));

        PolicyRuleContext context = PolicyRuleContext.builder()
                .principal(userDetails)
                .action(String.valueOf(action))
                .target(getDomainType())
                .environment(environment)
                .build();

        return policyChecker.check(context);
    }

    @Override
    public boolean check(SecurityUserDetails userDetails, Object target, UserAction action) {

        PolicyRuleContext context = PolicyRuleContext.builder()
                .principal(userDetails)
                .action(String.valueOf(action))
                .target(target)
                .build();

        return policyChecker.check(context);
    }
}
