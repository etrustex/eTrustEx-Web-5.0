package eu.europa.ec.etrustex.web.service.certificate;

import eu.europa.ec.etrustex.web.exchange.model.CertificateUpdateDto;
import eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummary;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.RecipientPreferences;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.persistence.repository.MessageSummaryRepository;
import eu.europa.ec.etrustex.web.persistence.repository.redirect.CertificateUpdateRedirectRepository;
import eu.europa.ec.etrustex.web.service.EncryptionService;
import eu.europa.ec.etrustex.web.service.redirect.RedirectService;
import eu.europa.ec.etrustex.web.service.security.GroupService;
import eu.europa.ec.etrustex.web.service.security.UserService;
import eu.europa.ec.etrustex.web.service.validation.model.UpdateCertificateSpec;
import eu.europa.ec.etrustex.web.util.crypto.Aes;
import eu.europa.ec.etrustex.web.util.crypto.Rsa;
import eu.europa.ec.etrustex.web.util.exchange.model.keys.SymmetricKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.util.List;

import static eu.europa.ec.etrustex.web.util.crypto.Aes.generateAesKey;
import static eu.europa.ec.etrustex.web.util.crypto.Aes.generateIvParameterSpec;
import static eu.europa.ec.etrustex.web.util.crypto.Base64.encodeToString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CertificateServiceImplTest {

    private static final String PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----\n" +
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAl0RG0bO26Ha1k0Db2xMl\n" +
            "0xIGnAMKSff0XWn6DnLi7TjfBjj+38bSpgNXV3ZitOYMy8HLJB723Bj03Ii7ARmF\n" +
            "YWz/3F80fK8ujgoU10+OtuT0XpS3NE4EkOIV4zxb1+ziTb4iZHV0nMsGN9BBUuNj\n" +
            "w3QUFbKASROrFiIYwJV2l3F3+Ho6m9z9E6XCOi1PjzszXKkeP2TDVHJuUZ1041pT\n" +
            "Y9K3+itf6uWm1An1ma3nleQwzRTnHd78XxemEFpp6UI7O/8zThi7npjn1eM7oFV9\n" +
            "QMHR6BSqG6XoPZmM7OGi5ELIxnV7Q0NLu/9Y4vtcZ82mAaRcG5P6j6CYXqmUoNdw\n" +
            "PQIDAQAB\n" +
            "-----END PUBLIC KEY-----\n";


    private static final String PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----\n" +
            "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCXREbRs7bodrWT\n" +
            "QNvbEyXTEgacAwpJ9/RdafoOcuLtON8GOP7fxtKmA1dXdmK05gzLwcskHvbcGPTc\n" +
            "iLsBGYVhbP/cXzR8ry6OChTXT4625PRelLc0TgSQ4hXjPFvX7OJNviJkdXScywY3\n" +
            "0EFS42PDdBQVsoBJE6sWIhjAlXaXcXf4ejqb3P0TpcI6LU+POzNcqR4/ZMNUcm5R\n" +
            "nXTjWlNj0rf6K1/q5abUCfWZreeV5DDNFOcd3vxfF6YQWmnpQjs7/zNOGLuemOfV\n" +
            "4zugVX1AwdHoFKobpeg9mYzs4aLkQsjGdXtDQ0u7/1ji+1xnzaYBpFwbk/qPoJhe\n" +
            "qZSg13A9AgMBAAECggEADhyehr2/WJyKf/xTBnMY2kIaP7/A2/m34GccnvB03zCp\n" +
            "DcmPbYz85eP16ADxYHL9ypx/uRTybsIdRd2zpHyfBuHGVObE3MTzEOk73IJdjA58\n" +
            "Bb7WAJkur92HsTVWVCSTUDFmv/x9Lot6Pwa8sWDpU7imjweiFC8zjNMNgBnxgFs+\n" +
            "Y+3gmg4ByS8Fsi7AivdgcDOXELTxSFRMRq1aTm9X3BQyN3vdDIdr5qe0+mKPkZjY\n" +
            "N7LfbPWvWWKxE4MH13CjDUtm7YWLqI8+FC/szDyOmqv6Ja0ZSwjw37mZdEbSLG5+\n" +
            "g1yfpuU06pkyRgDbftozu0TmtGgPvlFnXUTEhbyowQKBgQDnhwwejx4WmXybhY27\n" +
            "BTrnfWTkLMErt1DNJDagznipRcKfdEOM4GvhKgYaMOEnKJ07xUzmk5q2t23eJHQa\n" +
            "NI6A8pgdJQmxnUQb+XR82PTLgIMqQH9cJM+wtVhJcYIwC9xrAIobBDWVbA5ZkiN9\n" +
            "oUlVTHwTKY83hKL+0E5PevY0GQKBgQCnQW9CfYVgRbRLQB6AyaYMFAXenSX6thqn\n" +
            "VVy8+kQiD31IhVDmCLKIBtFgOH/2RDnmsA63m44JO2H+mL04ksQ0TVHOhahnysb+\n" +
            "dxq15TeKrPWuXlcgkgBX1DB3PWRe3Ln8OC4TeY2tnHohbrEeWRQTg6Ojan3YzM5M\n" +
            "fC3OjD9BxQKBgQCxg9G+/GxNV5E6jMOGSRRIuNPKPqY9JmhFYSahVK0e2+XE7Bd+\n" +
            "w/LyyI21NxTqOTGUykmT4EgXH7yVPc3m6bT5GQeYEPXFwJaf/DtcHdxSy03F7322\n" +
            "4GJ9Ug1HAkiXHgD56Yf5/tg2oIrAElQz1p2rUOEwsASmxINUa9IfDmAGwQKBgCfX\n" +
            "0qVIOYmhkT2L3LhZURD3hB5v2ShMUTUjhKAQDkL+5Z1tBMSHtd/pn98IFpOX1SM7\n" +
            "NYZPrBpO5x07TXg4njzHXoGQHXn8OENOJDLIk96ZLT2Ig8DihuplRdZh7ZMbicKn\n" +
            "r3I8Np/w8rccI11TYxDy/vxi1e/behmGS3M0vazxAoGBAL62YdAxymhVIF8PsOvI\n" +
            "K66Wx+22XaqeJczVOkkt0XP5rc65H1lwrjYVb49Uoc7qXmzIheOfv+PTWJaw/UQg\n" +
            "JLJ1EREUN5nq5gzSvlIPbwuK573n9wmnrUz+PN64m1QxvTChZ187sRNPBp5rStVV\n" +
            "UiYTEc/uwfKi3Ce42ZyuRxxc\n" +
            "-----END PRIVATE KEY-----";


    @Mock
    private UserService userService;
    @Mock
    private GroupService groupService;
    @Mock
    private RedirectService redirectService;
    @Mock
    private MessageSummaryRepository messageSummaryRepository;
    @Mock
    private EncryptionService encryptionService;
    @Mock
    private CertificateUpdateRedirectRepository certificateUpdateRedirectRepository;

    private CertificateService certificateService;

    @BeforeEach
    public void setUp() {
        certificateService = new CertificateServiceImpl(userService, groupService, redirectService,
                messageSummaryRepository,encryptionService, certificateUpdateRedirectRepository);
    }

    @Test
    void should_generate_certificate_update_link() {
        String link = "localhost:7003/etrustex/web/goto/sjsjkb54sdf5sdf";
        User user = EntityTestUtils.mockUser();
        Group group = EntityTestUtils.mockGroup();

        given(userService.findByEcasId(any())).willReturn(user);
        given(groupService.findById(any())).willReturn(group);
        given(redirectService.createPermalink(any())).willReturn(link);

        UpdateCertificateSpec updateCertificateSpec = UpdateCertificateSpec.builder()
                .entityId(group.getId())
                .euLoginId(user.getEcasId())
                .build();


        String generatedLink = certificateService.generateCertificateUpdateLink(updateCertificateSpec);
        assertTrue(generatedLink.contains("/goto/"));

    }

    @Test
    void should_update_compromised_messages() {
        List<MessageSummary> messageSummaries = EntityTestUtils.mockMessageSummaries(1);
        SecretKey secretKey = generateAesKey();
        IvParameterSpec ivParameterSpec = generateIvParameterSpec();
        String iv = encodeToString(ivParameterSpec.getIV());
        String encryptedRandomBits = Rsa.encrypt(PUBLIC_KEY, secretKey.getEncoded());
        SymmetricKey symmetricKey = SymmetricKey.builder()
                .randomBits(encryptedRandomBits)
                .encryptionMethod(SymmetricKey.EncryptionMethod.RSA_OAEP_E2E)
                .build();

        messageSummaries.forEach(messageSummary -> {
            messageSummary.setSymmetricKey(symmetricKey);
            messageSummary.getRecipient().setRecipientPreferences(RecipientPreferences.builder().publicKey(PUBLIC_KEY).build());
        });

        String encryptedPrivateKey = Aes.gcmEncryptAndEncode(secretKey, ivParameterSpec.getIV(), PRIVATE_KEY);

        given(messageSummaryRepository.findByRecipientIdAndAndPublicKeyHashValue(any(), any())).willReturn(messageSummaries);
        given(encryptionService.decryptWithServerPrivateKey(anyString())).willReturn(secretKey.getEncoded());

        CertificateUpdateDto certificateUpdateDto = CertificateUpdateDto.builder()
                .randomBits(encryptedRandomBits)
                .iv(iv)
                .encryptedPrivateKey(encryptedPrivateKey)
                .build();

        certificateService.updateCompromisedMessages(certificateUpdateDto, null);

        verify(messageSummaryRepository, times(1)).save(any());
    }

    @Test
    void should_check_redirect_link() {
        given(certificateUpdateRedirectRepository.existsByGroupIdAndGroupIdentifierAndUserId(any(), any(), any())).willReturn(Boolean.TRUE);
        assertTrue(certificateService.isValidRedirectLink(any(), any(), any()));
    }
}
