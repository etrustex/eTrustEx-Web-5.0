package eu.europa.ec.etrustex.web.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import eu.europa.ec.etrustex.web.common.EtrustexWebProperties;
import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.util.crypto.Aes;
import eu.europa.ec.etrustex.web.util.crypto.Rsa;
import eu.europa.ec.etrustex.web.util.exchange.model.keys.RsaPublicKeyDto;
import eu.europa.ec.etrustex.web.util.exchange.model.keys.SymmetricKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.mockMessage;
import static eu.europa.ec.etrustex.web.util.crypto.Aes.generateAesKey;
import static eu.europa.ec.etrustex.web.util.crypto.Aes.generateIvParameterSpec;
import static eu.europa.ec.etrustex.web.util.crypto.Base64.encodeToString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"java:S2068", "java:S3252"})
class EncryptionServiceImplTest {

    private static final String TEXT = "whatever";

    @Mock
    private EtrustexWebProperties etrustexWebProperties;

    private EncryptionServiceImpl encryptionService;

    private static final String SERVER_CERTIFICATE_PATH = "server-cert.p12";
    private static final String SERVER_CERTIFICATE_PASSWORD = "test123";
    private static final String SERVER_CERTIFICATE_TYPE = "PKCS12";
    private static final String SERVER_CERTIFICATE_ALIAS = "gui2_i1c2";
    private static final String SERVER_CERTIFICATE_PEM = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAl0RG0bO26Ha1k0Db2xMl0xIGnAMKSff0XWn6DnLi7TjfBjj+38bSpgNXV3ZitOYMy8HLJB723Bj03Ii7ARmFYWz/3F80fK8ujgoU10+OtuT0XpS3NE4EkOIV4zxb1+ziTb4iZHV0nMsGN9BBUuNjw3QUFbKASROrFiIYwJV2l3F3+Ho6m9z9E6XCOi1PjzszXKkeP2TDVHJuUZ1041pTY9K3+itf6uWm1An1ma3nleQwzRTnHd78XxemEFpp6UI7O/8zThi7npjn1eM7oFV9QMHR6BSqG6XoPZmM7OGi5ELIxnV7Q0NLu/9Y4vtcZ82mAaRcG5P6j6CYXqmUoNdwPQIDAQAB";


    private static final String OLD_SERVER_CERTIFICATE_PATH = "8192-cert.p12";
    private static final String OLD_SERVER_CERTIFICATE_PASSWORD = "12345";
    private static final String OLD_SERVER_CERTIFICATE_TYPE = "PKCS12";
    private static final String OLD_SERVER_CERTIFICATE_ALIAS = "1";
    private static final String OLD_SERVER_CERTIFICATE_PEM =
            "MIIEIjANBgkqhkiG9w0BAQEFAAOCBA8AMIIECgKCBAEAwO3dMjVGyeIJINu5QLVe" +
                    "dIR7OviqXtAJm8W0vgfdS92JODjX8dUpNpFVJlkTLo4fvAHe7EFwChW8ZjicS+QT" +
                    "vZ54G4rcyXJpn61QewRpSPegu9aiRoa+VernNpzcHpdA569sgcvOahXTE2t1QFqy" +
                    "D9FfCyRZKiCOlnahTxxCONjtzrhtmSn3RWxyCXTpY/jNIVnpzTgsI9TbB56FjPiH" +
                    "6mq5MoaDXBGlEpdUt02ySpvUmvMFkyO/BsGZfWuX75hPLlM/ThYiJO2cxLG9EghR" +
                    "e6TfoGcq2Y9ALSFZ+dlaTtYWDy10un2UsFlkzRD95N75RkXN0T8zSF6I1JQplmcE" +
                    "p3fo0O5kvCHPvWSC5FORvITZVkJNJmN0DXHqx5Tip2EqzGJlyz9TUmj80Hzj72Tn" +
                    "bV74K2TqDyOUn+fBTlVHA39c8kWdXtirCNme3covxggDS8czqm/moDVdfUd4+Pef" +
                    "IyS2n75happzltJoy20LGbRst4aTKmnXFKG/qyzJbGjUtfczt+lXmTryCYvMmzO/" +
                    "HBsiVUdVL+BDiFzqqVQ/hrJ3lKeTtgHEc6EO9ZbZE8g347ZPXrAitkpU9r3Kndcm" +
                    "v7U1uELXDwIdwquhTJmG/cGaV1TAu/PfA1tc5z+i075tbFbwI+OFhb4SZ9PLlXCy" +
                    "NVMRSgFJk3EJUh9sHQhMmt9XOlvJzXaI1gRVrzFDkd1yEZ96PAyS33WA4uGPA1sb" +
                    "lHlOGl5EVirL7lGN1elW5kcT4AQ8rXk37SN67dcTuFQc6HGDDnxHtaGQmE5SCDnK" +
                    "RE4fdhZbAcFtWuwAp34RLrn/wLRsUCM4l2vuHeP6UNO8evzl4TbhnPF9IUg54xsN" +
                    "WB2LOeo3fbfCUNpLjUDJm0uI3DnkxjiTzUJEItzoevBC4RPgsPN/qato4Zi5jIYB" +
                    "rY1f9sZeMrsXdsP1Zui657ExyoLxwd5n2jUvGyPmMYUoT29xW6S7DStOYC6atyjr" +
                    "IwKmuFbit2mfzFT8KQcHd3pXRZ7+H8NPOHC3QEOhCrkyvi9KkGhZNbbvOAgepbq8" +
                    "FPjpXWikPze1wVNioMILYjMFZgVh9wqxY53eeTUpo6nMNc8LNjWSiqX8+TZ6uC4T" +
                    "ViwDiPTsOQtr0hLyRo+I5mSnAPcRWE3hY7v5/KDmFz9skdIwYFeMMdMUStsoWIho" +
                    "Tl5QaVkkczBOnTf7a/5etxnotfjDyIijgDc29pHFnoDltAC5uWAnb1QSYo2uKNPK" +
                    "ZrckeHqcdraiIlvDUKdDLpJGY5G1YnAdOlM+dGEpMdLg4bzWYLbzC3I2sR2XTEl6" +
                    "uAL/5+IgvTK+3CiSz599mQtZoZqGpHA7lNQ+fy+8rKcz48vlGV+0Qg8Fpz8EZgLN" +
                    "ewIDAQAB";


