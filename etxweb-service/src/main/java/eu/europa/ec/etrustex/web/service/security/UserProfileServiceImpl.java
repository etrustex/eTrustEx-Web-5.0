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
import eu.europa.ec.etrustex.web.service.pagination.RestResponsePage;
import eu.europa.ec.etrustex.web.service.validation.model.*;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.util.exchange.model.EntitySpec;
import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static eu.europa.ec.etrustex.web.util.exchange.model.GroupType.ROOT;

@Service
@AllArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserService userService;
    private final GroupService groupService;
    private final MessageSummaryUserStatusRepository messageSummaryUserStatusRepository;
    private final MessageUserStatusRepository messageUserStatusRepository;
    private final GrantedAuthorityService grantedAuthorityService;
    private final CSVService csvService;
    private final EtrustexWebProperties etrustexWebProperties;
    private final AlertUserStatusRepository alertUserStatusRepository;

    @Override
    @Transactional(readOnly = true)
    public UserProfile findUserProfileByUserAndGroupId(User user, Long groupId) {
        UserProfile userProfile = userProfileRepository.findByUserEcasIdAndGroupId(user.getEcasId(), groupId);

        if (userProfile == null) {
            throw new EtxWebException(String.format("UserProfile not found for User id %1$s and Group id %2$s.", user.getId(), groupId));
        }

        return userProfile;
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfile findByUserAndGroupIdentifierAndBusinessIdentifier(User user, EntitySpec entitySpec) {
        Group entity = groupService.findByIdentifierAndParentIdentifier(entitySpec.getEntityIdentifier(), entitySpec.getBusinessIdentifier());

        try {
            return findUserProfileByUserAndGroup(user, entity);
        } catch (EtxWebException e) {
            if (groupService.findByIdentifierAndParentIdentifier(entitySpec.getBusinessIdentifier(), ROOT.name()) == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Business does not exist");
            }
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity does not exist");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfile findUserProfileByUserAndGroup(User user, Group group) {
        UserProfile userProfile = userProfileRepository.findByUserAndGroup(user, group);

        if (userProfile == null) {
            throw new EtxWebException(String.format("UserProfile not found for User id %1$s and Group id %2$s.", user.getId(), group.getId()));
        }

        return userProfile;
    }

    @Override
    public Stream<UserProfile> findByUserEcasId(String ecasId) {
        return userProfileRepository.findByUserEcasId(ecasId);
    }

    @Override
    public List<SearchItem> search(Long groupId, String filterValue, Boolean isRegistrationRequest, RoleName roleName) {
        Group group = groupService.findById(groupId);

        if (BooleanUtils.isTrue(isRegistrationRequest)) {
            return toSearchItem(userProfileRepository.findByGroupIdOrParentGroupIdAndNameOrEcasIdContainsAndUserRegistration(groupId, filterValue, Pageable.unpaged()).getContent());
        }

        if (group.getType().equals(GroupType.ROOT) && roleName != null) {
            return toSearchItem(userProfileRepository.getByRoleNameAndGroupIdAndNameOrEcasIdContains(roleName, groupId, filterValue, Pageable.unpaged()).getContent());
        }

        if (group.getType().equals(GroupType.BUSINESS)) {
            if (roleName == null) {
                return toSearchItem(userProfileRepository.findByGroupIdOrParentGroupIdAndNameOrEcasIdContains(groupId, filterValue, Pageable.unpaged()).getContent());
            }
            return toSearchItem(userProfileRepository.getByRoleNameAndGroupIdAndNameOrEcasIdContains(roleName, groupId, filterValue, Pageable.unpaged()).getContent());
        }

        return toSearchItem(userProfileRepository.findByGroupIdAndNameOrEcasIdContains(groupId, filterValue, Pageable.unpaged()).getContent());
    }

    @Override
    public List<SearchItem> searchSysAdmins(String filterValue) {
        Group root = groupService.getRoot();
        Page<UserListItem> sysAdmins = userProfileRepository.findByGroupIdAndNameOrEcasIdContains(root.getId(), filterValue, Pageable.unpaged());

        return toSearchItem(sysAdmins.getContent());

    }

    @Override
    @Transactional
    public UserProfile create(CreateUserProfileSpec userProfileSpec) {
        UserProfile userProfile = null;
        if (userProfileSpec.getRoleNames() != null && (userProfileSpec.getRoleNames().contains(RoleName.OFFICIAL_IN_CHARGE) || userProfileSpec.getRoleNames().contains(RoleName.SYS_ADMIN))) {
            userProfile = userProfileRepository.findByUserEcasIdAndGroupId(userProfileSpec.getEcasId(), userProfileSpec.getGroupId());
        }
        if (userProfile == null) {
            userProfile = userProfileRepository.save(toUserProfile(userProfileSpec));
        }

        if (userProfileSpec.getRoleNames() != null && !userProfileSpec.getRoleNames().isEmpty()) {
            User user = userProfile.getUser();
            grantedAuthorityService.create(user, userProfileSpec.getRoleNames(), userProfileSpec.getGroupId());
        }

        return userProfile;
    }

    @Override
    @Transactional
    public UserProfile update(UserProfileSpec userProfileSpec) {
        return userProfileRepository.save(toUserProfile(userProfileSpec));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserListItem> getByRoleNameAndGroupId(RoleName role, Long groupId, String filterValue, PageRequest pageRequest) {
        return userProfileRepository.getByRoleNameAndGroupIdAndNameOrEcasIdContains(role, groupId, filterValue, pageRequest);
    }

    @Override
    public Page<UserListItem> findSysAdmins(String filterValue, PageRequest pageRequest) {
        Group root = groupService.getRoot();

        return userProfileRepository.getByRoleNameAndGroupIdAndNameOrEcasIdContains(RoleName.SYS_ADMIN, root.getId(), filterValue, pageRequest);
    }

    @Override
    public Page<UserListItem> findOfficialsInCharge(String filterValue, PageRequest pageRequest) {
        return userProfileRepository.getByRoleNameAndNameOrEcasIdContains(RoleName.OFFICIAL_IN_CHARGE, filterValue, pageRequest);
    }

    @Override
    public Page<UserListItem> getByGroupId(Long groupId, String filterValue, PageRequest pageRequest, Boolean isRegistrationRequest) {
        Group group = groupService.findById(groupId);

        Page<UserListItem> userListItems;

        if (BooleanUtils.isTrue(isRegistrationRequest)) {
            userListItems = userProfileRepository.findByGroupIdOrParentGroupIdAndNameOrEcasIdContainsAndUserRegistration(groupId, filterValue, pageRequest);
            userListItems.forEach(userListItem -> {
                if (BooleanUtils.isTrue(userListItem.getIsOperator())) {
                    userListItem.getRoleNames().add(RoleName.OPERATOR);
                }

                if (BooleanUtils.isTrue(userListItem.getIsAdmin())) {
                    userListItem.getRoleNames().add(RoleName.GROUP_ADMIN);
                }
            });

            return userListItems;
        }

        if (group.getType().equals(GroupType.BUSINESS)) {
            userListItems = userProfileRepository.findByGroupIdOrParentGroupIdAndNameOrEcasIdContains(groupId, filterValue, pageRequest);
        } else {
            userListItems = userProfileRepository.findByGroupIdAndNameOrEcasIdContains(groupId, filterValue, pageRequest);
        }

        userListItems.forEach(this::initRoleNames);
        return userListItems;
    }

    @Override
    @Transactional
    public void delete(String ecasId, Long groupId) {
        userProfileRepository.deleteByUserEcasIdAndGroupId(ecasId, groupId);
        grantedAuthorityService.deleteByUserEcasIdAndGroupID(ecasId, groupId);
        if (!userProfileRepository.findByUserEcasId(ecasId).findAny().isPresent()) {
            User user = userService.findByEcasId(ecasId);
            if (user != null) {
                messageSummaryUserStatusRepository.deleteByUser(user);
                messageUserStatusRepository.deleteByUser(user);
                alertUserStatusRepository.deleteByUser(user);
                userService.delete(user);
            }
        }
    }

    @Override
    @Transactional
    public void delete(SysDeleteUserProfileSpec user) {
        int numberOfRoles = grantedAuthorityService.findByUserEcasIdAndGroupId(user.getEcasId(), user.getGroupId()).size();
        if (numberOfRoles == 1) {
            this.delete(user.getEcasId(), user.getGroupId());
            return;
        }
        grantedAuthorityService.delete(GrantedAuthoritySpec.builder()
                .userName(user.getEcasId())
                .groupId(user.getGroupId())
                .roleName(user.getRoleName())
                .build());
    }

    @Override
    public List<UserProfile> findByNewMessageNotificationsIsTrueAndGroup(Group recipient) {
        return userProfileRepository.findByNewMessageNotificationsIsTrueAndGroup(recipient);
    }

    @Override
    public List<UserProfile> findByRetentionWarningNotificationsIsTrueAndGroup(Group recipient) {
        return userProfileRepository.findByRetentionWarningNotificationsIsTrueAndGroup(recipient);
    }

    @Override
    public List<UserProfile> findByMessageStatusForSenderNotificationsIsTrueAndGroup(Group group) {
        return userProfileRepository.findByMessageStatusForSenderNotificationsIsTrueAndGroup(group);
    }

    @Override
    public List<String> bulkAddUserProfiles(byte[] fileContent, Long businessId) {
        List<String> errors = new ArrayList<>();
        List<BulkUserProfileSpec> userProfileSpecs;
        try {
            userProfileSpecs = csvService.parseCSVFileToUserProfile(fileContent, businessId);
            errors = validateBulkUserProfileSpec(userProfileSpecs, businessId);
        } catch (Exception e) {
            errors.add(e.getMessage());
            return errors;
        }

        if (errors.isEmpty()) {
            List<String> finalErrors = new ArrayList<>();
            try {
                userProfileSpecs.forEach(bulkUserProfileSpec -> {
                    UserProfile userProfile = userProfileRepository.save(toUserProfile(bulkUserProfileSpec));
                    if (grantedAuthorityService.existsByUserAndGroup(userProfile.getUser(), userProfile.getGroup()))
                        throw new EtxWebException(String.format("User with ecasId: %s and group: %s already configured", userProfile.getUser().getEcasId(),
                                userProfile.getGroup().getIdentifier()));

                    if (Boolean.TRUE.equals(bulkUserProfileSpec.getIsAdmin()))
                        grantedAuthorityService.create(userProfile.getUser(), RoleName.GROUP_ADMIN, userProfile.getGroup().getId());

                    if (Boolean.TRUE.equals(bulkUserProfileSpec.getIsOperator()))
                        grantedAuthorityService.create(userProfile.getUser(), RoleName.OPERATOR, userProfile.getGroup().getId());

                });

            } catch (Exception e) {
                finalErrors.add(e.getMessage());
            }

            return finalErrors;
        }

        return errors;
    }

    @Override
    @Transactional
    public Page<UserListItem> getUsers(String filterValue, PageRequest pageRequest) {
        Page<UserListItem> userListItems = userProfileRepository.findByNameOrEcasIdContains(filterValue, pageRequest);
        userListItems.forEach(this::initRoleNames);
        return userListItems;
    }

    @Override
    public Page<UserListItem> getUsers(String filterValue, String role, Boolean isAllUsers, PageRequest pageRequest) {
        if (Objects.equals(RoleName.OFFICIAL_IN_CHARGE.toString(), role)) {
            return new RestResponsePage<>(this.findOfficialsInCharge(filterValue, pageRequest));
        }

        if (Boolean.TRUE.equals(isAllUsers)) {
            return new RestResponsePage<>(this.getUsers(filterValue, pageRequest));
        } else {
            return new RestResponsePage<>(this.findSysAdmins(filterValue, pageRequest));
        }
    }

    @Override
    public List<SearchItem> searchAllUsers(String filterValue) {
        Page<UserListItem> sysAdmins = userProfileRepository.findByNameOrEcasIdContains(filterValue, Pageable.unpaged());

        return toSearchItem(sysAdmins.getContent());
    }

    @Override
    public List<SearchItem> preSearchUsers(String filterValue, Boolean isAllUsers) {
        if (Boolean.TRUE.equals(isAllUsers)) {
            return this.searchAllUsers(filterValue);
        } else {
            return this.searchSysAdmins(filterValue);
        }
    }

    @Override
    @Transactional
    public Set<String> retrieveEmailsForNotification(Long businessId) {
        Group group = groupService.findById(businessId);

        Set<String> emailSet = userProfileRepository.findEmailsByGroupIdOrBusinessId(businessId).collect(Collectors.toSet());
        if (group.getType() == GroupType.BUSINESS) {
            groupService.findByParentIdAndType(businessId, GroupType.ENTITY)
                    .filter(aGroup -> StringUtils.isNotEmpty(aGroup.getNewMessageNotificationEmailAddresses()) || StringUtils.isNotEmpty(aGroup.getStatusNotificationEmailAddress())
                            || StringUtils.isNotEmpty(aGroup.getRetentionWarningNotificationEmailAddresses()))
                    .forEach(aGroup -> collectEmailAddresses(emailSet, aGroup));
        } else {
            collectEmailAddresses(emailSet, group);
        }

        return emailSet;
    }

    private void collectEmailAddresses(Set<String> emailSet, Group group) {
        if (StringUtils.isNotEmpty(group.getStatusNotificationEmailAddress())) {
            emailSet.add(group.getStatusNotificationEmailAddress());
        }
        if (StringUtils.isNotEmpty(group.getNewMessageNotificationEmailAddresses())) {
            emailSet.addAll(Arrays.stream(group.getNewMessageNotificationEmailAddresses().split(",")).collect(Collectors.toSet()));
        }
        if (StringUtils.isNotEmpty(group.getRetentionWarningNotificationEmailAddresses())) {
            emailSet.addAll(Arrays.stream(group.getRetentionWarningNotificationEmailAddresses().split(",")).collect(Collectors.toSet()));
        }
    }

    private void initRoleNames(UserListItem userListItem) {
        grantedAuthorityService.findByUserEcasIdAndGroupId(userListItem.getEcasId(), userListItem.getGroupId())
                .forEach(grantedAuthority -> {
                    userListItem.getRoleNames().add(grantedAuthority.getRole().getName());
                    userListItem.setStatus(grantedAuthority.isEnabled());
                });

    }

    private List<SearchItem> toSearchItem(List<UserListItem> userProfiles) {
        return userProfiles.stream().map(x -> new SearchItem(x.getEcasId(), String.format("%s - %s", x.getEcasId(), x.getName()))).distinct().collect(Collectors.toList());
    }

    private UserProfile toUserProfile(UserProfileSpec userProfileSpec) {
        Group group = groupService.findById(userProfileSpec.getGroupId());

        String ecasId = userProfileSpec.getEcasId().toLowerCase();

        User user;
        if (etrustexWebProperties.isDevEnvironment()) {
            user = userService.findOptionalByEcasId(ecasId)
                    .orElse(userService.create(ecasId, userProfileSpec.getName(), userProfileSpec.getEuLoginEmailAddress()));
        } else {
            LdapUserDto ldapUserDto = userService.getEuLoginProfileFromLdap(ecasId);
            if (Objects.isNull(ldapUserDto) && !etrustexWebProperties.isDevEnvironment()) {
                throw new EtxWebException(String.format("Cannot find user with ecasId: %s in LDAP group", ecasId));
            }

            user = userService.findOptionalByEcasId((ecasId))
                    .map(existingUser -> {
                        existingUser.setName(cleanFunctionalUserName(ldapUserDto));
                        existingUser.setEuLoginEmailAddress(ldapUserDto.getEuLoginEmail());
                        return userService.save(existingUser);
                    }).orElseGet(() -> userService.save(User.builder()
                            .ecasId(ecasId)
                            .name(cleanFunctionalUserName(ldapUserDto))
                            .euLoginEmailAddress(ldapUserDto.getEuLoginEmail())
                            .build()));
        }

        return UserProfile.builder()
                .user(user)
                .group(group)
                .alternativeEmail(userProfileSpec.getAlternativeEmail())
                .alternativeEmailUsed(userProfileSpec.isAlternativeEmailUsed())
                .newMessageNotifications(userProfileSpec.isNewMessageNotification())
                .messageStatusForSenderNotifications(userProfileSpec.isStatusNotification())
                .retentionWarningNotifications(userProfileSpec.isRetentionWarningNotification())
                .build();
    }

    @Override
    public String cleanFunctionalUserName(LdapUserDto user) {
        String euLogin = user.getEuLogin().toLowerCase();
        if (euLogin.matches("^([jaw]).*\\d.*")) {
            return "Functional user";
        }
        return user.getFullName();
    }

    private List<String> validateBulkUserProfileSpec(List<BulkUserProfileSpec> userProfileSpecs, Long businessId) {
        List<String> errors = new ArrayList<>();
        Validator validator;
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }

        IntStream.range(0, userProfileSpecs.size())
                .forEach(i -> {
                    UserProfileSpec userProfileSpec = userProfileSpecs.get(i);
                    Set<ConstraintViolation<UserProfileSpec>> constraintViolations = validator.validate(userProfileSpec);

                    if (!constraintViolations.isEmpty()) {
                        constraintViolations.forEach(constraintViolation ->
                                errors.add(constraintViolation.getMessage() + " - line " + (i + 1)));
                    }

                    if (!groupService.existsByIdAndParentId(userProfileSpec.getGroupId(), businessId)) {
                        errors.add(String.format("Entity with id %s doesn't belong to business %s", userProfileSpec.getGroupId(), businessId));
                    }
                });

        return errors;
    }

}
