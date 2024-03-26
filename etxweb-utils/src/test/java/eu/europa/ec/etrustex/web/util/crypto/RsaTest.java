/*
 * Copyright (c) 2023. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package eu.europa.ec.etrustex.web.util.crypto;

import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;

import static eu.europa.ec.etrustex.web.util.crypto.Base64.encodeToString;
import static eu.europa.ec.etrustex.web.util.test.CryptoUtils.getKeyStore;
import static eu.europa.ec.etrustex.web.util.test.PrivateConstructorTester.testPrivateConstructor;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings({"java:S112", "java:S100", "java:S6437", "java:S2068"}) /* Suppress Define and throw a dedicated exception instead of using a generic one & method names false positive & 'PASSWORD' detected . */
class RsaTest {
    private static final Path CERTIFICATE_PATH = Paths.get("src","test","resources", "certificate", "TestConfidential.p12");
    private static final String CERTIFICATE_TYPE = "PKCS12";
    private static final String CERTIFICATE_PASSWORD = "test123";
    private static final String CERTIFICATE_ALIAS = "gui2_i1c2";

    private PrivateKey privateKey;
    private PublicKey publicKey;


    @Test
    void constructor_should_be_private() throws NoSuchMethodException {
        testPrivateConstructor(Rsa.class.getDeclaredConstructor());
    }

    @Test
    void should_RSA_encrypt_and_decrypt_text() {
        initKeyPair();

        String clearText = "Loren ipsum...";
        String encryptedText = Rsa.encrypt(publicKey, clearText.getBytes());
        byte[] decryptedBytes = Rsa.decrypt(privateKey, encryptedText);
        String decryptedText = new String(decryptedBytes, StandardCharsets.UTF_8);

        assertEquals(clearText, decryptedText);
    }

    @Test
    void should_Aes_encrypt_RSA_encrypt_key_and_decrypt_text() {
        String clearText = "Loren ipsum...";
        initKeyPair();
        SecretKey aesKey = Aes.generateAesKey();
        byte[] iv = Aes.generateIv();

        // AES encrypt text
        String encryptedText = Aes.gcmEncryptAndEncode(aesKey, iv, clearText);

        // Rsa encrypt AES key
        String encryptedKey = Rsa.encrypt(publicKey, aesKey.getEncoded());

        // Rsa decrypt AES key
        byte[] decryptedKeyBytes = Rsa.decrypt(privateKey, encryptedKey);

        // AES decrypt text
        String decryptedText = Aes.gcmDecodeAndDecrypt(decryptedKeyBytes, encodeToString(iv), encryptedText.getBytes());
        assertEquals(clearText, decryptedText);
    }

    @Test
    void should_throw_if_wrong_private_key_content() {
        assertThrows(EtxWebException.class, () -> Rsa.toPrivateKey("foo"));
    }

    @Test
    void should_throw_if_wrong_public_key_content() {
        assertThrows(EtxWebException.class, () -> Rsa.toPrivateKey("foo"));
    }

    @Test
    void should_throw_if_wrong_cypher_text() {
        assertThrows(EtxWebException.class, () -> Rsa.encrypt(publicKey, "wrong".getBytes()));
    }

    private void initKeyPair() {
        KeyStore keyStore = getKeyStore(CERTIFICATE_PATH, CERTIFICATE_TYPE, CERTIFICATE_PASSWORD);

        try {
            privateKey = (PrivateKey) keyStore.getKey(CERTIFICATE_ALIAS, CERTIFICATE_PASSWORD.toCharArray());
            publicKey = keyStore.getCertificate(CERTIFICATE_ALIAS).getPublicKey();
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new EtxWebException("Error retrieving Private Key from certificate", e);
        }
    }


}