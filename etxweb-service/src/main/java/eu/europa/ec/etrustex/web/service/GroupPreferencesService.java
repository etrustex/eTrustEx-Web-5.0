package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.persistence.entity.exchange.RecipientPreferences;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.SenderPreferences;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.service.validation.model.GroupSpec;

import java.util.Optional;

public interface GroupPreferencesService {

    Optional<RecipientPreferences> getRecipientPreferencesIfChanged(GroupSpec spec, Group group);

    Optional<SenderPreferences> getSenderPreferencesIfChanged(GroupSpec spec, Group group);

    RecipientPreferences getRecipientPreferences(GroupSpec spec);
    SenderPreferences getSenderPreferences(GroupSpec spec, Group parent);

    void delete(SenderPreferences senderPreferences);

    Group disableEncryption(Group entity);
}
