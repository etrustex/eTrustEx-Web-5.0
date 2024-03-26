package eu.europa.ec.etrustex.web.service.validation.validator;

import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import eu.europa.ec.etrustex.web.service.validation.annotation.UniqueGroupName;
import eu.europa.ec.etrustex.web.service.validation.model.GroupSpec;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
public class UniqueGroupNameValidator implements ConstraintValidator<UniqueGroupName, GroupSpec> {
    private final GroupRepository groupRepository;

    @Override
    public boolean isValid(GroupSpec groupSpec, ConstraintValidatorContext context) {
        return groupRepository.findByNameAndParentId(groupSpec.getDisplayName(), groupSpec.getParentGroupId()).stream().noneMatch(group -> !group.getId().equals(groupSpec.getId()));
    }
}
