package eu.europa.ec.etrustex.web.rest;

import eu.europa.ec.etrustex.web.common.template.TemplateType;
import eu.europa.ec.etrustex.web.exchange.model.TemplateSpec;
import eu.europa.ec.etrustex.web.persistence.entity.Template;
import eu.europa.ec.etrustex.web.service.template.TemplateService;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.Objects;

import static eu.europa.ec.etrustex.web.exchange.util.UrlTemplates.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TemplateController {

    private final TemplateService templateService;

    /**
     * @param businessId id of the business of which we want to retrieve the template.
     * @param type       type to be returned
     * @return Template or default template.
     */
    @GetMapping(value = TEMPLATE)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #businessId, T(eu.europa.ec.etrustex.web.persistence.entity.Template), T(eu.europa.ec.etrustex.web.common.UserAction).RETRIEVE)")
    public Template get(@PathVariable Long businessId, @RequestParam String type) {
        TemplateType templateType = Arrays.stream(TemplateType.values())
                .filter(t -> Objects.equals(t.name(), type.toUpperCase()))
                .findFirst()
                .orElseThrow(() -> new EtxWebException("Unsupported type"));
        return templateService.getByGroupType(businessId, templateType);
    }
    @GetMapping(value = DEFAULT_TEMPLATES)
    public Template getDefault(@RequestParam String type) {
        TemplateType templateType = Arrays.stream(TemplateType.values())
                .filter(t -> Objects.equals(t.name(), type.toUpperCase()))
                .findFirst()
                .orElseThrow(() -> new EtxWebException("Unsupported type"));
        return templateService.getDefaultTemplateByType(templateType);
    }

    @PutMapping(TEMPLATES)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #templateSpec, T(eu.europa.ec.etrustex.web.persistence.entity.Template), T(eu.europa.ec.etrustex.web.common.UserAction).UPDATE)")
    public Template update(@Valid @RequestBody TemplateSpec templateSpec) {
        return templateService.saveOrUpdate(templateSpec);
    }
}