    private static final long SERVER_CERTIFICATE_EXPIRATION_TIME = 1792407899000L;

    @BeforeEach
    public void setUp() {
        this.encryptionService = new EncryptionServiceImpl(this.etrustexWebProperties);
    }

    @Test
    void should_get_the_Server_PublicKey() {
        mockServerCertificateProps();

        RsaPublicKeyDto rsaPublicKeyDto = encryptionService.getServerPublicKey();

        assertEquals(SERVER_CERTIFICATE_PEM, rsaPublicKeyDto.getPem());
    }

    @Test
    void should_generate_jwk_set() throws JOSEException {
        mockServerCertificateProps();

        X509Certificate cert = encryptionService.getServerCertificate();
        JWK jwk = JWK.parse(cert);
        JWKSet jwkset = new JWKSet(jwk.toPublicJWK());

    }

    @Test
    void should_get_the_server_certificate_expiration_date() {
        mockServerCertificateProps();

        LocalDateTime expirationDate = encryptionService.getServerCertificateExpirationDate();
        assertEquals(SERVER_CERTIFICATE_EXPIRATION_TIME, expirationDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
    }

    @Test
    void should_encrypt_and_decrypt() {
        mockServerCertificateProps();

        PublicKey publicKey = Rsa.toPublicKey(SERVER_CERTIFICATE_PEM);
        String encryptedTextBase64encoded = Rsa.encrypt(publicKey, TEXT.getBytes());

        byte[] decrypted = encryptionService.decryptWithServerPrivateKey(encryptedTextBase64encoded);

        assertEquals(TEXT, new String(decrypted));
    }

    /**
     * Test decryption of text encrypted client side using node forge. Encryption code:
     * pki.publicKeyFromPem(Settings.serverPublicKey).encrypt(text)
     */
    @Test
    void should_decrypt_random_bits() {
        mockServerCertificateProps();

        String encryptedWithNodeForge = "bpIKYWJUgO24P1wa5EvqfdDHeOx4dsWUC9A0svJOnGA=";
        String encrypted = "MRNDybWhFfLPN75E+x1NYO0xpDnmB4YoqBri/RcbBMqPFTCJ6AuULwu1/F9OEqQUsE7oTuWOpQ3169NEmG9Lw53lzkYoD9RrPsNLB0MNlNYcbBuy/pr7W3BpJdnQX+ufZF8hj3x8w3ycD9t7vPIT+cvb4ZZYaLXRqVaWMy80Qunw0auRz4QV4wxxdRVs3ST9pjfSWANkCtKvk1zTGspa/6QKNsEieIqcDmpTIGHm5EnJw7SJ3oPwOlbOWdqO4HpRHkTDhrqoUs0niDge92prxMIpv7nH/qNh37qhk6cEbifjn3y6lTqoxoNscZup7lNbecQQtWOqOzrzlonbDUMCQQ==";
        byte[] decrypted = encryptionService.decryptWithServerPrivateKey(encrypted);

        assertEquals(encryptedWithNodeForge, new String(decrypted));
    }

    @Test
    void should_get_the_message_text_decrypted() {
        String messageText = "some text";
        mockServerCertificateProps();
        Message message = mockMessage();

        SecretKey secretKey = generateAesKey();
        IvParameterSpec ivParameterSpec = generateIvParameterSpec();
        String iv = encodeToString(ivParameterSpec.getIV());

        message.setIv(iv);

        // AES encrypt text
        String encryptedText = Aes.gcmEncryptAndEncode(secretKey, ivParameterSpec.getIV(), messageText);
        message.setText(encryptedText);

        // Rsa encrypt AES key
        String encryptedKey =  encryptionService.encryptWithServerPublicKey(secretKey.getEncoded());
        message.setSymmetricKey(SymmetricKey.builder()
                .randomBits(encryptedKey)
                .encryptionMethod(SymmetricKey.EncryptionMethod.RSA_OAEP_E2E)
                .build());

        // Decrypt
        String decryptedMessageText = encryptionService.getDecryptedMessageText(message);

        assertEquals(messageText, decryptedMessageText);
    }

    @Test
    void should_get_the_message_templates_decrypted() {
        String messageTemplates = "some text";
        mockServerCertificateProps();
        Message message = mockMessage();

        SecretKey secretKey = generateAesKey();
        IvParameterSpec ivParameterSpec = generateIvParameterSpec();
        String iv = encodeToString(ivParameterSpec.getIV());

        message.setIv(iv);

        // AES encrypt text
        String encryptedText = Aes.gcmEncryptAndEncode(secretKey, ivParameterSpec.getIV(), messageTemplates);
        message.setTemplateVariables(encryptedText);

        // Rsa encrypt AES key
        String encryptedKey =  encryptionService.encryptWithServerPublicKey(secretKey.getEncoded());
        message.setSymmetricKey(SymmetricKey.builder()
                .randomBits(encryptedKey)
                .encryptionMethod(SymmetricKey.EncryptionMethod.RSA_OAEP_E2E)
                .build());

        // Decrypt
        String decryptedMessageTemplates = encryptionService.getDecryptedTemplateVariables(message);

        assertEquals(messageTemplates, decryptedMessageTemplates);
    }

    @Test
    void should_decryptWithOldServerPrivateKey() {
        PublicKey publicKey = Rsa.toPublicKey(OLD_SERVER_CERTIFICATE_PEM);
        String encryptedTextBase64encoded = Rsa.encrypt(publicKey, TEXT.getBytes());

        given(etrustexWebProperties.getOldServerCertificate()).willReturn(new ClassPathResource(OLD_SERVER_CERTIFICATE_PATH));
        given(etrustexWebProperties.getOldServerCertificateType()).willReturn(OLD_SERVER_CERTIFICATE_TYPE);
        given(etrustexWebProperties.getOldServerCertificateAlias()).willReturn(OLD_SERVER_CERTIFICATE_ALIAS);
        given(etrustexWebProperties.getOldServerCertificatePassword()).willReturn(OLD_SERVER_CERTIFICATE_PASSWORD);

        byte[] decrypted = encryptionService.decryptWithOldServerPrivateKey(encryptedTextBase64encoded);

        assertEquals(TEXT, new String(decrypted));
    }


    private void mockServerCertificateProps() {
        given(etrustexWebProperties.getServerCertificate()).willReturn(new ClassPathResource(SERVER_CERTIFICATE_PATH));
        given(etrustexWebProperties.getServerCertificateType()).willReturn(SERVER_CERTIFICATE_TYPE);
        given(etrustexWebProperties.getServerCertificateAlias()).willReturn(SERVER_CERTIFICATE_ALIAS);
        given(etrustexWebProperties.getServerCertificatePassword()).willReturn(SERVER_CERTIFICATE_PASSWORD);
    }

}
