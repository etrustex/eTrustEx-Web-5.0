package eu.europa.ec.etrustex.web.rest.system.admin;

import eu.europa.ec.etrustex.web.common.features.FeatureDecision;
import eu.europa.ec.etrustex.web.common.features.FeaturesEnum;
import eu.europa.ec.etrustex.web.service.util.TestCleanUpService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static eu.europa.ec.etrustex.web.exchange.util.SystemAdminUrlTemplates.CLEAN_UP_TEST;


@Hidden
@RestController
@PreAuthorize("hasAuthority('SYS_ADMIN')")
@Slf4j
@AllArgsConstructor
public class TestCleanUpController {
    private final TestCleanUpService testCleanUpService;

    @FeatureDecision(FeaturesEnum.ETRUSTEX_6659)
    @DeleteMapping(value = CLEAN_UP_TEST)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> cleanUpAfterTests(@PathVariable("parentIdentifier") String parentIdentifier, @PathVariable("groupIdentifier") String groupIdentifier) {
        testCleanUpService.cleanUpAfterTests(parentIdentifier, groupIdentifier);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
