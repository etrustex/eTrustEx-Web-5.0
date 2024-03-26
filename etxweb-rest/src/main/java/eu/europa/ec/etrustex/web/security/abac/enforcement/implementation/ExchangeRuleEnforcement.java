package eu.europa.ec.etrustex.web.security.abac.enforcement.implementation;

import eu.europa.ec.etrustex.web.common.UserAction;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.ExchangeRule;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.ExchangeRuleId;
import eu.europa.ec.etrustex.web.persistence.repository.exchange.ChannelRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.security.abac.enforcement.PolicyEnforcement;
import eu.europa.ec.etrustex.web.security.abac.enforcement.checker.PolicyChecker;
import eu.europa.ec.etrustex.web.security.abac.enforcement.context.PolicyRuleContext;
import eu.europa.ec.etrustex.web.service.validation.model.ExchangeRuleSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExchangeRuleEnforcement implements PolicyEnforcement {

    private final ChannelRepository channelRepository;
    private final PolicyChecker policyChecker;
    private final GroupRepository groupRepository;

    @Override
    public String getDomainType() {
        return ExchangeRule.class.getSimpleName();
    }

    @Override
    public boolean check(SecurityUserDetails userDetails, Object target, UserAction action) {
        Map<String, Object> environment = new HashMap<>();

        if (target instanceof Collection) {
            if (((Collection<?>) target).isEmpty()) return true;
            target = ((Collection<?>) target).iterator().next();
        }

        Long channelId;
        if (target instanceof ExchangeRuleSpec) {
            channelId = ((ExchangeRuleSpec) target).getChannelId();
        } else if (target instanceof ExchangeRuleId) {
            channelId = ((ExchangeRuleId) target).getChannel();
        } else {
            throw new EtxWebException("target should have type ExchangeRuleSpec or ExchangeRuleId, instead it is: " + target);
        }

        if (channelId == null) {
            throw new EtxWebException("target should have a valid channelId, instead it is: null");
        }

        channelRepository.findById(channelId)
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
    public boolean check(SecurityUserDetails userDetails, Long targetId, UserAction action) {
        Map<String, Object> environment = new HashMap<>();

        groupRepository.findById(targetId)
                .ifPresent(group -> environment.put(group.getType().equals(GroupType.BUSINESS) ? "businessId" : "senderEntityId", targetId));

        PolicyRuleContext context = PolicyRuleContext.builder()
                .principal(userDetails)
                .action(String.valueOf(action))
                .target(getDomainType())
                .environment(environment)
                .build();

        return policyChecker.check(context);
    }
}
