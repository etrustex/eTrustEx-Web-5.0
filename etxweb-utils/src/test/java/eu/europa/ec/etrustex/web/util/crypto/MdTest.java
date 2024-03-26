/*
 * Copyright (c) 2023. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package eu.europa.ec.etrustex.web.util.crypto;

import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.Test;

import java.security.MessageDigest;

import static eu.europa.ec.etrustex.web.util.crypto.Md.SHA_512;
import static eu.europa.ec.etrustex.web.util.test.PrivateConstructorTester.testPrivateConstructor;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings({"java:S112", "java:S100"}) /* Suppress Define and throw a dedicated exception instead of using a generic one & method names false positive. */
class MdTest {
    @Test
    void constructor_should_be_private() throws NoSuchMethodException {
        testPrivateConstructor(Md.class.getDeclaredConstructor());
    }

    @Test
    void should_get_sha512MdInstance() {
        MessageDigest md = Md.getSha512MdInstance();
        assertEquals(SHA_512, md.getAlgorithm());
        assertEquals(BouncyCastleProvider.PROVIDER_NAME, md.getProvider().getName());
        assertEquals(64, md.getDigestLength());
    }

    @Test
    void should_verify() {
        byte[] bytes = "foo".getBytes();
        String md = Md.digest(bytes);

        assertDoesNotThrow(() -> Md.verify(bytes, md));
    }

    @Test
    void should_not_verify() {
        String md = Md.digest("foo".getBytes());

        Exception exception = assertThrows(EtxWebException.class, () -> Md.verify("bar".getBytes(), md));

        assertTrue(exception.getMessage().contains("Checksum mismatch"));
    }
}