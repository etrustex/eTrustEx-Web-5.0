/*
 * Copyright (c) 2023. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package eu.europa.ec.etrustex.web.util.exception;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings({"java:S112", "java:S100"}) /* Suppress Define and throw a dedicated exception instead of using a generic one & method names false positive. */
class EtxWebExceptionTest {
    @Test
    void should_create_instance() {
        String message = "message";
        EtxWebException exception1 = new EtxWebException(message);
        EtxWebException exception2 = new EtxWebException(new IOException(message));
        EtxWebException exception3 = new EtxWebException(message, new IOException());

        assertEquals(message, exception1.getMessage());
        assertEquals(IOException.class.getCanonicalName() + ": " + message, exception2.getMessage());
        assertEquals(message, exception3.getMessage());
    }
}