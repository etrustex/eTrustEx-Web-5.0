package eu.europa.ec.etrustex.web.service.validation.annotation;

import eu.europa.ec.etrustex.web.service.validation.validator.SortFieldValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = { SortFieldValidator.class })
@Target({ ElementType.PARAMETER })
@Retention(RUNTIME)
@Documented
public @interface CheckSortField {
    String message() default "sortField.notValid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String allowedFields() default "id";
}
