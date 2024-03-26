/*
 * Copyright (c) 2023. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package eu.europa.ec.etrustex.web.util.crypto;

import org.junit.jupiter.api.Test;

import static eu.europa.ec.etrustex.web.util.test.PrivateConstructorTester.testPrivateConstructor;
import static org.junit.jupiter.api.Assertions.assertNull;

@SuppressWarnings({"java:S112", "java:S100"}) /* Suppress Define and throw a dedicated exception instead of using a generic one & method names false positive. */
class Base64Test {
    @Test
    void constructor_should_be_private() throws NoSuchMethodException {
        testPrivateConstructor(Base64.class.getDeclaredConstructor());
    }

    @Test
    void should_return_null_hashing_of_empty_string() {
        assertNull(Base64.toHash(null));
        assertNull(Base64.toHash(""));
    }
}