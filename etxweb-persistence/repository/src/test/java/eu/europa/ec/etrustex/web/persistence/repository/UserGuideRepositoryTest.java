package eu.europa.ec.etrustex.web.persistence.repository;

import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import eu.europa.ec.etrustex.web.persistence.entity.UserGuide;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.Role;
import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.mockGroup;
import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.mockRole;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserGuideRepositoryTest {
    private Role operatorRole;

    private Group business1;

    @Autowired
    private UserGuideRepository userGuideRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private GroupRepository groupRepository;

    Resource operatorGuideFile = new ClassPathResource("user-guides/EtrustexWeb_UserGuide.pdf");

    @BeforeEach
    public void init() throws IOException {

        assertTrue(operatorGuideFile.isFile());

        operatorRole = roleRepository.save(mockRole(RoleName.OPERATOR));

        Group root = groupRepository.save(mockGroup("ROOT", "ROOT", null, GroupType.ROOT));
        business1 = groupRepository.save(mockGroup("BUSINESS_1", "BUSINESS_1", root, GroupType.BUSINESS));

        userGuideRepository.save(UserGuide.builder()
                .groupType(GroupType.ENTITY)
                .role(operatorRole)
                .business(business1)
                .binary(StreamUtils.copyToByteArray(operatorGuideFile.getInputStream()))
                .filename(operatorGuideFile.getFilename())
                .build());
    }


    @Test
    void should_findByBusinessIdAndRoleNameAndGroupType() {
        Optional<UserGuide> userGuide = userGuideRepository.findByBusinessIdAndRoleNameAndGroupType(business1.getId(), operatorRole.getName(), GroupType.ENTITY);

        assertTrue(userGuide.isPresent());
    }

    @Test
    void should_findByBusinessId() throws IOException {
        userGuideRepository.save(UserGuide.builder()
                .groupType(GroupType.BUSINESS)
                .role(operatorRole)
                .business(business1)
                .binary(StreamUtils.copyToByteArray(operatorGuideFile.getInputStream()))
                .filename("testFileName.pdf")
                .build());
        List<UserGuide> userGuides = userGuideRepository.findAllByBusinessId(business1.getId());

        assertEquals(2, userGuides.size());
    }
}