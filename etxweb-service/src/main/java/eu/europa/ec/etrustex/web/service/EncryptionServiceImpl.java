package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.common.EtrustexWebProperties;
import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.util.crypto.Aes;
import eu.europa.ec.etrustex.web.util.crypto.Rsa;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.util.exchange.model.keys.RsaPublicKeyDto;
import eu.europa.ec.etrustex.web.util.exchange.model.keys.SymmetricKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;


@Service
@Slf4j
@RequiredArgsConstructor
public class EncryptionServiceImpl implements EncryptionService {
    private final EtrustexWebProperties etrustexWebProperties;

    @Override
    public String getDecryptedMessageText(Message message) {
        String encryptedText = message.getText();
        byte[] decryptedSymmetricKeyB64 = decryptWithServerPrivateKey(message.getSymmetricKey().getRandomBits());
        String ivB64 = message.getIv();

        return Aes.gcmDecodeAndDecrypt(decryptedSymmetricKeyB64, ivB64, encryptedText.getBytes());
    }

    @Override
    public String getDecryptedTemplateVariables(Message message) {
        String encryptedText = message.getTemplateVariables();
        byte[] decryptedSymmetricKeyB64 = decryptWithServerPrivateKey(message.getSymmetricKey().getRandomBits());
        String ivB64 = message.getIv();

        return Aes.gcmDecodeAndDecrypt(decryptedSymmetricKeyB64, ivB64, encryptedText.getBytes());
    }

    @Override
    public RsaPublicKeyDto getServerPublicKey() {
        RSAPublicKey publicKey = (RSAPublicKey) getServerKeyPair().getPublic();

        return Rsa.toPublicKeyDto(publicKey);
    }

    @Override
    public byte[] decryptWithServerPrivateKey(String encryptedText) {
        PrivateKey privateKey = getServerKeyPair().getPrivate();

        return Rsa.decrypt(privateKey, encryptedText);
    }

    @Override
    public byte[] decryptWithOldServerPrivateKey(String encryptedText) {
        PrivateKey privateKey = getOldServerKeyPair().getPrivate();

        return Rsa.decrypt(privateKey, encryptedText);
    }


    @Override
    public String encryptWithServerPublicKey(byte[] clear) {
        PublicKey publicKey = getServerKeyPair().getPublic();

        return Rsa.encrypt(publicKey, clear);
    }

    @Override
    public SymmetricKey encryptWithNewCertificate(SymmetricKey symmetricKey) {
        byte[] decryptedTextWithOldCertificate = decryptWithOldServerPrivateKey(symmetricKey.getRandomBits());
        String encryptedTextWithNewCertificate = encryptWithServerPublicKey(decryptedTextWithOldCertificate);
        symmetricKey.setRandomBits(encryptedTextWithNewCertificate);
        return symmetricKey;
    }

    @Override
    public SymmetricKey decryptSymmetricKeyAndEncryptWithClientPublicKey(SymmetricKey symmetricKey, String clientPublicKey) {
        if (symmetricKey == null
                || symmetricKey.getEncryptionMethod().equals(SymmetricKey.EncryptionMethod.ENCRYPTED)
                || symmetricKey.getEncryptionMethod().equals(SymmetricKey.EncryptionMethod.PLAIN)) {
            return symmetricKey;
        }

        byte[] decryptedSymmetricKey = decryptWithServerPrivateKey(symmetricKey.getRandomBits());

        if (clientPublicKey != null) {
            String encryptedRandomBitsWithClientPublicKey = Rsa.encrypt(clientPublicKey, decryptedSymmetricKey);
            symmetricKey.setRandomBits(encryptedRandomBitsWithClientPublicKey);
        } else {
            symmetricKey.setRandomBits(new String(decryptedSymmetricKey));
        }

        return symmetricKey;
    }

    @Override
    public LocalDateTime getServerCertificateExpirationDate() {
        return Instant.ofEpochMilli(getServerCertificate().getNotAfter().getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    @Override
    public X509Certificate getServerCertificate() {
        String serverCertificateAlias = etrustexWebProperties.getServerCertificateAlias();
        try {
            return (X509Certificate) getServerKeyStore().getCertificate(serverCertificateAlias);
        } catch (Exception e) {
            throw new EtxWebException("Cannot retrieve server certificate!", e);
        }
    }

    @Override
    public String getServerCertificateAlias() {
        return etrustexWebProperties.getServerCertificateAlias();
    }

    @Override
    public KeyPair getServerKeyPair() {
        try {
            String serverCertificatePassword = etrustexWebProperties.getServerCertificatePassword();
            String serverCertificateAlias = etrustexWebProperties.getServerCertificateAlias();
            KeyStore keyStore = getServerKeyStore();

            PrivateKey privateKey = (PrivateKey) getServerKeyStore().getKey(serverCertificateAlias, serverCertificatePassword.toCharArray());

            Certificate cert = keyStore.getCertificate(serverCertificateAlias);
            PublicKey publicKey = cert.getPublicKey();

            return new KeyPair(publicKey, privateKey);
        } catch (Exception e) {
            throw new EtxWebException("Cannot retrieve server keypair!", e);
        }
    }

    @Override
    public KeyPair getOldServerKeyPair() {
        try {
            String serverCertificatePassword = etrustexWebProperties.getOldServerCertificatePassword();
            String serverCertificateAlias = etrustexWebProperties.getOldServerCertificateAlias();
            KeyStore keyStore = getOldServerKeyStore();

            PrivateKey privateKey = (PrivateKey) keyStore.getKey(serverCertificateAlias, serverCertificatePassword.toCharArray());

            Certificate cert = keyStore.getCertificate(serverCertificateAlias);
            PublicKey publicKey = cert.getPublicKey();

            return new KeyPair(publicKey, privateKey);
        } catch (Exception e) {
            throw new EtxWebException("Cannot retrieve server keypair!", e);
        }
    }


    @Override
    public KeyStore getServerKeyStore() {
        return getKeyStore(etrustexWebProperties.getServerCertificate(), etrustexWebProperties.getServerCertificateType(), etrustexWebProperties.getServerCertificatePassword());
    }


    private KeyStore getOldServerKeyStore() {
        return getKeyStore(etrustexWebProperties.getOldServerCertificate(), etrustexWebProperties.getOldServerCertificateType(), etrustexWebProperties.getOldServerCertificatePassword());
    }

    private KeyStore getKeyStore(Resource serverCertificatePath,
                                 String serverCertificateType,
                                 String serverCertificatePassword) {
        if (!serverCertificatePath.exists() || !serverCertificatePath.isReadable()) {
            throw new EtxWebException(String.format("Certificate file %s not found or not readable ", serverCertificatePath.getFilename()));
        }

        try {
            KeyStore keystore = KeyStore.getInstance(serverCertificateType);
            keystore.load(serverCertificatePath.getInputStream(), serverCertificatePassword.toCharArray());

            return keystore;
        } catch (Exception e) {
            throw new EtxWebException(String.format("Cannot retrieve server keystore from %s certificate file!", serverCertificatePath.getFilename()), e);
        }
    }
}
