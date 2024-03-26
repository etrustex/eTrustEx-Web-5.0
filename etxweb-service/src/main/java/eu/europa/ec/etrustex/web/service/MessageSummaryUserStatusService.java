package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.exchange.model.MessageSummaryUserStatusItem;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummary;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummaryUserStatus;
import eu.europa.ec.etrustex.web.util.exchange.model.Status;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.service.dto.PageableDto;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface MessageSummaryUserStatusService {
    Optional<MessageSummaryUserStatus> findByMessageSummaryAndUser(MessageSummary messageSummary, User user);

    MessageSummaryUserStatus updateMessageSummaryUserStatus(MessageSummary messageSummary, User user, Status status);

    Page<MessageSummaryUserStatusItem> findReadMessages(Long recipientEntityId, PageableDto page);
}
