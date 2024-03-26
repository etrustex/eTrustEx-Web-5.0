package eu.europa.ec.etrustex.web.rest;

import eu.europa.ec.etrustex.web.exchange.model.SearchItem;
import eu.europa.ec.etrustex.web.exchange.model.UserListItem;
import eu.europa.ec.etrustex.web.persistence.entity.UserProfile;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.service.MailService;
import eu.europa.ec.etrustex.web.service.UserRegistrationRequestService;
import eu.europa.ec.etrustex.web.service.dto.LdapUserDto;
import eu.europa.ec.etrustex.web.service.pagination.RestResponsePage;
import eu.europa.ec.etrustex.web.service.security.UserProfileService;
import eu.europa.ec.etrustex.web.service.security.UserService;
import eu.europa.ec.etrustex.web.service.validation.annotation.CheckSortField;
import eu.europa.ec.etrustex.web.service.validation.model.CreateUserProfileSpec;
import eu.europa.ec.etrustex.web.service.validation.model.DeleteUserProfileSpec;
import eu.europa.ec.etrustex.web.service.validation.model.NotificationEmailSpec;
import eu.europa.ec.etrustex.web.service.validation.model.UserProfileSpec;
import eu.europa.ec.etrustex.web.service.validation.post_auth_validation.PostAuthValidated;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static eu.europa.ec.etrustex.web.exchange.util.UrlTemplates.*;

@RestController
@AllArgsConstructor
@Slf4j
@Validated
public class UserProfileController {
    private static final String USER_LIST_ITEM_SORTABLE_FIELDS = "emailAddress,group.id,group.name,group.type," +
            "auditingEntity.createdDate,auditingEntity.createdBy,auditingEntity.modifiedDate," +
            "auditingEntity.modifiedBy,newMessageNotifications,messageStatusForSenderNotifications," +
            "user.name,user.ecasId";

    private final UserProfileService userProfileService;
    private final UserService userService;
    private final MailService mailService;
    private final UserRegistrationRequestService userRegistrationRequestService;

    @PostMapping(value = USER_PROFILES)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #userProfileSpec, T(eu.europa.ec.etrustex.web.persistence.entity.UserProfile), T(eu.europa.ec.etrustex.web.common.UserAction).CREATE)")
    @PostAuthValidated
    public UserProfile create(@Valid @RequestBody CreateUserProfileSpec userProfileSpec) {
        return userProfileService.create(userProfileSpec);
    }

    @GetMapping(value = USER_PROFILE_EULOGIN)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #groupId, T(eu.europa.ec.etrustex.web.persistence.entity.UserProfile), T(eu.europa.ec.etrustex.web.common.UserAction).RETRIEVE)")
    @PostAuthValidated
    public UserProfileSpec fetchEuLoginInfo(@RequestParam Long groupId, @RequestParam String mailOrEcasId) {
        LdapUserDto ldapUserDto = userService.getEuLoginProfileFromLdapWithValidation(mailOrEcasId);
        if (ldapUserDto != null) {
            return UserProfileSpec.builder()
                    .name(userProfileService.cleanFunctionalUserName(ldapUserDto))
                    .ecasId(ldapUserDto.getEuLogin())
                    .euLoginEmailAddress(ldapUserDto.getEuLoginEmail())
                    .build();
        }
        return null;
    }

    @GetMapping(value = USER_PROFILE_INFO)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #groupId, T(eu.europa.ec.etrustex.web.persistence.entity.UserProfile), T(eu.europa.ec.etrustex.web.common.UserAction).RETRIEVE)")
    public UserProfileSpec fetchUserProfileInfo(@RequestParam Long groupId, @RequestParam String ecasId) {
        User user = userService.findByEcasId(ecasId);
        UserProfile userProfile = userProfileService.findUserProfileByUserAndGroupId(user, groupId);
        if (userProfile != null) {
            return UserProfileSpec.builder()
                    .name(userProfile.getUser().getName())
                    .ecasId(userProfile.getUser().getEcasId())
                    .euLoginEmailAddress(userProfile.getUser().getEuLoginEmailAddress())
                    .build();
        }

        return null;
    }

