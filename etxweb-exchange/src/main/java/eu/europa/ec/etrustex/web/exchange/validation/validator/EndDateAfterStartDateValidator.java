package eu.europa.ec.etrustex.web.exchange.validation.validator;

import eu.europa.ec.etrustex.web.exchange.model.AlertSpec;
import eu.europa.ec.etrustex.web.exchange.validation.annotation.CheckEndDateAfterStartDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EndDateAfterStartDateValidator implements ConstraintValidator<CheckEndDateAfterStartDate, AlertSpec> {
    @Override
    public boolean isValid(AlertSpec value, ConstraintValidatorContext context) {
        if (value.getEndDate() == null) {
            return true;
        } else {
            return value.getEndDate().compareTo(value.getStartDate()) >= 0;
        }
    }
}
