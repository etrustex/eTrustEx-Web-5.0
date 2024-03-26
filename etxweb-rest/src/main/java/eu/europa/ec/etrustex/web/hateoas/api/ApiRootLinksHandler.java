package eu.europa.ec.etrustex.web.hateoas.api;

import eu.europa.ec.etrustex.web.hateoas.LinksConverter;
import eu.europa.ec.etrustex.web.rest.SettingsController;
import eu.europa.ec.etrustex.web.rest.api.messages.ApiExchangeRuleController;
import eu.europa.ec.etrustex.web.rest.api.messages.ApiMessageController;
import eu.europa.ec.etrustex.web.rest.api.messages.ApiMessageSummaryController;
import eu.europa.ec.etrustex.web.util.exchange.model.Rels;
import eu.europa.ec.etrustex.web.util.exchange.model.RootLinks;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static eu.europa.ec.etrustex.web.util.exchange.model.Rels.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
@RequiredArgsConstructor
public class ApiRootLinksHandler {
    private final LinksConverter linksConverter;

    @SuppressWarnings("ConstantConditions")
    public RootLinks addLinks(RootLinks rootLinks) {
        rootLinks.getLinks()
                .addAll(
                        Arrays.asList(
                                linksConverter.convert(linkTo(methodOn(SettingsController.class).getPublicKey()).withRel(SETTINGS_PUBLIC_KEY_GET.toString())),
                                linksConverter.convert(linkTo(methodOn(ApiMessageController.class).create(null, null)).withRel(MESSAGE_CREATE.toString())),
                                linksConverter.convert(linkTo(methodOn(ApiMessageSummaryController.class).status(null)).withRel(Rels.MESSAGE_STATUS.toString())),
                                linksConverter.convert(linkTo(methodOn(ApiMessageSummaryController.class).get(null, null, null)).withRel(MESSAGE_SUMMARIES_GET.toString())),
                                linksConverter.convert(linkTo(methodOn(ApiMessageSummaryController.class).get(null, null, null, null)).withRel(MESSAGE_SUMMARY_GET.toString())),
                                linksConverter.convert(linkTo(methodOn(ApiExchangeRuleController.class).get(null)).withRel(SENDER_EXCHANGE_RULE_GET.toString())),
                                linksConverter.convert(linkTo(methodOn(ApiMessageSummaryController.class).ack(null, null, null, null)).withRel(Rels.MESSAGE_SUMMARY_ACK.toString()))
                        )
                );
        return rootLinks;
    }
}
