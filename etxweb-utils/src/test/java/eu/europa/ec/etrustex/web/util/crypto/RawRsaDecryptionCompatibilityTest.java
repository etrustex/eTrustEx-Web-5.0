/*
 * Copyright (c) 2018-2023. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package eu.europa.ec.etrustex.web.util.crypto;

import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/*
 * TODO ETRUSTEX-8263 Remove this class after retention policies for messages take place
 * Using padding transformation without fully qualifying it (I.e. "RSA" ) is bad practice because it depends on the java implementation.
 * This catch is here only for backwards compatibility. To retry decryption if raw "RSA" was used to encrypt
 */
@SuppressWarnings({"java:S112", "java:S100", "java:S6437", "java:S2068"}) /* Suppress Define and throw a dedicated exception instead of using a generic one & method names false positive & 'PASSWORD' detected . */
@Disabled
public class RawRsaDecryptionCompatibilityTest {
    private static final String RAW_RSA_ALGO_CLEAR_TEXT = "UclZrM32X400h99viu/i99z4BEO7jHjkp9wHu7jogl0=";
    private static final String RAW_RSA_ALGO_CYPHER_TEXT = "DisKEMOg8A1dVrC6iRvashpjZIUwtfCgsqOB2pgrR9arrYXnLC9/xOpzRoeDUVeyGGwTr+eyS0iorQzSOalYHUKQWVsio0nekzXfgOoy+hC+DhZZgWxWb6OIvlI5XevNZkkD5p5EMkXw4m+IRo8/KyEhKUIAQDKIRHBn1tcin1JRvouR0UXb60rxTEbBB/rNA5QDtIlHyG/jc8K3zBO7kmycona9R1tUkYtL97Wksv626UfNGfk9pWR0F+pQwyjUt2qcR+3x8t13jXTB8rBx4PYbwYwa7GaaOu8EelQMI5hC/Y2G50VFMnq4LE0E+SMKSqqqEHcTOrdYPi/Tq0mfSw==";
    private static final Path CERTIFICATE_PATH = Paths.get("src","test","resources", "certificate", "ETRUSTEX_AP_ACC_ETRUSTEXWEB_ETRUSTEX.p12");
    private static final String CERTIFICATE_TYPE = "PKCS12";
    private static final String CERTIFICATE_PASSWORD = "";
    private static final String CERTIFICATE_ALIAS = "ETRUSTEX_AP_ACC_ETRUSTEXWEB_ETRUSTEX";

    @Test
    void should_decrypt_text_encrypted_using_RSA_raw_algorithm() {
        PrivateKey privateKey = getPrivateKey();
        String decryptedText = new String(Rsa.decrypt(privateKey, RAW_RSA_ALGO_CYPHER_TEXT));

        assertNotNull(decryptedText);
        assertEquals(RAW_RSA_ALGO_CLEAR_TEXT, decryptedText);
    }

    private PrivateKey getPrivateKey() {
        KeyStore keyStore = getKeyStore();
        try {
            return (PrivateKey) keyStore.getKey(CERTIFICATE_ALIAS, CERTIFICATE_PASSWORD.toCharArray());
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new EtxWebException("Error retrieving Private Key from certificate", e);
        }
    }

    private static KeyStore getKeyStore() {
        try ( InputStream inputStream = Files.newInputStream(CERTIFICATE_PATH)) {
            KeyStore keystore = KeyStore.getInstance(CERTIFICATE_TYPE);
            keystore.load(new BufferedInputStream(inputStream), CERTIFICATE_PASSWORD.toCharArray());

            return keystore;
        } catch (Exception e) {
            throw new EtxWebException(String.format("Cannot retrieve server keystore from %s certificate file!", CERTIFICATE_PATH), e);
        }
    }
}
