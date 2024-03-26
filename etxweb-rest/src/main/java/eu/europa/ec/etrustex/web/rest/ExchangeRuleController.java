package eu.europa.ec.etrustex.web.rest;

import com.fasterxml.jackson.annotation.JsonView;
import eu.europa.ec.etrustex.web.common.exchange.view.ExchangeRuleViewFilter;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.ExchangeRule;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.ExchangeRuleId;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.service.ExchangeRuleService;
import eu.europa.ec.etrustex.web.service.pagination.RestResponsePage;
import eu.europa.ec.etrustex.web.service.validation.model.ExchangeRuleSpec;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

import static eu.europa.ec.etrustex.web.exchange.util.UrlTemplates.*;

@RestController
@AllArgsConstructor
@Slf4j
public class ExchangeRuleController {

    private final ExchangeRuleService exchangeRuleService;


    @GetMapping(value = RECIPIENTS_FOR_SENDER)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #senderEntityId, T(eu.europa.ec.etrustex.web.persistence.entity.exchange.ExchangeRule), T(eu.europa.ec.etrustex.web.common.UserAction).RETRIEVE)")
    public List<Group> getValidRecipients(@RequestParam Long senderEntityId) {
        return exchangeRuleService.getValidRecipients(senderEntityId);
    }

    @GetMapping(value = EXCHANGE_RULES)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #channelId, T(eu.europa.ec.etrustex.web.persistence.entity.exchange.Channel), T(eu.europa.ec.etrustex.web.common.UserAction).RETRIEVE)")
    @JsonView(ExchangeRuleViewFilter.class)
    public RestResponsePage<ExchangeRule> get(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(required = false, defaultValue = "entity_identifier,entity_name") String filterBy,
            @RequestParam(required = false, defaultValue = "") String filterValue,
            @RequestParam Long channelId
    ) {
        Sort.Direction direction = Sort.Direction.fromString(StringUtils.isNotEmpty(sortOrder) ? sortOrder : Sort.Direction.ASC.toString());
        Sort.Order order = new Sort.Order(direction, StringUtils.isNotEmpty(sortBy) ? sortBy : "member.name").ignoreCase();

        Sort sort = Sort.by(order);

        if (!Objects.equals(filterBy, "entity_identifier,entity_name")) {
            throw new EtxWebException(String.format("Search Exchange Rules by %s not implemented!", filterBy));
        }

        PageRequest pageRequest = PageRequest.of(page, size, sort);

        Page<ExchangeRule> exchangeRulePage = exchangeRuleService.findByChannelId(channelId, filterValue, pageRequest);
        return new RestResponsePage<>(exchangeRulePage.getContent(), exchangeRulePage.getPageable(), exchangeRulePage.getTotalElements());
    }

    @GetMapping(value = EXCHANGE_RULES_SEARCH)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #channelId, T(eu.europa.ec.etrustex.web.persistence.entity.exchange.Channel), T(eu.europa.ec.etrustex.web.common.UserAction).RETRIEVE)")
    @JsonView(ExchangeRuleViewFilter.class)
    public List<ExchangeRule> search(
            @RequestParam(required = false, defaultValue = "") String filterValue,
            @RequestParam Long channelId
    ) {
        return exchangeRuleService.searchByChannelId(channelId, filterValue);
    }

    @PostMapping(value = EXCHANGE_RULES_BULK)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #exchangeRuleSpec, T(eu.europa.ec.etrustex.web.persistence.entity.exchange.ExchangeRule), T(eu.europa.ec.etrustex.web.common.UserAction).CREATE)")
    public List<ExchangeRule> bulkCreate(
            @RequestParam(required = false, defaultValue = "false") Boolean forced,
            @RequestBody List<@Valid ExchangeRuleSpec> exchangeRuleSpec) {
        return this.exchangeRuleService.create(exchangeRuleSpec, (forced != null && forced));
    }

    @PutMapping(value = EXCHANGE_RULES)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #exchangeRuleSpec, T(eu.europa.ec.etrustex.web.persistence.entity.exchange.ExchangeRule), T(eu.europa.ec.etrustex.web.common.UserAction).UPDATE)")
    public ExchangeRule update(@Valid @RequestBody ExchangeRuleSpec exchangeRuleSpec) {
        return this.exchangeRuleService.update(exchangeRuleSpec);
    }

    @DeleteMapping(value = EXCHANGE_RULES)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #exchangeRuleId, T(eu.europa.ec.etrustex.web.persistence.entity.exchange.ExchangeRule), T(eu.europa.ec.etrustex.web.common.UserAction).DELETE)")
    public ResponseEntity<Void> delete(@RequestBody ExchangeRuleId exchangeRuleId) {
        exchangeRuleService.delete(exchangeRuleId.getMember(), exchangeRuleId.getChannel());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(value = EXCHANGE_RULES_BULK_FILE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #businessId, T(eu.europa.ec.etrustex.web.persistence.entity.exchange.ExchangeRule), T(eu.europa.ec.etrustex.web.common.UserAction).CREATE)")
    public List<String> uploadBulk(@RequestBody byte[] fileContent, @RequestParam Long businessId) {
        log.info("Processing group bulk CSV file");
        return exchangeRuleService.bulkAddExchangeRule(fileContent, businessId);
    }

    @PutMapping(value = EXCHANGE_RULES_PARTICIPANTS_BULK_FILE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #businessId, T(eu.europa.ec.etrustex.web.persistence.entity.exchange.ExchangeRule), T(eu.europa.ec.etrustex.web.common.UserAction).CREATE)")
    public List<String> uploadParticipantsBulk(@RequestBody byte[] fileContent, @RequestParam Long businessId, @RequestParam Long channelId) {
        log.info("Processing upload participants bulk CSV file");
        return exchangeRuleService.bulkAddParticipants(fileContent, businessId, channelId);
    }

}
