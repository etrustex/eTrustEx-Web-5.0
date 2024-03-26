package eu.europa.ec.etrustex.web.service.security;

import eu.europa.ec.etrustex.web.common.EtrustexWebProperties;
import eu.europa.ec.etrustex.web.exchange.model.SearchItem;
import eu.europa.ec.etrustex.web.exchange.model.UserListItem;
import eu.europa.ec.etrustex.web.persistence.entity.UserProfile;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.persistence.repository.AlertUserStatusRepository;
import eu.europa.ec.etrustex.web.persistence.repository.MessageSummaryUserStatusRepository;
import eu.europa.ec.etrustex.web.persistence.repository.MessageUserStatusRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.UserProfileRepository;
import eu.europa.ec.etrustex.web.service.CSVService;
import eu.europa.ec.etrustex.web.service.dto.LdapUserDto;
import eu.europa.ec.etrustex.web.service.validation.model.BulkUserProfileSpec;
import eu.europa.ec.etrustex.web.service.validation.model.CreateUserProfileSpec;
import eu.europa.ec.etrustex.web.service.validation.model.UserProfileSpec;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.util.exchange.model.EntitySpec;
import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.*;
import static eu.europa.ec.etrustex.web.util.exchange.model.GroupType.ROOT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"java:S3252", "java:S100"})
class UserProfileServiceImplTest {
    private static final String USER_ENTITY_OPERATOR_ECAS_ID_1 = "operator1";
    private static final String USER_ENTITY_ADMIN_ECAS_ID_1 = "entityAdmin1";
    private static final String USER_BUSINESS_ADMIN_ECAS_ID_1 = "businessAdmin1";
    private static final String USER_SYSTEM_ADMIN_ECAS_ID = "systemAdmin";
    private static final String USER_NAME = "user_name";
    private static final String NAME = "_name";
    private static final String SYSTEM = "System";

    private static final Long GROUP_ENTITY_ID_1 = 1L;
    private static final Long GROUP_BUSINESS_ID_1 = 2L;
    private static final Long GROUP_ROOT_ID = 0L;

    @Mock
    private UserProfileRepository userProfileRepository;
    @Mock
    private UserService userService;
    @Mock
    private GroupService groupService;
    @Mock
    private MessageSummaryUserStatusRepository messageSummaryUserStatusRepository;
    @Mock
    private MessageUserStatusRepository messageUserStatusRepository;
    @Mock
    private GrantedAuthorityService grantedAuthorityService;
    @Mock
    private CSVService csvService;

    @Mock
    private EtrustexWebProperties etrustexWebProperties;
    @Mock
    private AlertUserStatusRepository alertUserStatusRepository;

    private UserProfileService userProfileService;

    private Group rootGroup;
    private Group business1;
    private Group entity1;

    private User operator1;
    private User entityAdmin1;
    private User businessAdmin1;
    private User systemAdmin;

    private BulkUserProfileSpec userProfileSpecGlobal;

    private List<UserListItem> userListItems;

    @BeforeEach
    void setUp() {
        userProfileService = new UserProfileServiceImpl(userProfileRepository, userService, groupService, messageSummaryUserStatusRepository, messageUserStatusRepository, grantedAuthorityService, csvService, etrustexWebProperties, alertUserStatusRepository);
        initGroups();
        initUsers();

        userProfileSpecGlobal = BulkUserProfileSpec.builder()
                .ecasId("ecasId")
                .groupId(12L)
                .name("test")
                .isAdmin(true)
                .isOperator(false)
                .build();

    }

    @Test
    void should_find_by_user_and_business() {
        given(userProfileRepository.findByUserAndGroup(operator1, business1)).willReturn(mockUserProfile(operator1, business1));

        UserProfile userProfile = userProfileService.findUserProfileByUserAndGroup(operator1, business1);

        assertEquals(operator1, userProfile.getUser());
    }

    @Test
    void should_not_find_by_user_and_business() {
        given(userProfileRepository.findByUserAndGroup(operator1, business1)).willReturn(null);
        assertThrows(EtxWebException.class, () -> userProfileService.findUserProfileByUserAndGroup(operator1, business1));
    }

