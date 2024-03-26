package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.exchange.model.MessageSummaryUserStatusItem;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummary;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummaryUserStatus;
import eu.europa.ec.etrustex.web.util.exchange.model.Status;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.persistence.repository.MessageSummaryUserStatusRepository;
import eu.europa.ec.etrustex.web.service.dto.PageableDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.Optional;

import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MessageSummaryUserStatusServiceImplTest {
    @Mock
    private MessageSummaryUserStatusRepository messageSummaryUserStatusRepository;
    private MessageSummaryUserStatusService messageSummaryUserStatusService;
    private MessageSummaryUserStatus messageSummaryUserStatus;
    private User mockUser;
    private final MessageSummary messageSummary = mockMessageSummary(Status.DELIVERED);

    @BeforeEach
    public void setUp() {
        this.messageSummaryUserStatusService = new MessageSummaryUserStatusServiceImpl(messageSummaryUserStatusRepository);

        mockUser = mockUser();
        messageSummaryUserStatus = mockMsgUserStatus(Status.DELIVERED);
    }

    @Test
    void should_find_by_msg_summary_and_user() {
        given(this.messageSummaryUserStatusRepository.findByMessageSummaryAndUser(any(), any())).willReturn(Optional.of(messageSummaryUserStatus));

        assertTrue(messageSummaryUserStatusRepository.findByMessageSummaryAndUser(messageSummary, mockUser).isPresent());
    }

    @Test
    void should_update_msg_user_status_to_read() {
        messageSummaryUserStatus.setStatus(Status.READ);

        given(this.messageSummaryUserStatusRepository.findByMessageSummaryAndUser(any(), any())).willReturn(Optional.of(messageSummaryUserStatus));
        given(this.messageSummaryUserStatusRepository.save(any())).willReturn(messageSummaryUserStatus);

        assertEquals(Status.READ, messageSummaryUserStatusService.updateMessageSummaryUserStatus(messageSummary, mockUser, Status.READ).getStatus());
    }

    @Test
    void should_retrieve_msg_summary_user_status() {
        MessageSummaryUserStatusItem messageSummaryUserStatusItem = MessageSummaryUserStatusItem.builder()
                .messageId(messageSummaryUserStatus.getMessageSummary().getMessage().getId())
                .subject(messageSummaryUserStatus.getMessageSummary().getMessage().getSubject())
                .ecasId(messageSummaryUserStatus.getUser().getEcasId())
                .name(messageSummaryUserStatus.getUser().getName())
                .modifiedDate(messageSummaryUserStatus.getAuditingEntity().getModifiedDate())
                .build();

        Page<MessageSummaryUserStatusItem> messageSummaryUserStatusItemPage = new PageImpl<>(Collections.singletonList(messageSummaryUserStatusItem));

        given(this.messageSummaryUserStatusRepository.findReadMessageSummaryUserStatusByRecipientIdAndMessageIdOrSubjectContainingIgnoreCase(any(), any(), any())).willReturn(messageSummaryUserStatusItemPage);

        PageableDto pageDto = PageableDto.builder()
                .pageable(PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "auditingEntity.modifiedDate")))
                .filterBy("")
                .filterValue("")
                .build();
        assertEquals(1, messageSummaryUserStatusService.findReadMessages(messageSummaryUserStatus.getMessageSummary().getMessage().getSenderGroup().getId(), pageDto).getTotalElements());
    }

    @Test
    void should_update_msg_user_status_to_failed() {
        messageSummaryUserStatus.setStatus(Status.FAILED);

        given(this.messageSummaryUserStatusRepository.findByMessageSummaryAndUser(any(), any())).willReturn(Optional.of(messageSummaryUserStatus));
        given(this.messageSummaryUserStatusRepository.save(any())).willReturn(messageSummaryUserStatus);

        assertEquals(Status.FAILED, messageSummaryUserStatusService.updateMessageSummaryUserStatus(messageSummary, mockUser, Status.FAILED).getStatus());
    }

}
