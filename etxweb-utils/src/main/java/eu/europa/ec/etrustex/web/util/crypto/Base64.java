/*
 * Copyright (c) 2018-2023. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package eu.europa.ec.etrustex.web.util.crypto;

import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import org.bouncycastle.util.encoders.Hex;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Base64 {
    public static final String SHA_256 = "SHA-256";
    private static final java.util.Base64.Decoder DECODER = java.util.Base64.getDecoder();
    private static final java.util.Base64.Encoder ENCODER = java.util.Base64.getEncoder();

    private Base64() {
        throw new IllegalStateException("Utility class");
    }

    public static byte[] decode(String b64String) {
        return DECODER.decode(b64String);
    }
    public static byte[] decode(byte[] b64Bytes) {
        return DECODER.decode(b64Bytes);
    }

    public static String decodeToString(byte[] b64Bytes) {
        return new String(decode(b64Bytes));
    }

    public static byte[] encode(byte[] binaryContent) {
        return ENCODER.encode(binaryContent);
    }

    public static byte[] encode(String text) {
        return ENCODER.encode(text.getBytes());
    }

    public static String encodeToString(byte[] binaryContent) {
        return new String(encode(binaryContent));
    }

    public static String toHash(String originalString) {
        if (originalString == null || originalString.isEmpty()) {
            return null;
        }
        final MessageDigest digest;
        try {
            digest = MessageDigest.getInstance(SHA_256);
        } catch (NoSuchAlgorithmException e) {
            throw new EtxWebException("Algorithm not found: " + SHA_256, e);
        }
        final byte[] hashBytes = digest.digest(originalString.getBytes(StandardCharsets.UTF_8));
        return Hex.toHexString(hashBytes);
    }
}
