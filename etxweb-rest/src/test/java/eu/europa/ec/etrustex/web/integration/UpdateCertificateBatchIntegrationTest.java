package eu.europa.ec.etrustex.web.integration;

import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.entity.NewServerCertificate;
import eu.europa.ec.etrustex.web.persistence.entity.UserProfile;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.persistence.repository.CredentialsRepository;
import eu.europa.ec.etrustex.web.persistence.repository.NewServerCertificateRepository;
import eu.europa.ec.etrustex.web.service.EncryptionService;
import eu.europa.ec.etrustex.web.util.crypto.Aes;
import eu.europa.ec.etrustex.web.util.crypto.Rsa;
import eu.europa.ec.etrustex.web.util.exchange.model.keys.SymmetricKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.PublicKey;

import static eu.europa.ec.etrustex.web.integration.RestTestUtils.TEXT_BODY;
import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.mockNewServerCertificate;
import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.mockUserProfile;
import static eu.europa.ec.etrustex.web.service.jobs.certificate.batch.config.UpdateCertificateBatchConfig.UPDATE_CERTIFICATE_JOB_NAME;
import static eu.europa.ec.etrustex.web.util.crypto.Base64.encodeToString;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UpdateCertificateBatchIntegrationTest extends AbstractControllerTest {
    @Autowired
    private NewServerCertificateRepository newServerCertificateRepository;

    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    private CredentialsRepository credentialsRepository;
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    @Qualifier("encryptWithNewCertificateJob")
    private Job encryptWithNewCertificateJob;

    private int expectedFailures = 0;


    /*
     *  Execution time in minutes. Multi-thread pool size: 64
         Messages | page size & chunk size 128 | page size & chunk size 256
           1000                 0
          10000                 3                           3
         100000
     */
    @BeforeEach
    void init() {
        NewServerCertificate newServerCertificate = mockNewServerCertificate(true, false, false);
        newServerCertificateRepository.save(newServerCertificate);

        User user = operatorUserDetails.getUser();
        Group entity = abstractTestEntity;

        for (int i = 0; i < 50; i++) {
            boolean shouldFailToDecrypt = sendMessage(user, entity, i);

            if (shouldFailToDecrypt) {
                expectedFailures++;
            }
        }
    }

    @Test
    void should_run_job() throws Exception {
        JobExecution jobExecution = jobLauncher.run(encryptWithNewCertificateJob, new JobParameters());
        ExitStatus status = jobExecution.getExitStatus();

        long failures = messageRepository.countBySymmetricKeyIsNotNullAndIvIsNotNullAndUpdatedWithNewCertificateIsFalse();

        assertEquals(expectedFailures, failures);

        assertEquals(UPDATE_CERTIFICATE_JOB_NAME, encryptWithNewCertificateJob.getName());
        assertEquals(ExitStatus.COMPLETED.getExitCode(), status.getExitCode());
    }


    private boolean sendMessage(User user, Group entity, int i) {
        SecretKey secretKey = Aes.generateAesKey();
        IvParameterSpec ivParameterSpec = Aes.generateIvParameterSpec();
        String iv = encodeToString(ivParameterSpec.getIV());
        byte[] encryptedMessageTextBytes = Aes.gcmEncrypt(secretKey, ivParameterSpec.getIV(), TEXT_BODY.getBytes());
        String encryptedText = encodeToString(encryptedMessageTextBytes);
        PublicKey publicKey = encryptionService.getOldServerKeyPair().getPublic();
        String encryptedRandomBitsWithOldKey = Rsa.encrypt(publicKey, secretKey.getEncoded());

        boolean shouldFailToDecrypt = false;

        if (i % 10 == 0) {
            encryptedRandomBitsWithOldKey = "should fail";
            shouldFailToDecrypt = true;
        }


        SymmetricKey symmetricKey = SymmetricKey.builder()
                .randomBits(encryptedRandomBitsWithOldKey)
                .encryptionMethod(SymmetricKey.EncryptionMethod.RSA_OAEP_E2E)
                .build();

        UserProfile userProfile = mockUserProfile(user, entity);

        Message message = Message.builder()
                .senderUserProfile(userProfile)
                .senderUserName(userProfile.getUser().getName())
                .senderGroup(userProfile.getGroup())
                .symmetricKey(symmetricKey)
                .iv(iv)
                .updatedWithNewCertificate(false)
                .text(encryptedText)
                .build();

        messageRepository.save(message);

        return shouldFailToDecrypt;
    }

}
