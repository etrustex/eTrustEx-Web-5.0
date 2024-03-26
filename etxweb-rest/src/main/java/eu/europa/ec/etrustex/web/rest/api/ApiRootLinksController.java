package eu.europa.ec.etrustex.web.rest.api;

import eu.europa.ec.etrustex.web.util.exchange.model.RootLinks;
import eu.europa.ec.etrustex.web.exchange.util.UrlTemplates;
import eu.europa.ec.etrustex.web.hateoas.api.ApiRootLinksHandler;
import eu.europa.ec.etrustex.web.util.exchange.LinkUtils;
import eu.europa.ec.etrustex.web.util.exchange.model.Rels;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static eu.europa.ec.etrustex.web.exchange.api.ApiUrlTemplates.MESSAGE_API_ROOT;

/**
 * The Class RootLinksController is the entry point to of the REST application.
 *
 * <p>
 * The url paths for the whole application are defined in Class
 * {@link UrlTemplates UrlTemplates}.
 * <p>
 * The <em>rels</em> for the links are defined in Class
 * {@link Rels Rels}.
 * <p>
 * Class {@link LinkUtils LinkUtils} provides helper functions to manage url
 * templates and rels and create links.
 * <p>
 *
 */
@RestController
@RequiredArgsConstructor
public class ApiRootLinksController {

    private final ApiRootLinksHandler apiRootLinksHandler;

    @GetMapping(MESSAGE_API_ROOT)
    public RootLinks get() {
        return apiRootLinksHandler.addLinks(new RootLinks());
    }
}
