package eu.europa.ec.etrustex.web.hateoas;

import eu.europa.ec.etrustex.web.util.exchange.model.Rels;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.RecipientPreferences;
import eu.europa.ec.etrustex.web.rest.RecipientPreferencesController;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
@RequiredArgsConstructor
public class RecipientPreferencesLinkHandler {
    private final LinksConverter linksConverter;
    
    public RecipientPreferences addLinks(RecipientPreferences recipientPreferences) {
        recipientPreferences.getLinks().addAll(
                Arrays.asList(
                        linksConverter.convert(linkTo(methodOn(RecipientPreferencesController.class).get(recipientPreferences.getId())).withSelfRel()),
                        linksConverter.convert(linkTo(methodOn(RecipientPreferencesController.class).update(recipientPreferences.getId(), null)).withRel(Rels.RECIPIENT_PREFERENCES_UPDATE.toString()))
                )
        );
        return recipientPreferences;
    }
}
