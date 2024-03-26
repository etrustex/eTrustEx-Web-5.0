package eu.europa.ec.etrustex.web.rest.system.admin;

import eu.europa.ec.etrustex.web.persistence.entity.NewServerCertificate;
import eu.europa.ec.etrustex.web.service.NewServerCertificateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static eu.europa.ec.etrustex.web.exchange.util.SystemAdminUrlTemplates.*;

@RestController
@PreAuthorize("hasAuthority('SYS_ADMIN')")
@Slf4j
@RequiredArgsConstructor
public class NewServerCertificateController {
    private final NewServerCertificateService newServerCertificateService;


    @PostMapping(LAUNCH_NEW_CERTIFICATE_JOB)
    public ResponseEntity<Void> launchJob() throws JobExecutionException {
        newServerCertificateService.launchJob();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(NEW_SERVER_CERTIFICATE)
    public ResponseEntity<NewServerCertificate> newServerCertificate() {
        return new ResponseEntity<>(newServerCertificateService.getNewServerCertificate(), HttpStatus.OK);
    }

    @PostMapping(NEW_SERVER_CERTIFICATE_RESET_UPDATED_FLAG)
    public ResponseEntity<Void> resetUpdatedFlag() {
        newServerCertificateService.resetUpdatedFlag();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
