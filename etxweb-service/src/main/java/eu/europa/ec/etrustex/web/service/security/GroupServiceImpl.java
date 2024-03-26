package eu.europa.ec.etrustex.web.service.security;

import eu.europa.ec.etrustex.web.exchange.model.EntityItem;
import eu.europa.ec.etrustex.web.exchange.model.GroupSearchItem;
import eu.europa.ec.etrustex.web.exchange.model.SearchItem;
import eu.europa.ec.etrustex.web.persistence.entity.Template;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.ExchangeRule;
import eu.europa.ec.etrustex.web.persistence.entity.security.GrantedAuthority;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.repository.exchange.ChannelRepository;
import eu.europa.ec.etrustex.web.persistence.repository.exchange.ExchangeRuleRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.GrantedAuthorityRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import eu.europa.ec.etrustex.web.service.CSVService;
import eu.europa.ec.etrustex.web.service.GroupPreferencesService;
import eu.europa.ec.etrustex.web.service.validation.annotation.CheckEntityIsEmpty;
import eu.europa.ec.etrustex.web.service.validation.annotation.CheckIsBusiness;
import eu.europa.ec.etrustex.web.service.validation.model.GroupSpec;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static eu.europa.ec.etrustex.web.util.exchange.model.GroupType.*;
import static eu.europa.ec.etrustex.web.util.validation.ValidationMessage.Constants.UNIQUE_GROUP_NAME_ERROR_MSG;


@Service
@Transactional
@RequiredArgsConstructor
@Validated
public class GroupServiceImpl implements GroupService {
    private final GroupPreferencesService groupPreferencesService;
    private final GroupRepository groupRepository;
    private final GrantedAuthorityRepository grantedAuthorityRepository;
    private final ChannelRepository channelRepository;
    private final CSVService csvService;
    private final ExchangeRuleRepository exchangeRuleRepository;

    private static final String LINE = " - line ";

    @Override
    public Group getRoot() {
        return groupRepository.findFirstByIdentifierAndParentIdentifier(ROOT.name(), null);
    }

    @Override
    public Group findByIdentifierAndParentIdentifier(String entityIdentifier, String businessIdentifier) {
        return groupRepository.findFirstByIdentifierAndParentIdentifier(entityIdentifier, businessIdentifier);
    }

