package eu.europa.ec.etrustex.web.service.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonUtil {
    private JsonUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static JsonNode readTree(String json) {
        try {
            return new ObjectMapper().readTree(json);
        } catch (IOException e) {
            return null;
        }
    }


}
