package eu.europa.ec.etrustex.web.exchange.validation;

import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.util.validation.ByteLengthValidator;
import eu.europa.ec.etrustex.web.util.validation.CheckByteLength;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.Payload;
import java.lang.annotation.Annotation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(MockitoExtension.class)
class ByteLengthValidatorTest {

    ByteLengthValidator byteLengthValidator;

    @BeforeEach
    public void init() {
        byteLengthValidator = new ByteLengthValidator();
        byteLengthValidator.initialize(new CheckByteLength() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }

            @Override
            public String message() {
                return null;
            }

            @Override
            public Class<?>[] groups() {
                throw new EtxWebException("not implemented!");
            }

            @Override
            public Class<? extends Payload>[] payload() {
                throw new EtxWebException("not implemented!");
            }

            @Override
            public int value() {
                return 11;
            }
        });
    }

    @Test
    void should_pass_validation() {
        assertTrue(byteLengthValidator.isValid("shortString", null));
    }

    @Test
    void should_pass_validation_with_null_parameter() {
        assertTrue(byteLengthValidator.isValid(null, null));
    }

    @Test
    void should_fail_validation_with_multibyte_characters() {
        assertFalse(byteLengthValidator.isValid("shörtStrìng", null));
    }

    @Test
    void should_fail_validation() {
        assertFalse(byteLengthValidator.isValid("a longer string", null));
    }
}
