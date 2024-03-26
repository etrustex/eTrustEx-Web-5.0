package eu.europa.ec.etrustex.web.service.validation.validator;

import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import eu.europa.ec.etrustex.web.util.validation.ValidationMessage;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.repository.security.GrantedAuthorityRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.UserProfileRepository;
import eu.europa.ec.etrustex.web.service.validation.annotation.CheckUserExistsInGroup;
import eu.europa.ec.etrustex.web.service.validation.model.BaseUserProfileSpec;
import eu.europa.ec.etrustex.web.service.validation.model.CreateUserProfileSpec;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;

@RequiredArgsConstructor
public class UserExistsInGroupValidator implements ConstraintValidator<CheckUserExistsInGroup, BaseUserProfileSpec> {
    private final GrantedAuthorityRepository grantedAuthorityRepository;
    private final UserProfileRepository userProfileRepository;
    private final GroupRepository groupRepository;

    @Override
    public boolean isValid(BaseUserProfileSpec value, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        Optional<Group> groupOptional = groupRepository.findById(value.getGroupId());
        if (!groupOptional.isPresent()) {
            context.buildConstraintViolationWithTemplate(ValidationMessage.formatGroupDoesNotExistMessage(value.getGroupId())).addConstraintViolation();
            return false;
        }

        Group group = groupOptional.get();

        if (group.getType().equals(GroupType.ROOT)) {
            boolean isAddingOfficialInCharge = value instanceof CreateUserProfileSpec && ((CreateUserProfileSpec) value).getRoleNames() != null && ((CreateUserProfileSpec) value).getRoleNames().contains(RoleName.OFFICIAL_IN_CHARGE);
            boolean isAddingSysAdmin = value instanceof CreateUserProfileSpec && ((CreateUserProfileSpec) value).getRoleNames() != null && ((CreateUserProfileSpec) value).getRoleNames().contains(RoleName.SYS_ADMIN);
            if (isAddingOfficialInCharge && grantedAuthorityRepository.existsByUserEcasIdAndGroupIdAndRoleNameAndEnabledTrue(value.getEcasId(), group.getId(), RoleName.OFFICIAL_IN_CHARGE)) {
                context.buildConstraintViolationWithTemplate(ValidationMessage.USER_ALREADY_OFFICIAL_IN_CHARGE.getMessage()).addConstraintViolation();
            } else if (isAddingSysAdmin && grantedAuthorityRepository.existsByUserEcasIdAndGroupIdAndRoleNameAndEnabledTrue(value.getEcasId(), group.getId(), RoleName.SYS_ADMIN)){
                context.buildConstraintViolationWithTemplate(ValidationMessage.USER_ALREADY_SYS_ADMIN.getMessage()).addConstraintViolation();
            } else {
                context.buildConstraintViolationWithTemplate(ValidationMessage.formatUserExistsInGroupMessage(group.getType())).addConstraintViolation();
                return true;
            }
        } else {
            context.buildConstraintViolationWithTemplate(ValidationMessage.formatUserExistsInGroupMessage(group.getType())).addConstraintViolation();
        }

        return !userProfileRepository.existsByUserEcasIdIgnoreCaseAndGroupId(value.getEcasId(), group.getId());
    }
}
