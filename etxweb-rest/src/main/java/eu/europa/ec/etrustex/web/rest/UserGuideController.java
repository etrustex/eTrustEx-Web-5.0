package eu.europa.ec.etrustex.web.rest;

import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import eu.europa.ec.etrustex.web.exchange.model.UserGuideSpec;
import eu.europa.ec.etrustex.web.persistence.entity.UserGuide;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.service.UserGuideService;
import eu.europa.ec.etrustex.web.service.security.GroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.List;

import static eu.europa.ec.etrustex.web.exchange.util.UrlTemplates.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserGuideController {
    private final UserGuideService userGuideService;

    private final GroupService groupService;

    @GetMapping(value = USER_GUIDE)
    @PreAuthorize("authenticated")
    public ResponseEntity<InputStreamResource> get(@RequestParam Long businessId,
                                                   @RequestParam RoleName role,
                                                   @RequestParam GroupType groupType) {

        UserGuide userGuide = userGuideService.findByBusinessAndRoleNameAndGroupType(businessId, role, groupType);

        log.info("Downloading user guide: {}", userGuide.getFilename());

        ContentDisposition contentDisposition = ContentDisposition.builder("inline")
                .filename(userGuide.getFilename())
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(contentDisposition);

        log.info("ContentDisposition header filename: {}", headers.getContentDisposition().getFilename());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .headers(headers)
                .body(new InputStreamResource(new ByteArrayInputStream(userGuide.getBinary())));
    }

    @GetMapping(value = USER_GUIDES)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #businessId, T(eu.europa.ec.etrustex.web.persistence.entity.UserGuide), T(eu.europa.ec.etrustex.web.common.UserAction).RETRIEVE)")
    public List<UserGuideSpec> getByBusinessId(@PathVariable Long businessId) {
        return userGuideService.findAllByBusinessId(businessId);
    }

    @PutMapping(value = USER_GUIDE_FILE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #businessId, T(eu.europa.ec.etrustex.web.persistence.entity.UserGuide), T(eu.europa.ec.etrustex.web.common.UserAction).UPDATE)")
    public ResponseEntity<Void> uploadUserGuide(@RequestBody byte[] fileContent,
                                                @RequestParam String fileName,
                                                @RequestParam Long businessId,
                                                @RequestParam RoleName role,
                                                @RequestParam GroupType groupType) {
        Group business = groupService.findById(businessId);

        userGuideService.saveOrUpdate(fileContent, fileName, business, role, groupType);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(value = USER_GUIDE_DELETE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #businessId, T(eu.europa.ec.etrustex.web.persistence.entity.UserGuide), T(eu.europa.ec.etrustex.web.common.UserAction).DELETE)")
    public ResponseEntity<Void> delete(@RequestParam Long businessId,
                                       @RequestParam RoleName role,
                                       @RequestParam GroupType groupType) {

        UserGuide userGuide = userGuideService.findByBusinessAndRoleNameAndGroupType(businessId, role, groupType);

        userGuideService.delete(userGuide);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
