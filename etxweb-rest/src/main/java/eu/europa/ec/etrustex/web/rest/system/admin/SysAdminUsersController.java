package eu.europa.ec.etrustex.web.rest.system.admin;

import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.exchange.model.SearchItem;
import eu.europa.ec.etrustex.web.exchange.model.UserListItem;
import eu.europa.ec.etrustex.web.persistence.entity.UserProfile;
import eu.europa.ec.etrustex.web.service.pagination.RestResponsePage;
import eu.europa.ec.etrustex.web.service.security.UserProfileService;
import eu.europa.ec.etrustex.web.service.validation.annotation.CheckSortField;
import eu.europa.ec.etrustex.web.service.validation.model.CreateUserProfileSpec;
import eu.europa.ec.etrustex.web.service.validation.model.SysDeleteUserProfileSpec;
import eu.europa.ec.etrustex.web.service.validation.model.UserProfileSpec;
import eu.europa.ec.etrustex.web.service.validation.post_auth_validation.PostAuthValidated;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import static eu.europa.ec.etrustex.web.exchange.util.SystemAdminUrlTemplates.*;


@RestController
@PreAuthorize("hasAuthority('SYS_ADMIN')")
@AllArgsConstructor
@Slf4j
@Validated
public class SysAdminUsersController {
    private static final String USER_LIST_ITEM_SORTABLE_FIELDS = "emailAddress,group.id,group.identifier,group.name,group.type,group.parent.name," +
            "auditingEntity.createdDate,auditingEntity.createdBy,auditingEntity.modifiedDate," +
            "auditingEntity.modifiedBy,newMessageNotifications,messageStatusForSenderNotifications," +
            "user.name,user.ecasId";

    private final UserProfileService userProfileService;


    @GetMapping(value = USER_PROFILES_SEARCH)
    @ResponseStatus(HttpStatus.OK)
    @PostAuthValidated
    public List<SearchItem> preSearchUsers(@RequestParam String euLoginOrName, @RequestParam Boolean isAllUsers) {
        return userProfileService.preSearchUsers(euLoginOrName, isAllUsers);
    }

    @GetMapping(value = USER_PROFILE_LIST_ITEMS)
    @ResponseStatus(HttpStatus.OK)
    @Validated
    public RestResponsePage<UserListItem> getSysAdminListItems(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false, defaultValue = "auditingEntity.createdDate")
            @CheckSortField(allowedFields = USER_LIST_ITEM_SORTABLE_FIELDS) String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") String sortOrder,
            @RequestParam(required = false, defaultValue = "SYS_ADMIN") String role,
            @RequestParam(required = false, defaultValue = "name,ecasId") String filterBy,
            @RequestParam(required = false, defaultValue = "") String filterValue,
            @RequestParam(required = false) Boolean isAllUsers) {


        Sort.Order order = new Sort.Order(Sort.Direction.fromString(sortOrder), sortBy);
        Sort sort = Sort.by(sortBy.equals("auditingEntity.createdDate") ? order : order.ignoreCase());
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        if (!Objects.equals(filterBy, "name,ecasId")) {
            throw new EtxWebException(String.format("Search UserProfiles by %s not implemented!", filterBy));
        }

        return new RestResponsePage<>(userProfileService.getUsers(filterValue, role, isAllUsers, pageRequest));

    }

    @PostMapping(value = USER_PROFILES)
    @ResponseStatus(HttpStatus.CREATED)
    @PostAuthValidated
    public UserProfile create(@Valid @RequestBody CreateUserProfileSpec userProfileSpec) {
        return userProfileService.create(userProfileSpec);
    }

    @PutMapping(value = USER_PROFILES)
    @ResponseStatus(HttpStatus.OK)
    public UserProfile update(@Valid @RequestBody UserProfileSpec userProfileSpec) {
        return userProfileService.update(userProfileSpec);
    }

    @DeleteMapping(value = USER_PROFILE_DELETE)
    @ResponseStatus(HttpStatus.OK)
    @Validated
    public ResponseEntity<Void> delete(@RequestBody SysDeleteUserProfileSpec deleteUserProfileSpec) {
        userProfileService.delete(deleteUserProfileSpec);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
