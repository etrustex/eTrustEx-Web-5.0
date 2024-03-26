package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.util.exchange.model.keys.RsaPublicKeyDto;
import eu.europa.ec.etrustex.web.util.exchange.model.keys.SymmetricKey;

import java.security.KeyPair;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;

public interface EncryptionService {

    String getDecryptedMessageText(Message message);

    String getDecryptedTemplateVariables(Message message);

    RsaPublicKeyDto getServerPublicKey();

    byte[] decryptWithServerPrivateKey(String encryptedText);

    byte[] decryptWithOldServerPrivateKey(String encryptedText);

    String encryptWithServerPublicKey(byte[] clear);

    SymmetricKey encryptWithNewCertificate(SymmetricKey symmetricKey);

    SymmetricKey decryptSymmetricKeyAndEncryptWithClientPublicKey(SymmetricKey symmetricKey, String clientPublicKey);

    LocalDateTime getServerCertificateExpirationDate();

    X509Certificate getServerCertificate();

    String getServerCertificateAlias();

    KeyPair getServerKeyPair();

    KeyPair getOldServerKeyPair();

    KeyStore getServerKeyStore();

}
