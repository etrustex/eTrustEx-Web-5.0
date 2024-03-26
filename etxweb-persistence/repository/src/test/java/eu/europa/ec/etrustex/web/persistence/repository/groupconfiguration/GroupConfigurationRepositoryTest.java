package eu.europa.ec.etrustex.web.persistence.repository.groupconfiguration;

import eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils;
import eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.GroupConfiguration;
import eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.impl.ForbiddenExtensionsGroupConfiguration;
import eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.impl.RetentionPolicyGroupConfiguration;
import eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.impl.RetentionPolicyNotificationGroupConfiguration;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.PersistenceException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class GroupConfigurationRepositoryTest {
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    GroupConfigurationRepository<GroupConfiguration<?>> groupConfigurationRepository;

    Group business1;
    Group business2;

    @Autowired
    TestEntityManager testEntityManager;

    @BeforeEach
    public void init() {
        business1 = groupRepository.save(EntityTestUtils.mockGroup("B1", "TEST_BUSINESS_1", null));
        business2 = groupRepository.save(EntityTestUtils.mockGroup("B2", "TEST_BUSINESS_2", null));

        groupConfigurationRepository.save(ForbiddenExtensionsGroupConfiguration.builder()
                .stringValue("doc,xls")
                .group(business1)
                .build());

        groupConfigurationRepository.save(ForbiddenExtensionsGroupConfiguration.builder()
                .stringValue("zip,tar")
                .group(business2)
                .build());

        groupConfigurationRepository.save(RetentionPolicyGroupConfiguration.builder()
                .integerValue(1)
                .group(business1)
                .build()
        );

        groupConfigurationRepository.save(RetentionPolicyGroupConfiguration.builder()
                .integerValue(2)
                .group(business2)
                .build());

        groupConfigurationRepository.save(RetentionPolicyNotificationGroupConfiguration.builder()
                .integerValue(1)
                .group(business1)
                .build()
        );

        groupConfigurationRepository.save(RetentionPolicyNotificationGroupConfiguration.builder()
                .integerValue(2)
                .group(business2)
                .build());
    }

    @Test
    void should_find_all_subtypes() {

        Set<String> subtypes = new HashSet<>();
        subtypes.add(ForbiddenExtensionsGroupConfiguration.class.getSimpleName());

        Set<String> retrievedTypes = groupConfigurationRepository.findByGroupId(business1.getId()).stream()
                .map(GroupConfiguration::getClass)
                .map(Class::getSimpleName)
                .collect(Collectors.toSet());

        assertTrue(retrievedTypes.containsAll(subtypes));
    }

    @Test
    void should_not_allow_duplicate_for_same_group_and_configuration_type() {
        groupConfigurationRepository.save(ForbiddenExtensionsGroupConfiguration.builder()
                .stringValue("zzz")
                .group(business1)
                .build());

        PersistenceException exception = assertThrows(PersistenceException.class, () -> testEntityManager.flush());
        assertTrue(exception.getCause() instanceof ConstraintViolationException);

    }

    @Test
    void should_retrieve_retention_policy_groupconfigurations_by_group() {
        RetentionPolicyGroupConfiguration retentionPolicyGroupConfigurationB1 = groupConfigurationRepository
                .findFirstByGroupId(business1.getId(), RetentionPolicyGroupConfiguration.class.getSimpleName(), RetentionPolicyGroupConfiguration.class)
                .orElseThrow(() -> new Error("Retention policy configuration for business1 not found!"));
        RetentionPolicyGroupConfiguration retentionPolicyGroupConfigurationB2 = groupConfigurationRepository
                .findFirstByGroupId(business2.getId(), RetentionPolicyGroupConfiguration.class.getSimpleName(), RetentionPolicyGroupConfiguration.class)
                .orElseThrow(() -> new Error("Retention policy configuration for business2 not found!"));

        assertEquals(business1, retentionPolicyGroupConfigurationB1.getGroup());
        assertEquals(business2, retentionPolicyGroupConfigurationB2.getGroup());
    }

    @Test
    void should_retrieve_retention_policy_notification_groupconfigurations_by_group() {
        RetentionPolicyNotificationGroupConfiguration retentionPolicyNotificationGroupConfigurationB1 = groupConfigurationRepository
                .findFirstByGroupId(business1.getId(), RetentionPolicyNotificationGroupConfiguration.class.getSimpleName(), RetentionPolicyNotificationGroupConfiguration.class)
                .orElseThrow(() -> new Error("Retention policy configuration for business1 not found!"));
        RetentionPolicyNotificationGroupConfiguration retentionPolicyNotificationGroupConfigurationB2 = groupConfigurationRepository
                .findFirstByGroupId(business2.getId(), RetentionPolicyNotificationGroupConfiguration.class.getSimpleName(), RetentionPolicyNotificationGroupConfiguration.class)
                .orElseThrow(() -> new Error("Retention policy configuration for business2 not found!"));

        assertEquals(business1, retentionPolicyNotificationGroupConfigurationB1.getGroup());
        assertEquals(business2, retentionPolicyNotificationGroupConfigurationB2.getGroup());
    }

    @Test
    void should_return_forbidden_extensions_groupconfigurations_by_group() {
        ForbiddenExtensionsGroupConfiguration disabledExtensionsGroupConfiguration =
                groupConfigurationRepository.findFirstByGroupId(business1.getId(), ForbiddenExtensionsGroupConfiguration.class.getSimpleName(), ForbiddenExtensionsGroupConfiguration.class)
                        .orElseThrow(() -> new Error("DisabledExtensionsGroupConfiguration not found for business1!"));
        assertEquals(business1.getId(), disabledExtensionsGroupConfiguration.getGroup().getId());

        disabledExtensionsGroupConfiguration = groupConfigurationRepository.findFirstByGroupId(business2.getId(), ForbiddenExtensionsGroupConfiguration.class.getSimpleName(), ForbiddenExtensionsGroupConfiguration.class)
                .orElseThrow(() -> new Error("DisabledExtensionsGroupConfiguration not found for business2!"));

        assertEquals(business2.getId(), disabledExtensionsGroupConfiguration.getGroup().getId());
    }
}
