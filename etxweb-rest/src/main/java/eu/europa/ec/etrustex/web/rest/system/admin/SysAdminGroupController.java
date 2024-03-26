package eu.europa.ec.etrustex.web.rest.system.admin;


import eu.europa.ec.etrustex.web.exchange.model.EntityItem;
import eu.europa.ec.etrustex.web.exchange.model.SearchItem;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.service.pagination.RestResponsePage;
import eu.europa.ec.etrustex.web.service.security.GroupService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static eu.europa.ec.etrustex.web.exchange.util.SystemAdminUrlTemplates.*;


@RestController("systemAdminGroupController")
@PreAuthorize("hasAuthority('SYS_ADMIN')")
@AllArgsConstructor
@Slf4j
public class SysAdminGroupController {

    private final GroupService groupService;

    @GetMapping(value = GROUP_SYS_ADMIN)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #groupId, T(eu.europa.ec.etrustex.web.persistence.entity.security.Group), T(eu.europa.ec.etrustex.web.common.UserAction).RETRIEVE)")
    public Group get() {
        return groupService.getRoot();
    }

    @GetMapping(value = GROUPS_SYS_ADMIN)
    @ResponseStatus(HttpStatus.OK)
    public RestResponsePage<EntityItem> get(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false, defaultValue = "auditingEntity.createdDate") String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") String sortOrder,
            @RequestParam(required = false, defaultValue = "") String filterValue) {

        Sort.Order order = new Sort.Order(Sort.Direction.fromString(sortOrder), sortBy);
        Sort sort = Sort.by(sortBy.equals("auditingEntity.createdDate") ? order : order.ignoreCase());
        PageRequest pageRequest = PageRequest.of(page, size, sort.and(Sort.by(Sort.Order.asc("id"))));

        Page<EntityItem> entityItemPage = groupService.findAllConfiguredGroups(pageRequest, filterValue);
        return new RestResponsePage<>(entityItemPage.getContent(), entityItemPage.getPageable(), entityItemPage.getTotalElements());
    }

    @GetMapping(value = GROUPS_SYS_ADMIN_SEARCH)
    @ResponseStatus(HttpStatus.OK)
    public List<SearchItem> search(
            @RequestParam(required = false, defaultValue = "") String filterValue) {
        return groupService.searchAllConfiguredGroups(filterValue);
    }
}
