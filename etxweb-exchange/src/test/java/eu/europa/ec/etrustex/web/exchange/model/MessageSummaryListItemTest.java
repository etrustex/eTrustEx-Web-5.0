/*
 * Copyright (c) 2018-2023. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package eu.europa.ec.etrustex.web.exchange.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings({"java:S112", "java:S100"}) /* Suppress Define and throw a dedicated exception instead of using a generic one & method names false positive. */
class MessageSummaryListItemTest {
    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void should_serialize() throws JsonProcessingException {
        MessageSummaryListItem messageSummaryListItem = MessageSummaryListItem.builder()
                .messageId(1L)
                .recipientEntity("r")
                .subject("s")
                .date(new Date())
                .build();

        String serialized = objectMapper.writeValueAsString(messageSummaryListItem);

        MessageSummaryListItem deserialized = objectMapper.readValue(serialized, MessageSummaryListItem.class);

        assertEquals(messageSummaryListItem, deserialized);
    }
}