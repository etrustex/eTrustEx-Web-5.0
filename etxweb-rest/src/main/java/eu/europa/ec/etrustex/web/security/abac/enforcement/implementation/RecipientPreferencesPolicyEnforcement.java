package eu.europa.ec.etrustex.web.security.abac.enforcement.implementation;

import eu.europa.ec.etrustex.web.common.UserAction;
import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.RecipientPreferences;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.security.abac.enforcement.PolicyEnforcement;
import eu.europa.ec.etrustex.web.security.abac.enforcement.checker.PolicyChecker;
import eu.europa.ec.etrustex.web.security.abac.enforcement.context.PolicyRuleContext;
import eu.europa.ec.etrustex.web.service.security.GroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecipientPreferencesPolicyEnforcement implements PolicyEnforcement {
    private final PolicyChecker policyChecker;
    private final GroupService groupService;

    @Override
    public String getDomainType() {
        return RecipientPreferences.class.getSimpleName();
    }

    @Override
    public boolean check(SecurityUserDetails userDetails, Long targetId, UserAction action) {

        Map<String, Object> environment = new HashMap<>();

        Group group = groupService.findByRecipientPreferencesId(targetId);
        if (group != null) {
            environment.put("groupId", group.getId());
            environment.put("businessId", group.getType().equals(GroupType.ENTITY) ? group.getBusinessId() : group.getId());
        }

        PolicyRuleContext context = PolicyRuleContext.builder()
                .principal(userDetails)
                .target(getDomainType())
                .environment(environment)
                .action(String.valueOf(action))
                .build();

        return policyChecker.check(context);
    }
}
