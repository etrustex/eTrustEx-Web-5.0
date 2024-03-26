package eu.europa.ec.etrustex.web.rest;


import eu.europa.ec.etrustex.web.persistence.entity.UserRegistrationRequest;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.service.UserRegistrationRequestService;
import eu.europa.ec.etrustex.web.service.validation.model.BaseUserRegistrationRequestSpec;
import eu.europa.ec.etrustex.web.service.validation.model.UserRegistrationRequestSpec;
import eu.europa.ec.etrustex.web.service.validation.post_auth_validation.PostAuthValidated;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static eu.europa.ec.etrustex.web.exchange.util.UrlTemplates.USER_REGISTRATION;

@RestController
@AllArgsConstructor
@Slf4j
public class UserRegistrationRequestController {

    private final UserRegistrationRequestService userRegistrationRequestService;
    @PostMapping(value = USER_REGISTRATION)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("authenticated")
    @PostAuthValidated
    public UserRegistrationRequest create(SecurityUserDetails userDetails, @Valid @RequestBody UserRegistrationRequestSpec userRegistrationRequestSpec) {
        return userRegistrationRequestService.create(userDetails.getUser(), userRegistrationRequestSpec);
    }

    @DeleteMapping(value = USER_REGISTRATION)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #baseUserRegistrationRequestSpecs, T(eu.europa.ec.etrustex.web.service.validation.model.BaseUserRegistrationRequestSpec), T(eu.europa.ec.etrustex.web.common.UserAction).DELETE)")
    public ResponseEntity<Void> delete(@Valid @RequestBody List<BaseUserRegistrationRequestSpec> baseUserRegistrationRequestSpecs) {

        userRegistrationRequestService.deleteUserRegistrationRequestAndCleanUp(baseUserRegistrationRequestSpecs);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(value = USER_REGISTRATION)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #userRegistrationRequestSpec, T(eu.europa.ec.etrustex.web.service.validation.model.BaseUserRegistrationRequestSpec), T(eu.europa.ec.etrustex.web.common.UserAction).UPDATE)")
    public UserRegistrationRequest update(@RequestBody UserRegistrationRequestSpec userRegistrationRequestSpec) {
        return userRegistrationRequestService.update(userRegistrationRequestSpec);
    }

}
