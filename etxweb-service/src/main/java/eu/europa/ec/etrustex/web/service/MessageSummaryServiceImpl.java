package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.common.DateFormatters;
import eu.europa.ec.etrustex.web.exchange.model.MessageSummaryListItem;
import eu.europa.ec.etrustex.web.exchange.model.SearchItem;
import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummary;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummaryId;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummaryUserStatus;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.persistence.repository.MessageSummaryRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import eu.europa.ec.etrustex.web.service.dto.FindMessageSummaryDto;
import eu.europa.ec.etrustex.web.service.pagination.MessageSummaryPage;
import eu.europa.ec.etrustex.web.util.cia.Confidentiality;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.util.exchange.model.EntitySpec;
import eu.europa.ec.etrustex.web.util.exchange.model.MessageSummarySpec;
import eu.europa.ec.etrustex.web.util.exchange.model.Status;
import eu.europa.ec.etrustex.web.util.exchange.model.keys.SymmetricKey;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static eu.europa.ec.etrustex.web.util.crypto.Base64.toHash;

@AllArgsConstructor
@Service
@Slf4j
public class MessageSummaryServiceImpl implements MessageSummaryService {
    private enum FilterBy {
        SUBJECT_OR_SENDER("subject_or_sender"),
        START_DATE("startDate"),
        END_DATE("endDate"),
        UNREAD("unread");

        private final String field;

        FilterBy(String field) {
            this.field = field;
        }
    }

    private final MessageSummaryRepository messageSummaryRepository;
    private final GroupRepository groupRepository;
    private final EncryptionService encryptionService;


    @Override
    public MessageSummary save(MessageSummary messageSummary) {
        return messageSummaryRepository.save(messageSummary);
    }

    @Override
    public List<MessageSummary> saveAll(List<MessageSummary> messageSummaries) {
        return messageSummaryRepository.saveAll(messageSummaries);
    }

    @Override
    public MessageSummary update(MessageSummary messageSummary) {
        return messageSummaryRepository.save(messageSummary);
    }

