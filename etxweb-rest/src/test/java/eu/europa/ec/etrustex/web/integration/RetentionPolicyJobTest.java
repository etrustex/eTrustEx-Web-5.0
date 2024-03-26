package eu.europa.ec.etrustex.web.integration;

import eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.impl.RetentionPolicyEntityConfiguration;
import eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.impl.RetentionPolicyGroupConfiguration;
import eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.impl.RetentionPolicyNotificationGroupConfiguration;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.service.groupconfiguration.GroupConfigurationService;
import eu.europa.ec.etrustex.web.service.jobs.RetentionPolicyJob;
import eu.europa.ec.etrustex.web.service.jobs.RetentionPolicyService;
import eu.europa.ec.etrustex.web.service.security.GroupService;
import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SuppressWarnings({"java:S112", "java:S100"})
@ExtendWith(MockitoExtension.class)
class RetentionPolicyJobTest {

    @Spy
    private RetentionPolicyService retentionPolicyService;

    @Spy
    private GroupService groupService;

    @Spy
    private GroupConfigurationService groupConfigurationService;

    private RetentionPolicyJob retentionPolicyJob;

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

        retentionPolicyJob = new RetentionPolicyJob(retentionPolicyService, groupService, groupConfigurationService);
    }

    @Test
    void should_delete_messages_for_entity() {
        RetentionPolicyGroupConfiguration retentionPolicyGroupConfiguration = mockRetentionPolicyGroupConfiguration(business, 20);

        given(groupService.findByType(GroupType.BUSINESS)).willReturn(Collections.singletonList(business));
        given(groupService.findByParentId(business.getId())).willReturn(Collections.singletonList(entity));
        given(groupConfigurationService.findActiveByGroupIdAndType(business.getId(), RetentionPolicyGroupConfiguration.class)).willReturn(Optional.ofNullable(retentionPolicyGroupConfiguration));

        retentionPolicyJob.runRetentionPolicyTask();

        verify(retentionPolicyService, times(1)).deleteMessagesForGroup(entity, 20);
    }

    @Test
    void should_mark_messages_to_be_deleted_for_group() {
        RetentionPolicyEntityConfiguration retentionPolicyEntityConfiguration = mockRetentionPolicyEntityConfiguration(entity, 10);

        given(groupService.findByType(GroupType.BUSINESS)).willReturn(Collections.emptyList());
        given(groupService.findByType(GroupType.ENTITY)).willReturn(Collections.singletonList(entity));
        given(groupConfigurationService.findActiveByGroupIdAndType(entity.getId(), RetentionPolicyEntityConfiguration.class)).willReturn(Optional.ofNullable(retentionPolicyEntityConfiguration));

        retentionPolicyJob.runRetentionPolicyTask();

        verify(retentionPolicyService, times(1)).deactivateMessagesForGroup(entity, 10);
    }

    @Test
    void should_try_to_send_message_for_entity_retention() {
        RetentionPolicyGroupConfiguration retentionPolicyGroupConfiguration = mockRetentionPolicyGroupConfiguration(business, 20);
        RetentionPolicyEntityConfiguration retentionPolicyEntityConfiguration = mockRetentionPolicyEntityConfiguration(entity, 10);
        RetentionPolicyNotificationGroupConfiguration retentionPolicyNotificationGroupConfiguration = mockRetentionPolicyNotificationGroupConfiguration(business, 8);

        given(groupService.findByType(GroupType.BUSINESS)).willReturn(Collections.singletonList(business));
        given(groupService.findByParentId(business.getId())).willReturn(Collections.singletonList(entity));
        given(groupConfigurationService.findActiveByGroupIdAndType(business.getId(), RetentionPolicyGroupConfiguration.class)).willReturn(Optional.ofNullable(retentionPolicyGroupConfiguration));
        given(groupConfigurationService.findActiveByGroupIdAndType(business.getId(), RetentionPolicyNotificationGroupConfiguration.class)).willReturn(Optional.ofNullable(retentionPolicyNotificationGroupConfiguration));
        given(groupConfigurationService.findActiveByGroupIdAndType(entity.getId(), RetentionPolicyEntityConfiguration.class)).willReturn(Optional.ofNullable(retentionPolicyEntityConfiguration));

        retentionPolicyJob.runRetentionPolicyNotificationWarningTask();

        verify(retentionPolicyService, times(1)).handleRetentionPolicyNotificationsForGroup(entity, 10, 20, Objects.requireNonNull(retentionPolicyNotificationGroupConfiguration).getIntegerValue());
    }

    @Test
    void should_try_to_send_message_for_business_retention() {
        RetentionPolicyGroupConfiguration retentionPolicyGroupConfiguration = mockRetentionPolicyGroupConfiguration(business, 20);
        RetentionPolicyNotificationGroupConfiguration retentionPolicyNotificationGroupConfiguration = mockRetentionPolicyNotificationGroupConfiguration(business, 8);

        given(groupService.findByType(GroupType.BUSINESS)).willReturn(Collections.singletonList(business));
        given(groupService.findByParentId(business.getId())).willReturn(Collections.singletonList(entity));
        given(groupConfigurationService.findActiveByGroupIdAndType(business.getId(), RetentionPolicyGroupConfiguration.class)).willReturn(Optional.ofNullable(retentionPolicyGroupConfiguration));
        given(groupConfigurationService.findActiveByGroupIdAndType(business.getId(), RetentionPolicyNotificationGroupConfiguration.class)).willReturn(Optional.ofNullable(retentionPolicyNotificationGroupConfiguration));
        given(groupConfigurationService.findActiveByGroupIdAndType(entity.getId(), RetentionPolicyEntityConfiguration.class)).willReturn(Optional.empty());

        retentionPolicyJob.runRetentionPolicyNotificationWarningTask();

        verify(retentionPolicyService, times(1)).handleRetentionPolicyNotificationsForGroup(entity, 20, 20, Objects.requireNonNull(retentionPolicyNotificationGroupConfiguration).getIntegerValue());
    }

    @Test
    void should_try_to_send_message_for_business_retention_with_wrong_entity_retention() {
        RetentionPolicyGroupConfiguration retentionPolicyGroupConfiguration = mockRetentionPolicyGroupConfiguration(business, 20);
        RetentionPolicyEntityConfiguration retentionPolicyEntityConfiguration = mockRetentionPolicyEntityConfiguration(entity, 8);
        RetentionPolicyNotificationGroupConfiguration retentionPolicyNotificationGroupConfiguration = mockRetentionPolicyNotificationGroupConfiguration(business, 10);

        given(groupService.findByType(GroupType.BUSINESS)).willReturn(Collections.singletonList(business));
        given(groupService.findByParentId(business.getId())).willReturn(Collections.singletonList(entity));
        given(groupConfigurationService.findActiveByGroupIdAndType(business.getId(), RetentionPolicyGroupConfiguration.class)).willReturn(Optional.ofNullable(retentionPolicyGroupConfiguration));
        given(groupConfigurationService.findActiveByGroupIdAndType(business.getId(), RetentionPolicyNotificationGroupConfiguration.class)).willReturn(Optional.ofNullable(retentionPolicyNotificationGroupConfiguration));
        given(groupConfigurationService.findActiveByGroupIdAndType(entity.getId(), RetentionPolicyEntityConfiguration.class)).willReturn(Optional.ofNullable(retentionPolicyEntityConfiguration));

        retentionPolicyJob.runRetentionPolicyNotificationWarningTask();

        verify(retentionPolicyService, times(1)).handleRetentionPolicyNotificationsForGroup(entity, null, 20, Objects.requireNonNull(retentionPolicyNotificationGroupConfiguration).getIntegerValue());
    }

    @Test
    void should_not_try_to_send_message_for_business_configuration_error() {
        RetentionPolicyGroupConfiguration retentionPolicyGroupConfiguration = mockRetentionPolicyGroupConfiguration(business, 10);
        RetentionPolicyNotificationGroupConfiguration retentionPolicyNotificationGroupConfiguration = mockRetentionPolicyNotificationGroupConfiguration(business, 18);

        given(groupService.findByType(GroupType.BUSINESS)).willReturn(Collections.singletonList(business));
        given(groupService.findByParentId(business.getId())).willReturn(Collections.singletonList(entity));
        given(groupConfigurationService.findActiveByGroupIdAndType(business.getId(), RetentionPolicyGroupConfiguration.class)).willReturn(Optional.ofNullable(retentionPolicyGroupConfiguration));
        given(groupConfigurationService.findActiveByGroupIdAndType(business.getId(), RetentionPolicyNotificationGroupConfiguration.class)).willReturn(Optional.ofNullable(retentionPolicyNotificationGroupConfiguration));
        given(groupConfigurationService.findActiveByGroupIdAndType(entity.getId(), RetentionPolicyEntityConfiguration.class)).willReturn(Optional.empty());

        retentionPolicyJob.runRetentionPolicyNotificationWarningTask();

        verify(retentionPolicyService, times(0)).handleRetentionPolicyNotificationsForGroup(any(), anyInt(), anyInt(), anyInt());
    }
}