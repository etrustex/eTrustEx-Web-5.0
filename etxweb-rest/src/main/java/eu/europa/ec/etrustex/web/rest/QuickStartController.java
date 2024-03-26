package eu.europa.ec.etrustex.web.rest;

import com.fasterxml.jackson.annotation.JsonView;
import eu.europa.ec.etrustex.web.util.exchange.filter.DetailsViewFilter;
import eu.europa.ec.etrustex.web.service.QuickStartService;
import eu.europa.ec.etrustex.web.service.validation.model.QuickStartSpec;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static eu.europa.ec.etrustex.web.exchange.util.UrlTemplates.QUICK_START;

@RestController
@AllArgsConstructor
@Slf4j
public class QuickStartController {
    private final QuickStartService quickStartService;

    @PostMapping(value = QUICK_START)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #spec.groupSpec, T(eu.europa.ec.etrustex.web.persistence.entity.security.Group), T(eu.europa.ec.etrustex.web.common.UserAction).CREATE)")
    @JsonView(DetailsViewFilter.class)
    public ResponseEntity<Void> create(@Valid @RequestBody QuickStartSpec spec) {
        quickStartService.create(spec);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
