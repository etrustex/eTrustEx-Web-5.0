package eu.europa.ec.etrustex.web.service.security;

import eu.europa.ec.etrustex.web.common.exchange.ExchangeMode;
import eu.europa.ec.etrustex.web.exchange.model.EntityItem;
import eu.europa.ec.etrustex.web.exchange.model.GroupSearchItem;
import eu.europa.ec.etrustex.web.exchange.model.SearchItem;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.Channel;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.RecipientPreferences;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.SenderPreferences;
import eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.GroupConfiguration;
import eu.europa.ec.etrustex.web.persistence.entity.security.GrantedAuthority;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.Role;
import eu.europa.ec.etrustex.web.persistence.repository.AlertRepository;
import eu.europa.ec.etrustex.web.persistence.repository.TemplateRepository;
import eu.europa.ec.etrustex.web.persistence.repository.exchange.ChannelRepository;
import eu.europa.ec.etrustex.web.persistence.repository.exchange.ExchangeRuleRepository;
import eu.europa.ec.etrustex.web.persistence.repository.groupconfiguration.GroupConfigurationRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.GrantedAuthorityRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import eu.europa.ec.etrustex.web.service.CSVService;
import eu.europa.ec.etrustex.web.service.GroupPreferencesService;
import eu.europa.ec.etrustex.web.service.validation.model.GroupSpec;
import eu.europa.ec.etrustex.web.util.cia.Confidentiality;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.*;
import static eu.europa.ec.etrustex.web.service.ServiceTestUtils.mockValidationGroupSpec;
import static eu.europa.ec.etrustex.web.util.exchange.model.GroupType.ROOT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"java:S106", "java:S100", "java:S6437"})
class GroupServiceImplTest {
    @Mock
    private GroupPreferencesService groupPreferencesService;
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private GrantedAuthorityRepository grantedAuthorityRepository;
    @Mock
    private GroupConfigurationRepository<GroupConfiguration<?>> groupConfigurationRepository;
    @Mock
    private ChannelRepository channelRepository;
    @Mock
    private AlertRepository alertRepository;
    @Mock
    private CSVService csvService;
    @Mock
    private ExchangeRuleRepository exchangeRuleRepository;
    @Mock
    private TemplateRepository templateRepository;

    private GroupService groupService;

    private static final Long ENTITY_ID = 1L;

    private static final String ENTITY_NAME = "entityName";
    private static final String ENTITY_IDENTIFIER = "entityId";
    private static final String BUSINESS_NAME = "businessName";
    private static final String BUSINESS_IDENTIFIER = "businessId";

    @BeforeEach
    public void setUp() {
        this.groupService = new GroupServiceImpl(groupPreferencesService, groupRepository, grantedAuthorityRepository, channelRepository, csvService, exchangeRuleRepository);
    }

    @Test
    void should_find_by_id() {
        Group group = mockGroup();

        given(groupRepository.findById(any())).willReturn(Optional.of(group));

        assertThat(groupService.findById(group.getId())).isEqualTo(group);
    }