    @Test
    void should_find_by_user_and_entity_and_business() {
        EntitySpec entitySpec = EntitySpec.builder()
                .entityIdentifier(entity1.getIdentifier())
                .businessIdentifier(business1.getIdentifier())
                .build();

        given(groupService.findByIdentifierAndParentIdentifier(entity1.getIdentifier(), business1.getIdentifier())).willReturn(entity1);
        given(userProfileRepository.findByUserAndGroup(operator1, entity1)).willReturn(mockUserProfile(operator1, entity1));

        UserProfile userProfile = userProfileService.findByUserAndGroupIdentifierAndBusinessIdentifier(operator1, entitySpec);

        assertEquals(operator1, userProfile.getUser());
    }

    @Test
    void should_raise_find_by_user_and_entity_and_business() {
        EntitySpec entitySpec = EntitySpec.builder()
                .entityIdentifier(entity1.getIdentifier())
                .businessIdentifier(business1.getIdentifier())
                .build();

        given(groupService.findByIdentifierAndParentIdentifier(entity1.getIdentifier(), business1.getIdentifier())).willReturn(mockGroup());
        assertThrows(ResponseStatusException.class, () -> userProfileService.findByUserAndGroupIdentifierAndBusinessIdentifier(operator1, entitySpec));

        given(groupService.findByIdentifierAndParentIdentifier(entity1.getBusinessIdentifier(), ROOT.name())).willReturn(business1);
        assertThrows(ResponseStatusException.class, () -> userProfileService.findByUserAndGroupIdentifierAndBusinessIdentifier(operator1, entitySpec));
    }


    @Test
    void should_get_the_user_profile_by_ecas_id() {
        String ecasId = "ecasId";
        UserProfile userProfile = UserProfile.builder()
                .build();
        given(userProfileRepository.findByUserEcasId(ecasId)).willReturn(Stream.of(userProfile));
        assertEquals(userProfile, userProfileService.findByUserEcasId(ecasId).findFirst().orElseThrow(() -> new Error("User profile not found!")));
    }


    @Test
    void should_create_for_operator() {
        UserProfile userProfile = mockUserProfile(operator1, entity1);
        CreateUserProfileSpec userProfileSpec = CreateUserProfileSpec.builder()
                .ecasId(USER_ENTITY_OPERATOR_ECAS_ID_1)
                .groupId(GROUP_ENTITY_ID_1)
                .alternativeEmail(userProfile.getAlternativeEmail())
                .alternativeEmailUsed(userProfile.isAlternativeEmailUsed())
                .build();

        given(groupService.findById(GROUP_ENTITY_ID_1)).willReturn(entity1);
        given(etrustexWebProperties.isDevEnvironment()).willReturn(true);
        given(userService.findOptionalByEcasId(anyString())).willReturn(Optional.of(operator1));
        given(userProfileRepository.save(userProfile)).willReturn(userProfile);

        assertEquals(userProfile, userProfileService.create(userProfileSpec));
    }


    @Test
    void should_create_for_entity_admin() {
        UserProfile userProfile = mockUserProfile(entityAdmin1, entity1);
        CreateUserProfileSpec userProfileSpec = CreateUserProfileSpec.builder()
                .ecasId(USER_ENTITY_ADMIN_ECAS_ID_1)
                .groupId(GROUP_ENTITY_ID_1)
                .alternativeEmail(userProfile.getAlternativeEmail())
                .alternativeEmailUsed(userProfile.isAlternativeEmailUsed())
                .build();

        given(groupService.findById(GROUP_ENTITY_ID_1)).willReturn(entity1);
        given(etrustexWebProperties.isDevEnvironment()).willReturn(true);
        given(userService.findOptionalByEcasId(anyString())).willReturn(Optional.of(entityAdmin1));
        given(userProfileRepository.save(userProfile)).willReturn(userProfile);

        assertEquals(userProfile, userProfileService.create(userProfileSpec));
    }

    @Test
    void should_create_for_business_admin() {
        UserProfile userProfile = mockUserProfile(businessAdmin1, business1);
        CreateUserProfileSpec userProfileSpec = CreateUserProfileSpec.builder()
                .ecasId(USER_BUSINESS_ADMIN_ECAS_ID_1)
                .groupId(GROUP_BUSINESS_ID_1)
                .alternativeEmail(userProfile.getAlternativeEmail())
                .alternativeEmailUsed(userProfile.isAlternativeEmailUsed())
                .build();

        given(groupService.findById(GROUP_BUSINESS_ID_1)).willReturn(business1);
        given(etrustexWebProperties.isDevEnvironment()).willReturn(true);
        given(userService.findOptionalByEcasId(anyString())).willReturn(Optional.of(businessAdmin1));
        given(userProfileRepository.save(userProfile)).willReturn(userProfile);

        assertEquals(userProfile, userProfileService.create(userProfileSpec));
    }

