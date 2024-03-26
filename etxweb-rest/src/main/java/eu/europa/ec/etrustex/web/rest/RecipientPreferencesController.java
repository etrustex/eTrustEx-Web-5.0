package eu.europa.ec.etrustex.web.rest;

import eu.europa.ec.etrustex.web.exchange.model.RecipientPreferencesSpec;
import eu.europa.ec.etrustex.web.hateoas.RecipientPreferencesLinkHandler;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.RecipientPreferences;
import eu.europa.ec.etrustex.web.service.exchange.RecipientPreferencesService;
import eu.europa.ec.etrustex.web.service.validation.post_auth_validation.PostAuthValidated;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static eu.europa.ec.etrustex.web.exchange.util.UrlTemplates.RECIPIENT_PREFERENCES;
import static eu.europa.ec.etrustex.web.exchange.util.UrlTemplates.RECIPIENTS_PREFERENCES;


@RestController
@AllArgsConstructor
@Slf4j
public class RecipientPreferencesController {

    private final RecipientPreferencesService recipientPreferencesService;
    private final RecipientPreferencesLinkHandler recipientPreferencesLinkHandler;

    @PostMapping(value= RECIPIENTS_PREFERENCES)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, T(eu.europa.ec.etrustex.web.persistence.entity.exchange.RecipientPreferences), T(eu.europa.ec.etrustex.web.common.UserAction).CREATE)")
    @ResponseStatus(HttpStatus.CREATED)
    @PostAuthValidated
    public RecipientPreferences create(
            @RequestBody @Valid RecipientPreferencesSpec recipientPreferencesSpec) {
        return recipientPreferencesLinkHandler.addLinks(recipientPreferencesService.create(recipientPreferencesSpec));
    }

    @GetMapping(value= RECIPIENT_PREFERENCES)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #recipientPreferencesId, T(eu.europa.ec.etrustex.web.persistence.entity.exchange.RecipientPreferences), T(eu.europa.ec.etrustex.web.common.UserAction).RETRIEVE)")
    @ResponseStatus(HttpStatus.OK)
    public RecipientPreferences get(
            @PathVariable Long recipientPreferencesId) {
        return recipientPreferencesLinkHandler.addLinks(recipientPreferencesService.findById(recipientPreferencesId));
    }

    @PutMapping(value= RECIPIENT_PREFERENCES)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #recipientPreferencesId, T(eu.europa.ec.etrustex.web.persistence.entity.exchange.RecipientPreferences), T(eu.europa.ec.etrustex.web.common.UserAction).UPDATE)")
    @ResponseStatus(HttpStatus.OK)
    @PostAuthValidated
    public RecipientPreferences update(
            @PathVariable Long recipientPreferencesId,
            @RequestBody @Valid RecipientPreferencesSpec recipientPreferencesSpec) {
        return recipientPreferencesLinkHandler.addLinks(recipientPreferencesService.update(recipientPreferencesId, recipientPreferencesSpec));
    }
    
}