    @Override
    @Transactional(readOnly = true)
    public Group findById(Long id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new EtxWebException("Cannot find group with id " + id, new IllegalArgumentException()));
    }

    @Override
    public Group findByRecipientPreferencesId(Long recipientPreferencesId) {
        return groupRepository.findByRecipientPreferencesId(recipientPreferencesId).orElse(null);
    }

    @Override
    public Group create(GroupSpec spec) {
        Group group = groupRepository.save(createGroup(spec));

        if (spec.isAddToChannel() && spec.getChannelId() != null && spec.getEntityRole() != null)
            channelRepository.findById(spec.getChannelId()).ifPresent(channel -> exchangeRuleRepository.save(
                    ExchangeRule.builder()
                            .exchangeMode(spec.getEntityRole())
                            .member(group)
                            .channel(channel).build()
            ));


        return group;
    }

    @Override
    public Group update(Long groupId, GroupSpec spec) {
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new EtxWebException(String.format("Group with id %s not found", groupId)));

        group.setIdentifier(spec.getIdentifier());
        group.setName(spec.getDisplayName());
        group.setDescription(spec.getDescription());

        groupPreferencesService.getRecipientPreferencesIfChanged(spec, group)
                .ifPresent(group::setRecipientPreferences);

        groupPreferencesService.getSenderPreferencesIfChanged(spec, group)
                .ifPresent(group::setSenderPreferences);

        group.setNewMessageNotificationEmailAddresses(spec.getNewMessageNotificationEmailAddresses());
        group.setRegistrationRequestNotificationEmailAddresses(spec.getRegistrationRequestNotificationEmailAddresses());
        group.setStatusNotificationEmailAddress(spec.getStatusNotificationEmailAddress());
        group.setRetentionWarningNotificationEmailAddresses(spec.getRetentionWarningNotificationEmailAddresses());
        group.setActive(spec.isActive());
        group.setIndividualStatusNotifications(spec.isIndividualStatusNotifications());

        if (group.getType() == ENTITY) {
            group.setSystem(spec.isSystem());
            group.setSystemEndpoint(spec.getEndpoint());
        }

        return groupRepository.save(group);
    }

    @Override
    public Page<Group> findByTypeAndParent(GroupType type, Long parentId, Collection<GrantedAuthority> grantedAuthorities, Pageable pageable, String filterBy, String filterValue) {
        Group parent = parentId != null ? findById(parentId) : getRoot();

        if (filterValue == null) {
            filterValue = "";
        }

        Page<Group> page;
        if (isAdminForParent(parent, grantedAuthorities) || isOfficialInChargeForParent(parent, grantedAuthorities)) {
            page = groupRepository.findByGroupTypeAndParentAndGroupIdOrGroupName(type, parent, filterValue, pageable);
        } else {
            page = groupRepository.findByGroupTypeAndParentAndGroupIdsAndGroupIdOrGroupName(type, parent, this.extractGroupIdentifiers(type, grantedAuthorities), filterValue, pageable);
        }

        return page;
    }

    @Override
    public Page<Group> findByIds(Pageable pageable, String filterValue, Long... groupIds) {
        if (filterValue == null) {
            filterValue = "";
        }

        return groupRepository.findByGroupIdInAndGroupIdOrGroupNameLike(filterValue, pageable, groupIds);
    }

    @Override
    public List<GroupSearchItem> findByIdentifierOrNameLike(Long businessId, String text) {
        if (businessId == null) {
            return groupRepository.findByTypeAndIdentifierOrNameLike(GroupType.BUSINESS, text);
        } else {
            return groupRepository.findByTypeAndParentIdAndIdentifierOrNameLike(GroupType.ENTITY, businessId, text);
        }

    }

    @Override
    public List<Group> findByType(GroupType type) {
        return groupRepository.findByType(type);
    }

    @Override
    public List<Group> findByParentId(Long parentId) {
        return groupRepository.findByParentId(parentId).collect(Collectors.toList());
    }

    @Override
    public boolean hasChildrenOrChannelsOrUsersOrMessages(Long groupId) {
        return groupRepository.countByParentId(groupId) > 0 ||
                channelRepository.countByBusinessId(groupId) > 0 ||
                groupRepository.getTotalRecordsFromGrantedAuthorityAndMessageAndMessageSummary(groupId) > 0;
    }

    @Override
    public boolean hasMessages(Long groupId) {
        return groupRepository.getTotalRecordsFromMessageAndMessageSummaryByGroupId(groupId) > 0;
    }

    @Override
    @Transactional
    public void deleteGroup(@CheckEntityIsEmpty Group group) {

        boolean isEmpty = !hasMessages(group.getId());
        if (isEmpty) {
            groupRepository.delete(group);
            groupPreferencesService.delete(group.getSenderPreferences());
        }
    }

    @Override
    @Transactional
    public void deleteBusiness(Group group) {
        if (!this.hasChildrenOrChannelsOrUsersOrMessages(group.getId())) {
            groupRepository.delete(group);
            return;
        }
        group.setPendingDeletion(true);
        groupRepository.save(group);
    }

    @Override
    @Transactional
    public void cancelBusinessDeletion(@CheckIsBusiness Long groupId) {
        Group group = this.findById(groupId);

        group.setPendingDeletion(false);
        group.setRemovedDate(null);
        group.setActive(true);
        groupRepository.save(group);

        List<Group> groups = groupRepository.findByParentId(group.getId())
                .map(entity -> {
                    entity.setActive(true);
                    entity.setPendingDeletion(false);
                    return entity;
                })
                .collect(Collectors.toList());
        groupRepository.saveAll(groups);

        List<GrantedAuthority> grantedAuthorities = grantedAuthorityRepository.findByGroupParent(group)
                .map(grantedAuthority -> {
                    grantedAuthority.setEnabled(true);
                    return grantedAuthority;
                })
                .collect(Collectors.toList());
        grantedAuthorityRepository.saveAll(grantedAuthorities);
    }

    @Override
    @Transactional
    public void confirmBusinessDeletion(@CheckIsBusiness Long groupId) {
        Group group = this.findById(groupId);

        group.setRemovedDate(new Date());
        group.setActive(false);
        groupRepository.save(group);

        List<Group> groups = groupRepository.findByParentId(group.getId())
                .map(entity -> {
                    entity.setActive(false);
                    return entity;
                }).collect(Collectors.toList());
        groupRepository.saveAll(groups);

        List<GrantedAuthority> grantedAuthorities = grantedAuthorityRepository.findByGroupParent(group)
                .map(grantedAuthority -> {
                    grantedAuthority.setEnabled(false);
                    return grantedAuthority;
                }).collect(Collectors.toList());
        grantedAuthorityRepository.saveAll(grantedAuthorities);
    }

    @Override
    public boolean existsByNameAndParentId(String name, Long parentId) {
        return groupRepository.existsByNameAndParentId(name, parentId);
    }

    @Override
    public boolean existsByIdAndParentId(Long id, Long parentId) {
        return groupRepository.existsByIdAndParentId(id, parentId);
    }

    @Override
    public Stream<Group> findByParentIdAndType(Long parentId, GroupType type) {
        return groupRepository.findByParentIdAndType(parentId, type);
    }

    @Override
    public List<String> bulkAddGroups(byte[] fileContent, Long businessId) {
        List<String> errors = new ArrayList<>();
        List<GroupSpec> groupSpecs;
        try {
            groupSpecs = csvService.parseCSVFileToGroupSpecs(fileContent, businessId);
            errors = validateBulkGroupSpec(groupSpecs);
        } catch (Exception e) {
            errors.add(e.getMessage());
            return errors;
        }

        if (errors.isEmpty()) {
            groupSpecs.forEach(groupSpec -> {
                Group group = createGroup(groupSpec);
                groupRepository.save(group);
            });
        }

        return errors;
    }

    @Override
    public Page<EntityItem> findAllConfiguredGroups(Pageable pageable, String filterValue) {
        if (filterValue == null) {
            filterValue = "";
        }
        return groupRepository.findAllConfiguredGroups(filterValue, pageable);
    }

    @Override
    public List<SearchItem> searchAllConfiguredGroups(String filterValue) {
        if (filterValue == null) {
            filterValue = "";
        }
        return groupRepository.findAllConfiguredGroups(filterValue, Pageable.unpaged())
                .getContent()
                .stream()
                .map(x -> new SearchItem(x.getEntityIdentifier(), String.format("%s - %s", x.getEntityIdentifier(), x.getEntityName())))
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public void disableEncryption(Long businessId) {
        groupRepository.findByParentId(businessId)
                .forEach(groupPreferencesService::disableEncryption);
    }

    @Override
    public Group findLastUpdatedGroup(String user) {
        return groupRepository.findFirstByAuditingEntityModifiedByOrderByAuditingEntityModifiedDateDesc(user);
    }


    private Group createGroup(GroupSpec groupSpec) {
        Group parent = null;

        if (groupSpec.getParentGroupId() != null) {
            parent = groupRepository.findById(groupSpec.getParentGroupId()).orElseThrow(() -> new EtxWebException(String.format("Parent group %s not found", groupSpec.getParentGroupId())));
        } else if (groupSpec.getType() != ROOT) {
            parent = getRoot();
        }

        List<Template> templates = (groupSpec.getType() != ENTITY && parent != null && parent.getTemplates() != null)
                ? new ArrayList<>(parent.getTemplates())
                : new ArrayList<>();

        return Group.builder()
                .identifier(groupSpec.getIdentifier())
                .name(groupSpec.getDisplayName())
                .description(groupSpec.getDescription())
                .isActive(groupSpec.isActive())
                .isSystem(groupSpec.getType() == ENTITY && groupSpec.isSystem())
                .systemEndpoint(groupSpec.getType() == ENTITY ? groupSpec.getEndpoint() : null)
                .type(groupSpec.getType())
                .parent(parent)
                .templates(templates)
                .recipientPreferences(groupPreferencesService.getRecipientPreferences(groupSpec))
                .senderPreferences(groupPreferencesService.getSenderPreferences(groupSpec, parent))
                .newMessageNotificationEmailAddresses(groupSpec.getNewMessageNotificationEmailAddresses())
                .registrationRequestNotificationEmailAddresses(groupSpec.getRegistrationRequestNotificationEmailAddresses())
                .statusNotificationEmailAddress(groupSpec.getStatusNotificationEmailAddress())
                .retentionWarningNotificationEmailAddresses(groupSpec.getRetentionWarningNotificationEmailAddresses())
                .individualStatusNotifications(groupSpec.isIndividualStatusNotifications())
                .build();
    }

    private boolean isAdminForParent(Group parent, Collection<GrantedAuthority> grantedAuthorities) {
        return grantedAuthorities.stream().anyMatch(grantedAuthority ->
                RoleName.SYS_ADMIN.equals(grantedAuthority.getRole().getName()) ||
                        (RoleName.GROUP_ADMIN.equals(grantedAuthority.getRole().getName()) && grantedAuthority.getGroup().getId().equals(parent.getId()))
        );
    }

    private boolean isOfficialInChargeForParent(Group parent, Collection<GrantedAuthority> grantedAuthorities) {
        return grantedAuthorities.stream().anyMatch(grantedAuthority ->
                RoleName.OFFICIAL_IN_CHARGE.equals(grantedAuthority.getRole().getName()) ||
                        (RoleName.GROUP_ADMIN.equals(grantedAuthority.getRole().getName()) && grantedAuthority.getGroup().getId().equals(parent.getId()))
        );
    }

    private Collection<String> extractGroupIdentifiers(GroupType type, Collection<GrantedAuthority> grantedAuthorities) {
        return grantedAuthorities.stream()
                .filter(grantedAuthority -> RoleName.GROUP_ADMIN.equals(grantedAuthority.getRole().getName()))
                .map(grantedAuthority -> getBusinessOrEntity(type, grantedAuthority))
                .map(Group::getIdentifier)
                .collect(Collectors.toSet());

    }

    private Group getBusinessOrEntity(GroupType type, GrantedAuthority grantedAuthority) {
        if (Objects.equals(grantedAuthority.getGroup().getType(), ENTITY) && BUSINESS.equals(type)) {
            return grantedAuthority.getGroup().getParent();
        } else {
            return grantedAuthority.getGroup();
        }
    }

    private List<String> validateBulkGroupSpec(List<GroupSpec> groupSpecs) {
        List<String> errors = new ArrayList<>();
        Validator validator;
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }

        IntStream.range(0, groupSpecs.size())
                .forEach(i -> {
                    GroupSpec groupSpec = groupSpecs.get(i);
                    String duplicateDisplayGroupName = checkDuplicateDisplayGroupName(groupSpec, i);

                    if (StringUtils.isNotEmpty(duplicateDisplayGroupName))
                        errors.add(duplicateDisplayGroupName);

                    Set<ConstraintViolation<GroupSpec>> constraintViolations = validator.validate(groupSpec);
                    if (!constraintViolations.isEmpty()) {
                        constraintViolations.forEach(constraintViolation ->
                                errors.add(constraintViolation.getMessage() + LINE + (i + 1)));
                    }

                    if (groupRepository.existsByIdentifierAndParentId(groupSpec.getIdentifier(), groupSpec.getParentGroupId())) {
                        errors.add(String.format("Entity with name %s already exists", groupSpec.getIdentifier()) + LINE + (i + 1));
                    }
                });

        return errors;
    }

    private String checkDuplicateDisplayGroupName(GroupSpec groupSpec, int lineNumber) {
        if (groupRepository.existsByNameAndParentId(groupSpec.getDisplayName(), groupSpec.getParentGroupId())) {
            return UNIQUE_GROUP_NAME_ERROR_MSG + LINE + (lineNumber + 1);
        }

        return null;
    }
}
