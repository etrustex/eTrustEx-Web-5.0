package eu.europa.ec.etrustex.web.hateoas;

import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.rest.AttachmentController;
import eu.europa.ec.etrustex.web.rest.MessageController;
import eu.europa.ec.etrustex.web.rest.PDFController;
import eu.europa.ec.etrustex.web.util.exchange.model.Rels;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Component
public class MessageLinksHandler extends SentMessageLinksHandler {

    public MessageLinksHandler(LinksConverter linksConverter) {
        super(linksConverter);
    }

    @Override
    public Message addLinks(Message message) {
        super.addLinks(message);
        message.getLinks().addAll(
                Arrays.asList(
                        linksConverter.convert(linkTo(methodOn(MessageController.class).update(null, message.getId(), null)).withRel(Rels.MESSAGE_UPDATE.toString())),
                        linksConverter.convert(linkTo(methodOn(MessageController.class).draft(null, message.getId(), null)).withRel(Rels.MESSAGE_DRAFT.toString())),
                        linksConverter.convert(linkTo(methodOn(MessageController.class).delete(null, message.getId())).withRel(Rels.MESSAGE_DELETE.toString())),
                        linksConverter.convert(linkTo(methodOn(AttachmentController.class).create(message.getId(), null)).withRel(Rels.ATTACHMENT_CREATE.toString())),
                        linksConverter.convert(linkTo(methodOn(AttachmentController.class).download(null,  null)).withRel(Rels.ATTACHMENT_DOWNLOAD.toString())),
                        linksConverter.convert(linkTo(methodOn(PDFController.class).getReceipt(message.getId(), null)).withRel(Rels.MESSAGE_RECEIPT_GET.toString()))
                )
        );
        return message;
    }
}
