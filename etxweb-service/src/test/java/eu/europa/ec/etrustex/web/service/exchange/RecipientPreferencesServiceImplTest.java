package eu.europa.ec.etrustex.web.service.exchange;

import eu.europa.ec.etrustex.web.util.cia.Confidentiality;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.exchange.model.RecipientPreferencesSpec;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.RecipientPreferences;
import eu.europa.ec.etrustex.web.persistence.repository.exchange.RecipientPreferencesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecipientPreferencesServiceImplTest {

    private static final String PK = "not really a key";
    private static final Long RECIPIENT_PREFERENCES_ID = 1234L;

    @Mock
    RecipientPreferencesRepository recipientPreferencesRepository;
    private RecipientPreferencesService recipientPreferencesService;

    private RecipientPreferences recipientPreferences;

    @BeforeEach
    public void init() {
        recipientPreferencesService = new RecipientPreferencesServiceImpl(recipientPreferencesRepository);
        recipientPreferences = RecipientPreferences.builder()
                .id(RECIPIENT_PREFERENCES_ID)
                .publicKey(PK)
                .build();

    }

    @Test
    void should_create_a_new_recipient_preferences_entry() {
        when(recipientPreferencesRepository.save(any(RecipientPreferences.class))).thenReturn(recipientPreferences);

        RecipientPreferencesSpec recipientPreferencesSpec = RecipientPreferencesSpec.builder()
                .confidentiality(Confidentiality.LIMITED_HIGH)
                .publicKey(PK)
                .build();

        RecipientPreferences recipientPref = recipientPreferencesService.create(recipientPreferencesSpec);
        assertThat(recipientPref.getId()).isNotNull();
    }

    @Test
    void should_update_an_existing_recipient_preferences_entry() {
        when(recipientPreferencesRepository.save(any(RecipientPreferences.class))).thenReturn(recipientPreferences);

        RecipientPreferencesSpec recipientPreferencesSpec = RecipientPreferencesSpec.builder().build();
        RecipientPreferences updated = recipientPreferencesService.update(RECIPIENT_PREFERENCES_ID, recipientPreferencesSpec);

        assertThat(updated.getId()).isNotNull();
        assertEquals(recipientPreferences.getPublicKey(), updated.getPublicKey());
    }

    @Test
    void should_get_the_recipient_preferences() {
        given(recipientPreferencesRepository.findById(anyLong())).willReturn(Optional.of(RecipientPreferences.builder().id(RECIPIENT_PREFERENCES_ID).build()));

        RecipientPreferences recipientPref = recipientPreferencesService.findById(RECIPIENT_PREFERENCES_ID);
        assertThat(recipientPref.getId()).isNotNull();

    }

    @Test
    void should_throw_exception_when_recipient_preferences_are_not_found() {
        given(recipientPreferencesRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(EtxWebException.class, () -> recipientPreferencesService.findById(RECIPIENT_PREFERENCES_ID));

    }
}