    @Test
    void should_create_for_system_admin() {
        UserProfile userProfile = mockUserProfile(systemAdmin, rootGroup);
        CreateUserProfileSpec userProfileSpec = CreateUserProfileSpec.builder()
                .ecasId(USER_SYSTEM_ADMIN_ECAS_ID)
                .groupId(GROUP_ROOT_ID)
                .alternativeEmail(userProfile.getAlternativeEmail())
                .alternativeEmailUsed(userProfile.isAlternativeEmailUsed())
                .build();

        given(groupService.findById(GROUP_ROOT_ID)).willReturn(rootGroup);
        given(etrustexWebProperties.isDevEnvironment()).willReturn(true);
        given(userService.findOptionalByEcasId(anyString())).willReturn(Optional.of(systemAdmin));
        given(userProfileRepository.save(userProfile)).willReturn(userProfile);

        assertEquals(userProfile, userProfileService.create(userProfileSpec));
    }

    @Test
    void should_update_entity_admin() {
        UserProfile userProfile = mockUserProfile(entityAdmin1, entity1);
        UserProfileSpec userProfileSpec = UserProfileSpec.builder()
                .ecasId(USER_ENTITY_ADMIN_ECAS_ID_1)
                .groupId(GROUP_ENTITY_ID_1)
                .alternativeEmail(userProfile.getAlternativeEmail())
                .alternativeEmailUsed(userProfile.isAlternativeEmailUsed())
                .build();

        given(groupService.findById(GROUP_ENTITY_ID_1)).willReturn(entity1);
        given(etrustexWebProperties.isDevEnvironment()).willReturn(true);
        given(userService.findOptionalByEcasId(anyString())).willReturn(Optional.of(entityAdmin1));
        given(userProfileRepository.save(userProfile)).willReturn(userProfile);

        assertEquals(userProfile, userProfileService.update(userProfileSpec));
    }

    @Test
    void should_update_business_admin() {
        UserProfile userProfile = mockUserProfile(businessAdmin1, business1);
        UserProfileSpec userProfileSpec = UserProfileSpec.builder()
                .ecasId(USER_BUSINESS_ADMIN_ECAS_ID_1)
                .groupId(GROUP_BUSINESS_ID_1)
                .alternativeEmail(userProfile.getAlternativeEmail())
                .alternativeEmailUsed(userProfile.isAlternativeEmailUsed())
                .build();

        given(groupService.findById(GROUP_BUSINESS_ID_1)).willReturn(business1);
        given(etrustexWebProperties.isDevEnvironment()).willReturn(true);
        given(userService.findOptionalByEcasId(anyString())).willReturn(Optional.of(businessAdmin1));
        given(userProfileRepository.save(userProfile)).willReturn(userProfile);

        assertEquals(userProfile, userProfileService.update(userProfileSpec));
    }

    @Test
    void should_update_system_admin() {
        UserProfile userProfile = mockUserProfile(systemAdmin, rootGroup);
        UserProfileSpec userProfileSpec = UserProfileSpec.builder()
                .ecasId(USER_SYSTEM_ADMIN_ECAS_ID)
                .groupId(GROUP_ROOT_ID)
                .alternativeEmail(userProfile.getAlternativeEmail())
                .build();

        given(groupService.findById(GROUP_ROOT_ID)).willReturn(rootGroup);
        given(etrustexWebProperties.isDevEnvironment()).willReturn(true);
        given(userService.findOptionalByEcasId(anyString())).willReturn(Optional.of(systemAdmin));
        given(userProfileRepository.save(userProfile)).willReturn(userProfile);

        assertEquals(userProfile, userProfileService.update(userProfileSpec));
    }

