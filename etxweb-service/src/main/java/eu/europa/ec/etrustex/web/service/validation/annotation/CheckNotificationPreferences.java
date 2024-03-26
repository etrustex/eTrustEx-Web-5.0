package eu.europa.ec.etrustex.web.service.validation.annotation;

import eu.europa.ec.etrustex.web.service.validation.validator.NotificationPreferencesValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static eu.europa.ec.etrustex.web.util.validation.ValidationMessage.Constants.NOTIFICATION_PREFERENCES_WITHOUT_EMAIL_ERROR_MSG;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = { NotificationPreferencesValidator.class })
@Target({ TYPE })
@Retention(RUNTIME)
@Documented
public @interface CheckNotificationPreferences {
    String message() default NOTIFICATION_PREFERENCES_WITHOUT_EMAIL_ERROR_MSG;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
