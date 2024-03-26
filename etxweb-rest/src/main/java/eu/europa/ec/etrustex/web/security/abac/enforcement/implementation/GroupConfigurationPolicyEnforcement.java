package eu.europa.ec.etrustex.web.security.abac.enforcement.implementation;

import eu.europa.ec.etrustex.web.common.UserAction;
import eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.GroupConfiguration;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.security.abac.enforcement.PolicyEnforcement;
import eu.europa.ec.etrustex.web.security.abac.enforcement.checker.PolicyChecker;
import eu.europa.ec.etrustex.web.security.abac.enforcement.context.PolicyRuleContext;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static eu.europa.ec.etrustex.web.util.exchange.model.GroupType.ENTITY;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupConfigurationPolicyEnforcement implements PolicyEnforcement {
    private final PolicyChecker policyChecker;
    private final GroupRepository groupRepository;

    @Override
    public String getDomainType() {
        return GroupConfiguration.class.getSimpleName();
    }


    @Override
    public boolean check(SecurityUserDetails userDetails, Long groupId, UserAction action) {
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new EtxWebException(String.format("Not found Group %s", groupId)));

        Map<String, Object> environment = new HashMap<>();
        environment.put("groupId", groupId);
        if (group.getType() == ENTITY) {
            environment.put("businessId", group.getBusinessId());
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
