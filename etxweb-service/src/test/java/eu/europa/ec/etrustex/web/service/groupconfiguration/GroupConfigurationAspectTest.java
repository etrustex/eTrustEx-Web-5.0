package eu.europa.ec.etrustex.web.service.groupconfiguration;


import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.service.security.GroupService;
import eu.europa.ec.etrustex.web.service.validation.model.GroupSpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.mockBusiness;
import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.mockGroup;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"java:S112", "java:S100"}) /* Suppress Define and throw a dedicated exception instead of using a generic one & method names false positive. */
class GroupConfigurationAspectTest {

    @Mock
    GroupConfigurationService groupConfigurationService;
    @Mock
    GroupService groupService;

    private GroupConfigurationAspect groupConfigurationAspect;

    @BeforeEach
    public void setUp() {
        this.groupConfigurationAspect = new GroupConfigurationAspect(groupConfigurationService, groupService);
    }

    @Test
    void should_create_default_group_configuration_for_business() {
        Group business = mockBusiness();
        GroupSpec groupSpec = GroupSpec.builder()
                .identifier(business.getIdentifier())
                .type(business.getType())
                .build();

        given(groupService.findById(any())).willReturn(business);

        groupConfigurationAspect.createDefaultGroupConfiguration(groupSpec, business);
        verify(groupConfigurationService, times(1)).saveDefaults(business);
    }

    @Test
    void should_create_default_group_configuration_for_entity() {
        Group group = mockGroup();
        GroupSpec groupSpec = GroupSpec.builder()
                .identifier(group.getIdentifier())
                .type(group.getType())
                .build();

        given(groupService.findById(any())).willReturn(group);

        groupConfigurationAspect.createDefaultGroupConfiguration(groupSpec, group);
        verify(groupConfigurationService, times(1)).saveDefaults(group);
    }
}
