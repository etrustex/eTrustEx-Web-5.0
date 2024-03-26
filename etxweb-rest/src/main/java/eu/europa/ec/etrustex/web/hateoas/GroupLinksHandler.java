package eu.europa.ec.etrustex.web.hateoas;

import eu.europa.ec.etrustex.web.util.exchange.model.Rels;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.rest.GroupController;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
@RequiredArgsConstructor
public class GroupLinksHandler {
    private final LinksConverter linksConverter;
    private final RecipientPreferencesLinkHandler recipientPreferencesLinkHandler;

    public Group addLinks(Group group) {

        group.getLinks().addAll(
                Arrays.asList(
                        linksConverter.convert(linkTo(methodOn(GroupController.class).get(group.getId())).withSelfRel()),
                        linksConverter.convert(linkTo(methodOn(GroupController.class).update(group.getId(), null)).withRel(Rels.GROUP_UPDATE.toString()))
                )
        );

        if (group.getRecipientPreferences() != null) {
            recipientPreferencesLinkHandler.addLinks(group.getRecipientPreferences());
        }

        return group;
    }
}
