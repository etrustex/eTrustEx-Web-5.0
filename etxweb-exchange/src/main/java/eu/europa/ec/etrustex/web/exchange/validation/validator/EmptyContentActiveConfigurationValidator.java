package eu.europa.ec.etrustex.web.exchange.validation.validator;

import eu.europa.ec.etrustex.web.exchange.model.groupconfiguration.SplashScreenGroupConfigurationSpec;
import eu.europa.ec.etrustex.web.exchange.validation.annotation.CheckEmptyContentActiveConfiguration;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EmptyContentActiveConfigurationValidator implements ConstraintValidator<CheckEmptyContentActiveConfiguration, SplashScreenGroupConfigurationSpec> {
    @Override
    public boolean isValid(SplashScreenGroupConfigurationSpec spec, ConstraintValidatorContext context) {
        return !(spec.isActive() && StringUtils.isBlank(spec.getValue()));
    }
}
