package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.exchange.model.MessageSummaryUserStatusItem;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummary;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummaryUserStatus;
import eu.europa.ec.etrustex.web.util.exchange.model.Status;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.persistence.repository.MessageSummaryUserStatusRepository;
import eu.europa.ec.etrustex.web.service.dto.PageableDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@Service
@Slf4j
public class MessageSummaryUserStatusServiceImpl implements MessageSummaryUserStatusService {
    private enum FilterBy {
        SUBJECT_OR_MESSAGE_ID("subject_or_message_id");

        private final String field;

        FilterBy(String field) {
            this.field = field;
        }
    }

    private final MessageSummaryUserStatusRepository messageSummaryUserStatusRepository;

    @Override
    public Optional<MessageSummaryUserStatus> findByMessageSummaryAndUser(MessageSummary messageSummary, User user) {
        return messageSummaryUserStatusRepository.findByMessageSummaryAndUser(messageSummary, user);
    }

    @Override
    @Transactional
    public MessageSummaryUserStatus updateMessageSummaryUserStatus(MessageSummary messageSummary, User user, Status status) {
        MessageSummaryUserStatus messageSummaryUserStatus = findByMessageSummaryAndUser(messageSummary, user)
                .orElse(MessageSummaryUserStatus.builder()
                        .user(user)
                        .messageSummary(messageSummary)
                        .build());

        messageSummaryUserStatus.setStatus(status);

        return messageSummaryUserStatusRepository.save(messageSummaryUserStatus);
    }

    @Override
    public Page<MessageSummaryUserStatusItem> findReadMessages(Long recipientEntityId, PageableDto page) {
        Map<String, String> filters = page.filters();
        String subjectOrMessageIdText = filters.getOrDefault(FilterBy.SUBJECT_OR_MESSAGE_ID.field, "");
        return messageSummaryUserStatusRepository.findReadMessageSummaryUserStatusByRecipientIdAndMessageIdOrSubjectContainingIgnoreCase(recipientEntityId, subjectOrMessageIdText, page.getPageable());
    }
}
