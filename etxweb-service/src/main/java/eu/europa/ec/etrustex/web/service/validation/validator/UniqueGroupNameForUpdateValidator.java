package eu.europa.ec.etrustex.web.service.validation.validator;

import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.service.security.GroupService;
import eu.europa.ec.etrustex.web.service.validation.annotation.CheckUniqueGroupNameForUpdate;
import eu.europa.ec.etrustex.web.service.validation.model.GroupSpec;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraintvalidation.SupportedValidationTarget;
import javax.validation.constraintvalidation.ValidationTarget;

import static eu.europa.ec.etrustex.web.util.validation.ValidationMessage.Constants.BUSINESS_DISPLAY_NAME_UNIQUE;
import static eu.europa.ec.etrustex.web.util.validation.ValidationMessage.Constants.ENTITY_DISPLAY_NAME_UNIQUE_IN_BUSINESS;

@Component
@AllArgsConstructor()
@SupportedValidationTarget(ValidationTarget.PARAMETERS)
@Slf4j
public class UniqueGroupNameForUpdateValidator implements ConstraintValidator<CheckUniqueGroupNameForUpdate, Object[]> {

    private final GroupService groupService;

    @Override
    public boolean isValid(Object[] arguments, ConstraintValidatorContext context) {

        Long groupId = null;
        GroupSpec groupSpec = null;

        context.disableDefaultConstraintViolation();

        for (Object arg : arguments) {
            if (arg instanceof Long) {
                groupId = (Long) arg;
            } else if (arg instanceof GroupSpec) {
                groupSpec = (GroupSpec) arg;
            }
        }

        if (groupId == null || groupSpec == null || groupSpec.getParentGroupId() == null) {
            throw new EtxWebException("Cannot retrieve the groupId and groupSpec arguments for unique name validation");
        }

        Group oldGroup = groupService.findById(groupId);
        if (
                !oldGroup.getName().equals(groupSpec.getDisplayName()) &&
                        groupService.existsByNameAndParentId(groupSpec.getDisplayName(), groupSpec.getParentGroupId())
        ) {
            context.buildConstraintViolationWithTemplate(selectErrorMessage(groupSpec.getType())).addConstraintViolation();
            return false;
        }
        return true;
    }

    private String selectErrorMessage(GroupType type) {
        switch (type) {
            case BUSINESS:
                return BUSINESS_DISPLAY_NAME_UNIQUE;
            case ENTITY:
                return ENTITY_DISPLAY_NAME_UNIQUE_IN_BUSINESS;
            default:
                throw new EtxWebException(String.format("Cannot select error message for unique name for group type %s", type));
        }
    }
}
