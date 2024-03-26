package eu.europa.ec.etrustex.web.service.groupconfiguration;

import eu.europa.ec.etrustex.web.exchange.model.groupconfiguration.BooleanGroupConfigurationSpec;
import eu.europa.ec.etrustex.web.exchange.model.groupconfiguration.ForbiddenExtensionsGroupConfigurationSpec;
import eu.europa.ec.etrustex.web.exchange.model.groupconfiguration.RetentionPolicyGroupConfigurationSpec;
import eu.europa.ec.etrustex.web.exchange.model.groupconfiguration.SplashScreenGroupConfigurationSpec;
import eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.GroupConfiguration;
import eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.impl.*;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.persistence.repository.groupconfiguration.GroupConfigurationRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import eu.europa.ec.etrustex.web.service.notification.NotificationService;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupConfigurationServiceTest {

    @Mock
    private GroupConfigurationRepository<GroupConfiguration<?>> groupConfigurationRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private GroupRepository groupRepository;

    private GroupConfigurationService groupConfigurationService;

    private Group business;
    private Group businessRoot;
    private User user;

    @Captor
    ArgumentCaptor<List<GroupConfiguration<?>>> groupConfigurationsArgumentCaptor;

    @BeforeEach
    public void init() {
        businessRoot = Group.builder()
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

        user = mockUser("senderUser", "senderUserName");

        groupConfigurationService = new GroupConfigurationServiceImpl(groupConfigurationRepository, groupRepository, notificationService);
    }

    @Test
    void should_update_WindowsCompatibleFilenamesGroupConfiguration_configuration() {
        WindowsCompatibleFilenamesGroupConfiguration mockBooleanConfiguration = mockWindowsCompatibleFilenamesGroupConfiguration(business, false);

        given(groupRepository.findById(business.getId())).willReturn(Optional.of(business));
        given(groupConfigurationRepository.findFirstByGroupId(business.getId(), WindowsCompatibleFilenamesGroupConfiguration.class.getSimpleName(), WindowsCompatibleFilenamesGroupConfiguration.class))
                .willReturn(Optional.of(mockBooleanConfiguration));
        given(groupConfigurationRepository.save(mockBooleanConfiguration))
                .willReturn(mockBooleanConfiguration);

        BooleanGroupConfigurationSpec config = BooleanGroupConfigurationSpec.builder()
                .active(true)
                .build();
        assertTrue(groupConfigurationService.updateValue(user, business.getId(), WindowsCompatibleFilenamesGroupConfiguration.class, config).isActive());
    }

    @Test
    void should_update_RetentionPolicyGroupConfiguration_configuration() {
        RetentionPolicyGroupConfiguration mockIntegerConfiguration = mockRetentionPolicyGroupConfiguration(business, -1);
        RetentionPolicyGroupConfigurationSpec spec = RetentionPolicyGroupConfigurationSpec.builder()
                .value(7)
                .active(true)
                .build();

        given(groupRepository.findById(business.getId())).willReturn(Optional.of(business));
        given(groupConfigurationRepository.findFirstByGroupId(business.getId(), RetentionPolicyGroupConfiguration.class.getSimpleName(), RetentionPolicyGroupConfiguration.class))
                .willReturn(Optional.of(mockIntegerConfiguration));
        given(groupConfigurationRepository.save(mockIntegerConfiguration))
                .willReturn(mockIntegerConfiguration);

        assertEquals(7, groupConfigurationService.updateValue(user, business.getId(), RetentionPolicyGroupConfiguration.class, spec).getIntegerValue());
    }

    @Test
    void should_update_ForbiddenExtensionsGroupConfiguration_configuration() {
        ForbiddenExtensionsGroupConfiguration mockStringConfiguration = mockForbiddenExtensionsGroupConfiguration(business, "");
        ForbiddenExtensionsGroupConfigurationSpec spec = ForbiddenExtensionsGroupConfigurationSpec.builder()
                .value("csv")
                .active(true)
                .build();

        given(groupRepository.findById(business.getId())).willReturn(Optional.of(business));
        given(groupConfigurationRepository.findFirstByGroupId(business.getId(), ForbiddenExtensionsGroupConfiguration.class.getSimpleName(), ForbiddenExtensionsGroupConfiguration.class))
                .willReturn(Optional.of(mockStringConfiguration));
        given(groupConfigurationRepository.save(mockStringConfiguration))
                .willReturn(mockStringConfiguration);


        assertEquals("csv", groupConfigurationService.updateValue(user, business.getId(), ForbiddenExtensionsGroupConfiguration.class, spec).getStringValue());
    }

    @Test
    void should_update_SplashScreenGroupConfiguration_configuration() {
        SplashScreenGroupConfiguration mockStringConfiguration = mockSplashScreenGroupConfiguration(business, "splash");
        SplashScreenGroupConfigurationSpec spec = SplashScreenGroupConfigurationSpec.builder()
                .value("csv")
                .active(true)
                .build();

        given(groupRepository.findById(business.getId())).willReturn(Optional.of(business));
        given(groupConfigurationRepository.findFirstByGroupId(business.getId(), SplashScreenGroupConfiguration.class.getSimpleName(), SplashScreenGroupConfiguration.class))
                .willReturn(Optional.of(mockStringConfiguration));
        given(groupConfigurationRepository.save(mockStringConfiguration))
                .willReturn(mockStringConfiguration);


        assertEquals("csv", groupConfigurationService.updateValue(user, business.getId(), SplashScreenGroupConfiguration.class, spec).getStringValue());
    }

    @Test
    void should_throw_update_configuration_value_if_configuration_not_found() {
        Long businessId = business.getId();
        BooleanGroupConfigurationSpec booleanGroupConfigurationSpec = BooleanGroupConfigurationSpec.builder()
                .active(true)
                .build();

        assertThrows(EtxWebException.class, () -> groupConfigurationService.updateValue(user, businessId, WindowsCompatibleFilenamesGroupConfiguration.class, booleanGroupConfigurationSpec));
    }

    @Test
    void should_find_by_group_id() {
        ForbiddenExtensionsGroupConfiguration mockStringConfiguration = mockForbiddenExtensionsGroupConfiguration(business, "");

        lenient().when(groupRepository.findById(business.getId())).thenReturn(Optional.of(business));
        lenient().when(groupConfigurationRepository.findByGroupId(business.getId())).thenReturn(Collections.singletonList(mockStringConfiguration));
        List<GroupConfiguration<?>> list = groupConfigurationService.findByGroupId(business.getId());

        assertEquals(mockStringConfiguration, list.get(0));
    }

    @Test
    void should_find_by_group_id_and_dtype() {
        ForbiddenExtensionsGroupConfiguration mockStringConfiguration = mockForbiddenExtensionsGroupConfiguration(businessRoot, "");
        WindowsCompatibleFilenamesGroupConfiguration mockBooleanConfiguration = mockWindowsCompatibleFilenamesGroupConfiguration(business, false);
        RetentionPolicyGroupConfiguration mockIntegerConfiguration = mockRetentionPolicyGroupConfiguration(business, -1);

        given(groupConfigurationRepository.findFirstByGroupIdAndDtype(business.getId(), ForbiddenExtensionsGroupConfiguration.class.getSimpleName()))
                .willReturn(Optional.of(mockStringConfiguration));
        given(groupConfigurationRepository.findFirstByGroupIdAndDtype(business.getId(), WindowsCompatibleFilenamesGroupConfiguration.class.getSimpleName()))
                .willReturn(Optional.of(mockBooleanConfiguration));
        given(groupConfigurationRepository.findFirstByGroupIdAndDtype(business.getId(), RetentionPolicyGroupConfiguration.class.getSimpleName()))
                .willReturn(Optional.of(mockIntegerConfiguration));

        GroupConfiguration<?> forbiddenExtensionsGroupConfiguration = groupConfigurationService.findByGroupIdAndType(business.getId(), ForbiddenExtensionsGroupConfiguration.class.getSimpleName());
        GroupConfiguration<?> windowsCompatibleFilenamesGroupConfiguration = groupConfigurationService.findByGroupIdAndType(business.getId(), WindowsCompatibleFilenamesGroupConfiguration.class.getSimpleName());
        GroupConfiguration<?> retentionPolicyGroupConfiguration = groupConfigurationService.findByGroupIdAndType(business.getId(), RetentionPolicyGroupConfiguration.class.getSimpleName());


        assertEquals(mockStringConfiguration, forbiddenExtensionsGroupConfiguration);
        assertEquals(mockBooleanConfiguration, windowsCompatibleFilenamesGroupConfiguration);
        assertEquals(mockIntegerConfiguration, retentionPolicyGroupConfiguration);
    }

    @Test
    void should_throw_find_by_group_id_and_dtype_if_not_found() {
        Long businessId = business.getId();
        String className = ForbiddenExtensionsGroupConfiguration.class.getSimpleName();
        assertThrows(EtxWebException.class, () -> groupConfigurationService.findByGroupIdAndType(businessId, className));
    }

    @Test
    void should_find_by_group_id_and_type() {
        ForbiddenExtensionsGroupConfiguration mockStringConfiguration = mockForbiddenExtensionsGroupConfiguration(businessRoot, "");
        WindowsCompatibleFilenamesGroupConfiguration mockBooleanConfiguration = mockWindowsCompatibleFilenamesGroupConfiguration(business, false);
        RetentionPolicyGroupConfiguration mockIntegerConfiguration = mockRetentionPolicyGroupConfiguration(business, -1);

        given(groupConfigurationRepository.findFirstByGroupId(business.getId(), ForbiddenExtensionsGroupConfiguration.class.getSimpleName(), ForbiddenExtensionsGroupConfiguration.class))
                .willReturn(Optional.of(mockStringConfiguration));
        given(groupConfigurationRepository.findFirstByGroupId(business.getId(), WindowsCompatibleFilenamesGroupConfiguration.class.getSimpleName(), WindowsCompatibleFilenamesGroupConfiguration.class))
                .willReturn(Optional.of(mockBooleanConfiguration));
        given(groupConfigurationRepository.findFirstByGroupId(business.getId(), RetentionPolicyGroupConfiguration.class.getSimpleName(), RetentionPolicyGroupConfiguration.class))
                .willReturn(Optional.of(mockIntegerConfiguration));

        GroupConfiguration<String> forbiddenExtensionsGroupConfiguration = groupConfigurationService.findByGroupIdAndType(business.getId(), ForbiddenExtensionsGroupConfiguration.class)
                .orElse(null);
        GroupConfiguration<Boolean> windowsCompatibleFilenamesGroupConfiguration = groupConfigurationService.findByGroupIdAndType(business.getId(), WindowsCompatibleFilenamesGroupConfiguration.class)
                .orElse(null);
        GroupConfiguration<Integer> retentionPolicyGroupConfiguration = groupConfigurationService.findByGroupIdAndType(business.getId(), RetentionPolicyGroupConfiguration.class)
                .orElse(null);

        assertEquals(mockStringConfiguration, forbiddenExtensionsGroupConfiguration);
        assertEquals(mockBooleanConfiguration, windowsCompatibleFilenamesGroupConfiguration);
        assertEquals(mockIntegerConfiguration, retentionPolicyGroupConfiguration);
    }

    @Test
    void should_create_default_configuration_for_business() {
        groupConfigurationService.saveDefaults(business);

        verify(groupConfigurationRepository, times(1)).saveAll(groupConfigurationsArgumentCaptor.capture());

        List<GroupConfiguration<?>> groupConfigurations = groupConfigurationsArgumentCaptor.getValue();

        assertEquals(17, groupConfigurations.size());

        groupConfigurations.forEach(groupConfiguration -> {
            assertEquals(groupConfiguration.getClass().getSimpleName(), groupConfiguration.getDtype());
            assertEquals(business, groupConfiguration.getGroup());
        });
    }

    @Test
    void should_update_SignatureGroupConfiguration_configuration() {
        SignatureGroupConfiguration mockBooleanConfiguration = mocksignatureGroupConfiguration(business, false);

        given(groupRepository.findById(business.getId())).willReturn(Optional.of(business));
        given(groupConfigurationRepository.findFirstByGroupId(business.getId(), SignatureGroupConfiguration.class.getSimpleName(), SignatureGroupConfiguration.class))
                .willReturn(Optional.of(mockBooleanConfiguration));
        given(groupConfigurationRepository.save(mockBooleanConfiguration))
                .willReturn(mockBooleanConfiguration);

        BooleanGroupConfigurationSpec config = BooleanGroupConfigurationSpec.builder()
                .active(true)
                .build();
        assertTrue(groupConfigurationService.updateValue(user, business.getId(), SignatureGroupConfiguration.class, config).isActive());
    }
}

