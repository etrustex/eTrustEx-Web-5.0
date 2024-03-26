/*
 * Copyright (c) 2023. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package eu.europa.ec.etrustex.web.util.exchange;

import eu.europa.ec.etrustex.web.util.exchange.model.Link;
import eu.europa.ec.etrustex.web.util.exchange.model.Rels;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static eu.europa.ec.etrustex.web.util.test.PrivateConstructorTester.testPrivateConstructor;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"java:S112", "java:S100"}) /* Suppress Define and throw a dedicated exception instead of using a generic one & method names false positive. */
class LinkUtilsTest {
    private static final String APP_PROPERTIES = "application-test.properties";
    private List<Link> links;


    @BeforeEach
    public void setUp() throws IOException {
        String contextPath;
        try (InputStream input = this.getClass().getClassLoader().getResourceAsStream(APP_PROPERTIES)) {
            Properties prop = new Properties();
            prop.load(input);
            contextPath = prop.getProperty("context.path");
        }
        links = new ArrayList<>();
        links.add(Link.of(contextPath + "/link1", Rels.SELF));
        links.add(Link.of(contextPath + "/link2", Rels.MESSAGE_UPDATE));
        links.add(Link.of(contextPath + "/link3", Rels.ATTACHMENT_CREATE));
    }

    @Test
    void constructor_should_be_private() throws NoSuchMethodException {
        testPrivateConstructor(LinkUtils.class.getDeclaredConstructor());
    }

    @Test
    void should_find_link_by_rel() {
        Link link = LinkUtils.findByRel(links, Rels.MESSAGE_UPDATE);
        assertEquals(links.get(1), link);
    }

    @Test
    void should_return_broken_link() {
        Link link = LinkUtils.findByRel(links, Rels.EXPORT_USERS);
        assertEquals(LinkUtils.BROKEN_LINK_HREF, link.getHref());
    }

    @Test
    void should_expand_the_template() {
        String template = "/some/{pathVariable}/{keptUnchanged}/and{?queryParameter1,missing,queryParameter2}";

        String partial = LinkUtils.getUri(
                template,
                Pair.of("pathVariable", "path-value"),
                Pair.of("queryParameter2", "query-value-2"),
                Pair.of("queryParameter1", "queryValue1"),
                Pair.of("emptyParameter", "")
        );

        String complete = LinkUtils.getUri(
                partial,
                Pair.of("keptUnchanged", "path-value2"),
                Pair.of("missing", "query value 3")
        );

        assertEquals("/some/path-value/{keptUnchanged}/and?queryParameter1=queryValue1&queryParameter2=query-value-2{&missing}", partial);
        assertEquals("/some/path-value/path-value2/and?queryParameter1=queryValue1&queryParameter2=query-value-2&missing=query%20value%203", complete);
    }
}
