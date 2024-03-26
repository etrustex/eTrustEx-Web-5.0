package eu.europa.ec.etrustex.web.service.validation.validator;

import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.service.security.GroupService;
import eu.europa.ec.etrustex.web.service.validation.annotation.CheckIsBusiness;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
public class GroupIsBusinessValidator implements ConstraintValidator<CheckIsBusiness, Long> {
    private final GroupService groupService;

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        Group group = groupService.findById(value);
        if (group.getType().equals(GroupType.BUSINESS)) {
            return true;
        }

        context.buildConstraintViolationWithTemplate("Not a business.").addConstraintViolation();
        return false;
    }
}
