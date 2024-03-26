package eu.europa.ec.etrustex.web.rest;

import eu.europa.ec.etrustex.web.common.EtrustexWebProperties;
import eu.europa.ec.etrustex.web.exchange.api.ApiUrlTemplates;
import eu.europa.ec.etrustex.web.util.exchange.model.keys.RsaPublicKeyDto;
import eu.europa.ec.etrustex.web.service.EncryptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static eu.europa.ec.etrustex.web.exchange.util.UrlTemplates.ENVIRONMENT;
import static eu.europa.ec.etrustex.web.exchange.util.UrlTemplates.PUBLIC_KEY;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SettingsController {
    private final EtrustexWebProperties etrustexWebProperties;
    private final EncryptionService encryptionService;

    @GetMapping(ENVIRONMENT)
    public ResponseEntity<String> getEnvironment() {
        return new ResponseEntity<>(etrustexWebProperties.getEnvironment(), HttpStatus.OK);
    }

    @GetMapping({ PUBLIC_KEY, ApiUrlTemplates.PUBLIC_KEY})
    @PreAuthorize("authenticated")
    public ResponseEntity<RsaPublicKeyDto> getPublicKey() {
        return new ResponseEntity<>(encryptionService.getServerPublicKey(), HttpStatus.OK);
    }
}
