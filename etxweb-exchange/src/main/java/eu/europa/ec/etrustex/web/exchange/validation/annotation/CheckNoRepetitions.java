package eu.europa.ec.etrustex.web.exchange.validation.annotation;

import eu.europa.ec.etrustex.web.exchange.validation.validator.NoRepetitionsValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


@Constraint(validatedBy = {NoRepetitionsValidator.class})
@Target({ FIELD, PARAMETER })
@Retention(RUNTIME)
@Documented
public @interface CheckNoRepetitions {
    String message() default "stringList.noRepetitions";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
