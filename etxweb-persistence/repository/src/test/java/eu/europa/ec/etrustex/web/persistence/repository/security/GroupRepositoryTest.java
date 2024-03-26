package eu.europa.ec.etrustex.web.persistence.repository.security;

import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collections;

import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.mockGroup;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class GroupRepositoryTest {

    @Autowired
    GroupRepository groupRepository;
    @Autowired
    GrantedAuthorityRepository grantedAuthorityRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;

    private Group business;
    private Group emptyBusiness;
    private Group root;

    public static final String PARENT_IDENTIFIER = "BUSINESS_IDENTIFIER";

    @BeforeEach
    public void init() {
        this.root = groupRepository.save(mockGroup("root", "root", null, GroupType.ROOT));
        this.business = groupRepository.save(mockGroup(PARENT_IDENTIFIER, "parent", root, GroupType.BUSINESS));
        this.emptyBusiness = groupRepository.save(mockGroup("emptyBusiness", "noChildren", root, GroupType.BUSINESS));

        String[] childrenNames = {"child1", "child2", "child3"};

        Arrays.asList(childrenNames).forEach(s -> groupRepository.save(mockGroup(s, s, business, GroupType.ENTITY)));

    }

    @Test
    void should_count_by_parent() {
        assertEquals(3, groupRepository.countByParentId(this.business.getId()));
        assertEquals(2, groupRepository.countByParentId(this.root.getId()));
        assertEquals(0, groupRepository.countByParentId(this.emptyBusiness.getId()));
    }

    @Test
    void should_count_the_businesses() {
        assertEquals(2, groupRepository.findByGroupTypeAndParentAndGroupIdOrGroupName(GroupType.BUSINESS, root, "", PageRequest.of(0, 10)).getTotalElements());
    }

    @Test
    void should_find_a_business() {
        assertTrue(groupRepository.findByIdAndType(this.business.getId(), GroupType.BUSINESS).isPresent());
    }

    @Test
    void should_not_find_the_group_when_type_does_not_match() {
        assertFalse(groupRepository.findByIdAndType(this.business.getId(), GroupType.ENTITY).isPresent());
    }

    @Test
    void should_find_by_business() {
        assertEquals(3, groupRepository.findByParentId(this.business.getId()).count());
    }

    @Test
    void should_not_find_any_records_when_no_user_configured_or_no_messages() {
        assertEquals(0, groupRepository.getTotalRecordsFromGrantedAuthorityAndMessageAndMessageSummary(this.business.getId()));
    }

    @Test
    void should_find_by_id_or_name_like() {
        assertEquals(3, groupRepository.findByGroupTypeAndParentAndGroupIdOrGroupName(GroupType.ENTITY, business, "child", PageRequest.of(0, 10)).getTotalElements());
    }

    @Test
    void should_find_by_group_ids_and_by_id_or_name_like() {
        assertEquals(1, groupRepository.findByGroupTypeAndParentAndGroupIdsAndGroupIdOrGroupName(GroupType.ENTITY, business, Collections.nCopies(1, "child1"), "child", PageRequest.of(0, 10)).getTotalElements());
    }

    @Test
    void should_return_true_if_group_exists_by_id_and_parend_id() {
        assertTrue(groupRepository.existsByIdAndParentId(business.getId(), root.getId()));
    }

}
