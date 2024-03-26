/*
 * Copyright (c) 2018-2023. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package eu.europa.ec.etrustex.web.util.exchange.model;

import com.fasterxml.jackson.annotation.JsonView;
import eu.europa.ec.etrustex.web.util.exchange.filter.ListViewFilter;
import eu.europa.ec.etrustex.web.util.exchange.filter.MessageSummaryViewFilter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@Setter
@Getter
@JsonView({ListViewFilter.class, MessageSummaryViewFilter.class})
@SuppressWarnings({"java:S1068"})
public class Link implements Serializable {

    private Rels rel;
    private String href;


    /**
     * private constructor
     * @see Link#of(String)
     * @see Link#of(String, Rels) 
     */
    private Link(String href, Rels rel) {
        this.href = href;
        this.rel = rel;
    }


    /**
     * Creates a new link to the given URI Template @see<a "href=https://tools.ietf.org/html/rfc6570"> with the self relation.
     *
     * @see Rels#SELF
     * @param href must be an URI or and URI Template, must not be {@literal null} or empty
     */
    public static Link of(String href) {
        return new Link(href, Rels.SELF);
    }

    /**
     * Creates a new link to the given URI Template @see<a "href=https://tools.ietf.org/html/rfc6570"> with the given rel.
     * @see Rels
     *
     * @param href must be an URI or and URI Template, must not be {@literal null} or empty
     * @param rel the rel of the link
     */
    public static Link of(String href, Rels rel) {
        return new Link(href, rel);
    }
}
