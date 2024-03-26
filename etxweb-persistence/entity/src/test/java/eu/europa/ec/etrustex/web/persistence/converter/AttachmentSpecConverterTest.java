/*
 * Copyright (c) 2018-2023. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package eu.europa.ec.etrustex.web.persistence.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.util.exchange.model.AttachmentSpec;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings({"java:S112", "java:S100"}) /* Suppress Define and throw a dedicated exception instead of using a generic one & method names false positive. */
class AttachmentSpecConverterTest {
    private final AttachmentSpecConverter converter = new AttachmentSpecConverter();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AttachmentSpec[] attachmentSpecs = new AttachmentSpec[]{AttachmentSpec.builder()
            .id(1L)
            .name("name")
            .build()};

    @Test
    void should_convert_to_database_column() throws JsonProcessingException {
        String json = converter.convertToDatabaseColumn(Arrays.asList(attachmentSpecs));

        assertEquals(objectMapper.writeValueAsString(attachmentSpecs), json);

        assertNull(converter.convertToDatabaseColumn(null));
    }

    @Test
    void should_convert_to_entity_attribute() throws JsonProcessingException {
        String json =  objectMapper.writeValueAsString(attachmentSpecs);
        List<AttachmentSpec> converted = converter.convertToEntityAttribute(json);

        assertEquals(attachmentSpecs[0], converted.get(0));

        assertNull(converter.convertToEntityAttribute(null));
        assertNull(converter.convertToEntityAttribute(""));
    }


    @Test
    void should_throw_convert_to_entity_attribute_with_wrong_json() {
        assertThrows(EtxWebException.class, () -> converter.convertToEntityAttribute("wrong_json"));
    }
}