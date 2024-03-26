package eu.europa.ec.etrustex.web.service.exchange;

import eu.europa.ec.etrustex.web.exchange.model.RecipientPreferencesSpec;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.RecipientPreferences;

public interface RecipientPreferencesService {

    RecipientPreferences save(RecipientPreferences recipientPreferences);

    RecipientPreferences create(RecipientPreferencesSpec recipientPreferencesSpec);

    RecipientPreferences findById(long recipientPreferencesId);

    RecipientPreferences update(long recipientPreferencesId, RecipientPreferencesSpec recipientPreferencesSpec);

    void delete(RecipientPreferences recipientPreferences);
}
