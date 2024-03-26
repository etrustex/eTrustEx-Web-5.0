package eu.europa.ec.etrustex.web.service.validation.annotation;

import eu.europa.ec.etrustex.web.service.validation.validator.EntityIsEmptyValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static eu.europa.ec.etrustex.web.util.validation.ValidationMessage.Constants.ENTITY_HAS_USERS_OR_MESSAGES_ERROR_MSG;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = { EntityIsEmptyValidator.class })
@Target({ PARAMETER })
@Retention(RUNTIME)
@Documented
public @interface CheckEntityIsEmpty {
    String message() default ENTITY_HAS_USERS_OR_MESSAGES_ERROR_MSG;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
