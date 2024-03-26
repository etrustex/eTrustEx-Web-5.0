package eu.europa.ec.etrustex.web.rest;

import eu.europa.ec.etrustex.web.exchange.model.AlertUserStatusSpec;
import eu.europa.ec.etrustex.web.persistence.entity.AlertUserStatus;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.service.AlertService;
import eu.europa.ec.etrustex.web.service.AlertUserStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static eu.europa.ec.etrustex.web.exchange.util.UrlTemplates.ALERT_USER_STATUS;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AlertUserStatusController {

    private final AlertUserStatusService alertUserStatusService;
    private final AlertService alertService;

    @GetMapping(ALERT_USER_STATUS)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #businessId, T(eu.europa.ec.etrustex.web.persistence.entity.AlertUserStatus), T(eu.europa.ec.etrustex.web.common.UserAction).RETRIEVE)")
    public List<AlertUserStatus> find(SecurityUserDetails principal, @RequestParam Long businessId) {
        List<AlertUserStatus> alertUserStatuses = new ArrayList<>();

        alertService.findActiveByGroupIdAndDate(businessId, new Date()).forEach(alert -> {
                    AlertUserStatus alertUserStatus = alertUserStatusService.findByUserIdAndAlertIdAndGroupId(principal.getUser().getId(), alert.getId(), alert.getGroup().getId());
                    if (Objects.nonNull(alertUserStatus)) {
                        alertUserStatuses.add(alertUserStatus);
                    }
                });
        return alertUserStatuses;
    }

    @PostMapping(ALERT_USER_STATUS)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #alertUserStatusSpec, T(eu.europa.ec.etrustex.web.persistence.entity.AlertUserStatus), T(eu.europa.ec.etrustex.web.common.UserAction).CREATE)")
    public AlertUserStatus create(SecurityUserDetails principal, @RequestBody AlertUserStatusSpec alertUserStatusSpec) {
        alertUserStatusSpec.setUserId(principal.getUser().getId());
        return alertUserStatusService.create(alertUserStatusSpec);
    }

    @PutMapping(ALERT_USER_STATUS)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #alertUserStatusSpec, T(eu.europa.ec.etrustex.web.persistence.entity.AlertUserStatus), T(eu.europa.ec.etrustex.web.common.UserAction).UPDATE)")
    public AlertUserStatus update(@RequestBody AlertUserStatusSpec alertUserStatusSpec) {
        return alertUserStatusService.update(alertUserStatusSpec);
    }
}
