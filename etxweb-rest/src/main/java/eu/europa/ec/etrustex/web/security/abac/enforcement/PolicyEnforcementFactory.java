package eu.europa.ec.etrustex.web.security.abac.enforcement;

import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PolicyEnforcementFactory {
    private final List<PolicyEnforcement> policyEnforcements;
    private static final Map<String, PolicyEnforcement> policyEnforcementCache = new HashMap<>();

    @PostConstruct
    public void initPermissionEvaluatorsCache() {
        policyEnforcements.forEach(policyEnforcement -> policyEnforcementCache.put(policyEnforcement.getDomainType(), policyEnforcement));
    }

    public static PolicyEnforcement getPolicyEnforcement(String domainType) {
        PolicyEnforcement policyEnforcement = policyEnforcementCache.get(domainType);

        if (policyEnforcement == null)
            throw new EtxWebException("Unknown policyEnforcement type: " + domainType);

        return policyEnforcement;
    }
}
