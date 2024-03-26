package eu.europa.ec.etrustex.web.security.abac.enforcement.implementation;

import eu.europa.ec.etrustex.web.common.UserAction;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummaryUserStatus;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.security.abac.enforcement.PolicyEnforcement;
import eu.europa.ec.etrustex.web.security.abac.enforcement.checker.PolicyChecker;
import eu.europa.ec.etrustex.web.security.abac.enforcement.context.PolicyRuleContext;
import eu.europa.ec.etrustex.web.service.security.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MessageSummaryUserStatusPolicyEnforcement implements PolicyEnforcement {
    private final PolicyChecker policyChecker;
    private final GroupService groupService;


    @Override
    public String getDomainType() {
        return MessageSummaryUserStatus.class.getSimpleName();
    }

    @Override
    public boolean check(SecurityUserDetails userDetails, Long recipientEntityId, UserAction action) {
        return policyChecker.check(getContext(userDetails, recipientEntityId, action));
    }

    private PolicyRuleContext getContext(SecurityUserDetails userDetails, Long recipientEntityId, UserAction action) {
        Map<String, Object> environment = new HashMap<>();

        Group group = groupService.findById(recipientEntityId);

        environment.put("businessId", group.getBusinessId());
        environment.put("recipientEntityId", recipientEntityId);

        return PolicyRuleContext.builder()
                .principal(userDetails)
                .action(String.valueOf(action))
                .target(getDomainType())
                .environment(environment)
                .build();
    }
}
