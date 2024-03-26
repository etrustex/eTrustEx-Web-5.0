package eu.europa.ec.etrustex.web.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.etrustex.web.exchange.model.ServerValidationErrors;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ValidationTestUtils {

    private ValidationTestUtils() {
        throw new IllegalStateException("Utility class");
    }

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void checkValidationErrors(MockHttpServletResponse response, String... expectedErrors) throws UnsupportedEncodingException, JsonProcessingException {
        ServerValidationErrors serverValidationErrors = objectMapper.readValue(response.getContentAsString(), ServerValidationErrors.class);
        assertEquals(Arrays.stream(expectedErrors).sorted().collect(Collectors.toList()), serverValidationErrors.getErrors().stream().sorted().collect(Collectors.toList()));
    }
}
