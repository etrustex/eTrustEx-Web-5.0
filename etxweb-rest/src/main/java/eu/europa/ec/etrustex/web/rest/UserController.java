package eu.europa.ec.etrustex.web.rest;

import eu.europa.ec.etrustex.web.exchange.model.UserDetails;
import eu.europa.ec.etrustex.web.exchange.model.UserPreferencesSpec;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.service.security.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static eu.europa.ec.etrustex.web.exchange.util.UrlTemplates.USER_DETAILS;
import static eu.europa.ec.etrustex.web.exchange.util.UrlTemplates.USER_PREFERENCES;

@RestController
@AllArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserService userService;


    @GetMapping(value = USER_DETAILS)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("authenticated")
    public UserDetails getUserDetails(SecurityUserDetails userDetails) {
        return userDetails.toExchangeModel();
    }

    @PutMapping(value = USER_PREFERENCES)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("authenticated")
    public User updateUserPreferences(SecurityUserDetails userDetails, @RequestBody UserPreferencesSpec userPreferencesSpec) {
        return userService.updateUserPreferences(userDetails.getUser(), userPreferencesSpec);
    }

}
