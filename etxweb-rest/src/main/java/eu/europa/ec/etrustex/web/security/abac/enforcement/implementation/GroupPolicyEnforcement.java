package eu.europa.ec.etrustex.web.security.abac.enforcement.implementation;

import eu.europa.ec.etrustex.web.common.UserAction;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.security.abac.enforcement.PolicyEnforcement;
import eu.europa.ec.etrustex.web.security.abac.enforcement.checker.PolicyChecker;
import eu.europa.ec.etrustex.web.security.abac.enforcement.context.PolicyRuleContext;
import eu.europa.ec.etrustex.web.service.security.GroupService;
import eu.europa.ec.etrustex.web.service.validation.model.GroupSpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupPolicyEnforcement implements PolicyEnforcement {
    private static final String TARGET_GROUP = "targetGroup";
    private static final String TARGET_GROUP_IDS = "targetGroupIds";
    private final GroupService groupService;
    private final PolicyChecker policyChecker;

    @Override
    public String getDomainType() {
        return Group.class.getSimpleName();
    }

    @Override
    public boolean check(SecurityUserDetails userDetails, Object target, UserAction action) {
        Map<String, Object> environment = new HashMap<>();

        if (target instanceof Long[]) {
            environment.put(TARGET_GROUP_IDS, target);
        } else if (target instanceof GroupSpec){
            environment.put(TARGET_GROUP, target);
        }

        PolicyRuleContext context = PolicyRuleContext.builder()
                .principal(userDetails)
                .action(String.valueOf(action))
                .target(Group.class.getSimpleName())
                .environment(environment)
                .build();

        return policyChecker.check(context);
    }

    @Override
    public boolean check(SecurityUserDetails userDetails, Long targetOrParentId, UserAction action) {
        Map<String, Object> environment = new HashMap<>();
        Group targetGroup;
        try {
            targetGroup = groupService.findById(targetOrParentId);
        } catch (EtxWebException ewe) {
            log.warn("Group not found trying to evaluate permission", ewe);
            return false;
        }

        environment.put(TARGET_GROUP, targetGroup);

        PolicyRuleContext context = PolicyRuleContext.builder()
                .principal(userDetails)
                .action(String.valueOf(action))
                .target(Group.class.getSimpleName())
                .environment(environment)
                .build();

        return policyChecker.check(context);
    }

}
