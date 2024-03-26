package eu.europa.ec.etrustex.web.hateoas.api.message;

import eu.europa.ec.etrustex.web.hateoas.LinksConverter;
import eu.europa.ec.etrustex.web.hateoas.SentMessageLinksHandler;
import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.rest.api.messages.ApiAttachmentController;
import eu.europa.ec.etrustex.web.rest.api.messages.ApiMessageController;
import eu.europa.ec.etrustex.web.util.exchange.model.Rels;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Component
public class ApiMessageLinksHandler extends SentMessageLinksHandler {

    public ApiMessageLinksHandler(LinksConverter linksConverter) {
        super(linksConverter);
    }

    @Override
    public Message addLinks(Message message) {
        super.addLinks(message);
        message.getLinks().addAll(
                Arrays.asList(
                        linksConverter.convert(linkTo(methodOn(ApiMessageController.class).update(null, message.getId(), null)).withRel(Rels.MESSAGE_UPDATE.toString())),
                        linksConverter.convert(linkTo(methodOn(ApiAttachmentController.class).create(message.getId(), null, new String[]{})).withRel(Rels.ATTACHMENT_CREATE.toString())),
                        linksConverter.convert(linkTo(methodOn(ApiAttachmentController.class).download(null, null)).withRel(Rels.ATTACHMENT_DOWNLOAD.toString()))
                )
        );
        return message;
    }
}
