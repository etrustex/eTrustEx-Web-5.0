/*
 * Copyright (c) 2023. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package eu.europa.ec.etrustex.web.util.exchange;

import eu.europa.ec.etrustex.web.util.exchange.model.Rels;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"java:S112", "java:S100"}) /* Suppress Define and throw a dedicated exception instead of using a generic one & method names false positive. */
class RelsTest {

    @Test
    void should_return_the_string_value() {
        assertEquals("/attachment/create", Rels.ATTACHMENT_CREATE.toString());
    }

    @Test
    void should_convert_to_a_rel() {
        assertEquals(Rels.ATTACHMENT_CREATE, Rels.getValue("/attachment/create"));
    }

    @Test
    void should_return_null_if_rel_not_exists() {
        assertNull(Rels.getValue("no_rel"));
    }
}
