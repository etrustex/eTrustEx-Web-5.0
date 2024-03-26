package eu.europa.ec.etrustex.web.service.validation.validator;

import eu.europa.ec.etrustex.web.service.validation.annotation.CheckSortField;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class SortFieldValidator implements ConstraintValidator<CheckSortField, Object> {

    Set<String> allowedFields;

    @Override
    public void initialize(CheckSortField constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.allowedFields = Arrays.stream(constraintAnnotation.allowedFields().split(",")).collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return this.allowedFields.contains(value.toString());
    }
}
