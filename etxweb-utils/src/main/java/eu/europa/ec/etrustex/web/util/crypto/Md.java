/*
 * Copyright (c) 2018-2023. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package eu.europa.ec.etrustex.web.util.crypto;

import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;

public final class Md {
    public static final String SHA_512 = "SHA-512";



    private Md() {
        throw new EtxWebException("Utility class");
    }


    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static MessageDigest getSha512MdInstance() {
        try {
            return MessageDigest.getInstance(SHA_512, BouncyCastleProvider.PROVIDER_NAME);
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new EtxWebException("Error getting SHA-512 message digest instance", e);
        }
    }


    public static String digest(byte[] bytes) {
        MessageDigest messageDigest = Md.getSha512MdInstance();

        return DatatypeConverter.printHexBinary(messageDigest.digest(bytes)).toUpperCase();
    }

    public static void verify(byte[] bytes, String expectedMD) {
        String computedMD = digest(bytes);

        if (! StringUtils.equalsIgnoreCase(expectedMD, computedMD)) {
            throw new EtxWebException(String.format("Checksum mismatch!%n Expected: %s%n Computed %s", expectedMD, computedMD));
        }
    }

    public static void verify(String computedMD, String expectedMD) {
        if (!StringUtils.equalsIgnoreCase(expectedMD, computedMD)) {
            throw new EtxWebException(String.format("Checksum mismatch!%n Expected: %s%n Computed %s", expectedMD, computedMD));
        }
    }
}
