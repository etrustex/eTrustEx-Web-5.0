package eu.europa.ec.etrustex.web.hateoas.api.message;

import eu.europa.ec.etrustex.web.hateoas.LinksConverter;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummary;
import eu.europa.ec.etrustex.web.rest.api.messages.ApiAttachmentController;
import eu.europa.ec.etrustex.web.rest.api.messages.ApiMessageSummaryController;
import eu.europa.ec.etrustex.web.util.exchange.model.Rels;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
@RequiredArgsConstructor
public class ApiMessageSummaryLinksHandler {
    private final LinksConverter linksConverter;

    public MessageSummary addLinks(MessageSummary messageSummary) {

        messageSummary.getLinks().addAll(
                Arrays.asList(
                        linksConverter.convert(linkTo(methodOn(ApiMessageSummaryController.class).get( null,  null, null)).withSelfRel()),
                        linksConverter.convert(linkTo(methodOn(ApiAttachmentController.class).download( null, null)).withRel(Rels.ATTACHMENT_DOWNLOAD.toString())),
                        linksConverter.convert(linkTo(methodOn(ApiMessageSummaryController.class).ack(null, messageSummary.getMessage().getId(), null, null)).withRel(Rels.MESSAGE_SUMMARY_ACK.toString()))
                )
        );

        return messageSummary;
    }
}
