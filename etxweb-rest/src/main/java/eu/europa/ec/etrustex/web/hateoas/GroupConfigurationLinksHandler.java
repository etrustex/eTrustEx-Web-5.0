package eu.europa.ec.etrustex.web.hateoas;

import eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.GroupConfiguration;
import eu.europa.ec.etrustex.web.rest.GroupConfigurationController;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.util.exchange.model.Link;
import eu.europa.ec.etrustex.web.util.exchange.model.Rels;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
@RequiredArgsConstructor
public class GroupConfigurationLinksHandler {
    private final LinksConverter linksConverter;

    public <T extends GroupConfiguration<?>> T addLinks(T groupConfiguration) {
        groupConfiguration.getLinks().addAll(Arrays.asList(linksConverter.convert(linkTo(methodOn(GroupConfigurationController.class).get(groupConfiguration.getGroup().getId(), groupConfiguration.getDtype())).withSelfRel()), getUpdateLink(groupConfiguration)));

        return groupConfiguration;
    }

    private Link getUpdateLink(GroupConfiguration<?> groupConfiguration) {
        return linksConverter.convert(linkTo(
                getControllerUpdateMethod(groupConfiguration),
                null, groupConfiguration.getGroup().getId(), null).withRel(Rels.GROUP_CONFIGURATION_UPDATE.toString())
        );
    }

    private Method getControllerUpdateMethod(GroupConfiguration<?> groupConfiguration) {
        try {
            return GroupConfigurationController.class.getMethod("update" + groupConfiguration.getClass().getSimpleName(), SecurityUserDetails.class, Long.class, groupConfiguration.specClass());
        } catch (NoSuchMethodException e) {
            throw new EtxWebException("Group configuration update method not found.", e);
        }
    }
}
