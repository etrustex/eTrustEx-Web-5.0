package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.common.EtrustexWebProperties;
import eu.europa.ec.etrustex.web.persistence.entity.Credentials;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.EncryptedPassword;
import eu.europa.ec.etrustex.web.persistence.repository.CredentialsRepository;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import javax.crypto.KeyGenerator;
import javax.xml.bind.DatatypeConverter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import static eu.europa.ec.etrustex.web.service.util.FSUtils.createMissingParentFolders;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CredentialServiceTest {

    private final ResourceLoader resourceLoader = new DefaultResourceLoader();
    private CredentialServiceImpl credentialEncryptionService;
    private static final String TEST_PASSWORD = "SomePassword123üã";
    @Mock
    private CredentialsRepository credentialsRepository;
    @Mock
    private EncryptionService encryptionService;

    private final EtrustexWebProperties etrustexWebProperties = new EtrustexWebProperties();

    @BeforeEach
    public void setup() {
        etrustexWebProperties.setEnvironment("dev");
        this.credentialEncryptionService = new CredentialServiceImpl(resourceLoader, credentialsRepository, encryptionService, etrustexWebProperties,"file:src/test/resources/credentials-aes-key/generic_key.txt");
    }


    @Test
    void should_encrypt_and_decrypt_a_password_b64() {
        EncryptedPassword encryptedPassword = this.credentialEncryptionService.cipherPassword(TEST_PASSWORD.getBytes());
        String clearText = this.credentialEncryptionService.decipherPassword(encryptedPassword);
        assertThat(clearText).isEqualTo(TEST_PASSWORD);
    }

    @Test
    void should_decrypt_with_secret_loaded_from_file() {
        String clearText = this.credentialEncryptionService.decipherPassword(
                EncryptedPassword.builder()
                        .passwordB64("rj/ba3NPx3ibFntEfMBG281/c3Wnc11JAXT6nONwb3tus3A=")
                        .ivB64("t0XPRK8ZSJ4Tfoba")
                        .build()
        );

        assertThat(clearText).isEqualTo(TEST_PASSWORD);
    }

    @Test
    void should_find_credentials_by_user_name() {
        EncryptedPassword encryptedPassword = EncryptedPassword.builder().passwordB64("akjnkdvkjndv").build();
        Credentials credentials = Credentials.builder().userName("userName").encryptedPassword(encryptedPassword).build();
        given(credentialsRepository.findCredentialsByUserName(any())).willReturn(credentials);
        assertThat(credentialEncryptionService.findCredentialsByUserName(any()).getUserName()).isEqualTo(credentials.getUserName());
    }

    @Test
    void should_save_credentials() {
        EncryptedPassword encryptedPassword = EncryptedPassword.builder().passwordB64("akjnkdvkjndv").build();
        Credentials credentials = Credentials.builder().userName("userName").encryptedPassword(encryptedPassword).build();
        given(credentialsRepository.save(any())).willReturn(credentials);
        assertThat(credentialEncryptionService.save(any()).getUserName()).isEqualTo(credentials.getUserName());
    }


    @Test
    /*
     * this test is used to generate a new credential encryption key, when needed
     * In order to run the test, first delete the key in resources/credentials-aes-key/new_key.txt (if existing)
     */
    void should_generate_a_new_credential_encryption_key() throws NoSuchAlgorithmException {

        Path path = Paths.get("src/test/resources/credentials-aes-key/new_key.txt");
        if (!Files.exists(path)) {
            SecureRandom secureRandom = SecureRandom.getInstanceStrong();
            final KeyGenerator aesKeyGenerator = KeyGenerator.getInstance("AES");
            aesKeyGenerator.init(256, secureRandom);
            String keyHex = DatatypeConverter.printHexBinary(aesKeyGenerator.generateKey().getEncoded());

            try {
                createMissingParentFolders(path.getParent());
            } catch (IOException e) {
                throw new EtxWebException("Cannot create aes key folder.", e);
            }
            try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                writer.write(keyHex);
            } catch (IOException e) {
                throw new EtxWebException("Cannot save the credentials aes key to file.", e);
            }
        }
        assertTrue(Files.exists(path));
    }
}
