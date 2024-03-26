package eu.europa.ec.etrustex.web.integration;

import eu.europa.ec.etrustex.web.integration.utils.MessageResult;
import eu.europa.ec.etrustex.web.integration.utils.MessageUtils;
import eu.europa.ec.etrustex.web.persistence.entity.NewServerCertificate;
import eu.europa.ec.etrustex.web.persistence.repository.NewServerCertificateRepository;
import eu.europa.ec.etrustex.web.service.jobs.certificate.NewServerCertificateJob;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.mockNewServerCertificate;
import static org.awaitility.Awaitility.await;

class NewServerCertificateIntegrationTest extends AbstractControllerTest {
    @Autowired
    private NewServerCertificateJob newServerCertificateJob;
    @Autowired
    private NewServerCertificateRepository newServerCertificateRepository;
    @Autowired
    private MessageUtils messageUtils;


    @AfterEach
    public void cleanUp() {
        messageUtils.cleanUp();
    }


    @Test
    void integrationTest() throws Exception {
        initConfigurations();
        happyFlows();
    }

    private void initConfigurations() throws Exception {
        int numberOfMessagesToCreate = 100;
        NewServerCertificate newServerCertificate = mockNewServerCertificate(true, false, false);
        newServerCertificateRepository.save(newServerCertificate);

        MessageResult messageResult = messageUtils.createMessageWithNewConfigurations(businessAdminUserDetails, abstractTestUser, 1, false, true);

        for (int i = 0; i < numberOfMessagesToCreate; i++) {
            messageUtils.createAndSendMessage(messageResult.getSenderUserDetails(), messageResult.getRecipientEntities().get(0).getId());
        }

        await()
                .atMost(60, TimeUnit.SECONDS)
                .until(() -> messageUtils.getTotalNumberOfMessages() == numberOfMessagesToCreate + 1);
    }

    private void happyFlows() {
        newServerCertificateJob.runNewCertificateTask();
    }
}
