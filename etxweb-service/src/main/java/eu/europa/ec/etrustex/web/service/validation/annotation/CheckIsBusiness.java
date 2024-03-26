package eu.europa.ec.etrustex.web.service.validation.annotation;
import eu.europa.ec.etrustex.web.service.validation.validator.GroupIsBusinessValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static eu.europa.ec.etrustex.web.util.validation.ValidationMessage.Constants.GROUP_IS_NOT_A_BUSINESS;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = { GroupIsBusinessValidator.class })
@Target({ PARAMETER })
@Retention(RUNTIME)
@Documented
public @interface CheckIsBusiness {
    String message() default GROUP_IS_NOT_A_BUSINESS;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
