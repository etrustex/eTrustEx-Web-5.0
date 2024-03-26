package eu.europa.ec.etrustex.web.integration;

import eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.impl.UnreadMessageReminderConfiguration;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.service.groupconfiguration.GroupConfigurationService;
import eu.europa.ec.etrustex.web.service.jobs.UnreadMessageReminderJob;
import eu.europa.ec.etrustex.web.service.jobs.UnreadMessageReminderService;
import eu.europa.ec.etrustex.web.service.security.GroupService;
import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.mockUnreadMessageReminderConfiguration;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"java:S112", "java:S100"}) /* Suppress Define and throw a dedicated exception instead of using a generic one & method names false positive. */
class UnreadMessageReminderJobTest {

    @Spy
    private UnreadMessageReminderService unreadMessageReminderService;

    @Spy
    private GroupService groupService;

    @Spy
    private GroupConfigurationService groupConfigurationService;

    private UnreadMessageReminderJob unreadMessageReminderJob;

    private Group business;
    private Group entity;


    @BeforeEach
    public void setUp() {
        Group businessRoot = Group.builder()
                .identifier("ABusinessId")
                .name("ABusinessName")
                .type(GroupType.ROOT)
                .build();

        business = Group.builder()
                .identifier("ABusinessId2")
                .name("ABusinessName2")
                .parent(businessRoot)
                .type(GroupType.BUSINESS)
                .build();

        entity = Group.builder()
                .identifier("AEntity")
                .name("AEntityName")
                .parent(business)
                .type(GroupType.ENTITY)
                .build();

        unreadMessageReminderJob = new UnreadMessageReminderJob(groupService, groupConfigurationService, unreadMessageReminderService);
    }

    @Test
    void should_send_reminder_for_business() {
        UnreadMessageReminderConfiguration unreadMessageReminderConfiguration = mockUnreadMessageReminderConfiguration(business, 20);

        given(groupService.findByType(GroupType.BUSINESS)).willReturn(Collections.singletonList(business));
        given(groupService.findByParentId(business.getId())).willReturn(Collections.singletonList(entity));
        given(groupConfigurationService.findActiveByGroupIdAndType(business.getId(), UnreadMessageReminderConfiguration.class)).willReturn(Optional.ofNullable(unreadMessageReminderConfiguration));

        unreadMessageReminderJob.runUnreadMessageReminderTask();

        verify(unreadMessageReminderService, times(1)).handleUnreadMessageReminderForGroup(entity, 20);
    }
}