package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.persistence.entity.UserGuide;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.Role;
import eu.europa.ec.etrustex.web.persistence.repository.UserGuideRepository;
import eu.europa.ec.etrustex.web.service.security.RoleService;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.mockBusiness;
import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.mockRole;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
class UserGuideServiceImplTest {
    Group business = mockBusiness();
    Role operatorRole = mockRole(RoleName.OPERATOR);
    GroupType entity = GroupType.ENTITY;
    UserGuide userGuide = UserGuide.builder()
            .business(business).role(operatorRole).groupType(entity)
            .build();
    @Mock
    private UserGuideRepository userGuideRepository;
    @Mock
    private RoleService roleService;
    private UserGuideService userGuideService;

    @BeforeEach
    public void setUp() {
        userGuideService = new UserGuideServiceImpl(userGuideRepository, roleService);
    }


    @Test
    void should_find_By_Business_And_Role_And_GroupType() {
        given(userGuideRepository.findByBusinessIdAndRoleNameAndGroupType(business.getId(), operatorRole.getName(), entity))
                .willReturn(Optional.of(userGuide));


        assertEquals(userGuide, userGuideService.findByBusinessAndRoleNameAndGroupType(business.getId(), operatorRole.getName(), entity));
    }

    @Test
    void should_find_generic_guide_if_custom_not_present() {
        given(userGuideRepository.findByBusinessIdAndRoleNameAndGroupType(business.getId(), operatorRole.getName(), entity))
                .willReturn(Optional.empty());

        given(userGuideRepository.findByBusinessIdentifierAndRoleNameAndGroupType(GroupType.ROOT.toString(), operatorRole.getName(), entity))
                .willReturn(Optional.of(userGuide));


        assertEquals(userGuide, userGuideService.findByBusinessAndRoleNameAndGroupType(business.getId(), operatorRole.getName(), entity));
    }

    @Test
    void should_throw_EtxWebException_if_guide_not_found() {
        RoleName roleName = operatorRole.getName();
        given(userGuideRepository.findByBusinessIdAndRoleNameAndGroupType(business.getId(), roleName, entity))
                .willReturn(Optional.empty());

        given(userGuideRepository.findByBusinessIdentifierAndRoleNameAndGroupType(GroupType.ROOT.toString(), roleName, entity))
                .willReturn(Optional.empty());


        assertThrows(EtxWebException.class, () -> userGuideService.findByBusinessAndRoleNameAndGroupType(business.getId(), roleName, entity));
    }

    @Test
    void should_find_all_By_Business_id() {
        given(userGuideRepository.findAllByBusinessId(business.getId()))
                .willReturn( Collections.nCopies(3, userGuide));

        assertEquals(3, userGuideService.findAllByBusinessId(business.getBusinessId()).size());
    }
}