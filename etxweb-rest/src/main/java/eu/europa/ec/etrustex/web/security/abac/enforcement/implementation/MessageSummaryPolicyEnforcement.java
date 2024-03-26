package eu.europa.ec.etrustex.web.security.abac.enforcement.implementation;

import eu.europa.ec.etrustex.web.common.UserAction;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummary;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.security.abac.enforcement.PolicyEnforcement;
import eu.europa.ec.etrustex.web.security.abac.enforcement.checker.PolicyChecker;
import eu.europa.ec.etrustex.web.security.abac.enforcement.context.PolicyRuleContext;
import eu.europa.ec.etrustex.web.service.MessageSummaryService;
import eu.europa.ec.etrustex.web.service.security.GroupService;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.util.exchange.model.EntitySpec;
import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageSummaryPolicyEnforcement implements PolicyEnforcement {
    private final PolicyChecker policyChecker;
    private final GroupService groupService;
    private final MessageSummaryService messageSummaryService;


    @Override
    public String getDomainType() {
        return MessageSummary.class.getSimpleName();
    }


    @Override
    public boolean check(SecurityUserDetails userDetails, Object target, UserAction action) {
        if (!(target instanceof EntitySpec)) {
            throw new EtxWebException(String.format("Expecting target to be instance of %1$s. Instead it is %2$s", EntitySpec.class, target.getClass()));
        }

        EntitySpec entitySpec = (EntitySpec) target;
        Group recipientEntity = groupService.findByIdentifierAndParentIdentifier(entitySpec.getEntityIdentifier(), entitySpec.getBusinessIdentifier());

        if (recipientEntity == null) {
            log.warn("Access denied trying to retrieve message summaries for non-existing entity: {}, business: {}", entitySpec.getEntityIdentifier(), entitySpec.getEntityIdentifier());
            return false;
        }

        return policyChecker.check(getContext(userDetails, recipientEntity, null, action));
    }

    @Override
    public boolean check(SecurityUserDetails userDetails, Object target, Long messageId, UserAction action) {
        Group recipientEntity;

        if (target instanceof EntitySpec) {
            EntitySpec entitySpec = (EntitySpec) target;
            recipientEntity = groupService.findByIdentifierAndParentIdentifier(entitySpec.getEntityIdentifier(), entitySpec.getBusinessIdentifier());
        } else if (target instanceof Long) {
            Long recipientEntityId = (Long) target;
            recipientEntity = findGroupById(recipientEntityId);
        } else {
            throw new EtxWebException(String.format("Expecting target to be instance of %1$s or %2$s. Instead it is %3$s", EntitySpec.class, Long.class, target.getClass()));
        }

        if (recipientEntity == null) {
            log.warn("Access denied trying to retrieve message summary for non-existing entity: {}, messageId: {}", target, messageId);
            return false;
        }

        MessageSummary messageSummary;

        try {
            messageSummary = messageSummaryService.findByMessageIdAndRecipientId(messageId, recipientEntity.getId());
        } catch (Exception e) {
            log.warn("Access denied trying to retrieve message summary. recipientId: {}, messageId: {}", recipientEntity.getId(), messageId);
            log.error("Exception: ", e);
            return false;
        }

        return policyChecker.check(getContext(userDetails, recipientEntity, messageSummary, action));
    }

    @Override
    public boolean check(SecurityUserDetails userDetails, Long recipientEntityId, UserAction action) {
        Group recipientEntity = findGroupById(recipientEntityId);

        if (recipientEntity == null) {
            log.warn("Access denied trying to retrieve message summary for non-existing entity: entityId: {}", recipientEntityId);
            return false;
        }

        return policyChecker.check(getContext(userDetails, recipientEntity, null, action));
    }

    private Group findGroupById(Long groupId) {
        Group recipientEntity;

        try {
            recipientEntity = groupService.findById(groupId);
        } catch (Exception e) {
            log.warn("Access denied trying to retrieve message summary for non-existing entity: entityId: {}", groupId);
            log.error("Exception: ", e);
            recipientEntity = null;
        }

        return recipientEntity;
    }

    private PolicyRuleContext getContext(SecurityUserDetails userDetails, Group group, MessageSummary messageSummary, UserAction action) {
        Map<String, Object> environment = new HashMap<>();

        environment.put(group.getType() == GroupType.ENTITY ? "recipientEntityId" : "businessId", group.getId());

        environment.put("messageSummary", messageSummary);

        return PolicyRuleContext.builder()
                .principal(userDetails)
                .action(String.valueOf(action))
                .target(getDomainType())
                .environment(environment)
                .build();
    }
}
