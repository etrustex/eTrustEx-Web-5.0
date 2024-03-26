package eu.europa.ec.etrustex.web.rest.system.admin;

import eu.europa.ec.etrustex.web.service.MessageSummaryService;
import eu.europa.ec.etrustex.web.service.validation.model.CertificateSpec;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static eu.europa.ec.etrustex.web.exchange.util.SystemAdminUrlTemplates.COUNT_MESSAGE_SUMMARIES_TO_DISABLE;
import static eu.europa.ec.etrustex.web.exchange.util.SystemAdminUrlTemplates.DISABLE_MESSAGE_SUMMARIES;
import static eu.europa.ec.etrustex.web.util.crypto.Base64.toHash;

@RestController
@AllArgsConstructor
@Slf4j
@PreAuthorize("hasAuthority('SYS_ADMIN')")
public class SysAdminMessageSummaryController {

    private final MessageSummaryService messageSummaryService;

    @PutMapping(value = DISABLE_MESSAGE_SUMMARIES)
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<Boolean> disable(@Valid @RequestBody CertificateSpec compromisedCertificateSpec) {
        messageSummaryService.disableByPublicKeyAndBusinessIdAndEntityId(compromisedCertificateSpec.getPublicKey(), compromisedCertificateSpec.getEntityId());
        return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
    }

    @PutMapping(value = COUNT_MESSAGE_SUMMARIES_TO_DISABLE)
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<Integer> countMessageSummariesToDisable(@Valid @RequestBody CertificateSpec compromisedCertificateSpec) {
        return new ResponseEntity<>(messageSummaryService.countByPublicKeyHashValueAndRecipientId(toHash(compromisedCertificateSpec.getPublicKey()), compromisedCertificateSpec.getEntityId()), HttpStatus.OK);
    }
}
