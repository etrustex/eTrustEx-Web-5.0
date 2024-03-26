package eu.europa.ec.etrustex.web.rest.system.admin;

import eu.europa.ec.etrustex.web.exchange.model.LogLevelItem;
import eu.europa.ec.etrustex.web.service.LogLevelService;
import eu.europa.ec.etrustex.web.service.pagination.RestResponsePage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static eu.europa.ec.etrustex.web.exchange.util.SystemAdminUrlTemplates.*;

@RestController
@PreAuthorize("hasAuthority('SYS_ADMIN')")
@AllArgsConstructor
@Slf4j
@Validated
public class SysAdminLogsController {
    private final LogLevelService logLevelService;

    @GetMapping(value = LOG_LEVEL_SEARCH)
    @ResponseStatus(HttpStatus.OK)
    public RestResponsePage<LogLevelItem> get(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false, defaultValue = "auditingEntity.createdDate") String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") String sortOrder,
            @RequestParam(required = false, defaultValue = "") String filterValue) {

        Sort.Order order = new Sort.Order(Sort.Direction.fromString(sortOrder), sortBy);
        Sort sort = Sort.by(sortBy.equals("auditingEntity.createdDate") ? order : order.ignoreCase());
        PageRequest pageRequest = PageRequest.of(page, size, sort.and(Sort.by(Sort.Order.asc("id"))));

        Page<LogLevelItem> logLevelItemPage = logLevelService.findLoggers(filterValue, pageRequest);
        return new RestResponsePage<>(logLevelItemPage.getContent(), logLevelItemPage.getPageable(), logLevelItemPage.getTotalElements());
    }

    @PutMapping(value = LOG_LEVEL_UPDATE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> update(@RequestParam String logger, @RequestParam String level) {
        try {
            logLevelService.setLoggingLevel(logger, level);
        } catch (Exception e) {
            log.error("Problem updating the logger",e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        logLevelService.signalLoggingSetLevel(logger, level);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(value = LOG_LEVEL_RESET)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> reset() {
        logLevelService.resetLogging();
        logLevelService.signalResetLogging();
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
