package eu.europa.ec.etrustex.web.persistence.repository.security;

import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import eu.europa.ec.etrustex.web.exchange.model.UserExportItem;
import eu.europa.ec.etrustex.web.exchange.model.UserListItem;
import eu.europa.ec.etrustex.web.persistence.entity.UserProfile;
import eu.europa.ec.etrustex.web.persistence.entity.security.GrantedAuthority;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.Role;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.*;
import java.util.stream.Collectors;

import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserProfileRepositoryTest {
    private static final String USER_ENTITY_OPERATOR_ECAS_ID_1 = "operator1";
    private static final String USER_ENTITY_OPERATOR_ECAS_ID_2 = "operator2";
    private static final String USER_ENTITY_OPERATOR_ECAS_ID_2_2 = "operator2_2";
    private static final String USER_ENTITY_ADMIN_ECAS_ID_1 = "entityAdmin1";
    private static final String USER_ENTITY_ADMIN_ECAS_ID_2 = "entityAdmin2";
    private static final String USER_BUSINESS_ADMIN_ECAS_ID_1 = "businessAdmin1";
    private static final String USER_BUSINESS_ADMIN_ECAS_ID_2 = "businessAdmin2";
    private static final String USER_SYSTEM_ADMIN_ECAS_ID = "systemAdmin";
    private static final String USER_OFFICIAL_ECAS_ID = "officialInCharge";
    public static final String GROUP_ENTITY_IDENTIFIER_1 = "GROUP_ENTITY_IDENTIFIER_1";
    public static final String GROUP_ENTITY_IDENTIFIER_2 = "GROUP_ENTITY_IDENTIFIER_2";

    public static final String GROUP_BUSINESS_IDENTIFIER_1 = "GROUP_BUSINESS_IDENTIFIER_1";
    public static final String GROUP_BUSINESS_IDENTIFIER_2 = "GROUP_BUSINESS_IDENTIFIER_2";
    public static final String DOMAIN = "@domain.eu";

    @Autowired
    private UserProfileRepository userProfileRepository;
    @Autowired
    private GrantedAuthorityRepository grantedAuthorityRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GroupRepository groupRepository;

    private Role groupAdminRole;
    private Role userRole;
    private Role systemAdminRole;
    private Role officialInChargeRole;

    private Group rootGroup;
    private Group business1;
    private Group business2;
    private Group entity1;
    private Group entity2;

    private User operator1;
    private User operator2;
    private User operator22;
    private User entityAdmin1;
    private User entityAdmin2;
    private User businessAdmin1;
    private User businessAdmin2;
    private User systemAdmin;
    private User officialInCharge;

    private Validator validator;

    @BeforeEach
    public void init() {
        initGroups();
        initRoles();
        initUsers();
        initGrantedAuthorities();
        initUserProfiles();

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @AfterEach
    public void cleanup() {
        userProfileRepository.deleteAll();
        grantedAuthorityRepository.deleteAll();
        userRepository.deleteAll();
        groupRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void should_find_by_user_and_business() {
        UserProfile userProfile = userProfileRepository.findByUserAndGroup(operator1, entity1);

        assertEquals(operator1, userProfile.getUser());
        assertEquals(entity1, userProfile.getGroup());
    }

    @Test
    void should_find_by_user_ecasId() {
        assertEquals(1, userProfileRepository.findByUserEcasId(USER_ENTITY_OPERATOR_ECAS_ID_1).count());
    }

    @Test
    void should_not_find_by_user_and_business() {
        UserProfile userProfile = userProfileRepository.findByUserAndGroup(operator1, business2);

        assertNull(userProfile);
    }

    @Test
    void should_return_true_if_user_profile_exists_by_user_ecas_id_and_business_id() {
        assertTrue(userProfileRepository.existsByUserEcasIdIgnoreCaseAndGroupId(USER_ENTITY_OPERATOR_ECAS_ID_1, entity1.getId()));
        assertTrue(userProfileRepository.existsByUserEcasIdIgnoreCaseAndGroupId(USER_ENTITY_ADMIN_ECAS_ID_1, entity1.getId()));
        assertTrue(userProfileRepository.existsByUserEcasIdIgnoreCaseAndGroupId(USER_BUSINESS_ADMIN_ECAS_ID_1, business1.getId()));
        assertTrue(userProfileRepository.existsByUserEcasIdIgnoreCaseAndGroupId(USER_SYSTEM_ADMIN_ECAS_ID, rootGroup.getId()));
    }

    @Test
    void should_return_false_if_user_profile_not_exists_by_user_ecas_id_and_business_id() {
        assertFalse(userProfileRepository.existsByUserEcasIdIgnoreCaseAndGroupId(USER_ENTITY_OPERATOR_ECAS_ID_1, business2.getId()));
        assertFalse(userProfileRepository.existsByUserEcasIdIgnoreCaseAndGroupId(USER_ENTITY_ADMIN_ECAS_ID_1, business2.getId()));
        assertFalse(userProfileRepository.existsByUserEcasIdIgnoreCaseAndGroupId(USER_BUSINESS_ADMIN_ECAS_ID_1, business2.getId()));
        assertFalse(userProfileRepository.existsByUserEcasIdIgnoreCaseAndGroupId(USER_BUSINESS_ADMIN_ECAS_ID_1, rootGroup.getId()));
    }

    @Test
    void should_return_true_if_user_profile_exists_by_user_ecas_id() {
        assertTrue(userProfileRepository.existsByUserEcasId(USER_SYSTEM_ADMIN_ECAS_ID));
    }

    @Test
    void should_return_false_if_user_profile_exists_by_user_ecas_id() {
        assertFalse(userProfileRepository.existsByUserEcasId("other_id"));
    }

    @Test
    void should_get_by_role() {
        Page<UserListItem> listItemPage = userProfileRepository.getByRoleNameAndNameOrEcasIdContains(RoleName.OFFICIAL_IN_CHARGE, "", PageRequest.of(0, 10));

        assertThat(listItemPage.getTotalElements()).isOne();
        assertThat(listItemPage.getContent().size()).isOne();
        assertEquals(officialInCharge.getEcasId(), listItemPage.getContent().get(0).getEcasId());
    }

    @Test
    void should_get_by_role_and_group_id() {
        Page<UserListItem> operatorListItemPageBusiness = userProfileRepository.getByRoleNameAndGroupIdAndNameOrEcasIdContains(RoleName.OPERATOR, entity1.getId(), "", PageRequest.of(0, 10));

        assertThat(operatorListItemPageBusiness.getTotalElements()).isOne();
        assertThat(operatorListItemPageBusiness.getContent().size()).isOne();
        assertEquals(operator1.getEcasId(), operatorListItemPageBusiness.getContent().get(0).getEcasId());
    }

    @Test
    void should_get_by_business() {
        Page<UserListItem> userListItemPageBusiness = userProfileRepository.findByGroupIdAndNameOrEcasIdContains(business2.getId(), "", PageRequest.of(0, 10,
                JpaSort.unsafe(
                        Sort.Direction.DESC,
                        "auditingEntity.createdDate"
                )
        ));

        Collection<String> business2EcasIds = Collections.singletonList(businessAdmin2.getEcasId());

        assertEquals(business2EcasIds.size(), userListItemPageBusiness.getContent().size());

        assertTrue(
                business2EcasIds.containsAll(
                        userListItemPageBusiness.stream()
                                .map(UserListItem::getEcasId)
                                .collect(Collectors.toList()))
        );
    }

    @Test
    void should_get_by_business_filtering() {
        Page<UserListItem> userListItemPageBusiness = userProfileRepository.findByGroupIdAndNameOrEcasIdContains(entity2.getId(), "perator", PageRequest.of(0, 10));

        Collection<String> entity2OperatorsEcasIds = Arrays.asList(operator2.getEcasId(), operator22.getEcasId());

        assertEquals(entity2OperatorsEcasIds.size(), userListItemPageBusiness.getTotalElements());

        assertTrue(
                entity2OperatorsEcasIds.containsAll(
                        userListItemPageBusiness.stream()
                                .map(UserListItem::getEcasId)
                                .collect(Collectors.toList()))
        );
    }

    @Test
    void should_get_by_root() {
        Page<UserListItem> operatorListItemPageBusiness = userProfileRepository.findByGroupIdAndNameOrEcasIdContains(rootGroup.getId(), "", PageRequest.of(0, 10));

        assertEquals(
                systemAdmin.getEcasId(),
                operatorListItemPageBusiness.stream()
                        .filter(u -> u.getEcasId().equals(USER_SYSTEM_ADMIN_ECAS_ID))
                        .findFirst().orElseThrow(() -> new Error("Sys admin not found!"))
                        .getEcasId()
        );
        assertEquals(
                officialInCharge.getEcasId(),
                operatorListItemPageBusiness.stream()
                        .filter(u -> u.getEcasId().equals(USER_OFFICIAL_ECAS_ID))
                        .findFirst().orElseThrow(() -> new Error("Sys admin not found!"))
                        .getEcasId()
        );
    }


    @Test
    void should_export_by_business() {
        UserProfile userProfile = userProfileRepository.findByUserAndGroup(operator1, entity1);
        userProfile.setNewMessageNotifications(true);
        userProfile.setMessageStatusForSenderNotifications(false);
        userProfileRepository.save(userProfile);

        List<UserExportItem> userExportItemList = userProfileRepository.exportByBusinessId(business1.getId()).collect(Collectors.toList());

        UserExportItem expected = UserExportItem.builder()
                .entityIdentifier(entity1.getIdentifier())
                .entityName(entity1.getName())
                .groupType(GroupType.ENTITY)
                .name(userProfile.getUser().getName())
                .euLoginEmail("")
                .alternativeEmail(userProfile.getUser().getEcasId() + DOMAIN)
                .euLoginId(userProfile.getUser().getEcasId())
                .useAlternativeEmail(false)
                .messageNotification(true)
                .statusNotification(false)
                .retentionWarningNotification(false)
                .status(true)
                .roleName(RoleName.OPERATOR)
                .build();

        assertTrue(userExportItemList.contains(expected));
    }

    @Test
    void find_configured_email_by_business_with_active_new_message_notification() {
        UserProfile userProfile = userProfileRepository.findByUserAndGroup(operator1, entity1);
        userProfile.setNewMessageNotifications(true);
        userProfile.setMessageStatusForSenderNotifications(false);
        userProfile.setAlternativeEmailUsed(true);
        userProfileRepository.save(userProfile);


        List<String> emails = userProfileRepository.findEmailsByGroupIdOrBusinessId(business1.getId()).collect(Collectors.toList());

        assertEquals(1, emails.size());
        assertEquals(emails.get(0), operator1.getEcasId() + DOMAIN);
    }

    @Test
    void find_configured_email_by_business_with_active_status_notification() {
        UserProfile userProfile = userProfileRepository.findByUserAndGroup(operator1, entity1);
        userProfile.setNewMessageNotifications(false);
        userProfile.setMessageStatusForSenderNotifications(true);
        userProfile.setAlternativeEmailUsed(true);
        userProfileRepository.save(userProfile);


        List<String> emails = userProfileRepository.findEmailsByGroupIdOrBusinessId(business1.getId()).collect(Collectors.toList());

        assertEquals(1, emails.size());
        assertEquals(emails.get(0), operator1.getEcasId() + DOMAIN);
    }

    @Test
    void should_not_find_emails_for_non_configured_notifications() {
        UserProfile userProfile = userProfileRepository.findByUserAndGroup(operator1, entity1);
        userProfile.setNewMessageNotifications(false);
        userProfile.setMessageStatusForSenderNotifications(false);
        userProfileRepository.save(userProfile);


        List<String> emails = userProfileRepository.findEmailsByGroupIdOrBusinessId(business1.getId()).collect(Collectors.toList());

        assertEquals(0, emails.size());
    }

    @Test
    void should_delete_by_user_id_and_group_id() {
        userProfileRepository.deleteByUserEcasIdAndGroupId(operator1.getEcasId(), entity1.getId());

        UserProfile userProfile = userProfileRepository.findByUserAndGroup(operator1, entity1);

        assertNull(userProfile);
    }

    @Test
    void should_retrieve_new_message_notifications() {
        UserProfile operator1Profile = userProfileRepository.findByUserAndGroup(operator1, entity1);
        operator1Profile.setNewMessageNotifications(true);

        List<UserProfile> toNotify = userProfileRepository.findByNewMessageNotificationsIsTrueAndGroup(operator1Profile.getGroup());
        assertEquals(1, toNotify.size());
        assertTrue(toNotify.contains(operator1Profile));
    }

    @Test
    void should_retrieve_new_status_for_sender_notifications() {
        UserProfile operator2Profile = userProfileRepository.findByUserAndGroup(operator2, entity2);
        operator2Profile.setMessageStatusForSenderNotifications(true);

        List<UserProfile> toNotify = userProfileRepository.findByMessageStatusForSenderNotificationsIsTrueAndGroup(operator2Profile.getGroup());
        assertEquals(1, toNotify.size());
        assertTrue(toNotify.contains(operator2Profile));
    }

    @Test
    void should_fail_validation_with_invalid_email_address() {
        UserProfile userProfile =UserProfile.builder()
                .user(operator1)
                .group(entity1)
                .alternativeEmail("foodomain.eu")
                .build();

        Set<ConstraintViolation<UserProfile>> violations = validator.validate(userProfile);
        assertFalse(violations.isEmpty());
    }


    private void initGroups() {
        rootGroup = groupRepository.save(mockGroup("ROOT", "root", null, GroupType.ROOT));

        business1 = groupRepository.save(mockGroup(GROUP_BUSINESS_IDENTIFIER_1, "business 1", rootGroup, GroupType.BUSINESS));
        business2 = groupRepository.save(mockGroup(GROUP_BUSINESS_IDENTIFIER_2, "business 2", rootGroup, GroupType.BUSINESS));

        entity1 = groupRepository.save(mockGroup(GROUP_ENTITY_IDENTIFIER_1, "ENTITY 1", business1, GroupType.ENTITY, "functional@test.com", null, null));
        entity2 = groupRepository.save(mockGroup(GROUP_ENTITY_IDENTIFIER_2, "ENTITY 2", business2, GroupType.ENTITY));
    }

    private void initRoles() {
        groupAdminRole = roleRepository.save(Role.builder().name(RoleName.GROUP_ADMIN).description(RoleName.GROUP_ADMIN.toString()).build());
        userRole = roleRepository.save(Role.builder().name(RoleName.OPERATOR).description(RoleName.OPERATOR.toString()).build());
        systemAdminRole = roleRepository.save(Role.builder().name(RoleName.SYS_ADMIN).description(RoleName.SYS_ADMIN.toString()).build());
        officialInChargeRole = roleRepository.save(Role.builder().name(RoleName.OFFICIAL_IN_CHARGE).description(RoleName.OFFICIAL_IN_CHARGE.toString()).build());
    }

    private void initUsers() {
        operator1 = userRepository.save(mockUser(USER_ENTITY_OPERATOR_ECAS_ID_1, "name"));
        operator2 = userRepository.save(mockUser(USER_ENTITY_OPERATOR_ECAS_ID_2, "name"));
        operator22 = userRepository.save(mockUser(USER_ENTITY_OPERATOR_ECAS_ID_2_2, "name"));

        entityAdmin1 = userRepository.save(mockUser(USER_ENTITY_ADMIN_ECAS_ID_1, "name"));
        entityAdmin2 = userRepository.save(mockUser(USER_ENTITY_ADMIN_ECAS_ID_2, "name"));

        businessAdmin1 = userRepository.save(mockUser(USER_BUSINESS_ADMIN_ECAS_ID_1, "name"));
        businessAdmin2 = userRepository.save(mockUser(USER_BUSINESS_ADMIN_ECAS_ID_2, "name"));

        systemAdmin = userRepository.save(mockUser(USER_SYSTEM_ADMIN_ECAS_ID, "name"));
        officialInCharge = userRepository.save(mockUser(USER_OFFICIAL_ECAS_ID, "name"));
    }


    private void initUserProfiles() {
        userProfileRepository.save(mockUserProfile(operator1, entity1));
        userProfileRepository.save(mockUserProfile(operator2, entity2));
        userProfileRepository.save(mockUserProfile(operator22, entity2));

        userProfileRepository.save(mockUserProfile(entityAdmin1, entity1));
        userProfileRepository.save(mockUserProfile(entityAdmin2, entity2));

        userProfileRepository.save(mockUserProfile(businessAdmin1, business1));
        userProfileRepository.save(mockUserProfile(businessAdmin2, business2));

        userProfileRepository.save(mockUserProfile(systemAdmin, rootGroup));
        userProfileRepository.save(mockUserProfile(officialInCharge, rootGroup));
    }

    private void initGrantedAuthorities() {
        grantedAuthorityRepository.save(GrantedAuthority.builder().user(operator1).group(entity1).role(userRole).build());
        grantedAuthorityRepository.save(GrantedAuthority.builder().user(operator2).group(entity2).role(userRole).build());
        grantedAuthorityRepository.save(GrantedAuthority.builder().user(operator22).group(entity2).role(userRole).build());

        grantedAuthorityRepository.save(GrantedAuthority.builder().user(entityAdmin1).group(entity1).role(groupAdminRole).build());
        grantedAuthorityRepository.save(GrantedAuthority.builder().user(entityAdmin2).group(entity2).role(groupAdminRole).build());

        grantedAuthorityRepository.save(GrantedAuthority.builder().user(businessAdmin1).group(business1).role(groupAdminRole).build());
        grantedAuthorityRepository.save(GrantedAuthority.builder().user(businessAdmin2).group(business2).role(groupAdminRole).build());

        grantedAuthorityRepository.save(GrantedAuthority.builder().user(systemAdmin).group(rootGroup).role(systemAdminRole).build());
        grantedAuthorityRepository.save(GrantedAuthority.builder().user(officialInCharge).group(rootGroup).role(officialInChargeRole).build());
    }
}