    @Test
    void should_create_when_user_does_not_exist_yet() {
        UserProfile userProfile = mockUserProfile(operator1, rootGroup);
        CreateUserProfileSpec userProfileSpec = CreateUserProfileSpec.builder()
                .ecasId(USER_SYSTEM_ADMIN_ECAS_ID)
                .groupId(GROUP_ROOT_ID)
                .name("Name")
                .euLoginEmailAddress("")
                .alternativeEmail(userProfile.getAlternativeEmail())
                .build();

        given(groupService.findById(GROUP_ROOT_ID)).willReturn(rootGroup);
        given(etrustexWebProperties.isDevEnvironment()).willReturn(true);
        given(userService.findOptionalByEcasId(anyString())).willReturn(Optional.empty());
        given(userService.create(anyString(), anyString(), anyString())).willReturn(operator1);

        given(userProfileRepository.save(userProfile)).willReturn(userProfile);

        assertEquals(userProfile, userProfileService.create(userProfileSpec));
    }

    @Test
    void should_clean_functional_user() {
        UserProfile userProfile = mockUserProfile(operator1, rootGroup);
        CreateUserProfileSpec userProfileSpec = CreateUserProfileSpec.builder()
                .ecasId("j432sa")
                .groupId(GROUP_ROOT_ID)
                .name("Name")
                .euLoginEmailAddress("")
                .alternativeEmail(userProfile.getAlternativeEmail())
                .build();

        given(groupService.findById(GROUP_ROOT_ID)).willReturn(rootGroup);
        given(etrustexWebProperties.isDevEnvironment()).willReturn(false);

        given(userService.getEuLoginProfileFromLdap(anyString())).willReturn(LdapUserDto.builder().euLogin("j432sa").build());
        given(userService.findOptionalByEcasId(anyString())).willReturn(Optional.empty());
        given(userProfileRepository.save(any())).willReturn(userProfile);

        assertEquals(userProfile, userProfileService.create(userProfileSpec));
    }

    @Test
    void should_throw_EtxWebException_if_group_not_found() {
        given(groupService.findById(rootGroup.getId())).willThrow(EtxWebException.class);
        CreateUserProfileSpec userProfileSpec = CreateUserProfileSpec.builder().groupId(rootGroup.getId()).build();
        assertThrows(EtxWebException.class, () -> userProfileService.create(userProfileSpec));
    }

    @Test
    void should_get_by_role_and_group_and_business() {
        initUserListItems(operator1, false);
        PageRequest pageRequest = PageRequest.of(0, 10);

        given(userProfileRepository.getByRoleNameAndGroupIdAndNameOrEcasIdContains(RoleName.OPERATOR, GROUP_ENTITY_ID_1, "", pageRequest))
                .willReturn(new PageImpl<>(userListItems));

        Page<UserListItem> page = userProfileService.getByRoleNameAndGroupId(RoleName.OPERATOR, GROUP_ENTITY_ID_1, "", pageRequest);

        assertThat(page.getContent()).isEqualTo(userListItems);

    }

    @Test
    void should_get_by_business() {
        initUserListItems(systemAdmin, false);
        PageRequest pageRequest = PageRequest.of(0, 10);
        userListItems.forEach(userListItem -> userListItem.getRoleNames().add(RoleName.OPERATOR));

        given(groupService.findById(GROUP_BUSINESS_ID_1)).willReturn(business1);
        given(userProfileRepository.findByGroupIdOrParentGroupIdAndNameOrEcasIdContains(GROUP_BUSINESS_ID_1, "", pageRequest))
                .willReturn(new PageImpl<>(userListItems));

        Page<UserListItem> page = userProfileService.getByGroupId(GROUP_BUSINESS_ID_1, "", pageRequest, false);

        assertThat(page.getContent()).isEqualTo(userListItems);

    }

    @Test
    void should_get_official_in_charge() {
        initUserListItems(systemAdmin, false);
        PageRequest pageRequest = PageRequest.of(0, 10);
        userListItems.forEach(userListItem -> userListItem.getRoleNames().add(RoleName.OFFICIAL_IN_CHARGE));

        given(userProfileRepository.getByRoleNameAndNameOrEcasIdContains(RoleName.OFFICIAL_IN_CHARGE, "", pageRequest))
                .willReturn(new PageImpl<>(userListItems));

        Page<UserListItem> page = userProfileService.findOfficialsInCharge("", pageRequest);

        assertThat(page.getContent()).isEqualTo(userListItems);

    }

