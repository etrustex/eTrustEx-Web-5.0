package eu.europa.ec.etrustex.web.rest;

import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.service.ExportService;
import eu.europa.ec.etrustex.web.service.security.GroupService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;

import static eu.europa.ec.etrustex.web.exchange.util.UrlTemplates.EXPORT_USERS;

@RestController
@AllArgsConstructor
@Slf4j
public class UserExportController {

    private final ExportService exportService;

    private final GroupService groupService;

    @GetMapping(EXPORT_USERS)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #groupId, T(eu.europa.ec.etrustex.web.exchange.model.UserExportItem), T(eu.europa.ec.etrustex.web.common.UserAction).RETRIEVE)")
    public ResponseEntity<InputStreamResource> exportUsers(@PathVariable Long groupId) {

        Group group = groupService.findById(groupId);
        boolean isBusiness = group.getType().equals(GroupType.BUSINESS);

        ContentDisposition contentDisposition = ContentDisposition.builder("inline")
                .filename(exportService.getExportUsersFilename(group.getType()))
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(contentDisposition);

        InputStream inputStream = isBusiness ? exportService.exportUsersAndFunctionalMailboxes(groupId) : exportService.exportUsersByEntity(groupId);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .headers(headers)
                .body(new InputStreamResource(inputStream));
    }
}
