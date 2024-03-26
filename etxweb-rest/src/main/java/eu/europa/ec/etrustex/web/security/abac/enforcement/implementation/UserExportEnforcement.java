package eu.europa.ec.etrustex.web.security.abac.enforcement.implementation;

import eu.europa.ec.etrustex.web.common.UserAction;
import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import eu.europa.ec.etrustex.web.exchange.model.UserExportItem;
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
public class UserExportEnforcement implements PolicyEnforcement {

    private final PolicyChecker policyChecker;

    private final GroupService groupService;

    @Override
    public String getDomainType() {
        return UserExportItem.class.getSimpleName();
    }

    @Override
    public boolean check(SecurityUserDetails userDetails, Long targetId, UserAction action) {

        Map<String, Object> environment = new HashMap<>();

        Group group = groupService.findById(targetId);

        if(group.getType().equals(GroupType.BUSINESS)){
            environment.put("businessId", targetId);
        }else {
            environment.put("businessId", group.getBusinessId());
            environment.put("entityId", targetId);
        }

        PolicyRuleContext context = PolicyRuleContext.builder()
                .principal(userDetails)
                .action(String.valueOf(action))
                .target(getDomainType())
                .environment(environment)
                .build();

        return policyChecker.check(context);
    }
}
