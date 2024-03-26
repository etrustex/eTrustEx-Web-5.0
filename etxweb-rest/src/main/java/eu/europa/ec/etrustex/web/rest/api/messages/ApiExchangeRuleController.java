package eu.europa.ec.etrustex.web.rest.api.messages;

import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.service.api.ApiMessageSummaryService;
import eu.europa.ec.etrustex.web.util.exchange.model.EntitySpec;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

import static eu.europa.ec.etrustex.web.exchange.api.ApiUrlTemplates.SENDER_EXCHANGE_RULE;


@Tag(name = "Sender Exchange Rules", description = "Retrieve Sender exchange rules")
@RestController
@AllArgsConstructor
@Slf4j
public class ApiExchangeRuleController {
    private final ApiMessageSummaryService apiMessageSummaryService;

    @Operation(summary = "Get valid recipients for a sender entity")
    @GetMapping(value = SENDER_EXCHANGE_RULE)
    @ResponseStatus(HttpStatus.OK)
    public List<Group> get(@Valid EntitySpec entitySpec) {
        return apiMessageSummaryService.getValidRecipients(entitySpec.getBusinessIdentifier(), entitySpec.getEntityIdentifier());
    }
}
