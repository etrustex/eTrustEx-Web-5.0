package eu.europa.ec.etrustex.web.service.jobs;

import eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummary;
import eu.europa.ec.etrustex.web.persistence.entity.UserProfile;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.persistence.repository.MessageSummaryRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.UserProfileRepository;
import eu.europa.ec.etrustex.web.service.MailService;
import eu.europa.ec.etrustex.web.service.MessageSummaryUserStatusServiceImpl;
import eu.europa.ec.etrustex.web.service.security.GrantedAuthorityService;
import eu.europa.ec.etrustex.web.service.template.TemplateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;

import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"java:S112", "java:S100"}) /* Suppress Define and throw a dedicated exception instead of using a generic one & method names false positive. */
class UnreadMessageReminderServiceImplTest {

    @Mock
    private UserProfileRepository userProfileRepository;
    @Mock
    private GrantedAuthorityService grantedAuthorityService;
    @Mock
    private MessageSummaryRepository messageSummaryRepository;
    @Mock
    private MailService mailService;
    @Mock
    private TemplateService templateService;

    @Mock
    private MessageSummaryUserStatusServiceImpl messageSummaryUserStatusService;

    private UnreadMessageReminderService unreadMessageReminderService;

    @BeforeEach
    void setUp() {
        unreadMessageReminderService = new UnreadMessageReminderServiceImpl(userProfileRepository, grantedAuthorityService, messageSummaryRepository,
                mailService, templateService, messageSummaryUserStatusService);
    }

    @Test
    void should_handleUnreadMessageReminderForGroup() {
        User user = mockUser();
        user.setEuLoginEmailAddress("test@eulogin.email");
        Group group = mockGroup();
        UserProfile userProfile = EntityTestUtils.mockUserProfile(user, group);
        MessageSummary messageSummary = mockMessageSummary(12L);
        messageSummary.getRecipient().setParent(mockGroup());
        given(messageSummaryRepository.findByRecipientAndMessageSentOnBetweenAndIsActiveTrue(any(), any(), any())).willReturn(Stream.of(messageSummary));
        given(userProfileRepository.findByNewMessageNotificationsIsTrueAndGroup(any())).willReturn(Collections.nCopies(5, userProfile));
        given(templateService.getByGroupType(any(Group.class), any())).willReturn(mockTemplate(100L));
        given(grantedAuthorityService.isUserActiveWithinGroup(any())).willReturn(Boolean.TRUE);
        given(messageSummaryUserStatusService.findByMessageSummaryAndUser(any(), any())).willReturn(Optional.empty());

        unreadMessageReminderService.handleUnreadMessageReminderForGroup(group, 10);

        verify(mailService, times(5)).send(any());

    }
}
