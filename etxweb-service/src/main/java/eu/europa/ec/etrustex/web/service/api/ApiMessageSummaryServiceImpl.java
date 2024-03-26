package eu.europa.ec.etrustex.web.service.api;

import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummary;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.persistence.repository.MessageSummaryRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import eu.europa.ec.etrustex.web.service.ExchangeRuleService;
import eu.europa.ec.etrustex.web.service.MessageSummaryService;
import eu.europa.ec.etrustex.web.service.MessageSummaryUserStatusService;
import eu.europa.ec.etrustex.web.service.StatusService;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.util.exchange.model.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static eu.europa.ec.etrustex.web.util.exchange.model.GroupType.ROOT;

@AllArgsConstructor
@Service
@Slf4j
public class ApiMessageSummaryServiceImpl implements ApiMessageSummaryService {
    private final MessageSummaryRepository messageSummaryRepository;
    private final GroupRepository groupRepository;
    private final MessageSummaryUserStatusService messageSummaryUserStatusService;
    private final MessageSummaryService messageSummaryService;
    private final ExchangeRuleService exchangeRuleService;
    private final StatusService statusService;

    @Override
    public void createMessageSummaries(Message message, Collection<MessageSummarySpec> messageSummarySpecs) {
        messageSummaryService.createMessageSummaries(message, messageSummarySpecs);
    }

    @Override
    @Transactional(readOnly = true)
    public MessageSummary findByMessageIdAndRecipientIdForCurrentUser(Long messageId, String clientPublicKey, EntitySpec entitySpec, User user) {
        Group recipientEntity = groupRepository.findFirstByIdentifierAndParentIdentifier(entitySpec.getEntityIdentifier(), entitySpec.getBusinessIdentifier());

        return messageSummaryService.findByMessageIdAndRecipientIdForCurrentUser(messageId, clientPublicKey, recipientEntity.getId(), user);
    }

    @Override
    public List<RecipientStatus> recipientStatusByMessageId(Long messageId) {
        return messageSummaryRepository.findByMessageIdAndIsActiveTrue(messageId)
                .map(messageSummary -> RecipientStatus.builder()
                        .messageId(messageId)
                        .status(messageSummary.getStatus())
                        .businessIdentifier(messageSummary.getRecipient().getBusinessIdentifier())
                        .entityIdentifier(messageSummary.getRecipient().getIdentifier())
                        .statusModifiedDate(messageSummary.getStatusModifiedDate())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageSummary> findUnreadByRecipientIdForCurrentUser(EntitySpec entitySpec, User user, String clientPublicKey) {
        if (StringUtils.isBlank(clientPublicKey)) {
            throw new EtxWebException(String.format("Missing Client's Public Key, needed to encrypt message body. %s", clientPublicKey));
        }

        Group recipientEntity = groupRepository.findFirstByIdentifierAndParentIdentifier(entitySpec.getEntityIdentifier(), entitySpec.getBusinessIdentifier());

        return messageSummaryRepository.findUnreadMessageSummariesByRecipientAndStatus(recipientEntity.getId(), Status.SENT)
                .map(messageSummary -> messageSummaryService.decryptAndEncryptSymmetricKeysAndFilterUserStatuses(messageSummary, user, clientPublicKey))
                .collect(Collectors.toList());
    }

    @Override
    public List<Group> getValidRecipients(String businessIdentifier, String entityIdentifier) {
        Group senderGroup = groupRepository.findFirstByIdentifierAndParentIdentifier(entityIdentifier, businessIdentifier);
        if (senderGroup == null) {
            if (groupRepository.findFirstByIdentifierAndParentIdentifier(businessIdentifier, ROOT.name()) == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Business does not exist");
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity does not exist");
            }
        }

        return exchangeRuleService.getValidRecipients(senderGroup.getId());
    }

    @Override
    @Transactional
    public void ack(User user, Long messageId, EntitySpec entitySpec, Ack ack) {
        Group recipientEntity = groupRepository.findFirstByIdentifierAndParentIdentifier(entitySpec.getEntityIdentifier(), entitySpec.getBusinessIdentifier());
        MessageSummary messageSummary = messageSummaryService.findByMessageIdAndRecipientId(messageId, recipientEntity.getId());

        switch (ack) {
            case MESSAGE_RETRIEVED_OK:
                if (!Status.READ.equals(messageSummary.getStatus())) {
                    messageSummary.setStatus(Status.DELIVERED);
                    messageSummary.setStatusModifiedDate(new Date());
                }
                break;
            case MESSAGE_READ:
                messageSummary.setStatus(Status.READ);
                messageSummaryUserStatusService.updateMessageSummaryUserStatus(messageSummary, user, Status.READ);
                messageSummary.setStatusModifiedDate(new Date());
                break;
            case MESSAGE_FAILED:
                messageSummary.setStatus(Status.FAILED);
                messageSummary.setStatusModifiedDate(new Date());
                break;
        }

        messageSummaryRepository.save(messageSummary);
        statusService.updateWithMultipleStatus(messageSummary.getMessage(), messageSummary.getStatus());
    }
}
