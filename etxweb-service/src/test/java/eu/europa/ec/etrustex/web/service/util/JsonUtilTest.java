package eu.europa.ec.etrustex.web.service.util;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;

import static eu.europa.ec.etrustex.web.util.test.PrivateConstructorTester.testPrivateConstructor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonUtilTest {

    @Test
    void testConstructorIsPrivate() throws NoSuchMethodException {
        testPrivateConstructor(JsonUtil.class.getDeclaredConstructor());
    }

    @Test
    void should_read_the_list_of_attachments_as_an_array() {
        String json = "[" +
                "{\"id\":\"f5eb81db-f50e-444c-a38b-41d3d8714b10\",\"byteLength\":41929,\"name\":\"EDMA-PDF000001 (7).pdf\",\"type\":\"application/pdf\",\"iv\":\"f4TIbABmTyOPpG1RyiSBjg==\",\"path\":\"\",\"checkSum\":\"E804EC24775501E43404D39213B248BAEC997B8FE5980D68941026BEE1B520AD74B457DFF2BF69721D918AA75AEAE916719A281BCB6D78309E56EB6924525BFA\"}," +
                "{\"id\":\"744ca511-b535-43da-9873-047341dac821\",\"byteLength\":14134,\"name\":\"EDMA-PDF000003 (2).pdf\",\"type\":\"application/pdf\",\"iv\":\"dTJ8KjwDJGjx12atS5vJOQ==\",\"path\":\"\",\"checkSum\":\"00AB56C30E75A32E782F4D2F4B57BE726A9DCCE9455BCE01AB70281DB0D0C70D49C2351B0C08DDC439FAC30039B22564C443C98FB4CCEFFB10932BC19968BB0A\"}" +
                "]";
        JsonNode array = JsonUtil.readTree(json);
        assertThat(array).isNotNull();
        boolean isArray = array.isArray();
        assertTrue(isArray);
    }

    @Test
    void should_return_null_with_wrong_json() {
        String noJson = "WRONG!!!";
        JsonNode array = JsonUtil.readTree(noJson);
        assertThat(array).isNull();
    }
}
