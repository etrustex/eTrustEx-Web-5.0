/*
 * Copyright (c) 2023. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package eu.europa.ec.etrustex.web.util.crypto;

import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import static eu.europa.ec.etrustex.web.util.crypto.Base64.encodeToString;
import static eu.europa.ec.etrustex.web.util.test.PrivateConstructorTester.testPrivateConstructor;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings({"java:S112", "java:S100"}) /* Suppress Define and throw a dedicated exception instead of using a generic one & method names false positive. */
class AesTest {
    @TempDir
    private static Path tmpDir;

    private static final String CLEAR_TEXT = "Loren ipsum...";

    private static final Path FILES_DIR_PATH = Paths.get("src","test", "resources", "files");


    @Test
    void constructor_should_be_private() throws NoSuchMethodException {
        testPrivateConstructor(Aes.class.getDeclaredConstructor());
    }


    @Test
    void gcm_iv_generated_should_be_12_bytes() {
        byte[] iv = Aes.generateIv();
        assertEquals(AesValues.IV_LENGTH_BYTES.value(), iv.length);
    }

    @Test
    void encryptAndDecryptFile() throws IOException {
        SecretKey aesKey = Aes.generateAesKey();
        byte[] keyB64 = aesKey.getEncoded();
        SecretKeySpec keySpec = new SecretKeySpec(keyB64, "AES");

        Path clearPath = FILES_DIR_PATH.resolve("30Mb.zip");
        Path encryptedPath = tmpDir.resolve("encrypted");
        Files.createFile(encryptedPath);

        assertNotEquals(0, Files.size(clearPath));

        File clearFile = clearPath.toFile();
        String originalChecksum = checkSum(clearFile);

        Aes.gcmEncryptFile(keySpec, clearPath, encryptedPath);

        assertNotEquals(0, Files.size(encryptedPath));

        Path decryptedPath = tmpDir.resolve("decrypted");
        Files.createFile(decryptedPath);
        byte[] decryptedBytes = Aes.gcmDecryptFile(keySpec, encryptedPath);
        FileUtils.writeByteArrayToFile(decryptedPath.toFile(), decryptedBytes);

        String decryptedChecksum = checkSum(decryptedPath.toFile());

        assertEquals(originalChecksum, decryptedChecksum);
    }


    @Test
    void should_AES_GCM_encrypt_and_decrypt_bytes() {
        SecretKey aesKey = Aes.generateAesKey();
        byte[] iv = Aes.generateIv();

        byte[] encryptedBytes = Aes.gcmEncrypt(aesKey, iv, CLEAR_TEXT.getBytes());
        String encryptedTextB64 = encodeToString(encryptedBytes);
        String decryptedText = Aes.gcmDecrypt(aesKey, encodeToString(iv), encryptedTextB64);

        assertEquals(CLEAR_TEXT, decryptedText);
    }

    @Test
    void should_fail_to_AES_GCM_encrypt_with_wrong_key() {
        SecretKey aesKey = generateDesKey();
        byte[] iv = Aes.generateIv();

        assertThrows(EtxWebException.class,
                () -> Aes.gcmEncrypt(aesKey, iv, CLEAR_TEXT.getBytes()));
    }

    @Test
    void should_fail_to_AES_GCM_decrypt_with_wrong_key() {
        byte[] iv = Aes.generateIv();
        byte[] encryptedBytes = Aes.gcmEncrypt(Aes.generateAesKey(), iv, CLEAR_TEXT.getBytes());
        String encryptedTextB64 = encodeToString(encryptedBytes);
        SecretKey anotherKey = Aes.generateAesKey();

        assertThrows(EtxWebException.class,
                () -> Aes.gcmDecrypt(anotherKey, encodeToString(iv), encryptedTextB64));
    }

    @Test
    void should_fail_to_AES_GCM_decrypt_with_wrong_iv() {
        SecretKey aesKey = Aes.generateAesKey();
        byte[] iv = Aes.generateIv();
        byte[] encryptedBytes = Aes.gcmEncrypt(aesKey, iv, CLEAR_TEXT.getBytes());
        String encryptedTextB64 = encodeToString(encryptedBytes);
        byte[] anotherIv = Aes.generateIv();

        assertThrows(EtxWebException.class,
                () -> Aes.gcmDecrypt(aesKey, encodeToString(anotherIv), encryptedTextB64));
    }


    private SecretKey generateDesKey()  {
        KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance("DES");
        } catch (NoSuchAlgorithmException e) {
            throw new EtxWebException("Failed to generate symmetric key ", e);
        }
        keyGenerator.init(56, new SecureRandom());

        return keyGenerator.generateKey();
    }

    private String checkSum(File file) throws IOException {
        byte[] bytes = FileUtils.readFileToByteArray(file);
        byte[] digest = Md.getSha512MdInstance().digest(bytes);

        return DatatypeConverter.printHexBinary(digest).toUpperCase();
    }
}