    @Test
    void should_search() {
        initUserListItems(systemAdmin, false);

        given(groupService.findById(GROUP_ENTITY_ID_1)).willReturn(entity1);
        given(userProfileRepository.findByGroupIdAndNameOrEcasIdContains(GROUP_ENTITY_ID_1, "", Pageable.unpaged()))
                .willReturn(new PageImpl<>(userListItems));

        List<SearchItem> page = userProfileService.search(GROUP_ENTITY_ID_1, "", false, null);

        assertThat(page.get(0).getSearchValue()).isEqualTo(userListItems.get(0).getEcasId());

    }

    @Test
    void should_search_as_business() {
        initUserListItems(systemAdmin, false);

        given(groupService.findById(GROUP_BUSINESS_ID_1)).willReturn(business1);
        given(userProfileRepository.getByRoleNameAndGroupIdAndNameOrEcasIdContains(RoleName.GROUP_ADMIN, GROUP_BUSINESS_ID_1, "", Pageable.unpaged()))
                .willReturn(new PageImpl<>(userListItems));

        List<SearchItem> page = userProfileService.search(GROUP_BUSINESS_ID_1, "", false, RoleName.GROUP_ADMIN);

        assertThat(page.get(0).getSearchValue()).isEqualTo(userListItems.get(0).getEcasId());

    }

    @Test
    void should_delete_by_ecas_id_and_group_id() {
        UserProfile userProfile = mockUserProfile(systemAdmin, rootGroup);

        userProfileService.delete(userProfile.getUser().getEcasId(), userProfile.getGroup().getId());

        verify(userProfileRepository).deleteByUserEcasIdAndGroupId(userProfile.getUser().getEcasId(), userProfile.getGroup().getId());

    }

    @Test
    void should_delete_user_if_last_user_profile() {
        User oneUseUser = mockUser();
        UserProfile userProfile = mockUserProfile(oneUseUser, entity1);

        given(userProfileRepository.findByUserEcasId(anyString())).willReturn(Stream.empty());
        given(userService.findByEcasId(anyString())).willReturn(oneUseUser);

        userProfileService.delete(oneUseUser.getEcasId(), userProfile.getGroup().getId());

        verify(userProfileRepository).deleteByUserEcasIdAndGroupId(oneUseUser.getEcasId(), userProfile.getGroup().getId());
        verify(userService).delete(oneUseUser);

    }

    @Test
    void should_bulk_add_user_profiles() {
        String fileContent = "user0101,user0101,test0101@test.com,party0101,true, false, true, false\n" +
                "user0202,user0202,test0202@ttt.com,party0202,true, false, true, true\n" +
                "user0303,user0303,test0303@ttt.com,party0303,false, true, false, false";
        UserProfile userProfile = mockUserProfile(entityAdmin1, entity1);

        given(csvService.parseCSVFileToUserProfile(fileContent.getBytes(StandardCharsets.UTF_8), business1.getId())).willReturn(Collections.nCopies(3, userProfileSpecGlobal));

        given(groupService.findById(any())).willReturn(entity1);
        given(etrustexWebProperties.isDevEnvironment()).willReturn(true);
        given(userProfileRepository.save(any())).willReturn(userProfile);
        given(groupService.existsByIdAndParentId(any(), any())).willReturn(true);

        List<String> errors = userProfileService.bulkAddUserProfiles(fileContent.getBytes(StandardCharsets.UTF_8), userProfile.getGroup().getBusinessId());
        Assertions.assertTrue(errors.isEmpty());

        verify(userProfileRepository, times(3)).save(any());
    }

    @Test
    void should_not_add_bulk_user_profiles() {
        String fileContent = "user0101,,tht,party0101,true, false, true, false\n" +
                "user0202,uijjh,test0202@ttt.com,party0202,true, false, true, true\n" +
                "user0303,,test0303@ttt.com,party0303,false, true, false, false";

        given(csvService.parseCSVFileToUserProfile(fileContent.getBytes(StandardCharsets.UTF_8), business1.getId())).willReturn(Collections.nCopies(1, userProfileSpecGlobal));

        List<String> errors = userProfileService.bulkAddUserProfiles(fileContent.getBytes(StandardCharsets.UTF_8), entity1.getBusinessId());
        assertEquals(1, errors.size());
    }

