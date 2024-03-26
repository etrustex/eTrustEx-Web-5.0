package eu.europa.ec.etrustex.web.service.exchange;

import eu.europa.ec.etrustex.web.exchange.model.RecipientPreferencesSpec;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.RecipientPreferences;
import eu.europa.ec.etrustex.web.persistence.repository.exchange.RecipientPreferencesRepository;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static eu.europa.ec.etrustex.web.util.crypto.Base64.toHash;

@Service
@RequiredArgsConstructor
public class RecipientPreferencesServiceImpl implements RecipientPreferencesService {

    private final RecipientPreferencesRepository recipientPreferencesRepository;


    @Override
    public RecipientPreferences findById(long recipientPreferencesId) {
        return recipientPreferencesRepository.findById(recipientPreferencesId)
                .orElseThrow(() -> new EtxWebException(String.format("RecipientPreferences with id %s not found", recipientPreferencesId)));
    }

    @Override
    public RecipientPreferences save(RecipientPreferences recipientPreferences) {
        return recipientPreferencesRepository.save(recipientPreferences);
    }

    @Override
    public RecipientPreferences create(RecipientPreferencesSpec recipientPreferencesSpec) {
        return recipientPreferencesRepository.save(toRecipientPreferences(recipientPreferencesSpec));
    }

    @Override
    public RecipientPreferences update(long recipientPreferencesId, RecipientPreferencesSpec recipientPreferencesSpec) {
        return recipientPreferencesRepository.save(toRecipientPreferences(recipientPreferencesId, recipientPreferencesSpec));
    }

    @Override
    public void delete(RecipientPreferences recipientPreferences) {
        recipientPreferencesRepository.delete(recipientPreferences);
    }

    private RecipientPreferences toRecipientPreferences(RecipientPreferencesSpec recipientPreferencesSpec) {
        return RecipientPreferences.builder()
                .confidentiality(recipientPreferencesSpec.getConfidentiality())
                .publicKey(recipientPreferencesSpec.getPublicKey())
                .publicKeyHashValue(toHash(recipientPreferencesSpec.getPublicKey()))
                .publicKeyFileName(recipientPreferencesSpec.getPublicKeyFileName())
                .build();
    }

    private RecipientPreferences toRecipientPreferences(long recipientPreferencesId, RecipientPreferencesSpec recipientPreferencesSpec) {
        RecipientPreferences recipientPreferences = this.toRecipientPreferences(recipientPreferencesSpec);
        recipientPreferences.setId(recipientPreferencesId);
        return recipientPreferences;
    }

}
