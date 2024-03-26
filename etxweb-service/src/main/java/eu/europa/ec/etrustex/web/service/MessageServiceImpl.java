package eu.europa.ec.etrustex.web.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.etrustex.web.common.DateFormatters;
import eu.europa.ec.etrustex.web.persistence.entity.Attachment;
import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.entity.MessageUserStatus;
import eu.europa.ec.etrustex.web.persistence.entity.UserProfile;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.persistence.repository.MessageRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import eu.europa.ec.etrustex.web.service.dto.FindMessageDto;
import eu.europa.ec.etrustex.web.service.pagination.MessagePage;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.util.exchange.model.AttachmentSpec;
import eu.europa.ec.etrustex.web.util.exchange.model.SendMessageRequestSpec;
import eu.europa.ec.etrustex.web.util.exchange.model.Status;
import eu.europa.ec.etrustex.web.util.exchange.model.UpdateMessageRequestSpec;
import eu.europa.ec.etrustex.web.util.exchange.model.keys.SymmetricKey;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
@Validated
public class MessageServiceImpl implements MessageService {
    private enum FilterBy {
        SUBJECT("subject"),
        STATUS("status"),
        SUBJECT_OR_RECIPIENT("subject_or_recipient"),
        START_DATE("startDate"),
        END_DATE("endDate"),
        UNREAD("unread");

        private final String field;

        FilterBy(String field) {
            this.field = field;
        }
    }

    private final MessageRepository messageRepository;
    private final GroupRepository groupRepository;
    private final MessageSummaryService messageSummaryService;
    private final AttachmentService attachmentService;
    private final EncryptionService encryptionService;

    private final ObjectMapper objectMapper;

