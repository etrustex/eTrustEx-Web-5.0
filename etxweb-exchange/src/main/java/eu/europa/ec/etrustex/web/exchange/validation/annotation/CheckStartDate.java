package eu.europa.ec.etrustex.web.exchange.validation.annotation;

import eu.europa.ec.etrustex.web.exchange.validation.validator.RequiredStartDateValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = { RequiredStartDateValidator.class })
@Target({ TYPE })
@Retention(RUNTIME)
@Documented
public @interface CheckStartDate {
    String message() default "alert.startDateRequired";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
