package eu.europa.ec.etrustex.web.hateoas;


import eu.europa.ec.etrustex.web.util.exchange.model.Link;
import org.springframework.stereotype.Component;

@Component
public interface LinksConverter {
    Link convert(org.springframework.hateoas.Link link);
    org.springframework.hateoas.Link convert(Link link);
}
