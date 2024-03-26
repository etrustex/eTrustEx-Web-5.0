/*
 * Copyright (c) 2018-2023. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package eu.europa.ec.etrustex.web.util.crypto;

import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.util.exchange.model.keys.RsaPublicKeyDto;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import static eu.europa.ec.etrustex.web.util.crypto.Base64.decode;
import static eu.europa.ec.etrustex.web.util.crypto.Base64.encodeToString;

public class Rsa {
    public static final String CLIENT_PUBLIC_KEY_HEADER_NAME = "CLIENT-PUBLIC-KEY";
    public static final String RSA_PKCS1_OAEP_PADDING = "RSA/ECB/OAEPWithSHA1AndMGF1Padding";
    public static final String RSA_PROVIDER_DEFAULT_PADDING = "RSA";

    private Rsa() {
        throw new IllegalStateException("Utility class");
    }


    static {
        Security.addProvider(new BouncyCastleProvider());
    }


    public static KeyPair generateRSAkeyPair() {
        KeyPairGenerator keyGenerator;
        try {
            keyGenerator = KeyPairGenerator.getInstance(RSA_PROVIDER_DEFAULT_PADDING, BouncyCastleProvider.PROVIDER_NAME);
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new EtxWebException("Error getting KeyPairGenerator instance", e);
        }
        keyGenerator.initialize(2048);

        return keyGenerator.genKeyPair();
    }

    public static PrivateKey toPrivateKey(String privateKeyContent) {
        String privateKeyPEM = privateKeyContent
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("\r", "")
                .replace("\n", "")
                .replace("\\s", "");

        try {
            byte[] privateBytes = decode(privateKeyPEM);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA", BouncyCastleProvider.PROVIDER_NAME);

            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            throw new EtxWebException("Error extracting PrivateKey from encoded string", e);
        }
    }

    public static PublicKey toPublicKey(String pem) {
        try {
            pem = pem
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replace("\r", "")
                    .replace("\n", "");

            byte[] byteKey = Base64.decode(pem.getBytes(StandardCharsets.UTF_8));
            X509EncodedKeySpec x509publicKey = new X509EncodedKeySpec(byteKey);
            KeyFactory kf = KeyFactory.getInstance("RSA", BouncyCastleProvider.PROVIDER_NAME);

            return kf.generatePublic(x509publicKey);
        } catch (Exception e) {
            throw new EtxWebException("Error extracting PublicKey from encoded string", e);
        }
    }

    public static RsaPublicKeyDto toPublicKeyDto(RSAPublicKey publicKey) {
            String pem = encodeToString(publicKey.getEncoded());

            return RsaPublicKeyDto.builder()
                    .algorithm(publicKey.getAlgorithm())
                    .format(publicKey.getFormat())
                    .modulus(publicKey.getModulus())
                    .publicExponent(publicKey.getPublicExponent())
                    .pem(pem)
                    .build();
    }

    /**
     * Encrypt bytes, B64 encode, toString
     * @return B64 String
     */
    public static String encrypt(PublicKey publicKey, byte[] bytes) {
        try {
            Cipher encryptCipher = Cipher.getInstance(RSA_PKCS1_OAEP_PADDING, BouncyCastleProvider.PROVIDER_NAME);
            encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);

            byte[] encryptedTextBytes = encryptCipher.doFinal(bytes);

            return encodeToString(encryptedTextBytes);
        } catch (Exception e) {
            throw new EtxWebException("Error asymmetrically encrypting ", e);
        }
    }

    public static String encrypt(String publicKeyPem, byte[] bytes) {
        return encrypt(Rsa.toPublicKey(publicKeyPem), bytes);
    }


    /**
     * Get bytes, decode, decrypt
     * @return decrypted bytes
     */
    public static byte[] decrypt(PrivateKey privateKey, String encryptedTextB64) {
        return decrypt(privateKey, encryptedTextB64.getBytes());
    }

    /**
     * Decode, decrypt
     * @return decrypted bytes
     */
    public static byte[] decrypt(PrivateKey privateKey, byte[] encryptedTextBytes) {
        Cipher decryptCipher;
        byte[] decoded;
        try {
            decoded = decode(encryptedTextBytes);
        } catch(IllegalArgumentException iae) {
            decoded = encryptedTextBytes;
        }

        byte[] decryptedTextBytes;
        try {
            decryptCipher = Cipher.getInstance(RSA_PKCS1_OAEP_PADDING, BouncyCastleProvider.PROVIDER_NAME);
            decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);

            decryptedTextBytes = decryptCipher.doFinal(decoded);
        }
        /*
         * TODO ETRUSTEX-8263 Remove this catch clause usage after retention policies for messages take place
         * Using padding transformation without fully qualifying it (I.e. "RSA" ) is bad practice because it depends on the java implementation.
         * This catch is here only for backwards compatibility. To retry decryption if raw "RSA" was used to encrypt
         */
        catch (BadPaddingException bpe) {
            try {
                decryptCipher = Cipher.getInstance(RSA_PROVIDER_DEFAULT_PADDING);
                decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
                decryptedTextBytes = decryptCipher.doFinal(decoded);
            } catch (InvalidKeyException | IllegalBlockSizeException | NoSuchAlgorithmException |
                     NoSuchPaddingException | BadPaddingException e) {
                throw new EtxWebException("Error asymmetrically decrypting text", e);
            }
        }
        catch (Exception e) {
            throw new EtxWebException("Error asymmetrically decrypting text", e);
        }

        return decryptedTextBytes;
    }
}
