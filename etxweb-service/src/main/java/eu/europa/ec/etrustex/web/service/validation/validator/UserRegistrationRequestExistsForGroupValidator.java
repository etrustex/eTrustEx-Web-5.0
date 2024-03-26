package eu.europa.ec.etrustex.web.service.validation.validator;

import eu.europa.ec.etrustex.web.persistence.entity.redirect.UserRegistrationRedirect;
import eu.europa.ec.etrustex.web.persistence.repository.UserRegistrationRequestRepository;
import eu.europa.ec.etrustex.web.persistence.repository.redirect.UserRegistrationRedirectRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.GrantedAuthorityRepository;
import eu.europa.ec.etrustex.web.service.validation.annotation.CheckUserRegistrationRequestExistsForGroup;
import eu.europa.ec.etrustex.web.service.validation.model.UserRegistrationRequestSpec;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

import static eu.europa.ec.etrustex.web.util.validation.ValidationMessage.Constants.*;

@RequiredArgsConstructor
public class UserRegistrationRequestExistsForGroupValidator implements ConstraintValidator<CheckUserRegistrationRequestExistsForGroup, UserRegistrationRequestSpec> {
    private final UserRegistrationRequestRepository userRegistrationRequestRepository;
    private final UserRegistrationRedirectRepository userRegistrationRedirectRepository;
    private final GrantedAuthorityRepository grantedAuthorityRepository;
    @Override
    public boolean isValid(UserRegistrationRequestSpec value, ConstraintValidatorContext context) {
        if (userRegistrationRequestRepository.existsByUserEcasIdAndGroupId(value.getEcasId(), value.getGroupId())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(String.format(USER_ALREADY_SENT_REQUEST_ERROR_MSG, value.getGroupIdentifier())).addConstraintViolation();
            return false;
        }

        if (!grantedAuthorityRepository.findByUserEcasIdAndGroupId(value.getEcasId(), value.getGroupId()).isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(String.format(USER_ALREADY_CONFIGURED_ERROR_MSG, value.getGroupIdentifier())).addConstraintViolation();
            return false;
        }

        List<UserRegistrationRedirect> optionalUserRegistrationRedirects = userRegistrationRedirectRepository.
                findByEmailAddressIgnoreCaseAndGroupId(value.getRequesterEmailAddress(), value.getGroupId());
        if (optionalUserRegistrationRedirects.isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(USER_NOT_ALLOWED_TO_USE_THIS_PAGE_ERROR_MSG).addConstraintViolation();
            return false;
        }

        return true;
    }
}
