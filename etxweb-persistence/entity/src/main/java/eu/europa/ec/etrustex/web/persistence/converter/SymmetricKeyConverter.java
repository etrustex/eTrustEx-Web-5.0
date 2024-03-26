package eu.europa.ec.etrustex.web.persistence.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.util.exchange.model.keys.SymmetricKey;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;

@Converter(autoApply = true)
public class SymmetricKeyConverter implements AttributeConverter<SymmetricKey, String> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(SymmetricKey meta) {
        if (meta == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(meta);
        } catch (JsonProcessingException ex) {
            throw new EtxWebException("Error converting between symmetric key object and JSON", ex);
        }
    }

    @Override
    public SymmetricKey convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        dbData = SymmetricKeyExtractor.extractSymmetricKey(dbData);
        try {
            return objectMapper.readValue(dbData, SymmetricKey.class);
        } catch (IOException ex) {
            throw new EtxWebException("Error converting between JSON and symmetric key object", ex);
        }
    }
}
