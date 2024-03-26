package eu.europa.ec.etrustex.web.service.validation.annotation;

import eu.europa.ec.etrustex.web.util.validation.Patterns;
import eu.europa.ec.etrustex.web.service.validation.validator.EmailListValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = { EmailListValidator.class })
@Target({ FIELD })
@Retention(RUNTIME)
@Documented
public @interface CheckEmailList {
    String message() default "emailList.notValid";
    int limit() default 10;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String pattern() default Patterns.VALID_EMAIL;
}
