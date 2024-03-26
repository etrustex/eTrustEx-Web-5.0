package eu.europa.ec.etrustex.web.exchange.validation.validator;

import eu.europa.ec.etrustex.web.exchange.model.AlertSpec;
import eu.europa.ec.etrustex.web.exchange.validation.annotation.CheckStartDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class RequiredStartDateValidator implements ConstraintValidator<CheckStartDate, AlertSpec> {
    @Override
    public boolean isValid(AlertSpec value, ConstraintValidatorContext context) {
        return !value.isActive() || value.getStartDate() != null;
    }
}
