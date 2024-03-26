package eu.europa.ec.etrustex.web.exchange.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static eu.europa.ec.etrustex.web.util.test.PrivateConstructorTester.testPrivateConstructor;

@ExtendWith(MockitoExtension.class)
class UrlTemplatesTest {
    @Test
    void constructor_should_be_private() throws NoSuchMethodException {
        testPrivateConstructor(UrlTemplates.class.getDeclaredConstructor());
    }
}
