package eu.europa.ec.etrustex.web.hateoas;

import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.rest.MessageController;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
@RequiredArgsConstructor
public class SentMessageLinksHandler {
    protected final LinksConverter linksConverter;

    public Message addLinks(Message message) {
        message.getLinks().add(
                linksConverter.convert(linkTo(methodOn(MessageController.class).get(message.getId(), null, null, null)).withSelfRel())
        );
        return message;
    }
}
