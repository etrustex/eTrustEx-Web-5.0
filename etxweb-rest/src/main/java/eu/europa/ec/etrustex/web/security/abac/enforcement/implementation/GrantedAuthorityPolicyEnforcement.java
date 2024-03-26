package eu.europa.ec.etrustex.web.security.abac.enforcement.implementation;

import eu.europa.ec.etrustex.web.common.UserAction;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.security.abac.enforcement.PolicyEnforcement;
import eu.europa.ec.etrustex.web.security.abac.enforcement.checker.PolicyChecker;
import eu.europa.ec.etrustex.web.security.abac.enforcement.context.PolicyRuleContext;
import eu.europa.ec.etrustex.web.service.validation.model.GrantedAuthoritySpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static eu.europa.ec.etrustex.web.util.exchange.model.RoleName.GROUP_ADMIN;
import static eu.europa.ec.etrustex.web.util.exchange.model.RoleName.OPERATOR;

@Service
@RequiredArgsConstructor
@Slf4j
public class GrantedAuthorityPolicyEnforcement implements PolicyEnforcement {
    private final PolicyChecker policyChecker;
    private final GroupRepository groupRepository;

    @Override
    public String getDomainType() {
        return GrantedAuthoritySpec.class.getSimpleName();
    }


    @Override
    public boolean check(SecurityUserDetails userDetails, Object spec, UserAction action) {
        if (spec instanceof List) {
            return ((List<?>) spec).stream().anyMatch(s -> check(userDetails, s, action));
        }
        if (!(spec instanceof GrantedAuthoritySpec)) {
            throw new EtxWebException(String.format("Expecting target to be instance of %1$s instead of %2$s", GrantedAuthoritySpec.class, spec.getClass()));
        }

        GrantedAuthoritySpec grantedAuthoritySpec = (GrantedAuthoritySpec) spec;

        Map<String, Object> environment = new HashMap<>();
        environment.put("validRoles", Arrays.asList(OPERATOR, GROUP_ADMIN));

        groupRepository.findById(grantedAuthoritySpec.getGroupId())
                .ifPresent(group -> environment.put("businessId", group.getBusinessOrRoot().getId()));

        PolicyRuleContext context = PolicyRuleContext.builder()
                .principal(userDetails)
                .action(String.valueOf(action))
                .target(spec)
                .environment(environment)
                .build();

        return policyChecker.check(context);
    }
}
