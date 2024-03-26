/*
 * Copyright (c) 2023. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package eu.europa.ec.etrustex.web.persistence.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.util.exchange.model.keys.SymmetricKey;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings({"java:S112", "java:S100"}) /* Suppress Define and throw a dedicated exception instead of using a generic one & method names false positive. */
class SymmetricKeyConverterTest {
    private final SymmetricKeyConverter converter = new SymmetricKeyConverter();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SymmetricKey symmetricKey = SymmetricKey.builder().build();

    @Test
    void should_convert_to_database_column() throws JsonProcessingException {
        String json = converter.convertToDatabaseColumn(symmetricKey);

        assertEquals(objectMapper.writeValueAsString(symmetricKey), json);

        assertNull(converter.convertToDatabaseColumn(null));
    }

    @Test
    void should_convert_to_entity_attribute() throws JsonProcessingException {
        String json =  objectMapper.writeValueAsString(symmetricKey);
        SymmetricKey converted = converter.convertToEntityAttribute(json);

        assertEquals(symmetricKey, converted);

        assertNull(converter.convertToEntityAttribute(null));
        assertNull(converter.convertToEntityAttribute(""));
    }


    @Test
    void should_throw_convert_to_entity_attribute_with_wrong_json() {
        assertThrows(EtxWebException.class, () -> converter.convertToEntityAttribute("wrong_json"));
    }
}