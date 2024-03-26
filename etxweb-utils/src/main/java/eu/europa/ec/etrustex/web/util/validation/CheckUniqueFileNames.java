package eu.europa.ec.etrustex.web.util.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = UniqueFileNamesValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CheckUniqueFileNames {
    String message() default "fileName.repeated";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
