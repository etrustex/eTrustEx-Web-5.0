package eu.europa.ec.etrustex.web.service.validation.validator;

import eu.europa.ec.etrustex.web.persistence.entity.exchange.RecipientPreferences;
import eu.europa.ec.etrustex.web.persistence.repository.exchange.RecipientPreferencesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class RecipientPreferencesExistsValidatorTest {

    @Mock
    RecipientPreferencesRepository recipientPreferencesRepository;

    private RecipientPreferencesExistsValidator recipientPreferencesExistsValidator;

    @BeforeEach
    public void init() {
        recipientPreferencesExistsValidator = new RecipientPreferencesExistsValidator(recipientPreferencesRepository);
    }

    @Test
    void should_pass_validation_with_null() {
        assertTrue(recipientPreferencesExistsValidator.isValid(null, null));
    }

    @Test
    void should_pass_validation_when_the_recipient_preferences_exists() {
        given(recipientPreferencesRepository.findById(any(Long.class))).willReturn(Optional.of(new RecipientPreferences()));
        assertTrue(recipientPreferencesExistsValidator.isValid(1234L, null));
    }

    @Test
    void should_fail_validation_when_the_recipient_preferences_does_not_exist() {
        given(recipientPreferencesRepository.findById(any(Long.class))).willReturn(Optional.empty());
        assertFalse(recipientPreferencesExistsValidator.isValid(1234L, null));
    }
}
