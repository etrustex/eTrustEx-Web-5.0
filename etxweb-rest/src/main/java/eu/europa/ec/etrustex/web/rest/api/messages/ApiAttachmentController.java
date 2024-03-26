package eu.europa.ec.etrustex.web.rest.api.messages;


import eu.europa.ec.etrustex.web.exchange.model.AttachmentUploadResponseSpec;
import eu.europa.ec.etrustex.web.hateoas.api.message.ApiAttachmentLinksHandler;
import eu.europa.ec.etrustex.web.persistence.entity.Attachment;
import eu.europa.ec.etrustex.web.persistence.entity.security.GrantedAuthority;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.service.api.ApiAttachmentService;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.util.exchange.model.Rels;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.hateoas.NonComposite;
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

import static eu.europa.ec.etrustex.web.exchange.api.ApiUrlTemplates.*;

@Tag(name = "Attachment", description = "Attachments API")
@RestController
@Slf4j
@AllArgsConstructor
public class ApiAttachmentController {
    private final ApiAttachmentService apiAttachmentService;
    private final ApiAttachmentLinksHandler apiAttachmentLinksHandler;

    @PostMapping(value = ATTACHMENTS )
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #messageId, T(eu.europa.ec.etrustex.web.persistence.entity.Attachment), T(eu.europa.ec.etrustex.web.common.UserAction).CREATE)")
    public List<Attachment> create(
            @RequestParam Long messageId,
            @RequestParam Integer numberOfAttachments,
            @NonComposite @RequestParam(required = false, defaultValue = "") String[] clientRefs) {
        log.trace("entering {}", Rels.ATTACHMENT_CREATE);

        List<Attachment> attachments = new ArrayList<>();

        IntStream.range(0, numberOfAttachments).forEach(
                nbr -> {
                    String clientRef = clientRefs == null || clientRefs.length == 0 ? null : clientRefs[nbr];
                    Attachment attachment = apiAttachmentService.save(messageId, clientRef);
                    attachments.add(apiAttachmentLinksHandler.addLinks(attachment));
                }
        );

        return attachments;
    }

    @GetMapping(value = ATTACHMENT)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #attachmentId, T(eu.europa.ec.etrustex.web.persistence.entity.Attachment), T(eu.europa.ec.etrustex.web.common.UserAction).RETRIEVE)")
    public Attachment get(@PathVariable Long attachmentId) {
        Attachment attachment = apiAttachmentService.findById(attachmentId);
        return apiAttachmentLinksHandler.addLinks(attachment);
    }

    @GetMapping(value = ATTACHMENT_FILE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal, #attachmentId, T(eu.europa.ec.etrustex.web.persistence.entity.Attachment), T(eu.europa.ec.etrustex.web.common.UserAction).RETRIEVE)")
    public ResponseEntity<InputStreamResource> download(
            @PathVariable Long attachmentId,
            HttpServletResponse response) {

        InputStreamResource resource = apiAttachmentService.getResource(attachmentId);

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

        byte[] md = apiAttachmentService.appendChunkAndChecksum(fileContent, attachmentId, senderGroup);

        return this.apiAttachmentLinksHandler.addLinks(new AttachmentUploadResponseSpec(md, attachmentId));
    }

}


