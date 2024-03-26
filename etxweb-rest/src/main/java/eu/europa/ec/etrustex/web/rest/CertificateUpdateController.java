package eu.europa.ec.etrustex.web.rest;

import eu.europa.ec.etrustex.web.exchange.model.CertificateUpdateDto;
import eu.europa.ec.etrustex.web.exchange.util.UrlTemplates;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.service.certificate.CertificateService;
import eu.europa.ec.etrustex.web.service.validation.model.UpdateCertificateSpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static eu.europa.ec.etrustex.web.exchange.util.SystemAdminUrlTemplates.CERTIFICATE_UPDATE;

@RestController
@Slf4j
@RequiredArgsConstructor
public class CertificateUpdateController {

    private final CertificateService certificateService;

    @PostMapping(value = CERTIFICATE_UPDATE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('SYS_ADMIN')")
    public ResponseEntity<String> generateLinkToUpdateCertificate(@Valid @RequestBody UpdateCertificateSpec updateCertificateSpec) {
        String generatedLink = certificateService.generateCertificateUpdateLink(updateCertificateSpec);
        return new ResponseEntity<>(generatedLink, HttpStatus.OK);
    }

    @PutMapping(value = UrlTemplates.CERTIFICATE_UPDATE_COMPROMISED_MESSAGES)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #certificateUpdateDto.recipientEntityId, T(eu.europa.ec.etrustex.web.persistence.entity.MessageSummary), T(eu.europa.ec.etrustex.web.common.UserAction).UPDATE)")
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<Boolean> updateCompromisedMessages(SecurityUserDetails principal, @RequestBody CertificateUpdateDto certificateUpdateDto) {
        certificateService.updateCompromisedMessages(certificateUpdateDto, principal.getUser().getId());
        return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);

    }

    @GetMapping(value = UrlTemplates.CERTIFICATE_UPDATE_IS_VALID_REDIRECT_LINK)
    @PreAuthorize("authenticated")
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<Boolean> isValidRedirectLink(
            @RequestParam Long groupId,
            @RequestParam String groupIdentifier,
            @RequestParam Long userId
    ) {


        return new ResponseEntity<>(certificateService.isValidRedirectLink(groupId, groupIdentifier, userId), HttpStatus.OK);

    }

}
