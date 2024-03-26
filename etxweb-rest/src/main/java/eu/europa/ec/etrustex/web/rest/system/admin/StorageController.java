package eu.europa.ec.etrustex.web.rest.system.admin;

import eu.europa.ec.etrustex.web.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static eu.europa.ec.etrustex.web.exchange.util.SystemAdminUrlTemplates.GROUP_ORPHAN_FILES;
import static eu.europa.ec.etrustex.web.exchange.util.SystemAdminUrlTemplates.ORPHAN_FILES;

@RestController
@PreAuthorize("hasAuthority('SYS_ADMIN')")
@Slf4j
@RequiredArgsConstructor
public class StorageController {

    private final StorageService storageService;

    @DeleteMapping(ORPHAN_FILES)
    public ResponseEntity<Void> deleteOrphanFiles() throws IOException {
        storageService.deleteOrphanFiles();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(GROUP_ORPHAN_FILES)
    public ResponseEntity<Void> deleteOrphanFiles(@RequestParam Long groupId) {
        storageService.deleteOrphanFilesForGroup(groupId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
