package eu.europa.ec.etrustex.web.exchange.validation.validator;

import eu.europa.ec.etrustex.web.util.cia.Confidentiality;
import eu.europa.ec.etrustex.web.exchange.model.RecipientPreferencesSpec;
import eu.europa.ec.etrustex.web.exchange.validation.annotation.CheckConfidentialityAndPublicKey;
import eu.europa.ec.etrustex.web.util.crypto.Rsa;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ConfidentialityAndPublicKeyValidator implements ConstraintValidator<CheckConfidentialityAndPublicKey, RecipientPreferencesSpec> {
    @Override
    public boolean isValid(RecipientPreferencesSpec value, ConstraintValidatorContext context) {

        if (Confidentiality.PUBLIC.equals(value.getConfidentiality()) && StringUtils.isBlank(value.getPublicKey())) {
            return true;
        }

        if (Confidentiality.LIMITED_HIGH.equals(value.getConfidentiality()) && StringUtils.isBlank(value.getPublicKey())) {
            return false;
        }


        try {
            Rsa.toPublicKey(value.getPublicKey());
        } catch (Exception e) {
            return false;
        }

        return true;
    }

}
