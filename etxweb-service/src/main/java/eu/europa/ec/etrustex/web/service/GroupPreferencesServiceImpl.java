package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.util.cia.Confidentiality;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.RecipientPreferences;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.SenderPreferences;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.repository.exchange.RecipientPreferencesRepository;
import eu.europa.ec.etrustex.web.persistence.repository.exchange.SenderPreferencesRepository;
import eu.europa.ec.etrustex.web.service.validation.model.GroupSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GroupPreferencesServiceImpl implements GroupPreferencesService {
    private final RecipientPreferencesRepository recipientPreferencesRepository;
    private final SenderPreferencesRepository senderPreferencesRepository;

    @Override
    public Optional<RecipientPreferences> getRecipientPreferencesIfChanged(GroupSpec spec, Group group) {
        Optional<RecipientPreferences> recipientPreferences;

        if (didRecipientPreferencesChange(spec, group) && spec.getRecipientPreferencesId() != null) {
            recipientPreferences = recipientPreferencesRepository.findById(spec.getRecipientPreferencesId());
        } else {
            recipientPreferences = Optional.empty();
        }

      return recipientPreferences;
    }

    @Override
    public Optional<SenderPreferences> getSenderPreferencesIfChanged(GroupSpec spec, Group group) {
        Optional<SenderPreferences> senderPreferences;

        if (didSenderPreferencesChange(spec, group)) {
            senderPreferences = senderPreferencesRepository.findById(spec.getSenderPreferencesId());
        } else {
            senderPreferences = Optional.empty();
        }

        return senderPreferences;
    }

    @Override
    public RecipientPreferences getRecipientPreferences(GroupSpec spec) {
        RecipientPreferences recipientPreferences;

        if (spec.getRecipientPreferencesId() != null) {
            recipientPreferences = recipientPreferencesRepository.findById(spec.getRecipientPreferencesId())
                    .orElseThrow(() -> new EtxWebException(String.format("RecipientPreferences with id %s not found", spec.getRecipientPreferencesId())));
        } else {
            recipientPreferences = null;
        }

        return recipientPreferences;
    }

    @Override
    public SenderPreferences getSenderPreferences(GroupSpec spec, Group parent) {
        SenderPreferences senderPreferences = null;

        if (spec.getSenderPreferencesId() != null) {
            senderPreferences = senderPreferencesRepository.findById(spec.getSenderPreferencesId())
                    .orElseThrow(() -> new EtxWebException(String.format("SenderPreferences %s not found", spec.getSenderPreferencesId())));
        } else if (parent != null && parent.getSenderPreferences() != null) {
            senderPreferences = parent.getSenderPreferences();
        }

        return senderPreferences;
    }


    @Override
    public void delete(SenderPreferences senderPreferences) {
        if (senderPreferences != null && senderPreferencesRepository.countGroupsById(senderPreferences.getId()) == 0) {
            senderPreferencesRepository.delete(senderPreferences);
        }
    }

    @Override
    public Group disableEncryption(Group entity) {
        Optional.ofNullable(entity.getRecipientPreferences())
                .ifPresent(recipientPreferences -> {
                    recipientPreferences.setConfidentiality(Confidentiality.PUBLIC);
                    recipientPreferences.setPublicKey(null);
                    recipientPreferences.setPublicKeyFileName(null);
                    recipientPreferencesRepository.save(recipientPreferences);
                });

        return entity;
    }

    private boolean didRecipientPreferencesChange(GroupSpec spec, Group group) {
        return spec.getRecipientPreferencesId() != null && (
                group.getRecipientPreferences() == null || !Objects.equals(group.getRecipientPreferences().getId(), spec.getRecipientPreferencesId())
        );
    }

    private boolean didSenderPreferencesChange(GroupSpec spec, Group group) {
        return group.getSenderPreferences() != null && !Objects.equals(group.getSenderPreferences().getId(), spec.getSenderPreferencesId());
    }
}
