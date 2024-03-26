package eu.europa.ec.etrustex.web.service.validation.annotation;

import eu.europa.ec.etrustex.web.service.validation.validator.UniqueGroupIdentifierValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = UniqueGroupIdentifierValidator.class)
@Target({TYPE})
@Retention(RUNTIME)
@Documented
public @interface CheckUniqueGroupIdentifier {
    String message() default "groupIdentifier.unique";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
