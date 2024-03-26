package eu.europa.ec.etrustex.web.service.validation.validator;

import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.service.security.GroupService;
import eu.europa.ec.etrustex.web.service.validation.annotation.CheckEntityIsEmpty;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import eu.europa.ec.etrustex.web.util.validation.ValidationMessage;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
public class EntityIsEmptyValidator implements ConstraintValidator<CheckEntityIsEmpty, Group> {
    private final GroupService groupService;

    @Override
    public boolean isValid(Group group, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        if (group.getType().equals(GroupType.BUSINESS) || isPendingDeletion(group)) {
            return true;
        }
        if (groupService.hasMessages(group.getId())) {
            context.buildConstraintViolationWithTemplate(selectErrorMessage(group.getType())).addConstraintViolation();
            return false;
        }
        return true;
    }

    private String selectErrorMessage(GroupType groupType) {
        switch (groupType) {
            case ENTITY:
                return ValidationMessage.Constants.ENTITY_HAS_USERS_OR_MESSAGES_ERROR_MSG;
            case ROOT:
                throw new EtxWebException("ROOT group cannot be deleted!");
            default:
                throw new EtxWebException(String.format("Trying to delete a group with unhandled type %s", groupType));
        }
    }

    private boolean isPendingDeletion(Group group) {
        return group.isPendingDeletion() && !group.isActive() && group.getType() == GroupType.ENTITY;
    }
}
