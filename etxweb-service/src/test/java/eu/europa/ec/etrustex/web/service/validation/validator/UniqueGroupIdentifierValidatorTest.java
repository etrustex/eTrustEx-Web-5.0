package eu.europa.ec.etrustex.web.service.validation.validator;

import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import eu.europa.ec.etrustex.web.service.validation.model.GroupSpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintValidatorContext;

import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.TEST_ENTITY_IDENTIFIER;
import static eu.europa.ec.etrustex.web.service.validation.model.GroupSpecTestUtils.mockGroupSpec;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UniqueGroupIdentifierValidatorTest {
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private ConstraintValidatorContext context;

    private UniqueGroupIdentifierValidator uniqueGroupIdentifierValidator;


    @BeforeEach
    public void init() {
        uniqueGroupIdentifierValidator = new UniqueGroupIdentifierValidator(groupRepository);
    }


    @Test
    void should_validate() {
        GroupSpec spec = mockGroupSpec(TEST_ENTITY_IDENTIFIER);

        given(groupRepository.existsByIdentifierAndParentId(spec.getIdentifier(), spec.getParentGroupId())).willReturn(false);
        assertTrue(uniqueGroupIdentifierValidator.isValid(spec, context));

        given(groupRepository.existsByIdentifierAndParentId(spec.getIdentifier(), spec.getParentGroupId())).willReturn(true);
        assertFalse(uniqueGroupIdentifierValidator.isValid(spec, context));
    }
}
