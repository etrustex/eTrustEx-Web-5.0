package eu.europa.ec.etrustex.web.service.validation.annotation;

import eu.europa.ec.etrustex.web.service.validation.validator.WindowsCompatibleValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Constraint(validatedBy = WindowsCompatibleValidator.class)
@Documented
public @interface CheckWindowsCompatible {
    Class<?>[] groups() default {  };

    Class<? extends Payload>[] payload() default {};

    String message() default "{windows.compatible}";
}
