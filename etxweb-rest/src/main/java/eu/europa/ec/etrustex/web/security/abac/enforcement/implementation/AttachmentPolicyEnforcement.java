package eu.europa.ec.etrustex.web.security.abac.enforcement.implementation;

import eu.europa.ec.etrustex.web.common.UserAction;
import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import eu.europa.ec.etrustex.web.persistence.entity.Attachment;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummary;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.security.abac.enforcement.PolicyEnforcement;
import eu.europa.ec.etrustex.web.security.abac.enforcement.checker.PolicyChecker;
import eu.europa.ec.etrustex.web.security.abac.enforcement.context.PolicyRuleContext;
import eu.europa.ec.etrustex.web.service.AttachmentService;
import eu.europa.ec.etrustex.web.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttachmentPolicyEnforcement implements PolicyEnforcement {
    private final PolicyChecker policyChecker;
    private final AttachmentService attachmentService;
    private final MessageService messageService;

    @Override
    public String getDomainType() {
        return Attachment.class.getSimpleName();
    }

    @Override
    public boolean check(SecurityUserDetails userDetails, Long targetId, UserAction action) {
        Object target;
        Map<String, Object> environment = new HashMap<>();

        if (action == UserAction.CREATE) {
            target = messageService.findById(targetId);
        } else {
            Attachment attachment = attachmentService.findById(targetId);

            if (attachment == null) {
                log.warn("Attachment not found trying to evaluate permission");
                return false;
            }

            target = attachment;
            Long[] recipientIds = attachment.getMessage().getMessageSummaries().stream()
                    .map(MessageSummary::getRecipient)
                    .map(Group::getId)
                    .toArray(Long[]::new);

            environment.put("isRecipient", userDetails.hasAnyAuthority(RoleName.OPERATOR.name(), recipientIds));
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
