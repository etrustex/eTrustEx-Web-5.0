package eu.europa.ec.etrustex.web.service.validation.validator;

import eu.europa.ec.etrustex.web.persistence.repository.security.GrantedAuthorityRepository;
import eu.europa.ec.etrustex.web.service.validation.annotation.CheckUserExistsForGroup;
import eu.europa.ec.etrustex.web.service.validation.model.UpdateCertificateSpec;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static eu.europa.ec.etrustex.web.util.validation.ValidationMessage.Constants.USER_IS_NOT_CONFIGURED_IN_GROUP_VALUE;

@RequiredArgsConstructor
public class UserExistsForGroupValidator implements ConstraintValidator<CheckUserExistsForGroup, UpdateCertificateSpec> {
    private final GrantedAuthorityRepository grantedAuthorityRepository;
    @Override
    public boolean isValid(UpdateCertificateSpec value, ConstraintValidatorContext context) {
        if (grantedAuthorityRepository.findByUserEcasIdAndGroupId(value.getEuLoginId(), value.getEntityId()).isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(USER_IS_NOT_CONFIGURED_IN_GROUP_VALUE).addConstraintViolation();
            return false;
        }

        return true;
    }
}
