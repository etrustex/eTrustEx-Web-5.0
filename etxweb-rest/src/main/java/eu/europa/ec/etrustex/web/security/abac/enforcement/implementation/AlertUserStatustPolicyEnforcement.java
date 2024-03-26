package eu.europa.ec.etrustex.web.security.abac.enforcement.implementation;

import eu.europa.ec.etrustex.web.common.UserAction;
import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import eu.europa.ec.etrustex.web.exchange.model.AlertUserStatusSpec;
import eu.europa.ec.etrustex.web.persistence.entity.AlertUserStatus;
import eu.europa.ec.etrustex.web.persistence.entity.security.GrantedAuthority;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.security.abac.enforcement.PolicyEnforcement;
import eu.europa.ec.etrustex.web.security.abac.enforcement.checker.PolicyChecker;
import eu.europa.ec.etrustex.web.security.abac.enforcement.context.PolicyRuleContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlertUserStatustPolicyEnforcement implements PolicyEnforcement {
    private final PolicyChecker policyChecker;
    private final GroupRepository groupRepository;

    @Override
    public String getDomainType() {
        return AlertUserStatus.class.getSimpleName();
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
        Map<String, Object> environment = new HashMap<>();
        if (target instanceof AlertUserStatusSpec) {
            Optional<Group> group = groupRepository.findById(((AlertUserStatusSpec)target).getGroupId());
            if (!group.isPresent()) {
                return false;
            }

            environment.put(group.get().getType().equals(GroupType.BUSINESS) ? "businessId" : "rootId", group.get().getId());


        }

        PolicyRuleContext context = PolicyRuleContext.builder()
                .principal(userDetails)
                .action(String.valueOf(action))
                .target(target)
                .environment(environment)
                .build();

        return policyChecker.check(context);
    }
}
