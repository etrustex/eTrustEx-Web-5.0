package eu.europa.ec.etrustex.web.rest;


import eu.europa.ec.etrustex.web.exchange.model.AttachmentUploadResponseSpec;
import eu.europa.ec.etrustex.web.hateoas.AttachmentLinksHandler;
import eu.europa.ec.etrustex.web.hateoas.AttachmentUploadResponseLinksHandler;
import eu.europa.ec.etrustex.web.persistence.entity.Attachment;
import eu.europa.ec.etrustex.web.persistence.entity.security.GrantedAuthority;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.service.AttachmentService;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.util.exchange.model.Rels;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static eu.europa.ec.etrustex.web.exchange.util.UrlTemplates.*;

@RestController
@Slf4j
@AllArgsConstructor
public class AttachmentController {
    private final AttachmentService attachmentService;
    private final AttachmentLinksHandler attachmentLinksHandler;
    private final AttachmentUploadResponseLinksHandler attachmentUploadResponseLinksHandler;

    @PostMapping(value = ATTACHMENTS)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #messageId, T(eu.europa.ec.etrustex.web.persistence.entity.Attachment), T(eu.europa.ec.etrustex.web.common.UserAction).CREATE)")
    public List<Attachment> create(@RequestParam Long messageId, @RequestParam Integer numberOfAttachments) {
        log.trace("entering {}", Rels.ATTACHMENT_CREATE);

        List<Attachment> attachments = new ArrayList<>();
        attachmentService.deleteAttachmentsIfEmptyStorePath(messageId);

        IntStream.range(0, numberOfAttachments).forEach(
                nbr -> {
                    Attachment attachment = attachmentService.save(messageId);
                    attachments.add(attachmentLinksHandler.addLinks(attachment));
                }
        );

        return attachments;
    }

    @GetMapping(value = ATTACHMENT)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #attachmentId, T(eu.europa.ec.etrustex.web.persistence.entity.Attachment), T(eu.europa.ec.etrustex.web.common.UserAction).RETRIEVE)")
    public Attachment get(@PathVariable Long attachmentId) {

        Attachment attachment = attachmentService.findById(attachmentId);
        return attachmentLinksHandler.addLinks(attachment);
    }

    @GetMapping(value = ATTACHMENT_FILE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #attachmentId, T(eu.europa.ec.etrustex.web.persistence.entity.Attachment), T(eu.europa.ec.etrustex.web.common.UserAction).RETRIEVE)")
    public ResponseEntity<InputStreamResource> download(
            @PathVariable Long attachmentId,
            HttpServletResponse response) {

        InputStreamResource resource = attachmentService.getResource(attachmentId);

        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader("Content-Disposition", "attachment;filename=" + resource.getFilename());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment;filename=" + resource.getFilename())
                .body(resource);
    }


    @PutMapping(value = ATTACHMENT_FILE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #attachmentId, T(eu.europa.ec.etrustex.web.persistence.entity.Attachment), T(eu.europa.ec.etrustex.web.common.UserAction).UPDATE)")
    public AttachmentUploadResponseSpec upload(
            @RequestBody byte[] fileContent,
            @PathVariable Long attachmentId,
            @RequestParam Long senderEntityId,
            SecurityUserDetails userDetails) {
        Group senderGroup = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getGroup)
                .filter(group -> Objects.equals(senderEntityId, group.getId()))
                .findFirst()
                .orElseThrow(() -> new EtxWebException(String.format("User is not operator for groupId %s", senderEntityId)));

        byte[] md = attachmentService.appendChunkAndChecksum(fileContent, attachmentId, senderGroup);

        return this.attachmentUploadResponseLinksHandler.addLinks(new AttachmentUploadResponseSpec(md, attachmentId));
    }

    @DeleteMapping(value = ATTACHMENT)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #attachmentId, T(eu.europa.ec.etrustex.web.persistence.entity.Attachment), T(eu.europa.ec.etrustex.web.common.UserAction).DELETE)")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> delete(@PathVariable Long attachmentId) {
        attachmentService.delete(attachmentId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(value = ATTACHMENTS)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #messageId, T(eu.europa.ec.etrustex.web.persistence.entity.Message), T(eu.europa.ec.etrustex.web.common.UserAction).DELETE)")
    public ResponseEntity<Void> bulkDelete(SecurityUserDetails principal, @RequestParam Long messageId, @RequestBody List<Long> attachmentIds) {
        attachmentService.delete(attachmentIds);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}


