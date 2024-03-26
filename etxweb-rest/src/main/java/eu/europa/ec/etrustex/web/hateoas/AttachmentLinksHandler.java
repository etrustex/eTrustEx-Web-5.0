package eu.europa.ec.etrustex.web.hateoas;

import eu.europa.ec.etrustex.web.persistence.entity.Attachment;
import eu.europa.ec.etrustex.web.rest.AttachmentController;
import eu.europa.ec.etrustex.web.util.exchange.model.Rels;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
@RequiredArgsConstructor
public class AttachmentLinksHandler {
    private final LinksConverter linksConverter;

    public Attachment addLinks(Attachment attachment) {
        attachment.getLinks().addAll(
                Arrays.asList(
                        linksConverter.convert(linkTo(methodOn(AttachmentController.class).get(attachment.getId())).withSelfRel()),
                        linksConverter.convert(linkTo(methodOn(AttachmentController.class).upload(null, attachment.getId(), null, null)).withRel(Rels.ATTACHMENT_UPLOAD.toString())),
                        linksConverter.convert(linkTo(methodOn(AttachmentController.class).download(attachment.getId(), null)).withRel(Rels.ATTACHMENT_DOWNLOAD.toString())),
                        linksConverter.convert(linkTo(methodOn(AttachmentController.class).delete(attachment.getId())).withRel(Rels.ATTACHMENT_DELETE.toString()))));
        return attachment;
    }
}
