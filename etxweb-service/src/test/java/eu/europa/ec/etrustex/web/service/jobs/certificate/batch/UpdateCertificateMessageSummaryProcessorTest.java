/*
 * Copyright (c) 2023. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package eu.europa.ec.etrustex.web.service.jobs.certificate.batch;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummary;
import eu.europa.ec.etrustex.web.service.EncryptionService;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.util.exchange.model.keys.SymmetricKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.mockGroup;
import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.mockMessage;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"java:S112", "java:S100"}) /* Suppress Define and throw a dedicated exception instead of using a generic one & method names false positive. */
class UpdateCertificateMessageSummaryProcessorTest {
    private static final String ENCRYPTED_SYMMETRIC_KEY = "QyV10wiToV4ZgOUhXtY20JLXwsLGLOCxGd3/tXlFVO0=";

    @Mock
    private EncryptionService encryptionService;

    private ListAppender<ILoggingEvent> logWatcher;

    private UpdateCertificateMessageSummaryProcessor updateCertificateMessageSummaryProcessor;

    @BeforeEach
    void init() {
        updateCertificateMessageSummaryProcessor = new UpdateCertificateMessageSummaryProcessor(encryptionService);
    }


    @Test
    void should_process() {
        MessageSummary messageSummary = MessageSummary.builder()
                .symmetricKey(new SymmetricKey(ENCRYPTED_SYMMETRIC_KEY, SymmetricKey.EncryptionMethod.ENCRYPTED))
                .build();

        SymmetricKey updatedSymmetricKey = messageSummary.getSymmetricKey();
        updatedSymmetricKey.setRandomBits("JaV10wiToV4ZgOUhXtY20JLXwsLGLOCxGd3/tXlFV99=");

        given(encryptionService.encryptWithNewCertificate(messageSummary.getSymmetricKey())).willReturn(updatedSymmetricKey);

        MessageSummary processed = updateCertificateMessageSummaryProcessor.process(messageSummary);

        assertNotNull(processed);
        assertTrue(processed.isProcessed());
        assertEquals(updatedSymmetricKey.getRandomBits(), processed.getSymmetricKey().getRandomBits());
    }

    @Test
    void should_not_process_if_key_is_RSA_OAEP_E2E() {
        MessageSummary messageSummary = MessageSummary.builder()
                .symmetricKey(new SymmetricKey(ENCRYPTED_SYMMETRIC_KEY, SymmetricKey.EncryptionMethod.RSA_OAEP_E2E))
                .build();

        MessageSummary processed = updateCertificateMessageSummaryProcessor.process(messageSummary);

        assertNotNull(processed);
        assertFalse(processed.isProcessed());
        assertEquals(messageSummary.getSymmetricKey().getRandomBits(), processed.getSymmetricKey().getRandomBits());
    }

    @Test
    void should_not_process_if_key_is_null() {
        MessageSummary messageSummary = MessageSummary.builder()
                .build();

        MessageSummary processed = updateCertificateMessageSummaryProcessor.process(messageSummary);

        assertNotNull(processed);
        assertFalse(processed.isProcessed());
        assertNull(processed.getSymmetricKey());
    }

    @Test
    void should_not_process_if_key_is_blank() {
        MessageSummary messageSummary = MessageSummary.builder()
                .symmetricKey(new SymmetricKey("", SymmetricKey.EncryptionMethod.RSA_OAEP_SERVER))
                .build();

        MessageSummary processed = updateCertificateMessageSummaryProcessor.process(messageSummary);

        assertNotNull(processed);
        assertFalse(processed.isProcessed());
        assertTrue(processed.getSymmetricKey().getRandomBits().isEmpty());
    }

    @Test
    void should_log_error_if_encryption_fails() {
        MessageSummary messageSummary = MessageSummary.builder()
                .message(mockMessage())
                .recipient(mockGroup())
                .symmetricKey(new SymmetricKey(ENCRYPTED_SYMMETRIC_KEY, SymmetricKey.EncryptionMethod.ENCRYPTED))
                .build();

        initLogWatcher();

        given(encryptionService.encryptWithNewCertificate(messageSummary.getSymmetricKey())).willThrow(EtxWebException.class);

        MessageSummary processed = updateCertificateMessageSummaryProcessor.process(messageSummary);


        int logSize = logWatcher.list.size();
        assertEquals(Level.ERROR, logWatcher.list.get(logSize - 1).getLevel());
        assertThat(logWatcher.list.get(logSize - 1).getFormattedMessage()).contains("Error");
        assertThat(logWatcher.list.get(logSize - 1).getFormattedMessage()).contains("" + messageSummary.getMessage().getId());
        assertThat(logWatcher.list.get(logSize - 1).getFormattedMessage()).contains("" + messageSummary.getRecipient().getId());

        assertNotNull(processed);
        assertFalse(processed.isProcessed());
        assertEquals(messageSummary.getSymmetricKey().getRandomBits(), processed.getSymmetricKey().getRandomBits());

        destroyLogWatcher();
    }

    private void initLogWatcher() {
        logWatcher = new ListAppender<>();
        logWatcher.start();
        ((Logger) LoggerFactory.getLogger(UpdateCertificateMessageSummaryProcessor.class)).addAppender(logWatcher);
    }

    private void destroyLogWatcher() {
        ((Logger) LoggerFactory.getLogger(UpdateCertificateMessageSummaryProcessor.class)).detachAndStopAllAppenders();
    }
}