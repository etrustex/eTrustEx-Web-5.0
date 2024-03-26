package eu.europa.ec.etrustex.web.exchange.validation.annotation;

import eu.europa.ec.etrustex.web.exchange.validation.validator.PublicOrLimitedHighValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = PublicOrLimitedHighValidator.class)
@Target({ FIELD })
@Retention(RUNTIME)
@Documented
public @interface CheckPublicOrLimitedHigh {
    String message() default "confidentiality.publicOrLimitedHigh";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

