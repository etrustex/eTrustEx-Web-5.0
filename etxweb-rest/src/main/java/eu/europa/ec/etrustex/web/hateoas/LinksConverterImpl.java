package eu.europa.ec.etrustex.web.hateoas;

import eu.europa.ec.etrustex.web.util.exchange.model.Link;
import eu.europa.ec.etrustex.web.util.exchange.model.Rels;
import eu.europa.ec.etrustex.web.common.EtrustexWebProperties;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LinksConverterImpl implements LinksConverter {
    private final EtrustexWebProperties etrustexWebProperties;

    public Link convert(org.springframework.hateoas.Link link) {
        String href = link.getHref();

        if (StringUtils.isNotBlank(etrustexWebProperties.getTargetServerContextRoot())
                && !etrustexWebProperties.getTargetServerContextRoot().equals(etrustexWebProperties.getContextPath())) {
            href = href.replaceFirst(etrustexWebProperties.getTargetServerContextRoot(), etrustexWebProperties.getContextPath());
        }

        return Link.of(href, Rels.getValue(link.getRel().value()));
    }

    public org.springframework.hateoas.Link convert(Link link) {
        return org.springframework.hateoas.Link.of(link.getHref(), link.getRel().toString());
    }
}