    @Test
    void should_create_group() {
        Group group = mockGroupWithRecipientAndSenderPreferences();
        GroupSpec spec = mockValidationGroupSpec(group);

        given(groupRepository.findById(group.getParent().getId())).willReturn(Optional.of(group.getParent()));
        given(groupPreferencesService.getRecipientPreferences(spec)).willReturn(group.getRecipientPreferences());
        given(groupPreferencesService.getSenderPreferences(spec, group.getParent())).willReturn(group.getSenderPreferences());
        when(groupRepository.save(any(Group.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Group createdGroup = groupService.create(spec);
        assertThat(createdGroup.getIdentifier()).isEqualTo(group.getIdentifier());
    }

    @Test
    void should_create_group_with_channel() {
        Group group = mockGroupWithRecipientAndSenderPreferences();
        GroupSpec spec = mockValidationGroupSpec(group);

        Channel channel = mockChannel();
        spec.setChannelId(channel.getId());
        spec.setAddToChannel(true);
        spec.setEntityRole(ExchangeMode.BIDIRECTIONAL);

        given(groupRepository.findById(group.getParent().getId())).willReturn(Optional.of(group.getParent()));
        given(groupPreferencesService.getRecipientPreferences(spec)).willReturn(group.getRecipientPreferences());
        given(groupPreferencesService.getSenderPreferences(spec, group.getParent())).willReturn(group.getSenderPreferences());
        given(channelRepository.findById(anyLong())).willReturn(Optional.of(channel));
        when(groupRepository.save(any(Group.class))).thenAnswer(invocation -> invocation.getArgument(0));

        groupService.create(spec);

        verify(channelRepository, times(1)).findById(anyLong());
        verify(exchangeRuleRepository, times(1)).save(any());
    }

    @Test
    void should_create_group_with_root_parent() {
        Group group = mockGroupWithRecipientAndSenderPreferences();
        Group root = mockRoot();

        GroupSpec spec = mockValidationGroupSpec(group);
        spec.setParentGroupId(null);

        given(groupService.getRoot()).willReturn(root);
        given(groupPreferencesService.getRecipientPreferences(spec)).willReturn(group.getRecipientPreferences());
        given(groupPreferencesService.getSenderPreferences(spec, root)).willReturn(group.getSenderPreferences());
        when(groupRepository.save(any(Group.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Group createdGroup = groupService.create(spec);
        assertThat(createdGroup.getIdentifier()).isEqualTo(group.getIdentifier());
        assertThat(createdGroup.getParent().getIdentifier()).isEqualTo(root.getIdentifier());
    }

    @Test
    void should_update_an_existing_group() {
        Group group = mockGroupWithRecipientAndSenderPreferences();
        GroupSpec groupSpec = mockValidationGroupSpec(group);

        given(groupRepository.findById(group.getId())).willReturn(Optional.of(group));
        when(groupRepository.save(any(Group.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertThat(groupService.update(group.getId(), groupSpec)).isEqualTo(group);
    }

    @Test
    void should_assign_recipient_preferences_when_updating() {
        Group group = mockGroupWithRecipientAndSenderPreferences();
        RecipientPreferences recipientPreferences = group.getRecipientPreferences();

        GroupSpec groupSpec = mockValidationGroupSpec(group);
        group.setRecipientPreferences(null);

        given(groupRepository.findById(group.getId())).willReturn(Optional.of(group));
        given(groupPreferencesService.getRecipientPreferencesIfChanged(groupSpec, group)).willReturn(Optional.of(recipientPreferences));
        when(groupRepository.save(any(Group.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertThat(groupService.update(group.getId(), groupSpec)).isEqualTo(group);
    }

    @Test
    void should_throw_and_exception_if_the_group_does_not_exist() {
        Group group = mockGroup();
        GroupSpec groupSpec = mockValidationGroupSpec(group);

        given(groupRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(EtxWebException.class, () -> groupService.update(TEST_ENTITY_ID, groupSpec));
    }

    @Test
    void should_find_by_type_and_parent() {
        Group root = mockGroup(ROOT, "root", "root");
        Group business = mockGroup(BUSINESS_IDENTIFIER, BUSINESS_NAME, root, GroupType.BUSINESS);

        PageRequest pageRequest = PageRequest.of(0, 10);

        given(groupRepository.findByGroupTypeAndParentAndGroupIdOrGroupName(eq(GroupType.BUSINESS), eq(root), eq(""), any(PageRequest.class))).willReturn(new PageImpl<>(Collections.singletonList(business)));
        given(groupRepository.findById(root.getId())).willReturn(Optional.of(root));

        Page<Group> groupPage = groupService.findByTypeAndParent(
                GroupType.BUSINESS,
                root.getId(),
                Collections.singletonList(
                        GrantedAuthority
                                .builder()
                                .role(
                                        Role.builder()
                                                .name(RoleName.SYS_ADMIN)
                                                .build())
                                .build()
                )
                ,
                pageRequest,
                null,
                null);

        assertEquals(1, groupPage.getTotalElements());

    }

    @Test
    void should_find_by_type() {
        Group root = mockGroup(ROOT, "root", "root");
        Group business = mockGroup(BUSINESS_IDENTIFIER, BUSINESS_NAME, root, GroupType.BUSINESS);

        given(groupRepository.findByType(any())).willReturn(Collections.nCopies(3, business));

        List<Group> groups = groupService.findByType(GroupType.BUSINESS);

        assertEquals(3, groups.size());
    }

    @Test
    void should_find_by_parent_id() {
        Group root = mockGroup(ROOT, "root", "root");
        Group business = mockGroup(BUSINESS_IDENTIFIER, BUSINESS_NAME, root, GroupType.BUSINESS);

        given(groupRepository.findByParentId(any())).willReturn(Stream.of(business));

        List<Group> groups = groupService.findByParentId(root.getId());

        assertEquals(1, groups.size());
    }

    @Test
    void should_check_if_group_has_messages() {
        given(groupRepository.getTotalRecordsFromMessageAndMessageSummaryByGroupId(any())).willReturn(1);
        assertTrue(groupService.hasMessages(1L));
    }

    @Test
    void should_exist_by_id_and_parent_id() {
        given(groupRepository.existsByIdAndParentId(anyLong(), any())).willReturn(true);
        assertTrue(groupService.existsByIdAndParentId(1L, 2L));
    }

    @Test
    void should_exist_by_parent_id_and_type() {
        given(groupRepository.findByParentIdAndType(anyLong(), any())).willReturn(Stream.of(mockGroup()));
        assertThat(groupService.findByParentIdAndType(1L, GroupType.ENTITY).count()).isEqualTo(1);
    }

    @Test
    void should_get_all_groups() {
        Group root = mockGroup(ROOT, "root", "root");
        Group business = mockGroup(BUSINESS_IDENTIFIER, BUSINESS_NAME, root, GroupType.BUSINESS);
        Group entity = mockGroup(ENTITY_IDENTIFIER, ENTITY_NAME, business, GroupType.ENTITY);

        EntityItem entityItem = new EntityItem(business.getIdentifier(), business.getName(), entity.getIdentifier(), entity.getName(), null, null, true, null, false);

        PageRequest pageRequest = PageRequest.of(0, 10);

        given(groupRepository.findAllConfiguredGroups(eq(""), any(PageRequest.class))).willReturn(new PageImpl<>(Collections.singletonList(entityItem)));

        Page<EntityItem> groupPage = groupService.findAllConfiguredGroups(pageRequest, "");

        assertEquals(1, groupPage.getTotalElements());
    }

    @Test
    void should_find_by_ids() {
        Group root = mockGroup(ROOT, "root", "root");
        Group business = mockGroup(BUSINESS_IDENTIFIER, BUSINESS_NAME, root, GroupType.BUSINESS);

        PageRequest pageRequest = PageRequest.of(0, 10);

        given(groupRepository.findByGroupIdInAndGroupIdOrGroupNameLike(any(), any(PageRequest.class), any())).willReturn(new PageImpl<>(Collections.singletonList(business)));

        Page<Group> groupPage = groupService.findByIds(
                pageRequest,
                "filter",
                1L);

        assertEquals(1, groupPage.getTotalElements());
    }

    @Test
    void should_search_all_groups() {
        Group root = mockGroup(ROOT, "root", "root");
        Group business = mockGroup(BUSINESS_IDENTIFIER, BUSINESS_NAME, root, GroupType.BUSINESS);
        Group entity = mockGroup(ENTITY_IDENTIFIER, ENTITY_NAME, business, GroupType.ENTITY);
        EntityItem entityItem = new EntityItem(business.getIdentifier(), business.getName(), entity.getIdentifier(), entity.getName(), null, null, true, null, false);

        given(groupRepository.findAllConfiguredGroups(anyString(), any(Pageable.class))).willReturn(new PageImpl<>(Collections.singletonList(entityItem)));

        List<SearchItem> groups = groupService.searchAllConfiguredGroups("");

        assertEquals(1, groups.size());
    }

    @Test
    void should_find_business_by_id_or_name_like() {

        GroupSearchItem groupSearchItem = new GroupSearchItem(ENTITY_ID, ENTITY_IDENTIFIER, ENTITY_NAME, null, null);

        given(groupRepository.findByTypeAndIdentifierOrNameLike(any(), any())).willReturn(Collections.nCopies(1, groupSearchItem));


        assertEquals(1, groupService.findByIdentifierOrNameLike(null, "entity").size());

    }

    @Test
    void should_find_entity_by_id_or_name_like() {
        GroupSearchItem groupSearchItem = new GroupSearchItem(ENTITY_ID, ENTITY_IDENTIFIER, ENTITY_NAME, null, null);

        given(groupRepository.findByTypeAndParentIdAndIdentifierOrNameLike(any(GroupType.class), anyLong(), anyString()))
                .willReturn(Collections.singletonList(groupSearchItem));

        assertEquals(1, groupService.findByIdentifierOrNameLike(1L, "group").size());
    }

    @Test
    void should_allow_to_delete_entity() {
        given(groupRepository.getTotalRecordsFromGrantedAuthorityAndMessageAndMessageSummary(any())).willReturn(1);
        assertTrue(groupService.hasChildrenOrChannelsOrUsersOrMessages(1L));
    }

    @Test
    void should_not_allow_delete_entity() {
        given(groupRepository.getTotalRecordsFromGrantedAuthorityAndMessageAndMessageSummary(any())).willReturn(0);
        assertFalse(groupService.hasChildrenOrChannelsOrUsersOrMessages(1L));
    }

    @Test
    void should_delete_entity() {
        Group group = mockGroup();

        groupService.deleteGroup(group);
        verify(groupRepository).delete(group);
    }

    @Test
    void should_delete_entity_with_RecipientPreferences() {
        Group group = mockGroup();
        RecipientPreferences recipientPreferences = RecipientPreferences.builder().id(12L).build();
        group.setRecipientPreferences(recipientPreferences);

        groupService.deleteGroup(group);
        verify(groupPreferencesService, times(1)).delete(group.getSenderPreferences());
    }

    @Test
    void should_delete_entity_with_SenderPreferences() {
        Group group = mockGroup();
        SenderPreferences senderPreferences = SenderPreferences.builder().id(12L).build();
        group.setSenderPreferences(senderPreferences);

        groupService.deleteGroup(group);
        verify(groupPreferencesService, times(1)).delete(group.getSenderPreferences());
    }

    @Test
    void should_not_delete_SenderPreferences_if_it_is_configured_with_other_groups() {
        Group group = mockGroup();
        SenderPreferences senderPreferences = SenderPreferences.builder().id(12L).build();
        group.setSenderPreferences(senderPreferences);

        groupService.deleteGroup(group);
        verify(groupRepository).delete(group);
    }

    @Test
    void should_find_a_group_with_same_name() {
        Group group = mockGroup();

        given(groupRepository.existsByNameAndParentId(any(), any())).willReturn(true);

        assertTrue(groupRepository.existsByNameAndParentId(group.getName(), group.getParent().getId()));
        verify(groupRepository, times(1)).existsByNameAndParentId(any(), any());
    }

    @Test
    void should_bulk_add_groups() {
        String bulkEntities = "entity1,entityDisplayName1,entityDescription1\n" +
                "entity2;entityDisplayName2,entityDescription2";
        Group group = mockGroup();
        group.setDescription("Entity description");
        GroupSpec spec = mockValidationGroupSpec(group);
        spec.setSenderPreferencesId(null);
        spec.setRecipientPreferencesId(null);

        given(csvService.parseCSVFileToGroupSpecs(any(), any())).willReturn(Collections.nCopies(2, spec));
        given(groupRepository.findById(any())).willReturn(Optional.of(group));
        List<String> errors = groupService.bulkAddGroups(bulkEntities.getBytes(StandardCharsets.UTF_8), TEST_BUSINESS_ID);

        assertEquals(0, errors.size());
        verify(groupRepository, times(2)).save(any());
    }

    @Test
    void should_fail_bulk_add_groups() {
        String bulkEntities = "entity1,entityDisplayName1,entityDescription1\n" +
                "entity2;entityDisplayName2,entityDescription2";
        Group group = mockGroup();
        group.setDescription("Entity description");
        GroupSpec spec = mockValidationGroupSpec(group);
        spec.setSenderPreferencesId(null);
        spec.setRecipientPreferencesId(null);

        given(csvService.parseCSVFileToGroupSpecs(any(), any())).willThrow(EtxWebException.class);
        List<String> errors = groupService.bulkAddGroups(bulkEntities.getBytes(StandardCharsets.UTF_8), TEST_BUSINESS_ID);

        assertEquals(1, errors.size());
    }

    @Test
    void should_disable_encryption_in_the_business_domain() {
        Group business = mockBusiness();
        Group entity = mockGroup();
        entity.setParent(business);
        RecipientPreferences recipientPreferences = RecipientPreferences.builder()
                .confidentiality(Confidentiality.LIMITED_HIGH)
                .publicKey("a public key")
                .build();
        entity.setRecipientPreferences(recipientPreferences);

        given(groupRepository.findByParentId(business.getId())).willReturn(Stream.of(entity));

        when(groupPreferencesService.disableEncryption(entity)).then(invocation -> {
            Group g = (Group) invocation.getArguments()[0];
            g.getRecipientPreferences().setConfidentiality(Confidentiality.PUBLIC);
            g.getRecipientPreferences().setPublicKey(null);
            g.getRecipientPreferences().setPublicKeyFileName(null);

            return g;
        });

        groupService.disableEncryption(business.getId());

        assertEquals(Confidentiality.PUBLIC, recipientPreferences.getConfidentiality());
        assertNull(recipientPreferences.getPublicKey());
    }
}
