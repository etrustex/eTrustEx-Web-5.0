package eu.europa.ec.etrustex.web.service.validation.annotation;


import eu.europa.ec.etrustex.web.service.validation.validator.TypeDependantUpdateGroupFieldsValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = TypeDependantUpdateGroupFieldsValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CheckTypeDependantUpdateGroupFields {
    String message() default "TypeDependantUpdateGroupFields";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
