package eu.europa.ec.etrustex.web.rest;

import eu.europa.ec.etrustex.web.exchange.model.AlertSpec;
import eu.europa.ec.etrustex.web.hateoas.AlertLinksHandler;
import eu.europa.ec.etrustex.web.persistence.entity.Alert;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.service.AlertService;
import eu.europa.ec.etrustex.web.service.AlertUserStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static eu.europa.ec.etrustex.web.exchange.util.UrlTemplates.ALERT;
import static eu.europa.ec.etrustex.web.exchange.util.UrlTemplates.ALERTS;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AlertController {

    private final AlertService alertService;
    private final AlertUserStatusService alertUserStatusService;
    private final AlertLinksHandler alertLinksHandler;

    /**
     * @param businessId       id of the business of which we want the active alert or ROOT for system-wide alerts.
     * @param retrieveInactive whether inactive alerts will be returned
     * @return List of Alert. If retrieveInactive is true, one element list (business or ROOT alert). Otherwise both
     */
    @GetMapping(value = ALERT)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #businessId, T(eu.europa.ec.etrustex.web.persistence.entity.Alert), T(eu.europa.ec.etrustex.web.common.UserAction).RETRIEVE)")
    public List<Alert> get(SecurityUserDetails principal, @PathVariable Long businessId, @RequestParam(required = false) Boolean retrieveInactive) {
        if (retrieveInactive != null && retrieveInactive) {
            return alertService.findByGroupId(businessId)
                    .map(Collections::singletonList)
                    .orElse(Collections.emptyList());
        }

        return alertService.findActiveByGroupIdAndDate(businessId, new Date()).stream().filter(alert ->
                Objects.isNull(alertUserStatusService.findByUserIdAndAlertIdAndGroupId(principal.getUser().getId(), alert.getId(), alert.getGroup().getId())))
                .collect(Collectors.toList());
    }

    @PostMapping(ALERTS)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #alertSpec, T(eu.europa.ec.etrustex.web.persistence.entity.Alert), T(eu.europa.ec.etrustex.web.common.UserAction).CREATE)")
    public Alert create(@Valid @RequestBody AlertSpec alertSpec) {
        return alertLinksHandler.addLinks(alertService.save(alertSpec));
    }

    @PutMapping(ALERTS)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #alertSpec, T(eu.europa.ec.etrustex.web.persistence.entity.Alert), T(eu.europa.ec.etrustex.web.common.UserAction).UPDATE)")
    public Alert update(@Valid @RequestBody AlertSpec alertSpec) {
        return alertLinksHandler.addLinks(alertService.save(alertSpec));
    }
}
