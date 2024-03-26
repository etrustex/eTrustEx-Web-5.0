package eu.europa.ec.etrustex.web.hateoas.api.message;

import eu.europa.ec.etrustex.web.exchange.model.AttachmentUploadResponseSpec;
import eu.europa.ec.etrustex.web.hateoas.LinksConverter;
import eu.europa.ec.etrustex.web.persistence.entity.Attachment;
import eu.europa.ec.etrustex.web.rest.api.messages.ApiAttachmentController;
import eu.europa.ec.etrustex.web.util.exchange.model.Link;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static eu.europa.ec.etrustex.web.util.exchange.model.Rels.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
@RequiredArgsConstructor
public class ApiAttachmentLinksHandler {
    private final LinksConverter linksConverter;

    public Attachment addLinks(Attachment attachment) {
        attachment.getLinks().addAll(links(attachment.getId()));

        return attachment;
    }

    public AttachmentUploadResponseSpec addLinks(AttachmentUploadResponseSpec attachmentUploadResponseSpec) {
        attachmentUploadResponseSpec.getLinks().addAll(links(attachmentUploadResponseSpec.getAttachmentId()));

        return attachmentUploadResponseSpec;
    }

    private List<? extends Link> links(Long attachmentId) {
        return Arrays.asList(
                linksConverter.convert(linkTo(methodOn(ApiAttachmentController.class).get(attachmentId)).withRel(ATTACHMENT_GET.toString())),
                linksConverter.convert(linkTo(methodOn(ApiAttachmentController.class).download(attachmentId, null)).withRel(ATTACHMENT_DOWNLOAD.toString())),
                linksConverter.convert(linkTo(methodOn(ApiAttachmentController.class).upload(null, attachmentId, null, null)).withRel(ATTACHMENT_UPLOAD.toString()))
        );
    }
}
