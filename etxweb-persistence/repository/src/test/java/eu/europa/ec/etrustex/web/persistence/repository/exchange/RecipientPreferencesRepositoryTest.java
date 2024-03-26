package eu.europa.ec.etrustex.web.persistence.repository.exchange;

import eu.europa.ec.etrustex.web.util.cia.Availability;
import eu.europa.ec.etrustex.web.util.cia.Confidentiality;
import eu.europa.ec.etrustex.web.util.cia.Integrity;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.RecipientPreferences;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RecipientPreferencesRepositoryTest {
    @Autowired
    RecipientPreferencesRepository recipientPreferencesRepository;

    @Test
    void should_save_and_retrieve_a_recipientPreferences() {
        RecipientPreferences recipientPreferences = recipientPreferencesRepository.save(
                RecipientPreferences.builder()
                        .integrity(Integrity.MODERATE)
                        .availability(Availability.MODERATE)
                        .confidentiality(Confidentiality.PUBLIC)
                        .build()
        );

        RecipientPreferences retrieved = recipientPreferencesRepository.findById(recipientPreferences.getId())
                .orElseThrow(() -> new RuntimeException("Impossible to retrieve the recipientPreferences"));
        assertEquals(retrieved, recipientPreferences);
    }

}
