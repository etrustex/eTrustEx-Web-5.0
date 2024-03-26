/*
 * Copyright (c) 2023. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package eu.europa.ec.etrustex.web.hateoas.api;

import eu.europa.ec.etrustex.web.hateoas.LinksConverter;
import eu.europa.ec.etrustex.web.util.exchange.model.RootLinks;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"java:S100"}) /* Suppress method names false positive. */
class ApiRootLinksHandlerTest {
    @Mock
    private LinksConverter linksConverter;

    @Test
    void should_add_the_root_links() {
        RootLinks rootLinks = new RootLinks();
        ApiRootLinksHandler rootLinksHandler = new ApiRootLinksHandler(linksConverter);

        rootLinksHandler.addLinks(rootLinks);

        assertThat(rootLinks.getLinks()).isNotEmpty();
    }
}