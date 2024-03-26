package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.common.EtrustexWebProperties;
import eu.europa.ec.etrustex.web.persistence.entity.Credentials;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.EncryptedPassword;
import eu.europa.ec.etrustex.web.persistence.repository.CredentialsRepository;
import eu.europa.ec.etrustex.web.util.crypto.Aes;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.util.Scanner;

import static eu.europa.ec.etrustex.web.util.crypto.Base64.encodeToString;

@Service
@Slf4j
public class CredentialServiceImpl implements CredentialService {
    private final ResourceLoader resourceLoader;
    private final CredentialsRepository credentialsRepository;
    private final EncryptionService encryptionService;
    private SecretKey credentialAESKey;
    private final EtrustexWebProperties etrustexWebProperties;

    public CredentialServiceImpl(ResourceLoader resourceLoader,
                                 CredentialsRepository credentialsRepository,
                                 EncryptionService encryptionService,
                                 EtrustexWebProperties etrustexWebProperties,
                                 @Value("${etrustexweb.credentials-aes-key-file}") String credentialsAESKeyFile) {
        this.resourceLoader = resourceLoader;
        this.credentialsRepository = credentialsRepository;
        this.encryptionService = encryptionService;
        this.etrustexWebProperties = etrustexWebProperties;
        this.loadCredentialsAESKey(credentialsAESKeyFile);
    }

    @Override
    public EncryptedPassword cipherPassword(byte[] passwordBytes) {
        byte[] iv = Aes.generateIv();
        byte[] encryptedPassword = Aes.gcmEncrypt(credentialAESKey, iv, passwordBytes);

        return EncryptedPassword.builder()
                .passwordB64(encodeToString(encryptedPassword))
                .ivB64(encodeToString(iv))
                .build();
    }

    @Override
    public String decipherPassword(EncryptedPassword encryptedPassword) {
        return Aes.gcmDecrypt(credentialAESKey, encryptedPassword.getIvB64(), encryptedPassword.getPasswordB64());
    }

    @Override
    public void encryptCredentialPasswordsWithNewCertificate() {
        credentialsRepository.findAll().forEach(credentials -> {
            byte[] decryptedWithOldCertificate = encryptionService.decryptWithOldServerPrivateKey(credentials.getEncryptedPassword().getPasswordB64());
            String encryptedWithNewCertificate = encryptionService.encryptWithServerPublicKey(decryptedWithOldCertificate);
            credentials.getEncryptedPassword().setPasswordB64(encryptedWithNewCertificate);
            credentialsRepository.save(credentials);
        });
    }

    @Override
    public Credentials findCredentialsByUserName(String userName) {
        return credentialsRepository.findCredentialsByUserName(userName);
    }

    @Override
    public Credentials save(Credentials credentials) {
        return credentialsRepository.save(credentials);
    }


    private void loadCredentialsAESKey(String credentialsAESKeyFile) {
        if (!etrustexWebProperties.isDevEnvironment()) {
            this.credentialAESKey = new SecretKeySpec(DatatypeConverter.parseHexBinary(etrustexWebProperties.getCredentialsAesKey()), "AES");
            return;
        }

        Resource aesKeyFile = resourceLoader.getResource(credentialsAESKeyFile);
        try (Scanner scanner = new Scanner(aesKeyFile.getFile()))
        {
            String keyHex = scanner.nextLine();
            this.credentialAESKey = new SecretKeySpec(DatatypeConverter.parseHexBinary(keyHex), "AES");
        } catch (IOException e) {
            throw new EtxWebException("Cannot load credential encryption key!", e);
        }
    }
}
