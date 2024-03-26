/*
 * Copyright (c) 2023. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package eu.europa.ec.etrustex.web.util.exchange;

import com.damnhandy.uri.template.UriTemplate;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.util.exchange.model.Link;
import eu.europa.ec.etrustex.web.util.exchange.model.Rels;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;


/**
 * Utility class for handling the urls, replacing parameters with actual values, and dealing with rels.
 */
public class LinkUtils {
    public static final String BROKEN_LINK_HREF = "Broken link";

    private LinkUtils() {
        throw new EtxWebException("Utility class");
    }

    /**
     * Finds a Link within a list of links filtering by rel.
     *
     * @param links the list of Link to filter
     * @param rel the Rel to apply the filter
     * @return the found Link or a Link with href value of {@value #BROKEN_LINK_HREF}
     */
    public static Link findByRel(Collection<Link> links, Rels rel) {
        return links.stream().filter(link -> link.getRel().equals(rel))
                .findAny()
                .orElse(Link.of(BROKEN_LINK_HREF));
    }

    /**
     * @param uriTemplateStr the template (with path and query parameters templates) for the link url
     * @param keyValuePairs Url Template Path variables as Key-Value pairs
     * @return The URI string obtained by replacing the parameters in the template with optional values
     */
    @SafeVarargs
    public static String getUri(String uriTemplateStr, Pair<String, String>... keyValuePairs) {
        UriTemplate uriTemplate =  UriTemplate.fromTemplate(uriTemplateStr);

        for(Pair<String, String> keyValuePair: keyValuePairs) {
            if(StringUtils.isNotEmpty(keyValuePair.getValue())) {
                uriTemplate.set(keyValuePair.getKey(), keyValuePair.getValue());
            }
        }

        return uriTemplate.expandPartial();
    }
}
