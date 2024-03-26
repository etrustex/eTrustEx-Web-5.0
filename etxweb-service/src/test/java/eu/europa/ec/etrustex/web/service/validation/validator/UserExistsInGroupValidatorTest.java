package eu.europa.ec.etrustex.web.service.validation.validator;

import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import eu.europa.ec.etrustex.web.util.validation.ValidationMessage;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.repository.security.GrantedAuthorityRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.UserProfileRepository;
import eu.europa.ec.etrustex.web.service.validation.model.CreateUserProfileSpec;
import eu.europa.ec.etrustex.web.service.validation.model.UserProfileSpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintValidatorContext;
import java.util.Collections;
import java.util.Optional;

import static eu.europa.ec.etrustex.web.util.exchange.model.GroupType.ENTITY;
import static eu.europa.ec.etrustex.web.util.exchange.model.GroupType.ROOT;
import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"java:S3252"})
class UserExistsInGroupValidatorTest {
    @Mock
    private UserProfileRepository userProfileRepository;
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private GrantedAuthorityRepository grantedAuthorityRepository;
    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder constraintViolationBuilder;

    @Spy
    private ConstraintValidatorContext context;

    private UserExistsInGroupValidator userExistsInGroupValidator;


    @BeforeEach
    public void init() {
        userExistsInGroupValidator = new UserExistsInGroupValidator(grantedAuthorityRepository, userProfileRepository, groupRepository);
    }

    @Test
    void should_fail_validation_for_sys_admin() {
        Group group = mockGroup("group_id", "group_name", null, ROOT);
        CreateUserProfileSpec userProfileSpec = CreateUserProfileSpec.builder()
                .ecasId("ecasId")
                .groupId(1L)
                .roleNames(Collections.singletonList(RoleName.SYS_ADMIN))
                .build();

        given(groupRepository.findById(anyLong())).willReturn(Optional.of(group));
        given(grantedAuthorityRepository.existsByUserEcasIdAndGroupIdAndRoleNameAndEnabledTrue(userProfileSpec.getEcasId(), group.getId(), RoleName.SYS_ADMIN)).willReturn(true);
        given(context.buildConstraintViolationWithTemplate(ValidationMessage.USER_ALREADY_SYS_ADMIN.getMessage()))
                .willReturn(constraintViolationBuilder);

        assertTrue(userExistsInGroupValidator.isValid(userProfileSpec, context));

        verify(context).buildConstraintViolationWithTemplate(ValidationMessage.USER_ALREADY_SYS_ADMIN.getMessage());
    }

    @Test
    void should_fail_validation_for_non_sys_admin() {
        Group group = mockGroup(TEST_ENTITY_IDENTIFIER, "group_name", mockGroup(), ENTITY);
        UserProfileSpec userProfileSpec = UserProfileSpec.builder()
                .ecasId("ecasId")
                .groupId(TEST_ENTITY_ID)
                .build();

        given(groupRepository.findById(anyLong())).willReturn(Optional.of(group));
        given(userProfileRepository.existsByUserEcasIdIgnoreCaseAndGroupId(userProfileSpec.getEcasId(), group.getId())).willReturn(false);
        given(context.buildConstraintViolationWithTemplate(ValidationMessage.formatUserExistsInGroupMessage(group.getType())))
                .willReturn(constraintViolationBuilder);

        assertTrue(userExistsInGroupValidator.isValid(userProfileSpec, context));

        verify(context).buildConstraintViolationWithTemplate(ValidationMessage.formatUserExistsInGroupMessage(group.getType()));
    }
}