    @Override
    public List<MessageSummary> createMessageSummaries(Message message, Collection<MessageSummarySpec> messageSummarySpecs) {
        log.info("About to create message summaries");
        return messageSummarySpecs.stream().map(messageSummarySpec -> {
            Group recipientGroup = null;
            EntitySpec entitySpec = messageSummarySpec.getEntitySpec();

            if (messageSummarySpec.getRecipientId() != null) {
                recipientGroup = groupRepository.findById(messageSummarySpec.getRecipientId())
                        .orElseThrow(() -> new EtxWebException(String.format("Error delivering message with id %1$s to %2$s. Group %2$s  not found!", message.getId(), messageSummarySpec.getRecipientId())));
            } else if (entitySpec != null && StringUtils.isNotBlank(entitySpec.getBusinessIdentifier()) && StringUtils.isNotBlank(entitySpec.getEntityIdentifier())) {
                recipientGroup = groupRepository.findFirstByIdentifierAndParentIdentifier(entitySpec.getEntityIdentifier(), entitySpec.getBusinessIdentifier());
            }

            if (recipientGroup == null) {
                throw new EtxWebException(String.format("Invalid %s. No group identifiers present", messageSummarySpec.getClass().getSimpleName()));
            }

            MessageSummary messageSummary = createMessageSummary(message, recipientGroup, messageSummarySpec);
            log.info("Message summary has been created id: {} recipient {}", message.getId(), recipientGroup.getIdentifier());
            return messageSummary;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MessageSummary findByMessageIdAndRecipientIdForCurrentUser(Long messageId, String clientPublicKey, Long recipientEntityId, User user) {
        if (StringUtils.isBlank(clientPublicKey)) {
            throw new EtxWebException(String.format("Missing Client's Public Key, needed to encrypt message body. %s", clientPublicKey));
        }

        MessageSummary messageSummary = findByMessageIdAndRecipientId(messageId, recipientEntityId);

        messageSummary = decryptAndEncryptSymmetricKeysAndFilterUserStatuses(messageSummary, user, clientPublicKey);

        return messageSummary;
    }

    @Override
    public MessageSummary decryptAndEncryptSymmetricKeysAndFilterUserStatuses(MessageSummary messageSummary, User user, String clientPublicKey) {
        if (messageSummary.getMessage().getSymmetricKey() != null &&
                StringUtils.isNotBlank(messageSummary.getMessage().getSymmetricKey().getRandomBits()) &&
                StringUtils.isNotBlank(messageSummary.getMessage().getIv())) {
            SymmetricKey encryptedSymmetricKey = encryptionService.decryptSymmetricKeyAndEncryptWithClientPublicKey(messageSummary.getMessage().getSymmetricKey(), clientPublicKey);
            messageSummary.getMessage().setSymmetricKey(encryptedSymmetricKey);
        }

        if (messageSummary.getSymmetricKey() != null && messageSummary.getSymmetricKey().getEncryptionMethod().equals(SymmetricKey.EncryptionMethod.RSA_OAEP_SERVER)) {
            messageSummary.setSymmetricKey(encryptionService.decryptSymmetricKeyAndEncryptWithClientPublicKey(messageSummary.getSymmetricKey(), clientPublicKey));
        }

        filterByUser(messageSummary.getMessageSummaryUserStatuses(), user);

        return messageSummary;
    }

    @Override
    public MessageSummary findByMessageIdAndRecipientId(Long messageId, Long recipientEntityId) {
        return findOptionalByMessageIdAndRecipientId(messageId, recipientEntityId).orElseThrow(() -> new EtxWebException(String.format("Cannot retrieve the messageSummary for group %s and message %s", recipientEntityId, messageId)));
    }

    @Override
    public Optional<MessageSummary> findOptionalByMessageIdAndRecipientId(Long messageId, Long recipientId) {
        return messageSummaryRepository.findByMessageIdAndRecipientIdAndIsActiveIsTrue(messageId, recipientId);
    }

    @Override
    public MessageSummary getCurrent(MessageSummary updated) {
        MessageSummaryId messageSummaryId = new MessageSummaryId(updated.getRecipient().getId(), updated.getMessage().getId());
        return messageSummaryRepository.findById(messageSummaryId).orElseThrow(() -> new EtxWebException(String.format("Cannot find MessageSummary for Message id: %1$s and Recipient id: %2$s", messageSummaryId.getMessage(), messageSummaryId.getRecipient())));
    }

    @Override
    public boolean groupIsRecipient(Long messageId, Group recipient) {
        try {
            return messageSummaryRepository.findByMessageIdAndRecipientId(messageId, recipient.getId()).isPresent();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Find Paged result of message summaries optionally filtered. See {@link FindMessageSummaryDto}
     * So far messages can only be filtered by subject. If filtered by more fields we can add a private enum
     *
     * @param findMessageSummaryDto {@link FindMessageSummaryDto}
     * @param user                  the message will only contain user status for current user
     * @return MessageSummaryPage
     */
    @Override
    @Transactional(readOnly = true)
    public MessageSummaryPage findByRecipientForUser(FindMessageSummaryDto findMessageSummaryDto, User user) {
        Page<MessageSummary> page;
        Pageable pageable = findMessageSummaryDto.getPageable();
        Long recipientId = findMessageSummaryDto.getRecipientGroupId();
        int pageNumber = getPageNumber(findMessageSummaryDto);
        Pageable pageRequest = PageRequest.of(pageNumber, pageable.getPageSize(), pageable.getSort().and(Sort.by(Sort.Order.asc("message"))));

        Map<String, String> filters = findMessageSummaryDto.filters();
        if (!filters.isEmpty()) {
            Date startDate = filters.containsKey(FilterBy.START_DATE.field) ? DateFormatters.parseISODateTime(filters.get(FilterBy.START_DATE.field)) : null;
            Date endDate = filters.containsKey(FilterBy.END_DATE.field) ? DateFormatters.parseISODateTime(filters.get(FilterBy.END_DATE.field)) : null;

            String subjectOrSender = filters.get(FilterBy.SUBJECT_OR_SENDER.field);
            Boolean unread = filters.containsKey(FilterBy.UNREAD.field) && !filters.get(FilterBy.UNREAD.field).isEmpty() && Boolean.parseBoolean(filters.get(FilterBy.UNREAD.field));

            subjectOrSender = subjectOrSender == null ? "" : subjectOrSender;
            page = messageSummaryRepository.findByRecipientGroupIdAndSubjectContainingIgnoreCaseOrMessageSenderGroupIdentifierContainingIgnoreCase(recipientId, user, subjectOrSender, unread, startDate, endDate, pageRequest);
        } else {
            page = messageSummaryRepository.findByRecipientIdAndIsValidPublicKeyTrueAndIsActiveTrue(recipientId, pageRequest);
        }

        int unreadMessages = messageSummaryRepository.countUnread(recipientId, user);
        page.forEach(messageSummary -> filterByUser(messageSummary.getMessageSummaryUserStatuses(), user));

        return MessageSummaryPage.builder()
                .content(page.getContent())
                .first(page.isFirst())
                .last(page.isLast())
                .number(page.getNumber())
                .numberOfElements(page.getNumberOfElements())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .unreadMessages(unreadMessages)
                .build();
    }

    @Override
    public List<MessageSummary> findByRecipientGroupAndSentOnBefore(Group group, Date expiryDate) {
        return messageSummaryRepository.findByRecipientAndMessageSentOnBefore(group, expiryDate);
    }

    @Override
    public List<MessageSummary> findByRecipientIdAndMessageIdIn(List<Long> messageIds, Long recipientId) {
        return messageSummaryRepository.findByRecipientIdAndMessageIdIn(recipientId, messageIds);
    }

    @Override
    @Transactional(readOnly = true)
    public int countUnreadMessages(Long to, Long recipientId, User user) {
        Date toDate = to != null ? new Date(to) : new Date();
        return messageSummaryRepository.countUnread(toDate, recipientId, user);
    }

    @Override
    public Optional<MessageSummary> findByClientReferenceAndRecipientIdentifier(String referenceId, String recipientIdentifier) {
        return messageSummaryRepository.findByClientReferenceAndRecipientIdentifier(referenceId, recipientIdentifier);
    }

    @Override
    public boolean isMessageSummaryCreatedForClientReferenceAndRecipientId(String referenceId, Long recipientId) {
        return messageSummaryRepository.existsByClientReferenceAndRecipientId(referenceId, recipientId);
    }

    @Override
    public void deleteAll(Set<MessageSummary> messageSummaries) {
        messageSummaryRepository.deleteAll(messageSummaries);
    }

    @Override
    @Transactional
    public void setUpdatedWithNewCertificateToTrueForProcessedMessages() {
        messageSummaryRepository.setUpdatedWithNewCertificateToTrueForProcessedMessages();
    }

    @Override
    @Transactional
    public void setProcessed(boolean value) {
        messageSummaryRepository.setProcessed(value);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUpdatedWithNewCertificateFailures() {
        return messageSummaryRepository.findByConfidentialityAndSymmetricKeyIsNotNullAndUpdatedWithNewCertificateIsFalse(Confidentiality.LIMITED_HIGH)
                .filter(messageSummary -> messageSummary.getSymmetricKey() != null && StringUtils.isNotBlank(messageSummary.getSymmetricKey().getRandomBits()))
                .count();
    }

    @Override
    @Transactional
    public void disableByPublicKeyAndBusinessIdAndEntityId(String publicKey, Long entityId) {
        int numberOfMessagesToDeactivated = messageSummaryRepository.countByPublicKeyHashValueAndRecipientIdAndIsValidPublicKeyIsTrue(toHash(publicKey), entityId);
        String entityIdentifier = groupRepository.findById(entityId)
                .map(Group::getIdentifier)
                .orElseThrow(() -> new EtxWebException(String.format("Entity id %s not found", entityId)));
        log.info(String.format("%s message summaries will be deactivated for %s", numberOfMessagesToDeactivated, entityIdentifier));
        messageSummaryRepository.disableByPublicKeyHashValueAndBusinessIdAndEntityId(toHash(publicKey), entityId);
    }

    @Override
    public void filterByUser(Set<MessageSummaryUserStatus> messageSummaryUserStatuses, User user) {
        Set<MessageSummaryUserStatus> filtered = messageSummaryUserStatuses.stream()
                .filter(msgUserStatus -> user.equals(msgUserStatus.getUser()))
                .collect(Collectors.toSet());
        messageSummaryUserStatuses.clear();
        messageSummaryUserStatuses.addAll(filtered);
    }

    @Override
    public int countByPublicKeyHashValueAndRecipientId(String hashValue, Long entityId) {
        return messageSummaryRepository.countByPublicKeyHashValueAndRecipientIdAndIsValidPublicKeyIsTrue(hashValue, entityId);
    }

    @Override
    public Page<MessageSummaryListItem> findMessageSummaryListItemsByBusinessIdAndMessageIdOrSubject(Long businessId, String filterValue, Pageable pageable) {
        return messageSummaryRepository.findMessageSummaryListItemsByBusinessIdAndMessageIdOrSubject(businessId, filterValue, pageable);
    }

    @Override
    public List<SearchItem> search(Long businessId, String messageIdOrSubject) {
        return toSearchItem(messageSummaryRepository.findMessageSummaryListItemsByBusinessIdAndMessageIdOrSubject(businessId,
                messageIdOrSubject, Pageable.unpaged()).getContent());
    }

    @Override
    @Transactional
    public void setActive(Long messageId, String recipientIdentifier, Boolean isActive) {

        MessageSummary messageSummary = messageSummaryRepository.findByMessageIdAndRecipientIdentifier(messageId, recipientIdentifier)
                .orElseThrow(() -> new EtxWebException(String.format("MessageSummary with id:%s and recipientIdentifier: %s not found", messageId, recipientIdentifier)));
        messageSummary.setActive(isActive);
        messageSummaryRepository.save(messageSummary);

    }

    @Override
    @Transactional
    public void activateOrInactivateMessageSummaries(List<MessageSummaryListItem> messageSummaryListItems, boolean activate) {
        messageSummaryListItems.forEach(messageSummary -> this.setActive(messageSummary.getMessageId(), messageSummary.getRecipientEntity(), activate));
    }

    @Override
    public List<MessageSummary> findByRecipientGroupAndMessageSentOnBetween(Group group, Date startDate, Date endDate) {
        return messageSummaryRepository.findByRecipientAndMessageSentOnBetween(group, startDate, endDate);
    }

    private MessageSummary createMessageSummary(Message message, Group recipientGroup, MessageSummarySpec messageSummarySpec) {
        String messageHash = null;
        if (recipientGroup.getRecipientPreferences() != null && recipientGroup.getRecipientPreferences().getPublicKeyHashValue() != null
                && messageSummarySpec.getSymmetricKey() != null && messageSummarySpec.getSymmetricKey().getEncryptionMethod() == SymmetricKey.EncryptionMethod.RSA_OAEP_E2E) {
            messageHash = recipientGroup.getRecipientPreferences().getPublicKeyHashValue();
        }
        MessageSummary messageSummary = MessageSummary.builder()
                .message(message)
                .recipient(recipientGroup)
                .symmetricKey(messageSummarySpec.getSymmetricKey())
                .confidentiality(messageSummarySpec.getConfidentiality())
                .integrity(messageSummarySpec.getIntegrity())
                .clientReference(messageSummarySpec.getClientReference())
                .signature(messageSummarySpec.getSignature())
                .publicKeyHashValue(messageHash)
                .isValidPublicKey(true)
                .status(recipientGroup.isSystem() ? Status.SENT : Status.DELIVERED)
                .build();

        log.trace("messageSummary {}", messageSummary);

        return messageSummaryRepository.save(messageSummary);
    }

    private int getPageNumber(FindMessageSummaryDto findMessageSummaryDto) {
        int pageSize = findMessageSummaryDto.getPageable().getPageSize();
        Long messageId = findMessageSummaryDto.getMessageId();

        return messageId == null
                ? findMessageSummaryDto.getPageable().getPageNumber()
                : findIndexOfMessage(messageId, findMessageSummaryDto.getRecipientGroupId(), pageSize);
    }

    private int findIndexOfMessage(Long messageId, Long recipientId, int pageSize) {
        MessageSummary selectedMessageSummary = findByMessageIdAndRecipientId(messageId, recipientId);
        int messageSummaryPosition = messageSummaryRepository.countByRecipientIdAndMessageSentOnGreaterThan(recipientId, selectedMessageSummary.getMessage().getSentOn());
        return messageSummaryPosition / pageSize;
    }

    private List<SearchItem> toSearchItem(List<MessageSummaryListItem> summaryListItems) {
        return summaryListItems.stream().map(x -> new SearchItem(x.getMessageId().toString(), String.format("%s - %s", x.getMessageId(), x.getSubject()))).distinct().collect(Collectors.toList());
    }
}
