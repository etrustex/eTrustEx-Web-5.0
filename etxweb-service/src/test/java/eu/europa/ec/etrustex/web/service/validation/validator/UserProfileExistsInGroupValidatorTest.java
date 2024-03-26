package eu.europa.ec.etrustex.web.service.validation.validator;

import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.repository.security.GrantedAuthorityRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.UserProfileRepository;
import eu.europa.ec.etrustex.web.service.validation.model.UserProfileSpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintValidatorContext;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserProfileExistsInGroupValidatorTest {
    @Mock
    private UserProfileRepository userProfileRepository;
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private GrantedAuthorityRepository grantedAuthorityRepository;
    @Mock
    private ConstraintValidatorContext context;
    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder constraintViolationBuilder;

    private UserExistsInGroupValidator userExistsInGroupValidator;

    private Group group;
    private UserProfileSpec userProfileSpec;

    @BeforeEach
    public void init() {
        userExistsInGroupValidator = new UserExistsInGroupValidator(grantedAuthorityRepository, userProfileRepository, groupRepository);
        group = Group.builder()
                .identifier("groupId")
                .type(GroupType.BUSINESS)
                .build();

        userProfileSpec = UserProfileSpec.builder()
                .ecasId("ecasId")
                .groupId(group.getId())
                .build();

        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(constraintViolationBuilder);
    }


    @Test
    void should_fail_validation_when_the_group_does_not_exist() {
        given(groupRepository.findById(group.getId())).willReturn(Optional.empty());
        assertFalse(userExistsInGroupValidator.isValid(userProfileSpec, context));
    }

    @Test
    void should_fail_validation_when_the_profile_exists_for_root() {
        given(groupRepository.findById(group.getId())).willReturn(Optional.of(group));

        given(userProfileRepository.existsByUserEcasIdIgnoreCaseAndGroupId(userProfileSpec.getEcasId(), userProfileSpec.getGroupId())).willReturn(true);
        assertFalse(userExistsInGroupValidator.isValid(userProfileSpec, context));

    }

    @Test
    void should_fail_validation_when_the_profile_exists_for_a_business() {
        given(groupRepository.findById(group.getId())).willReturn(Optional.of(group));

        given(userProfileRepository.existsByUserEcasIdIgnoreCaseAndGroupId(userProfileSpec.getEcasId(), userProfileSpec.getGroupId())).willReturn(true);
        assertFalse(userExistsInGroupValidator.isValid(userProfileSpec, context));

    }

    @Test
    void should_pass_validation() {
        given(groupRepository.findById(group.getId())).willReturn(Optional.of(group));

        given(userProfileRepository.existsByUserEcasIdIgnoreCaseAndGroupId(userProfileSpec.getEcasId(), userProfileSpec.getGroupId())).willReturn(false);
        assertTrue(userExistsInGroupValidator.isValid(userProfileSpec, context));

    }
}
