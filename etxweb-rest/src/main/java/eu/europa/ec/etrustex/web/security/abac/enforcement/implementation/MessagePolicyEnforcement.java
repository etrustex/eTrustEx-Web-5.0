package eu.europa.ec.etrustex.web.security.abac.enforcement.implementation;

import eu.europa.ec.etrustex.web.common.UserAction;
import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.security.abac.enforcement.PolicyEnforcement;
import eu.europa.ec.etrustex.web.security.abac.enforcement.checker.PolicyChecker;
import eu.europa.ec.etrustex.web.security.abac.enforcement.context.PolicyRuleContext;
import eu.europa.ec.etrustex.web.service.ExchangeRuleService;
import eu.europa.ec.etrustex.web.service.MessageService;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.util.exchange.model.EntitySpec;
import eu.europa.ec.etrustex.web.util.exchange.model.MessageSummarySpec;
import eu.europa.ec.etrustex.web.util.exchange.model.SendMessageRequestSpec;
import eu.europa.ec.etrustex.web.util.exchange.model.UpdateMessageRequestSpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static eu.europa.ec.etrustex.web.common.UserAction.CREATE;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessagePolicyEnforcement implements PolicyEnforcement {
    private final PolicyChecker policyChecker;
    private final MessageService messageService;
    private final ExchangeRuleService exchangeRuleService;
    private final GroupRepository groupRepository;

    @Override
    public String getDomainType() {
        return Message.class.getSimpleName();
    }

    @Override
    public boolean check(SecurityUserDetails userDetails, Long targetId, UserAction action) {
        PolicyRuleContext.PolicyRuleContextBuilder contextBuilder = PolicyRuleContext.builder()
                .principal(userDetails)
                .action(String.valueOf(action));

        if (CREATE.equals(action)) {
            contextBuilder.target(Message.class.getSimpleName());
            contextBuilder.environment(Collections.singletonMap("senderEntityId", targetId));
        } else {
            try {
                Message message = messageService.findById(targetId);
                contextBuilder.target(message);
            } catch (Exception e) {
                return false;
            }
        }

        return policyChecker.check(contextBuilder.build());
    }

    @Override
    public boolean check(SecurityUserDetails userDetails, Object target, UserAction action) {
        if (!(target instanceof EntitySpec)) {
            throw new EtxWebException(String.format("Expecting target to be instance of %1$s. Instead it is %2$s", EntitySpec.class, target.getClass()));
        }

        EntitySpec entitySpec = (EntitySpec) target;
        Group senderEntity = findByIdentifierAndParentIdentifier(entitySpec);

        if (senderEntity == null) {
            log.warn("Access denied trying to retrieve message summaries for non-existing entity: {}, business: {}", entitySpec.getEntityIdentifier(), entitySpec.getEntityIdentifier());
            return false;
        }

        PolicyRuleContext context = PolicyRuleContext.builder()
                .principal(userDetails)
                .action(String.valueOf(action))
                .target(Message.class.getSimpleName())
                .environment(Collections.singletonMap("senderEntityId", senderEntity.getId()))
                .build();

        return policyChecker.check(context);
    }

    @Override
    public boolean check(SecurityUserDetails userDetails, Object target, Long targetId, UserAction action) {
        if ( !(target instanceof UpdateMessageRequestSpec)) {
            if ( target instanceof Long ) {
                Long senderId = (Long) target;
                try {
                    messageService.findByIdAndSenderGroupId(targetId, senderId);
                    return check(userDetails, targetId, action);
                } catch (Exception e) {
                    log.warn("Access denied trying to retrieve message. senderId: {}, messageId: {}", senderId, targetId);
                    log.error("Exception: ", e);
                    return false;
                }
            } else {
                throw new EtxWebException(String.format("Expecting target to be instance of %1$s or %2$s. instead it is %3$s",
                        SendMessageRequestSpec.class, UpdateMessageRequestSpec.class, target.getClass()));
            }
        }

        UpdateMessageRequestSpec updateMessageRequestSpec = (UpdateMessageRequestSpec) target;

        List<Long> recipientsIds = updateMessageRequestSpec.getRecipients().stream()
                .map(this::getRecipientId)
                .collect(Collectors.toList());

        Message message = messageService.findById(targetId);

        List<Long> validRecipientIds = exchangeRuleService.getValidRecipients(message.getSenderGroup().getId()).stream()
                .map(Group::getId)
                .collect(Collectors.toList());

        Map<String, Object> environment = new HashMap<>();
        environment.put("recipients", recipientsIds);
        environment.put("validRecipients", validRecipientIds);

        PolicyRuleContext context = PolicyRuleContext.builder()
                .principal(userDetails)
                .action(String.valueOf(action))
                .target(message)
                .environment(environment)
                .build();

        return policyChecker.check(context);
    }

    private Long getRecipientId(MessageSummarySpec messageSummarySpec) {
        if (messageSummarySpec.getRecipientId() != null ) {
            return messageSummarySpec.getRecipientId();
        } else {
            return findByIdentifierAndParentIdentifier(messageSummarySpec.getEntitySpec())
                    .getId();
        }
    }

    private Group findByIdentifierAndParentIdentifier(EntitySpec entitySpec) {
        return groupRepository.findFirstByIdentifierAndParentIdentifier(
                        entitySpec.getEntityIdentifier(),
                        entitySpec.getBusinessIdentifier());
    }
}
