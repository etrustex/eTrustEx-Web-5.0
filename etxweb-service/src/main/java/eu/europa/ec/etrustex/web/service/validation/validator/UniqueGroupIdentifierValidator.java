package eu.europa.ec.etrustex.web.service.validation.validator;

import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import eu.europa.ec.etrustex.web.service.validation.annotation.CheckUniqueGroupIdentifier;
import eu.europa.ec.etrustex.web.service.validation.model.GroupSpec;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
public class UniqueGroupIdentifierValidator implements ConstraintValidator<CheckUniqueGroupIdentifier, GroupSpec> {
    private final GroupRepository groupRepository;


    @Override
    public boolean isValid(GroupSpec spec, ConstraintValidatorContext context) {
        if (spec.getId() != null) {
            Group group = groupRepository.findById(spec.getId()).orElseThrow(() -> new EtxWebException(String.format("Group with id %s not found", spec.getId())));

            if (group.getIdentifier().equals(spec.getIdentifier())) {
                return true;
            }
        }

        return !groupRepository.existsByIdentifierAndParentId(spec.getIdentifier(), spec.getParentGroupId());
    }
}
