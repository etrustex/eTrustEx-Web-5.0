package eu.europa.ec.etrustex.web.service.validation.validator;

import eu.europa.ec.etrustex.web.service.validation.annotation.CheckEmptyBccAndCcEmail;
import eu.europa.ec.etrustex.web.service.validation.model.NotificationEmailSpec;
import org.apache.commons.collections4.CollectionUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class EmptyBccAndCcEmailValidator implements ConstraintValidator<CheckEmptyBccAndCcEmail, NotificationEmailSpec> {

    @Override
    public boolean isValid(NotificationEmailSpec notificationEmailSpec, ConstraintValidatorContext context) {
        return CollectionUtils.isNotEmpty(notificationEmailSpec.getBcc()) || CollectionUtils.isNotEmpty(notificationEmailSpec.getCc());
    }
}
