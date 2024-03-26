package eu.europa.ec.etrustex.web.rest;

import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.service.security.GrantedAuthorityService;
import eu.europa.ec.etrustex.web.service.security.UserService;
import eu.europa.ec.etrustex.web.service.validation.model.GrantedAuthoritySpec;
import eu.europa.ec.etrustex.web.service.validation.post_auth_validation.PostAuthValidated;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static eu.europa.ec.etrustex.web.exchange.util.UrlTemplates.*;

@RestController
@AllArgsConstructor
@Slf4j
public class GrantedAuthorityController {
    private final GrantedAuthorityService grantedAuthorityService;
    private final UserService userService;

    @PostMapping(value = GRANTED_AUTHORITIES)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #grantedAuthoritySpec, T(eu.europa.ec.etrustex.web.service.validation.model.GrantedAuthoritySpec), T(eu.europa.ec.etrustex.web.common.UserAction).CREATE)")
    @PostAuthValidated
    public ResponseEntity<Void> create(@Valid @RequestBody GrantedAuthoritySpec grantedAuthoritySpec) {
        grantedAuthorityService.create(grantedAuthoritySpec);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping(value = GRANTED_AUTHORITIES)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #grantedAuthoritySpec, T(eu.europa.ec.etrustex.web.service.validation.model.GrantedAuthoritySpec), T(eu.europa.ec.etrustex.web.common.UserAction).DELETE)")
    public ResponseEntity<Void> delete(@Valid @RequestBody GrantedAuthoritySpec grantedAuthoritySpec) {
        grantedAuthorityService.delete(grantedAuthoritySpec);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(value = GRANTED_AUTHORITIES_UPDATE_GROUP)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #grantedAuthoritySpec, T(eu.europa.ec.etrustex.web.service.validation.model.GrantedAuthoritySpec), T(eu.europa.ec.etrustex.web.common.UserAction).UPDATE)")
    public ResponseEntity<Void> updateGroup(@RequestBody GrantedAuthoritySpec grantedAuthoritySpec, SecurityUserDetails userDetails) {
        grantedAuthorityService.updateGroup(grantedAuthoritySpec, userDetails.getUser());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(value = GRANTED_AUTHORITIES_UPDATE_GROUP_BULK)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #grantedAuthoritySpecs, T(eu.europa.ec.etrustex.web.service.validation.model.GrantedAuthoritySpec), T(eu.europa.ec.etrustex.web.common.UserAction).UPDATE)")
    public ResponseEntity<Void> updateGroupBulk(@RequestBody List<GrantedAuthoritySpec> grantedAuthoritySpecs, SecurityUserDetails userDetails) {
        grantedAuthorityService.updateGroups(grantedAuthoritySpecs, userDetails.getUser());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = GRANTED_AUTHORITIES_CREATE_BULK)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #grantedAuthoritySpecs, T(eu.europa.ec.etrustex.web.service.validation.model.GrantedAuthoritySpec), T(eu.europa.ec.etrustex.web.common.UserAction).CREATE)")
    @PostAuthValidated
    public ResponseEntity<Void> createBulk(@Valid @RequestBody List<GrantedAuthoritySpec> grantedAuthoritySpecs) {
        grantedAuthoritySpecs.forEach(grantedAuthoritySpec -> {
            User user = userService.findByEcasId(grantedAuthoritySpec.getUserName());
            grantedAuthorityService.create(user, grantedAuthoritySpec.getRoleName(), grantedAuthoritySpec.getGroupId());
        });

        return new ResponseEntity<>(HttpStatus.CREATED);
    }


}
