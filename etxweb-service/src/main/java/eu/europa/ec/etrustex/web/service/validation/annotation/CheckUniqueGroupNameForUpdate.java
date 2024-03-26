package eu.europa.ec.etrustex.web.service.validation.annotation;

import eu.europa.ec.etrustex.web.service.validation.validator.UniqueGroupNameForUpdateValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Constraint(validatedBy = UniqueGroupNameForUpdateValidator.class)
@Documented
public @interface CheckUniqueGroupNameForUpdate {
    Class<?>[] groups() default {  };

    Class<? extends Payload>[] payload() default {};

    String message() default "group.displayName.unique";
}