    @GetMapping(value = USER_PROFILES_SEARCH)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #groupId, T(eu.europa.ec.etrustex.web.persistence.entity.UserProfile), T(eu.europa.ec.etrustex.web.common.UserAction).RETRIEVE)")
    @PostAuthValidated
    public List<SearchItem> search(
            @RequestParam Long groupId,
            @RequestParam String euLoginOrName,
            @RequestParam(required = false) Boolean isRegistrationRequest,
            @RequestParam(required = false, defaultValue = "") String roleName) {

        return userProfileService.search(groupId, euLoginOrName, isRegistrationRequest, StringUtils.isNotEmpty(roleName) ? RoleName.valueOf(roleName) : null);
    }

    @GetMapping(value = USER_LIST_ITEMS)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #groupId ,T(eu.europa.ec.etrustex.web.persistence.entity.UserProfile), T(eu.europa.ec.etrustex.web.common.UserAction).RETRIEVE)")
    @Validated
    public RestResponsePage<UserListItem> getUserListItems(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false, defaultValue = "auditingEntity.createdDate")
            @CheckSortField(allowedFields = USER_LIST_ITEM_SORTABLE_FIELDS) String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") String sortOrder,
            @RequestParam(required = false) String role,
            @RequestParam(required = false, defaultValue = "name,ecasId") String filterBy,
            @RequestParam(required = false, defaultValue = "") String filterValue,
            @RequestParam Long groupId,
            @RequestParam(required = false) Boolean isRegistrationRequest) {

        Sort.Order order = new Sort.Order(Sort.Direction.fromString(sortOrder), sortBy);
        Sort sort = Sort.by(sortBy.equals("auditingEntity.createdDate") ? order : order.ignoreCase());
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        if (!Objects.equals(filterBy, "name,ecasId")) {
            throw new EtxWebException(String.format("Search UserProfiles by %s not implemented!", filterBy));
        }

        if (role != null) {
            return new RestResponsePage<>(userProfileService.getByRoleNameAndGroupId(RoleName.valueOf(role), groupId, filterValue, pageRequest));
        } else {
            return new RestResponsePage<>(userProfileService.getByGroupId(groupId, filterValue, pageRequest, isRegistrationRequest));
        }
    }

    @GetMapping(value = USER_PROFILE_NOTIFICATION_EMAILS)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #groupId ,T(eu.europa.ec.etrustex.web.persistence.entity.UserProfile), T(eu.europa.ec.etrustex.web.common.UserAction).RETRIEVE)")
    public Set<String> retrieveEmailsForNotification(@RequestParam Long groupId) {
        return userProfileService.retrieveEmailsForNotification(groupId);
    }

    @PostMapping(value = USER_PROFILE_SEND_NOTIFICATION_EMAILS)
    @ResponseStatus(HttpStatus.OK)
    @PostAuthValidated
    public ResponseEntity<Void> sendNotificationEmails(@Valid @RequestBody NotificationEmailSpec notificationEmailSpec) {
        if (notificationEmailSpec.isUserRegistration()) {
            userRegistrationRequestService.sendNotificationEmail(notificationEmailSpec);
        } else {
            mailService.send(notificationEmailSpec);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(value = USER_PROFILE_DELETE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #deleteUserProfileSpec, T(eu.europa.ec.etrustex.web.persistence.entity.UserProfile), T(eu.europa.ec.etrustex.web.common.UserAction).DELETE)")
    @Validated
    public ResponseEntity<Void> delete(@RequestBody DeleteUserProfileSpec deleteUserProfileSpec) {
        userProfileService.delete(deleteUserProfileSpec.getEcasId(), deleteUserProfileSpec.getGroupId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(value = USER_PROFILES)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #userProfileSpec, T(eu.europa.ec.etrustex.web.persistence.entity.UserProfile), T(eu.europa.ec.etrustex.web.common.UserAction).UPDATE)")
    public UserProfile update(@Valid @RequestBody UserProfileSpec userProfileSpec) {
        return userProfileService.update(userProfileSpec);
    }

    @PutMapping(value = USER_PROFILE_FILE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #businessId, T(eu.europa.ec.etrustex.web.persistence.entity.UserProfile), T(eu.europa.ec.etrustex.web.common.UserAction).UPDATE)")
    public List<String> uploadBulk(@RequestBody byte[] fileContent, @RequestParam Long businessId) {
        log.info("Processing CSV file");
        return userProfileService.bulkAddUserProfiles(fileContent, businessId);
    }

}
