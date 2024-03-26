package eu.europa.ec.etrustex.web.service.validation.validator;

import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import eu.europa.ec.etrustex.web.service.validation.model.GroupSpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintValidatorContext;
import java.util.Collections;

import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.TEST_ENTITY_ID;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UniqueGroupNameValidatorTest {
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private ConstraintValidatorContext context;

    private UniqueGroupNameValidator uniqueGroupNameValidator;


    @BeforeEach
    public void init() {
        uniqueGroupNameValidator = new UniqueGroupNameValidator(groupRepository);
    }


    @Test
    void should_validate() {
        GroupSpec groupSpec = GroupSpec.builder().id(1L)
                .displayName("name").parentGroupId(TEST_ENTITY_ID).build();

        Group sameGroupId = Group.builder().id(1L).name("name").build();
        Group differentGroupId = Group.builder().id(5L).name("name").build();

        given(groupRepository.findByNameAndParentId(groupSpec.getDisplayName(), groupSpec.getParentGroupId())).willReturn(Collections.singletonList(sameGroupId));
        assertTrue(uniqueGroupNameValidator.isValid(groupSpec, context));

        given(groupRepository.findByNameAndParentId(groupSpec.getDisplayName(), groupSpec.getParentGroupId())).willReturn(Collections.singletonList(differentGroupId));
        assertFalse(uniqueGroupNameValidator.isValid(groupSpec, context));
    }

}
