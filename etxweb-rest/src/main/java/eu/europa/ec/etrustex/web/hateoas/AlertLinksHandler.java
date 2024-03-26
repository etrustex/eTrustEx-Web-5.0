package eu.europa.ec.etrustex.web.hateoas;

import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.persistence.entity.Alert;
import eu.europa.ec.etrustex.web.rest.AlertController;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static eu.europa.ec.etrustex.web.util.exchange.model.Rels.ALERT_UPDATE;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
@RequiredArgsConstructor
public class AlertLinksHandler {
    private final LinksConverter linksConverter;

    public Alert addLinks (Alert alert) {
        try {
            alert.getLinks().add(
                    linksConverter.convert(linkTo(methodOn(AlertController.class).update(null)).withRel(ALERT_UPDATE.toString()))
            );
        } catch (Exception e) {
            throw new EtxWebException(e);
        }

        return alert;
    }
}
