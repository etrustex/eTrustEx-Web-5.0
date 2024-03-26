package eu.europa.ec.etrustex.web.rest;

import eu.europa.ec.etrustex.web.util.exchange.model.RootLinks;
import eu.europa.ec.etrustex.web.exchange.util.UrlTemplates;
import eu.europa.ec.etrustex.web.hateoas.RootLinksHandler;
import eu.europa.ec.etrustex.web.util.exchange.LinkUtils;
import eu.europa.ec.etrustex.web.util.exchange.model.Rels;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The Class RootLinksController is the entry point to of the REST application.
 *
 * <p>
 * The url paths for the whole application are defined in Class
 * {@link eu.europa.ec.etrustex.web.exchange.util.UrlTemplates UrlTemplates}.
 * <p>
 * The <em>rels</em> for the links are defined in Class
 * {@link Rels Rels}.
 * <p>
 * Class {@link LinkUtils LinkUtils} provides helper functions to manage url
 * templates and rels and create links.
 * <p>
 * There is an annotation processor and annotations that automate the process of creating links.
 * For an usage example see the annotations in Class
 * {@link eu.europa.ec.etrustex.web.persistence.entity.Attachment Attachment} and the controller
 * {@link eu.europa.ec.etrustex.web.rest.AttachmentController AttachmentController}.
 *
 *
 * @see <a href="https://en.wikipedia.org/wiki/HATEOAS">
 *      wikipedia page on HATEOAS</a>
 * @author fuscoem
 */
@RestController
@RequiredArgsConstructor
public class RootLinksController {

    private final RootLinksHandler rootLinksHandler;

    @GetMapping(UrlTemplates.API_ROOT)
    public RootLinks get() {
        return rootLinksHandler.addLinks(new RootLinks());
    }
}
