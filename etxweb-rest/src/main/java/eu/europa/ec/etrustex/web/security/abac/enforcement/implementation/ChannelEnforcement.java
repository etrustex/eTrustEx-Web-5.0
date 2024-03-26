package eu.europa.ec.etrustex.web.security.abac.enforcement.implementation;

import eu.europa.ec.etrustex.web.common.UserAction;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.Channel;
import eu.europa.ec.etrustex.web.persistence.repository.exchange.ChannelRepository;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.security.abac.enforcement.PolicyEnforcement;
import eu.europa.ec.etrustex.web.security.abac.enforcement.checker.PolicyChecker;
import eu.europa.ec.etrustex.web.security.abac.enforcement.context.PolicyRuleContext;
import eu.europa.ec.etrustex.web.service.validation.model.ChannelSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChannelEnforcement implements PolicyEnforcement {

    private final PolicyChecker policyChecker;
    private final ChannelRepository channelRepository;

    @Override
    public String getDomainType() {
        return Channel.class.getSimpleName();
    }

    @Override
    public boolean check(SecurityUserDetails userDetails, Object target, UserAction action) {
        Map<String, Object> environment = new HashMap<>();

        if (target instanceof ChannelSpec) {
            environment.put("channelSpec", target);
        }

        PolicyRuleContext context = PolicyRuleContext.builder()
                .principal(userDetails)
                .action(String.valueOf(action))
                .target(getDomainType())
                .environment(environment)
                .build();

        return policyChecker.check(context);
    }

    @Override
    public boolean check(SecurityUserDetails userDetails, Long targetId, UserAction action) {
        Map<String, Object> environment = new HashMap<>();

        channelRepository.findById(targetId)
                .ifPresent(channel -> environment.put("businessId", channel.getBusiness().getId()));

        PolicyRuleContext context = PolicyRuleContext.builder()
                .principal(userDetails)
                .action(String.valueOf(action))
                .target(getDomainType())
                .environment(environment)
                .build();

        return policyChecker.check(context);
    }

    @Override
    public boolean check(SecurityUserDetails userDetails, String targetId, UserAction action) {
        Map<String, Object> environment = new HashMap<>();

        environment.put("businessId", Long.parseLong(targetId));

        PolicyRuleContext context = PolicyRuleContext.builder()
                .principal(userDetails)
                .action(String.valueOf(action))
                .target(getDomainType())
                .environment(environment)
                .build();

        return policyChecker.check(context);
    }
}
