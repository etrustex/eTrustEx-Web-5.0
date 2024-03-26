package eu.europa.ec.etrustex.web.service.validation.annotation;

import eu.europa.ec.etrustex.web.service.validation.validator.EmptyBccAndCcEmailValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = { EmptyBccAndCcEmailValidator.class })
@Target({TYPE})
@Retention(RUNTIME)
@Documented
public @interface CheckEmptyBccAndCcEmail {
    String message() default "bccOrCcEmail.required";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
