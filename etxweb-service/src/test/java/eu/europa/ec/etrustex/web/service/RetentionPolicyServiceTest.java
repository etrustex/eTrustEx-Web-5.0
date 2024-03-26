package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.common.EtrustexWebProperties;
import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummary;
import eu.europa.ec.etrustex.web.persistence.entity.UserProfile;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.repository.MessageSummaryRepository;
import eu.europa.ec.etrustex.web.service.jobs.RetentionPolicyServiceImpl;
import eu.europa.ec.etrustex.web.service.security.UserProfileService;
import eu.europa.ec.etrustex.web.service.template.TemplateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"java:S112", "java:S100"}) /* Suppress Define and throw a dedicated exception instead of using a generic one & method names false positive. */
public class RetentionPolicyServiceTest {

    @Mock
    private MessageService messageService;
    @Mock
    private MessageSummaryService messageSummaryService;
    @Mock
    private MessageSummaryRepository messageSummaryRepository;
    @Mock
    private MailService mailService;
    @Mock
    private TemplateService templateService;
    @Mock
    private UserProfileService userProfileService;

    @Mock
    private EtrustexWebProperties etrustexWebProperties;

    private RetentionPolicyServiceImpl retentionPolicyService;

    @BeforeEach
    public void setUp() {
        this.retentionPolicyService = new RetentionPolicyServiceImpl(messageService, messageSummaryService, messageSummaryRepository, mailService, templateService, userProfileService, etrustexWebProperties);
    }

    @Test
    void should_delete_messages_for_group() {
        Message message = mockMessage();
        List<Message> messages = new ArrayList<>();
        messages.add(message);

        given(messageService.findBySenderGroupAndSentOnBefore(eq(message.getSenderGroup()), any(), eq(true)))
                .willReturn(messages);

        retentionPolicyService.deleteMessagesForGroup(message.getSenderGroup(), 15);
        verify(messageService, times(1)).delete(any(Message.class));
    }

    @Test
    void should_deactivate_messages_for_group() {
        Message message = mockMessage();
        MessageSummary messageSummary = mockMessageSummary(message);

        given(messageSummaryService.findByRecipientGroupAndSentOnBefore(eq(message.getSenderGroup()), any()))
                .willReturn(Collections.singletonList(messageSummary));

        retentionPolicyService.deactivateMessagesForGroup(message.getSenderGroup(), 15);
        verify(messageSummaryService, times(1)).saveAll(Collections.singletonList(messageSummary));
    }

    @Test
    void should_not_send_notification_twice() {
        Message message = mockMessage();
        MessageSummary messageSummary = mockMessageSummary(message);
        UserProfile userProfile = mockUserProfile(mockUser(), message.getSenderGroup());
        userProfile.setAlternativeEmailUsed(true);
        userProfile.setRetentionWarningNotifications(true);

        message.getMessageSummaries().add(messageSummary);

        given(messageService.findBySenderGroupAndSentOnBetween(any(), any(), any())).willReturn(Collections.singletonList(messageSummary.getMessage()));
        given(templateService.getByGroupType(any(Group.class), any())).willReturn(mockTemplate(1L));
        given(templateService.process(any(), any())).willReturn("");
        given(userProfileService.findByRetentionWarningNotificationsIsTrueAndGroup(any())).willReturn(Collections.singletonList(userProfile));

        retentionPolicyService.handleRetentionPolicyNotificationsForGroup(message.getSenderGroup(), 13, 15, 5);

        verify(mailService, times(1)).send(any());
        assertTrue(messageSummary.isNotifiedOfRetentionPolicy());

        retentionPolicyService.handleRetentionPolicyNotificationsForGroup(message.getSenderGroup(), 13, 15, 5);
        verify(mailService, times(1)).send(any());  // verify send is not called again
    }
}
