package eu.europa.ec.etrustex.web.exchange.validation.validator;

import eu.europa.ec.etrustex.web.util.cia.Confidentiality;
import eu.europa.ec.etrustex.web.exchange.validation.annotation.CheckPublicOrLimitedHigh;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PublicOrLimitedHighValidator implements ConstraintValidator<CheckPublicOrLimitedHigh, Confidentiality> {
    @Override
    public boolean isValid(Confidentiality value, ConstraintValidatorContext context) {
        return Confidentiality.LIMITED_HIGH.equals(value) || Confidentiality.PUBLIC.equals(value);
    }
}