    @Test
    void should_not_add_duplicate_users() {
        String duplicateUsers = "user0100,user0100,test0101@test.com,TEST_PARTY,true, false, true, false\n" +
                "user0101,user0104,test010@test.com,TEST_PARTY,true, false, true, false\n" +
                "user0102,user0104,test0102@test.com,TEST_PARTY,true, false, true, false";

        UserProfile userProfile = mockUserProfile(entityAdmin1, entity1);

        given(csvService.parseCSVFileToUserProfile(duplicateUsers.getBytes(StandardCharsets.UTF_8), business1.getId())).willReturn(Collections.nCopies(1, userProfileSpecGlobal));

        List<String> errors = userProfileService.bulkAddUserProfiles(duplicateUsers.getBytes(StandardCharsets.UTF_8), userProfile.getGroup().getBusinessId());
        Assertions.assertFalse(errors.isEmpty());

        verify(userProfileRepository, times(0)).save(any());
    }

    @Test
    void should_search_all_users() {
        initUserListItems(systemAdmin, true);
        PageRequest pageRequest = PageRequest.of(0, 10);

        given(userProfileRepository.findByNameOrEcasIdContains( anyString(), any()))
                .willReturn(new PageImpl<>(userListItems));

        Page<UserListItem> page = userProfileService.getUsers(NAME, pageRequest);

        assertThat(page.getTotalElements()).isEqualTo(userListItems.size());

    }

    @Test
    void should_preSearch_all_users() {
        initUserListItems(systemAdmin, true);

        given(userProfileRepository.findByNameOrEcasIdContains( anyString(), any()))
                .willReturn(new PageImpl<>(userListItems));

        List<SearchItem> searchUsers = userProfileService.preSearchUsers(NAME, true);

        assertThat(searchUsers.size()).isEqualTo(userListItems.size());
    }

    @Test
    void should_preSearch_sys_admins() {
        initUserListItems(systemAdmin, false);

        given(groupService.getRoot()).willReturn(entity1.getBusinessOrRoot());
        given(userProfileRepository.findByGroupIdAndNameOrEcasIdContains( any(), any(), any()))
                .willReturn(new PageImpl<>(userListItems));

        List<SearchItem> searchUsers = userProfileService.preSearchUsers(NAME, false);

        assertThat(searchUsers.size()).isEqualTo(userListItems.size());
    }



    private void initGroups() {
        rootGroup = mockGroup("GROUP_ROOT_ID", "root", null, GroupType.ROOT);
        business1 = mockGroup("GROUP_BUSINESS_ID_1", "business 1", rootGroup, GroupType.BUSINESS);
        entity1 = mockGroup("GROUP_ENTITY_ID_1", "ENTITY 1", business1, GroupType.ENTITY);
    }

    private void initUsers() {
        operator1 = mockUser(USER_ENTITY_OPERATOR_ECAS_ID_1, USER_NAME);
        entityAdmin1 = mockUser(USER_ENTITY_ADMIN_ECAS_ID_1, USER_NAME);
        businessAdmin1 = mockUser(USER_BUSINESS_ADMIN_ECAS_ID_1, USER_NAME);
        systemAdmin = mockUser(USER_SYSTEM_ADMIN_ECAS_ID, USER_NAME);
    }

    private void initUserListItems(User user, boolean isAllUsers) {
        if (isAllUsers) {
            userListItems = Collections.singletonList(new UserListItem(
                            user.getEcasId(),
                            user.getEcasId() + NAME,
                            2L,
                            entity1.getIdentifier(),
                            entity1.getName(),
                            GroupType.ENTITY,
                            entity1.getBusinessId() + "_businessIdentifier",
                    entity1.getBusinessId() + "_businessName",
                    new Date(),
                    SYSTEM,
                    new Date(),
                    SYSTEM
                    )
            );
        } else {
            userListItems = Collections.singletonList(new UserListItem(
                            user.getEcasId(),
                            user.getEcasId() + NAME,
                            user.getEcasId() + "@test.com",
                            user.getEcasId() + "@test.com",
                            true,
                            GROUP_ENTITY_ID_1,
                            entity1.getIdentifier(),
                            entity1.getName(),
                            entity1.getType(),
                            new Date(),
                            SYSTEM,
                            new Date(),
                            SYSTEM,
                            true,
                            false,
                            true,
                            false
                    )
            );
        }

    }
}
