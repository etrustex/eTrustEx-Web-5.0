package eu.europa.ec.etrustex.web.service.validation.validator;

import eu.europa.ec.etrustex.web.service.validation.annotation.CheckNotificationPreferences;
import eu.europa.ec.etrustex.web.service.validation.model.UserProfileSpec;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NotificationPreferencesValidator implements ConstraintValidator<CheckNotificationPreferences, UserProfileSpec> {

    @Override
    public boolean isValid(UserProfileSpec value, ConstraintValidatorContext context) {
        return !(value.isNewMessageNotification() || value.isStatusNotification() || value.isRetentionWarningNotification())
                || (StringUtils.isNotBlank(value.getEuLoginEmailAddress())
                || (value.isAlternativeEmailUsed() && StringUtils.isNotBlank(value.getAlternativeEmail()))
        );
    }
}
