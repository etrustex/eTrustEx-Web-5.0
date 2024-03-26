package eu.europa.ec.etrustex.web.service.validation.validator;

import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import eu.europa.ec.etrustex.web.util.validation.ValidationMessage;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.repository.security.GrantedAuthorityRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import eu.europa.ec.etrustex.web.service.validation.annotation.CheckUniqueGrantedAuthority;
import eu.europa.ec.etrustex.web.service.validation.model.GrantedAuthoritySpec;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static eu.europa.ec.etrustex.web.util.exchange.model.GroupType.ROOT;

@RequiredArgsConstructor
public class UniqueGrantedAuthorityValidator implements ConstraintValidator<CheckUniqueGrantedAuthority, GrantedAuthoritySpec> {
    private final GrantedAuthorityRepository grantedAuthorityRepository;
    private final GroupRepository groupRepository;

    @Override
    public boolean isValid(GrantedAuthoritySpec value, ConstraintValidatorContext context) {
        boolean isValid = !grantedAuthorityRepository.existsByUserEcasIdAndGroupIdAndRoleNameAndEnabledTrue(value.getUserName(), value.getGroupId(), value.getRoleName());

        if (!isValid) {
            context.disableDefaultConstraintViolation();

            Group group = groupRepository.findById(value.getGroupId())
                    .orElseThrow(() -> new EtxWebException("Cannot find group with id " + value.getGroupId(), new IllegalArgumentException()));

            if (group.getType().equals(ROOT)) {
                if (value.getRoleName().equals(RoleName.OFFICIAL_IN_CHARGE)) {
                    context.buildConstraintViolationWithTemplate(ValidationMessage.USER_ALREADY_OFFICIAL_IN_CHARGE.getMessage()).addConstraintViolation();
                } else {
                    context.buildConstraintViolationWithTemplate(ValidationMessage.USER_ALREADY_SYS_ADMIN.getMessage()).addConstraintViolation();
                }
            } else {
                context.buildConstraintViolationWithTemplate(ValidationMessage.formatUniqueGrantedAuthorityMessage(group.getType(), value.getRoleName()))
                        .addConstraintViolation();
            }
        }

        return isValid;
    }
}
