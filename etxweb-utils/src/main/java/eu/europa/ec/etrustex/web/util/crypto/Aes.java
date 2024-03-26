/*
 * Copyright (c) 2018-2023. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package eu.europa.ec.etrustex.web.util.crypto;

import eu.europa.ec.etrustex.web.util.chunkprocessor.ChunkProcessor;
import eu.europa.ec.etrustex.web.util.chunkprocessor.ChunkProcessorFn;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Arrays;

import static eu.europa.ec.etrustex.web.util.crypto.AesValues.*;
import static eu.europa.ec.etrustex.web.util.crypto.Base64.decode;
import static eu.europa.ec.etrustex.web.util.crypto.Base64.encodeToString;

public class Aes {
    public static final String AES_ALGORITHM = "AES";
    private static final String AES_GCM_NO_PADDING = "AES/GCM/NoPadding";
    private static final SecureRandom secureRandom = new SecureRandom();


    private Aes() {
        throw new IllegalStateException("Utility class");
    }


    static {
        Security.addProvider(new BouncyCastleProvider());
    }


    public static byte[] gcmEncrypt(SecretKey key, byte[] iv, byte[] clear) {
        try {
            Cipher cipher = Cipher.getInstance(AES_GCM_NO_PADDING, BouncyCastleProvider.PROVIDER_NAME);
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH_BITS.value(), iv));

            return cipher.doFinal(clear);
        } catch (Exception e) {
            throw new EtxWebException("Error symmetrically encrypting", e);
        }
    }

    public static byte[] gcmEncryptChunk(SecretKey key, byte[] clear) {
        byte[] iv = generateIv();
        byte[] encrypted = gcmEncrypt(key, iv, clear);

        return ArrayUtils.addAll(encrypted, iv);
    }

    public static String gcmEncryptAndEncode(SecretKey key, byte[] iv, String clear) {
        byte[] encryptedData = gcmEncrypt(key, iv, clear.getBytes());

        return encodeToString(encryptedData);
    }

    public static void gcmEncryptFile(SecretKey key, Path source, Path dest) throws IOException {
        File clearFile = source.toFile();
        FileInputStream clearFileInputStream = FileUtils.openInputStream(clearFile);

        try (BufferedOutputStream encryptedOutputStream = new BufferedOutputStream(Files.newOutputStream(dest))) {
            ChunkProcessorFn processFn = (chunk, bytesProcessed) -> {
                byte[] encrypted = Aes.gcmEncryptChunk(key, chunk);
                encryptedOutputStream.write(encrypted);
            };

            ChunkProcessor chunkProcessor = new ChunkProcessor(processFn, CHUNK_SIZE.value());

            chunkProcessor.process(new BufferedInputStream(clearFileInputStream, CHUNK_SIZE.value()));
        }
    }

    public static String gcmDecodeAndDecrypt(byte[] symmetricKeyB64, String ivB64, byte[] encryptedTextB64) {
        SecretKeySpec keySpec = new SecretKeySpec(symmetricKeyB64, "AES");
        byte[] decodeBytes = Base64.decode(encryptedTextB64);
        byte[] decryptedTextBytes = Aes.gcmDecrypt(keySpec, ivB64, decodeBytes);

        return new String(decryptedTextBytes);
    }

    public static String gcmDecrypt(SecretKey key, String ivB64, String encryptedTextB64) {
        try {
            byte[] decodedText = decode(encryptedTextB64);

            Cipher cipher = Cipher.getInstance(AES_GCM_NO_PADDING, BouncyCastleProvider.PROVIDER_NAME);
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH_BITS.value(), decode(ivB64)));
            byte[] clearText = cipher.doFinal(decodedText);

            return new String(clearText, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new EtxWebException("Error symmetrically decrypting text: " + encryptedTextB64, e);
        }
    }

    public static byte[] gcmDecrypt(SecretKey key, String ivB64, byte[] encryptedData) {
        return gcmDecrypt(key, decode(ivB64), encryptedData);
    }

    public static byte[] gcmDecrypt(SecretKey key, byte[] iv, byte[] encryptedData) {
        try {
            Cipher cipher = Cipher.getInstance(AES_GCM_NO_PADDING, BouncyCastleProvider.PROVIDER_NAME);
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH_BITS.value(), iv));

            return cipher.doFinal(encryptedData);
        } catch (Exception e) {
            throw new EtxWebException("Error symmetrically decrypting bytes", e);
        }
    }

    public static byte[] gcmDecryptFile(SecretKey key, Path source) throws IOException {
        byte[] decryptedFileBytes;
        FileInputStream encryptedFileInputStream = FileUtils.openInputStream(source.toFile());

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ChunkProcessorFn processFn = (chunk, bytesProcessed) -> {
                byte[] decrypted = Aes.gcmDecryptChunk(key, chunk);
                byteArrayOutputStream.write(decrypted);
            };

            ChunkProcessor chunkProcessor = new ChunkProcessor(processFn, CHUNK_SIZE.value() + TAG_LENGTH_BYTES.value() + IV_LENGTH_BYTES.value());

            chunkProcessor.process(new BufferedInputStream(encryptedFileInputStream, CHUNK_SIZE.value() + TAG_LENGTH_BYTES.value() + IV_LENGTH_BYTES.value()));
            decryptedFileBytes = byteArrayOutputStream.toByteArray();
        }

        return decryptedFileBytes;
    }

    public static byte[] gcmDecryptChunk(SecretKey key, byte[] chunk) {
        try {
            byte[] iv = Arrays.copyOfRange(chunk, chunk.length - IV_LENGTH_BYTES.value(), chunk.length);
            byte[] cipherAndTag = Arrays.copyOfRange(chunk, 0, chunk.length - IV_LENGTH_BYTES.value());

            Cipher cipher = Cipher.getInstance(AES_GCM_NO_PADDING, BouncyCastleProvider.PROVIDER_NAME);
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH_BITS.value(), iv));

            return cipher.doFinal(cipherAndTag);
        } catch (Exception e) {
            throw new EtxWebException("Error in AES GCM decrypting chunk bytes", e);
        }
    }

    public static SecretKey generateAesKey()  {
        KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance("AES", BouncyCastleProvider.PROVIDER_NAME);
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new EtxWebException("Failed to generate symmetric key ", e);
        }
        keyGenerator.init(AES_KEY_LENGTH_BITS.value(), secureRandom);

        return keyGenerator.generateKey();
    }

    public static byte[] generateIv() {
        byte[] iv = new byte[IV_LENGTH_BYTES.value()];
        secureRandom.nextBytes(iv);
        return iv;
    }

    @SuppressWarnings("java:S3329")
    public static IvParameterSpec generateIvParameterSpec() {
        return new IvParameterSpec(generateIv());
    }
}
