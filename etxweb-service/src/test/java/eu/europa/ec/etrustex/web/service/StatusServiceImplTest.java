package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummary;
import eu.europa.ec.etrustex.web.persistence.entity.UserProfile;
import eu.europa.ec.etrustex.web.util.exchange.model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"java:S100"}) /* method names false positive. */
public class StatusServiceImplTest {
    @Mock
    private MessageSummaryUserStatusService messageSummaryUserStatusService;
    @Mock
    private MessageUserStatusService messageUserStatusService;
    @Mock
    private MessageSummaryService messageSummaryService;
    @Mock
    private MessageService messageService;

    private StatusService statusService;

    @BeforeEach
    public void setUp() {
        this.statusService = new StatusServiceImpl(messageSummaryUserStatusService, messageUserStatusService, messageSummaryService, messageService);
    }

    @Test
    void should_mark_message_summary_read() {
        UserProfile userProfile = mockUserProfile();
        Message message = mockMessage();
        MessageSummary messageSummary = mockMessageSummary(message);

        message.getMessageSummaries().add(messageSummary);

        given(messageSummaryService.findByRecipientIdAndMessageIdIn(any(), any())).willReturn(Collections.singletonList(messageSummary));

        statusService.markMessageSummaryRead(new Long[] {message.getId()}, messageSummary.getRecipient().getId(), userProfile.getUser());

        verify(messageSummaryUserStatusService, times(1)).updateMessageSummaryUserStatus(messageSummary, userProfile.getUser(), Status.READ);
        verify(messageSummaryService, times(1)).save(messageSummary);
        verify(messageService, times(1)).save(message);
    }

    @Test
    void should_not_update_read_message_summary() {
        UserProfile userProfile = mockUserProfile();
        Message message = mockMessage();
        MessageSummary messageSummary = mockMessageSummary(message);

        Date date = new Date();
        messageSummary.setStatus(Status.READ);
        messageSummary.setStatusModifiedDate(date);

        message.getMessageSummaries().add(messageSummary);

        given(messageSummaryService.findByRecipientIdAndMessageIdIn(any(), any())).willReturn(Collections.singletonList(messageSummary));

        statusService.markMessageSummaryRead(new Long[] {message.getId()}, messageSummary.getRecipient().getId(), userProfile.getUser());

        verify(messageSummaryUserStatusService, times(1)).updateMessageSummaryUserStatus(messageSummary, userProfile.getUser(), Status.READ);
        verify(messageSummaryService, times(1)).save(messageSummary);
        verify(messageService, times(1)).save(message);

        assertEquals(date, messageSummary.getStatusModifiedDate());
    }

    @Test
    void should_mark_message_read() {
        UserProfile userProfile = mockUserProfile();
        Message message = mockMessage();

        given(messageService.findById(any())).willReturn(message);

        statusService.markMessageRead(new Long[] {message.getId()}, userProfile.getUser());

        verify(messageUserStatusService, times(1)).createReadMessageUserStatusIfNotExists(message, userProfile.getUser(), Status.READ);
    }

    @Test
    void should_not_mark_draft_read() {
        UserProfile userProfile = mockUserProfile();
        Message message = mockMessage();
        message.setStatus(Status.DRAFT);

        given(messageService.findById(any())).willReturn(message);

        statusService.markMessageRead(new Long[] {message.getId()}, userProfile.getUser());

        verify(messageUserStatusService, times(0)).createReadMessageUserStatusIfNotExists(message, userProfile.getUser(), Status.READ);
    }

    @Test
    void should_update_message_and_summaries() {
        Message message = mockMessage();
        MessageSummary messageSummary = mockMessageSummary(message);

        message.getMessageSummaries().add(messageSummary);

        given(messageSummaryService.findOptionalByMessageIdAndRecipientId(any(), any())).willReturn(Optional.of(messageSummary));

        statusService.updateMessageAndSummariesStatus(message.getId(), messageSummary.getRecipient().getId(), Status.READ);

        verify(messageSummaryService, times(1)).save(messageSummary);
        verify(messageService, times(1)).save(message);

        statusService.updateMessageAndSummariesStatus(message.getId(), messageSummary.getRecipient().getId(), "new-client-id", Status.READ);

        verify(messageSummaryService, times(1)).save(messageSummary);
        verify(messageService, times(2)).save(message);

        assertEquals("new-client-id", messageSummary.getClientReference());
    }

    @Test
    void should_not_update_message_and_summaries_with_same_status() {
        Message message = mockMessage();
        MessageSummary messageSummary = mockMessageSummary(message);

        Date date = new Date();
        messageSummary.setStatus(Status.READ);
        messageSummary.setStatusModifiedDate(date);

        message.getMessageSummaries().add(messageSummary);

        given(messageSummaryService.findOptionalByMessageIdAndRecipientId(any(), any())).willReturn(Optional.ofNullable(messageSummary));

        statusService.updateMessageAndSummariesStatus(message.getId(), messageSummary.getRecipient().getId(), Status.READ);

        verify(messageSummaryService, times(0)).save(messageSummary);
        verify(messageService, times(1)).save(message);

        assertEquals(date, messageSummary.getStatusModifiedDate());
    }

    @Test
    void should_update_message_multiple() {
        Message message = mockMessage();
        MessageSummary messageSummary1 = mockMessageSummary(message);
        MessageSummary messageSummary2 = mockMessageSummary(message);

        messageSummary1.setStatus(Status.FAILED);

        message.getMessageSummaries().add(messageSummary1);
        message.getMessageSummaries().add(messageSummary2);

        given(messageSummaryService.findOptionalByMessageIdAndRecipientId(any(), any())).willReturn(Optional.of(messageSummary2));

        statusService.updateMessageAndSummariesStatus(message.getId(), messageSummary2.getRecipient().getId(), Status.READ);

        verify(messageSummaryService, times(1)).save(messageSummary2);
        verify(messageService, times(1)).save(message);

        assertEquals(message.getStatus(), Status.MULTIPLE);

    }
}
