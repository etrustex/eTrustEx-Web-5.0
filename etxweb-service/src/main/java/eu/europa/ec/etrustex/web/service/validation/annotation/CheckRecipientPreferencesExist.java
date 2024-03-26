package eu.europa.ec.etrustex.web.service.validation.annotation;

import eu.europa.ec.etrustex.web.service.validation.validator.RecipientPreferencesExistsValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = { RecipientPreferencesExistsValidator.class })
@Target({ FIELD })
@Retention(RUNTIME)
@Documented
public @interface CheckRecipientPreferencesExist {
    String message() default "recipientPreferences.notFound";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
