package eu.europa.ec.etrustex.web.hateoas;

import eu.europa.ec.etrustex.web.persistence.entity.MessageSummary;
import eu.europa.ec.etrustex.web.rest.AttachmentController;
import eu.europa.ec.etrustex.web.rest.MessageSummaryController;
import eu.europa.ec.etrustex.web.util.exchange.model.Rels;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
@RequiredArgsConstructor
public class MessageSummaryLinksHandler {
    private final LinksConverter linksConverter;

    public MessageSummary addLinks(MessageSummary messageSummary) {

        messageSummary.getLinks().addAll(
                Arrays.asList(
                        linksConverter.convert(linkTo(methodOn(MessageSummaryController.class).get(messageSummary.getMessage().getId(), null,  null, null)).withSelfRel()),
                        linksConverter.convert(linkTo(methodOn(AttachmentController.class).download(null,  null)).withRel(Rels.ATTACHMENT_DOWNLOAD.toString()))
                )
        );
        return messageSummary;
    }
}