    @Override
    @Transactional(readOnly = true)
    /*
      the @Transactional annotation is needed to ensure that the Message is updated in the db when retrieved while
      trying to send the wrappers and the bundle to the node, thus avoid a null pointer exception when looking for the
      attachments.
     */
    public Message findById(Long id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new EtxWebException(String.format("Message %s cannot be retrieved!", id)));
    }

    @Override
    public Message findByIdAndSenderGroupId(Long messageId, Long senderId) {
        return messageRepository.findByIdAndSenderGroupId(messageId, senderId).orElseThrow(() -> new EtxWebException(String.format("Cannot retrieve a message with id: %s for Sender id: %s", messageId, senderId)));
    }

    @Override
    public Optional<Message> findOptionalById(Long id) {
        return messageRepository.findById(id);
    }

    @Override
    public Message save(Message message) {
        return messageRepository.save(message);
    }

    @Override
    public Message create(UserProfile userProfile) {
        Message message = Message.builder()
                .senderUserProfile(userProfile)
                .build();
        message.getAuditingEntity().setModifiedDate(new Date());
        try {
            return messageRepository.save(message);
        } catch (Exception e) {
            String senderUser = Optional.of(userProfile).map(UserProfile::getUser).map(User::toString).orElse(null);
            String senderGroup = Optional.of(userProfile).map(UserProfile::getGroup).map(Group::toString).orElse(null);
            log.error("Cannot save message from sender {} of group {}", senderUser, senderGroup);
            throw new EtxWebException("Error while saving message", e);
        }
    }

    @Override
    public Message create(Long senderEntityId, String senderUserName) {
        Group senderGroup = groupRepository.findById(senderEntityId)
                .orElseThrow(() -> new EtxWebException(String.format("Sender group %s not found", senderEntityId)));

        log.info("About to create Message for senderGroup: " + senderGroup.getIdentifier() + " and senderUserName: + " + senderUserName);

        try {
            return messageRepository.save(
                    Message.builder()
                            .senderUserName(senderUserName)
                            .senderGroup(senderGroup)
                            .build()
            );
        } catch (Exception e) {
            log.error("Cannot save message from group {} with senderUserName {} ", senderGroup, senderUserName);
            throw new EtxWebException("Error while saving message", e);
        }
    }

    @Override
    @Transactional
    public void delete(Message message) {
        attachmentService.
                findByMessageId(message.getId()).
                forEach(attachmentService::delete);
        if (message.getMessageSummaries() != null && !message.getMessageSummaries().isEmpty()) {
            messageSummaryService.deleteAll(message.getMessageSummaries());
            message.getMessageSummaries().clear();
        }
        messageRepository.delete(message);
    }

    @Transactional
    @Override
    public void delete(Long messageId) {
        messageRepository.deleteById(messageId);
    }

    @Transactional
    @Override
    public Message update(Long messageId, SendMessageRequestSpec sendMessageRequestSpec, String userName) {
        Message message = findById(messageId);

        populateSentMessage(message, sendMessageRequestSpec, userName);
        removeNotPresentAttachments(message, sendMessageRequestSpec);
        message.getDraftRecipients().clear();

        message = messageRepository.save(message);
        log.info("Message has been updated id: {}", message.getId());
        try {
            messageSummaryService.createMessageSummaries(message, sendMessageRequestSpec.getRecipients());
        } catch (Exception e) {
            throw new EtxWebException(String.format("Error when creating message summaries for message id: %s", messageId), e);
        }


        return message;
    }

    @Transactional
    @Override
    public void update(Long messageId, UpdateMessageRequestSpec updateMessageRequestSpec, String userName) {
        Message message = findById(messageId);

        populateDraftMessage(message, updateMessageRequestSpec, userName);

        save(message);
    }

    @Override
    public boolean groupIsSender(Long messageId, Group sender) {
        return messageRepository.findByIdAndSenderGroup(messageId, sender).isPresent();
    }

    /**
     * Find Paged result of messages optionally filtered. See {@link FindMessageDto}
     * So far messages can only be filtered by subject. If filtered by more fields we can add a private enum
     * <p>
     * If findMessageDto.status is not set, Status.SENT will be used.
     *
     * @param findMessageDto {@link FindMessageDto}
     * @param user           the message will only contain user status for current user
     * @return MessagePage
     */
    @Override
    @Transactional(readOnly = true)
    public MessagePage getMessagesForUser(FindMessageDto findMessageDto, User user) {
        Pageable pageable = findMessageDto.getPageable();
        Pageable pageRequest = PageRequest.of(getPageNumber(findMessageDto), pageable.getPageSize(), pageable.getSort().and(Sort.by(Sort.Order.asc("id"))));

        Group sender = groupRepository.findById(findMessageDto.getSenderGroupId())
                .orElseThrow(() -> new EtxWebException(String.format("Sender group %s not found", findMessageDto.getSenderGroupId())));

        Map<String, String> filters = findMessageDto.filters();
        String statusText = filters.get(FilterBy.STATUS.field);
        Status status = statusText == null ? null : Status.valueOf(statusText.toUpperCase());

        Page<Message> page = getMessagesPage(sender, user, pageRequest, filters);
        page.forEach(message ->
                message.setMessageUserStatuses(filterStatusesByUser(message.getMessageUserStatuses(), user)));

        return MessagePage.builder()
                .content(page.getContent())
                .first(page.isFirst())
                .last(page.isLast())
                .number(page.getNumber())
                .numberOfElements(page.getNumberOfElements())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .unreadMessages((status != Status.DRAFT) ? messageRepository.countUnread(sender.getId(), user) : 0)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public int countUnreadSent(Long senderEntityId, User user) {
        return messageRepository.countUnread(senderEntityId, user);
    }

    @Override
    @Transactional(readOnly = true)
    public Message getMessage(long messageId, Long senderId, String clientPublicKey, User user) {
        Message message = findByIdAndSenderGroupId(messageId, senderId);

        if (message.getSymmetricKey() != null &&
                StringUtils.isNotBlank(message.getSymmetricKey().getRandomBits()) &&
                StringUtils.isNotBlank(message.getIv())) {
            SymmetricKey encryptedSymmetricKey = encryptionService.decryptSymmetricKeyAndEncryptWithClientPublicKey(message.getSymmetricKey(), clientPublicKey);
            message.setSymmetricKey(encryptedSymmetricKey);
        }

        message.getMessageSummaries().forEach(
                messageSummary -> {
                    if (messageSummary.getSymmetricKey() != null && messageSummary.getSymmetricKey().getEncryptionMethod().equals(SymmetricKey.EncryptionMethod.RSA_OAEP_SERVER)) {
                        messageSummary.setSymmetricKey(encryptionService.decryptSymmetricKeyAndEncryptWithClientPublicKey(messageSummary.getSymmetricKey(), clientPublicKey));
                    }
                }
        );

        message.setMessageUserStatuses(filterStatusesByUser(message.getMessageUserStatuses(), user));
        return message;
    }

    @Override
    public String getTemplateVarsAsString(Map<String, Object> templateVariables) {
        try {
            return objectMapper.writeValueAsString(templateVariables);
        } catch (JsonProcessingException jpe) {
            throw new EtxWebException("Error serializing template variables form : " + templateVariables, jpe);
        }
    }

    @Override
    public List<Message> findBySenderGroupAndSentOnBefore(Group group, Date expiryDate, boolean filterDrafts) {
        if (filterDrafts) {
            return messageRepository.findBySenderGroupAndSentOnBeforeAndSentOnNotNull(group, expiryDate);
        }
        return messageRepository.findBySenderGroupAndSentOnBefore(group, expiryDate);
    }

    @Override
    @Transactional
    public void setUpdatedWithNewCertificateToTrueForProcessedMessages() {
        messageRepository.setUpdatedWithNewCertificateToTrueForProcessedMessages();
    }

    @Override
    @Transactional
    public void setProcessed(boolean value) {
        messageRepository.setProcessed(value);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUpdatedWithNewCertificateFailures() {
        return messageRepository.countBySymmetricKeyIsNotNullAndIvIsNotNullAndUpdatedWithNewCertificateIsFalse();
    }

    @Override
    public List<Message> findBySenderGroupAndSentOnBetween(Group group, Date startDate, Date endDate) {
        return messageRepository.findBySenderGroupAndSentOnBetween(group, startDate, endDate);
    }

    @Override
    public boolean isReadyToSend(Long id) {
        List<Attachment> attachments = attachmentService.findByMessageId(id);
        long storePathTotal = attachments.stream().filter(attachment -> StringUtils.isNotBlank(attachment.getServerStorePath())).count();
        return attachments.size() == storePathTotal;
    }

    private Page<Message> getMessagesPage(Group sender, User user, Pageable pageRequest, Map<String, String> filters) {
        if (filters.isEmpty()) {
            return messageRepository.findBySenderGroupAndStatusNotAndSentOnIsNotNull(sender, Status.DRAFT, pageRequest);
        }

        Date startDate = filters.containsKey(FilterBy.START_DATE.field) ? DateFormatters.parseISODateTime(filters.get(FilterBy.START_DATE.field)) : new Date(0);
        Date endDate = filters.containsKey(FilterBy.END_DATE.field) ? DateFormatters.parseISODateTime(filters.get(FilterBy.END_DATE.field)) : new Date();
        Status status = getStatus(filters);

        if (status == null) {
            return messageRepository.findBySenderGroupAndStatusNotAndSubjectContainingIgnoreCaseOrSenderGroupIdentifierContainingIgnoreCase(sender, user, Status.DRAFT, getSubjectText(filters), getUnread(filters), startDate, endDate, pageRequest);
        } else if (status != Status.DRAFT) {
            return messageRepository.findBySenderGroupAndStatusAndSubjectContainingIgnoreCaseOrSenderGroupIdentifierContainingIgnoreCase(sender, user, status, getSubjectText(filters), getUnread(filters), startDate, endDate, pageRequest);
        }
        return messageRepository.findDraftMessages(sender, getSubjectText(filters), startDate, endDate, pageRequest);
    }

    private Status getStatus(Map<String, String> filters) {
        String statusText = filters.get(FilterBy.STATUS.field);
        return (statusText == null || statusText.isEmpty()) ? null : Status.valueOf(statusText.toUpperCase());
    }

    private String getSubjectText(Map<String, String> filters) {
        String subjectText = filters.containsKey(FilterBy.SUBJECT_OR_RECIPIENT.field) ? filters.get(FilterBy.SUBJECT_OR_RECIPIENT.field) : filters.get(FilterBy.SUBJECT.field);
        return subjectText == null ? "" : subjectText;
    }

    private Boolean getUnread(Map<String, String> filters) {
        return filters.containsKey(FilterBy.UNREAD.field) && !filters.get(FilterBy.UNREAD.field).isEmpty() && Boolean.parseBoolean(filters.get(FilterBy.UNREAD.field));
    }

    private int getPageNumber(FindMessageDto findMessageDto) {
        int pageSize = findMessageDto.getPageable().getPageSize();
        Long messageId = findMessageDto.getMessageId();

        return messageId == null
                ? findMessageDto.getPageable().getPageNumber()
                : findIndexOfMessage(messageId, findMessageDto.getSenderGroupId(), pageSize);
    }

    private int findIndexOfMessage(Long messageId, Long senderEntityId, int pageSize) {
        Message selectedMessage = findById(messageId);
        int messagePosition = messageRepository.countBySenderGroupIdAndSentOnGreaterThan(
                senderEntityId, selectedMessage.getSentOn()
        );
        return messagePosition / pageSize;
    }

    private void removeNotPresentAttachments(Message message, SendMessageRequestSpec source) {
        if (source.getAttachmentSpecs() == null) {
            log.info("No present attachments to remove");
            return;
        }

        try{
            List<Long> attachmentIds = source.getAttachmentSpecs().stream().map(AttachmentSpec::getId).collect(Collectors.toList());

            List<Attachment> toRemove = new ArrayList<>();
            attachmentService.findByMessageId(message.getId()).forEach(attachment -> {
                if (!attachmentIds.contains(attachment.getId())) {
                    toRemove.add(attachment);
                }
            });

            // chunked to avoid - Generates ORA-01795 maximum number of expressions in a list is 1000.
            ListUtils.partition(toRemove, 500).forEach(attachmentService::deleteAll);
        } catch (Exception e) {
            log.error("Error when removing not present attachments, " + e);
        }


    }

    private void populateSentMessage(Message target, SendMessageRequestSpec source, String user) {
        populateMessage(target, source, user);
        target.setSentOn(new Date());
        target.setAttachmentsTotalByteLength(source.getAttachmentsTotalByteLength());
        target.setAttachmentTotalNumber(source.getAttachmentTotalNumber());
        target.setAttachmentSpecs(source.getAttachmentSpecs());
        target.setSymmetricKey(source.getSymmetricKey());
        target.setIv(source.getIv());
        target.setHighImportance(source.getHighImportance());
    }

    private void populateDraftMessage(Message target, UpdateMessageRequestSpec source, String user) {
        populateMessage(target, source, user);
        target.setStatus(Status.DRAFT);
        target.setSymmetricKey(source.getSymmetricKey());
        target.setIv(source.getIv());
        target.setHighImportance(source.getHighImportance());

        target.getDraftRecipients().clear();

        source.getRecipients().forEach(messageSummarySpec -> {
            Group recipientGroup = groupRepository.findById(messageSummarySpec.getRecipientId())
                    .orElseThrow(() -> new EtxWebException(String.format("Recipient group %s not found", messageSummarySpec.getRecipientId())));
            target.getDraftRecipients().add(recipientGroup);
        });
    }

    private void populateMessage(Message target, UpdateMessageRequestSpec source, String user) {
        target.setSenderUserName(user);
        target.setSubject(source.getSubject());
        target.setText(source.getText());
        target.setHighImportance(source.getHighImportance());

        if (StringUtils.isEmpty(source.getTemplateVariables())) {
            target.setTemplateVariables("");
        } else {
            target.setTemplateVariables(source.getTemplateVariables());
        }
    }

    // See ETRUSTEX-7338
    private Set<MessageUserStatus> filterStatusesByUser(Set<MessageUserStatus> messageUserStatuses, User user) {
        if (messageUserStatuses == null) {
            return new HashSet<>();
        } else {
            return messageUserStatuses.stream()
                    .filter(msgUserStatus -> user.equals(msgUserStatus.getUser()))
                    .collect(Collectors.toSet());
        }

    }
}
