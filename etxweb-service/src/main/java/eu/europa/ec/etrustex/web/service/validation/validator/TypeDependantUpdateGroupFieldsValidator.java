package eu.europa.ec.etrustex.web.service.validation.validator;

import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import eu.europa.ec.etrustex.web.service.validation.annotation.CheckTypeDependantUpdateGroupFields;
import eu.europa.ec.etrustex.web.service.validation.model.GroupSpec;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static eu.europa.ec.etrustex.web.util.validation.ValidationMessage.Constants.*;

public class TypeDependantUpdateGroupFieldsValidator implements ConstraintValidator<CheckTypeDependantUpdateGroupFields, GroupSpec> {
    @Override
    public boolean isValid(GroupSpec value, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        boolean isValid = true;
        if (GroupType.BUSINESS.equals(value.getType())) {
            if (StringUtils.isNotBlank(value.getNewMessageNotificationEmailAddresses())) {
                isValid = false;
                context.buildConstraintViolationWithTemplate(BUSINESS_CANNOT_HAVE_NEW_MESSAGE_NOTIFICATION_EMAILS).addConstraintViolation();
            }
            if (StringUtils.isNotBlank(value.getRegistrationRequestNotificationEmailAddresses())) {
                isValid = false;
                context.buildConstraintViolationWithTemplate(BUSINESS_CANNOT_HAVE_REGISTRATION_REQUEST_NOTIFICATION_EMAILS).addConstraintViolation();
            }
            if (StringUtils.isNotBlank(value.getStatusNotificationEmailAddress())) {
                isValid = false;
                context.buildConstraintViolationWithTemplate(BUSINESS_CANNOT_HAVE_A_STATUS_NOTIFICATION_EMAIL).addConstraintViolation();
            }
            if (value.getRecipientPreferencesId() != null) {
                isValid = false;
                context.buildConstraintViolationWithTemplate(BUSINESS_CANNOT_HAVE_RECIPIENT_PREFERENCES).addConstraintViolation();
            }
        }

        return isValid;
    }
}
