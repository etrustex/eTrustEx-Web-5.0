package eu.europa.ec.etrustex.web.rest;


import eu.europa.ec.etrustex.web.exchange.model.GroupSearchItem;
import eu.europa.ec.etrustex.web.hateoas.GroupLinksHandler;
import eu.europa.ec.etrustex.web.persistence.entity.security.GrantedAuthority;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.service.pagination.RestResponsePage;
import eu.europa.ec.etrustex.web.service.security.GroupService;
import eu.europa.ec.etrustex.web.service.validation.model.GroupSpec;
import eu.europa.ec.etrustex.web.service.validation.post_auth_validation.PostAuthValidated;
import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static eu.europa.ec.etrustex.web.exchange.util.UrlTemplates.*;

@RestController
@AllArgsConstructor
@Slf4j
public class GroupController {

    private final GroupService groupService;
    private final GroupLinksHandler groupLinksHandler;

    /**
     * @param groupId Group id
     * @return the Group
     */
    @GetMapping(value = GROUP)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #groupId, T(eu.europa.ec.etrustex.web.persistence.entity.security.Group), T(eu.europa.ec.etrustex.web.common.UserAction).RETRIEVE)")
    public Group get(@PathVariable Long groupId) {
        return groupLinksHandler.addLinks(groupService.findById(groupId));
    }

    /**
     * @param page
     * @param size
     * @param sortBy
     * @param sortOrder
     * @param filterBy
     * @param filterValue
     * @param groupType
     * @param parentId    required if groupIds not present
     * @param groupIds    required if parentId not present
     * @param principal
     * @return
     */
    @GetMapping(value = GROUPS)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #parentId != null ? #parentId : #groupIds, T(eu.europa.ec.etrustex.web.persistence.entity.security.Group), T(eu.europa.ec.etrustex.web.common.UserAction).RETRIEVE)")
    public RestResponsePage<Group> get(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false, defaultValue = "auditingEntity.createdDate") String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") String sortOrder,
            @RequestParam(required = false, defaultValue = "") String filterBy,
            @RequestParam(required = false, defaultValue = "") String filterValue,
            @RequestParam(required = false) String groupType,
            @RequestParam(required = false) Long parentId,
            @RequestParam(required = false) Long[] groupIds,
            SecurityUserDetails principal) {

        Sort.Order order = new Sort.Order(Sort.Direction.fromString(sortOrder), sortBy);
        Sort sort = Sort.by(sortBy.equals("auditingEntity.createdDate") ? order : order.ignoreCase());
        PageRequest pageRequest = PageRequest.of(page, size, sort.and(Sort.by(Sort.Order.asc("id"))));

        Page<Group> groupsPage;

        if (StringUtils.isNotBlank(groupType) && parentId != null) {
            Collection<GrantedAuthority> grantedAuthorities = new HashSet<>(principal.getAuthorities());
            groupsPage = groupService.findByTypeAndParent(GroupType.valueOf(groupType), parentId, grantedAuthorities, pageRequest, filterBy, filterValue);
        } else {
            groupsPage = groupService.findByIds(pageRequest, filterBy, groupIds);
        }

        groupsPage.forEach(groupLinksHandler::addLinks);

        return new RestResponsePage<>(groupsPage);
    }

    @PostMapping(value = GROUPS)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #spec, T(eu.europa.ec.etrustex.web.persistence.entity.security.Group), T(eu.europa.ec.etrustex.web.common.UserAction).CREATE)")
    @PostAuthValidated
    public Group create(@Valid @RequestBody GroupSpec spec) {
        return groupLinksHandler.addLinks(this.groupService.create(spec));
    }

    @PostMapping(value = IS_VALID_GROUP)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #spec, T(eu.europa.ec.etrustex.web.persistence.entity.security.Group), T(eu.europa.ec.etrustex.web.common.UserAction).CREATE)")
    @PostAuthValidated
    public ResponseEntity<Boolean> isValid(@Valid @RequestBody GroupSpec spec) {
        return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
    }

    @PutMapping(value = GROUP)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #groupId, T(eu.europa.ec.etrustex.web.persistence.entity.security.Group), T(eu.europa.ec.etrustex.web.common.UserAction).UPDATE)")
    @PostAuthValidated
    public Group update(
            @PathVariable Long groupId,
            @Valid @RequestBody GroupSpec groupSpec) {
        return groupLinksHandler.addLinks(this.groupService.update(groupId, groupSpec));
    }

    @GetMapping(value = GROUP_SEARCH)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #businessId, T(eu.europa.ec.etrustex.web.persistence.entity.security.Group), T(eu.europa.ec.etrustex.web.common.UserAction).RETRIEVE)")
    public List<GroupSearchItem> find(@RequestParam(required = false) Long businessId,
                                      @RequestParam String groupIdOrName) {
        return groupService.findByIdentifierOrNameLike(businessId, groupIdOrName);
    }

    @GetMapping(value = IS_BUSINESS_EMPTY)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #groupId, T(eu.europa.ec.etrustex.web.persistence.entity.security.Group), T(eu.europa.ec.etrustex.web.common.UserAction).RETRIEVE)")
    public ResponseEntity<Boolean> isBusinessEmpty(@PathVariable Long groupId) {
        return new ResponseEntity<>(!groupService.hasChildrenOrChannelsOrUsersOrMessages(groupId), HttpStatus.OK);
    }

    @DeleteMapping(value = GROUP_DELETE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #groupId, T(eu.europa.ec.etrustex.web.persistence.entity.security.Group), T(eu.europa.ec.etrustex.web.common.UserAction).DELETE)")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long groupId) {
        Group group = groupService.findById(groupId);
        groupService.deleteGroup(group);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(value = BUSINESS_DELETE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #groupId, T(eu.europa.ec.etrustex.web.persistence.entity.security.Group), T(eu.europa.ec.etrustex.web.common.UserAction).DELETE)")
    public ResponseEntity<Void> deleteBusiness(@PathVariable Long groupId) {
        Group group = groupService.findById(groupId);
        groupService.deleteBusiness(group);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(value = BUSINESS_CANCEL_DELETION)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #groupId, T(eu.europa.ec.etrustex.web.persistence.entity.security.Group), T(eu.europa.ec.etrustex.web.common.UserAction).UPDATE)")
    public ResponseEntity<Void> cancelBusinessDeletion(@PathVariable Long groupId) {
        groupService.cancelBusinessDeletion(groupId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(value = BUSINESS_CONFIRM_DELETION)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #groupId, T(eu.europa.ec.etrustex.web.persistence.entity.security.Group), T(eu.europa.ec.etrustex.web.common.UserAction).DELETE)")
    public ResponseEntity<Void> confirmBusinessDeletion(@PathVariable Long groupId) {
        groupService.confirmBusinessDeletion(groupId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = IS_GROUP_EMPTY)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #groupId, T(eu.europa.ec.etrustex.web.persistence.entity.security.Group), T(eu.europa.ec.etrustex.web.common.UserAction).RETRIEVE)")
    public ResponseEntity<Boolean> isGroupEmpty(@PathVariable Long groupId) {
        return new ResponseEntity<>(!groupService.hasMessages(groupId), HttpStatus.OK);
    }

    @PutMapping(value = GROUP_BULK_FILE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #businessId, T(eu.europa.ec.etrustex.web.persistence.entity.security.Group), T(eu.europa.ec.etrustex.web.common.UserAction).UPDATE)")
    public List<String> uploadBulk(@RequestBody byte[] fileContent, @RequestParam Long businessId) {
        log.info("Processing group bulk CSV file");
        return groupService.bulkAddGroups(fileContent, businessId);
    }
}
