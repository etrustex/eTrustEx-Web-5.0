package eu.europa.ec.etrustex.web.service.validation.validator;

import eu.europa.ec.etrustex.web.persistence.repository.exchange.RecipientPreferencesRepository;
import eu.europa.ec.etrustex.web.service.validation.annotation.CheckRecipientPreferencesExist;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
public class RecipientPreferencesExistsValidator implements ConstraintValidator<CheckRecipientPreferencesExist, Long> {

    private final RecipientPreferencesRepository recipientPreferencesRepository;

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        if(value == null) {
            return true;
        }
        return recipientPreferencesRepository.findById(value).isPresent();
    }
}
