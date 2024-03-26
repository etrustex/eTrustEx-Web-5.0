/*
 * Copyright (c) 2018-2024. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package eu.europa.ec.etrustex.web.util.test;

import eu.europa.ec.etrustex.web.util.exception.EtxWebException;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;

public class CryptoUtils {
    public static KeyStore getKeyStore(Path certificatePath, String certificateType, String certificatePassword) {
        try ( InputStream inputStream = Files.newInputStream(certificatePath)) {
            KeyStore keystore = KeyStore.getInstance(certificateType);
            keystore.load(new BufferedInputStream(inputStream), certificatePassword.toCharArray());

            return keystore;
        } catch (Exception e) {
            throw new EtxWebException(String.format("Cannot retrieve server keystore from %s certificate file!", certificatePath), e);
        }
    }

    private CryptoUtils() {
        throw new IllegalStateException("Utility class");
    }
}
