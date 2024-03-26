package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.persistence.entity.Credentials;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.EncryptedPassword;


public interface CredentialService {

    EncryptedPassword cipherPassword(byte[] passwordBytes);

    String decipherPassword(EncryptedPassword encryptedPassword);
    void encryptCredentialPasswordsWithNewCertificate();
    Credentials findCredentialsByUserName(String userName);

    Credentials save(Credentials credentials);
}
