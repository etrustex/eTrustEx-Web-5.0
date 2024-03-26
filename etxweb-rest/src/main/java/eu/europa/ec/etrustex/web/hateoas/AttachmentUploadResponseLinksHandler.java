package eu.europa.ec.etrustex.web.hateoas;

import eu.europa.ec.etrustex.web.exchange.model.AttachmentUploadResponseSpec;
import eu.europa.ec.etrustex.web.rest.AttachmentController;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static eu.europa.ec.etrustex.web.util.exchange.model.Rels.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
@RequiredArgsConstructor
public class AttachmentUploadResponseLinksHandler {
    private final LinksConverter linksConverter;

    public AttachmentUploadResponseSpec addLinks(AttachmentUploadResponseSpec attachmentUploadResponseSpec) {
        attachmentUploadResponseSpec.getLinks().addAll(
                Arrays.asList(
                        linksConverter.convert(linkTo(methodOn(AttachmentController.class).get(attachmentUploadResponseSpec.getAttachmentId())).withRel(ATTACHMENT_GET.toString())),
                        linksConverter.convert(linkTo(methodOn(AttachmentController.class).delete(attachmentUploadResponseSpec.getAttachmentId())).withRel(ATTACHMENT_DELETE.toString())),
                        linksConverter.convert(linkTo(methodOn(AttachmentController.class).download(attachmentUploadResponseSpec.getAttachmentId(), null)).withRel(ATTACHMENT_DOWNLOAD.toString())),
                        linksConverter.convert(linkTo(methodOn(AttachmentController.class).upload(null, attachmentUploadResponseSpec.getAttachmentId(), null, null)).withRel(ATTACHMENT_UPLOAD.toString()))
                )
        );

        return attachmentUploadResponseSpec;
    }
}
