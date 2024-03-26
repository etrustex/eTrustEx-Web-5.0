package eu.europa.ec.etrustex.web.service.notification;

import eu.europa.ec.etrustex.web.persistence.entity.MessageSummary;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummaryUserStatus;
import eu.europa.ec.etrustex.web.util.exchange.model.Status;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.persistence.repository.security.UserProfileRepository;
import eu.europa.ec.etrustex.web.service.MessageSummaryService;
import eu.europa.ec.etrustex.web.service.MessageSummaryUserStatusService;
import eu.europa.ec.etrustex.web.service.groupconfiguration.GroupConfigurationService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.mockMessageSummary;
import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.mockUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationAspectTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private MessageSummaryService messageSummaryService;

    @Mock
    private MessageSummaryUserStatusService messageSummaryUserStatusService;

    @Mock
    private GroupConfigurationService groupConfigurationService;

    @Mock
    private UserProfileRepository userProfileRepository;

    private NotificationAspect notificationAspect;

    @BeforeEach
    public void setUp() {
        notificationAspect = new NotificationAspect(notificationService, messageSummaryService, messageSummaryUserStatusService, groupConfigurationService, userProfileRepository);
    }

    @Test
    void should_notify_of_message_summary_status_change_available() throws Throwable {
        MessageSummary messageSummary = mockMessageSummary(Status.DELIVERED);
        messageSummary.setStatus(Status.DELIVERED);
        ProceedingJoinPoint proceedingJoinPoint = mock(ProceedingJoinPoint.class);

        given(messageSummaryService.getCurrent(messageSummary)).willReturn(messageSummary);

        notificationAspect.notifyOfMessageSummaryStatusChange(proceedingJoinPoint, messageSummary);

        verify(messageSummaryService).getCurrent(any());
    }

    @Test
    void should_notify_of_unread_message_if_no_message_summary_status_is_present() throws Throwable {
        ProceedingJoinPoint proceedingJoinPoint = mock(ProceedingJoinPoint.class);
        MessageSummary messageSummary = mockMessageSummary(Status.SENT);
        messageSummary.getMessage().getSenderGroup().setIndividualStatusNotifications(true);
        User user = mockUser();

        given(messageSummaryService.getCurrent(messageSummary)).willReturn(MessageSummary.builder().status(Status.SENT).build());
        given(messageSummaryUserStatusService.findByMessageSummaryAndUser(any(MessageSummary.class), any(User.class))).willReturn(Optional.empty());

        notificationAspect.notifyOfReadStatus(proceedingJoinPoint, messageSummary, user, Status.READ);

        verify(notificationService).notifyOfNewMessageSummaryStatus(any(MessageSummary.class), any(User.class));
    }

    @Test
    void should_notify_of_unread_message_if_message_summary_status_is_SENT() throws Throwable {
        ProceedingJoinPoint proceedingJoinPoint = mock(ProceedingJoinPoint.class);
        MessageSummary messageSummary = mockMessageSummary(Status.SENT);
        messageSummary.getMessage().getSenderGroup().setIndividualStatusNotifications(true);
        User user = mockUser();
        MessageSummaryUserStatus messageSummaryUserStatus = MessageSummaryUserStatus.builder()
                .user(user)
                .messageSummary(messageSummary)
                .status(Status.SENT)
                .build();

        given(messageSummaryService.getCurrent(messageSummary)).willReturn(MessageSummary.builder().status(Status.SENT).build());
        given(messageSummaryUserStatusService.findByMessageSummaryAndUser(any(MessageSummary.class), any(User.class))).willReturn(Optional.of(messageSummaryUserStatus));

        notificationAspect.notifyOfReadStatus(proceedingJoinPoint, messageSummary, user, Status.READ);

        verify(notificationService).notifyOfNewMessageSummaryStatus(any(MessageSummary.class), any(User.class));
    }

    @Test
    void should_not_notify_of_unread_message_if_message_summary_status_is_already_READ() throws Throwable {
        ProceedingJoinPoint proceedingJoinPoint = mock(ProceedingJoinPoint.class);
        MessageSummary messageSummary = mockMessageSummary(Status.READ);
        messageSummary.getMessage().getSenderGroup().setIndividualStatusNotifications(true);
        User user = mockUser();
        MessageSummaryUserStatus messageSummaryUserStatus = MessageSummaryUserStatus.builder()
                .user(user)
                .messageSummary(messageSummary)
                .status(Status.READ)
                .build();

        given(messageSummaryService.getCurrent(messageSummary)).willReturn(messageSummary);
        given(messageSummaryUserStatusService.findByMessageSummaryAndUser(any(MessageSummary.class), any(User.class))).willReturn(Optional.of(messageSummaryUserStatus));

        notificationAspect.notifyOfReadStatus(proceedingJoinPoint, messageSummary, user, Status.READ);

        verifyNoInteractions(notificationService);
    }
}
