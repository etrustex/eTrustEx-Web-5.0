package eu.europa.ec.etrustex.web.service.validation.annotation;

import eu.europa.ec.etrustex.web.service.validation.validator.ForbiddenExtensionsValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Constraint(validatedBy = ForbiddenExtensionsValidator.class)
@Documented
public @interface CheckForbiddenExtensions {
    Class<?>[] groups() default {  };

    Class<? extends Payload>[] payload() default {};

    String message() default "{extension.forbidden}";
}
