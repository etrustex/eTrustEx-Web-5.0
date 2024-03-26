package eu.europa.ec.etrustex.web.persistence.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.etrustex.web.util.exchange.model.AttachmentSpec;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Converter(autoApply = true)
public class AttachmentSpecConverter implements AttributeConverter<List<AttachmentSpec>, String> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<AttachmentSpec> attribute) {
        if (attribute == null) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException ex) {
            throw new EtxWebException("Error converting between attachment spec object and JSON", ex);
        }
    }

    @Override
    public List<AttachmentSpec> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }

        try {
            return Arrays.asList(objectMapper.readValue(dbData, AttachmentSpec[].class));
        } catch (IOException ex) {
            throw new EtxWebException("Error converting between JSON and attachment spec object", ex);
        }
    }
}
