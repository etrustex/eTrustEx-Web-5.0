/*
 * Copyright (c) 2023. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package eu.europa.ec.etrustex.web.persistence.converter;

import org.junit.jupiter.api.Test;

import static eu.europa.ec.etrustex.web.util.test.PrivateConstructorTester.testPrivateConstructor;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SuppressWarnings("java:S100")
class SymmetricKeyExtractorTest {
    String withWrappingSymmetricKey = "{\"symmetricKey\":{\"randomBits\":\"4+FckqfA5fGO+RqwuS+zsdKvKcbf3S8I4UTTJRGRBRc=\",\"encryptionMethod\":\"PLAIN\"}}";
    String withoutWrappingSymmetricKey = "{\"randomBits\":\"4+FckqfA5fGO+RqwuS+zsdKvKcbf3S8I4UTTJRGRBRc=\",\"encryptionMethod\":\"PLAIN\"}";

    @Test
    void testConstructorIsPrivate() throws NoSuchMethodException {
        testPrivateConstructor(SymmetricKeyExtractor.class.getDeclaredConstructor());
    }

    @Test
    void should_remove_the_wrapping_symmetricKey() {
        assertEquals(withoutWrappingSymmetricKey, SymmetricKeyExtractor.extractSymmetricKey(withWrappingSymmetricKey));
    }

    @Test
    void should_return_the_unwrapped_value_unchanged() {
        assertEquals(withoutWrappingSymmetricKey, SymmetricKeyExtractor.extractSymmetricKey(withoutWrappingSymmetricKey));
    }

    @Test
    void should_return_null_if_null_string() {
        assertNull(SymmetricKeyExtractor.extractSymmetricKey(null));
    }

    @Test
    void should_return_same_string_if_string_not_start_with_symmetricKey() {
        String string = "foo";
        assertEquals(string, SymmetricKeyExtractor.extractSymmetricKey(string));
    }

    @Test
    void should_return_same_string_if_pattern_not_matches() {
        String string = "{\"symmetricKey\":";
        assertEquals(string, SymmetricKeyExtractor.extractSymmetricKey(string));
    }
}
