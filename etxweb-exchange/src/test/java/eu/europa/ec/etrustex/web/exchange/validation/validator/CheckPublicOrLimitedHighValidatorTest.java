package eu.europa.ec.etrustex.web.exchange.validation.validator;

import eu.europa.ec.etrustex.web.util.cia.Confidentiality;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class CheckPublicOrLimitedHighValidatorTest {

    private final PublicOrLimitedHighValidator publicOrLimitedHighValidator = new PublicOrLimitedHighValidator();

    @Test
    void should_pas_validation_with_public_or_limited_high() {
        assertTrue(publicOrLimitedHighValidator.isValid(Confidentiality.PUBLIC, null));
        assertTrue(publicOrLimitedHighValidator.isValid(Confidentiality.LIMITED_HIGH, null));
    }

    @Test
    void should_fail_for_any_other_than_public_or_limited_high() {
        Arrays.stream(Confidentiality.values())
                .filter(confidentiality -> confidentiality != Confidentiality.PUBLIC && confidentiality != Confidentiality.LIMITED_HIGH)
                .forEach(confidentiality -> assertFalse(publicOrLimitedHighValidator.isValid(confidentiality, null)));
    }
}
