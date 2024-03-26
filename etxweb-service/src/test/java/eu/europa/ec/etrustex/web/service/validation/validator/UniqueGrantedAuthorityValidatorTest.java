package eu.europa.ec.etrustex.web.service.validation.validator;

import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import eu.europa.ec.etrustex.web.util.validation.ValidationMessage;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.repository.security.GrantedAuthorityRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import eu.europa.ec.etrustex.web.service.validation.model.GrantedAuthoritySpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintValidatorContext;
import java.util.Optional;

import static eu.europa.ec.etrustex.web.util.exchange.model.GroupType.ENTITY;
import static eu.europa.ec.etrustex.web.util.exchange.model.GroupType.ROOT;
import static eu.europa.ec.etrustex.web.util.exchange.model.RoleName.OPERATOR;
import static eu.europa.ec.etrustex.web.util.exchange.model.RoleName.SYS_ADMIN;
import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.mockGroup;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UniqueGrantedAuthorityValidatorTest {
    private static final String USERNAME = "username";
    @Mock
    private GrantedAuthorityRepository grantedAuthorityRepository;
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder constraintViolationBuilder;
    @Spy
    private ConstraintValidatorContext context;

    private UniqueGrantedAuthorityValidator uniqueGrantedAuthorityValidator;


    @BeforeEach
    public void init() {
        uniqueGrantedAuthorityValidator = new UniqueGrantedAuthorityValidator(grantedAuthorityRepository, groupRepository);
    }


    @Test
    void should_validate() {
        GrantedAuthoritySpec grantedAuthoritySpec = GrantedAuthoritySpec.builder()
                .userName(USERNAME)
                .groupId(1L)
                .roleName(OPERATOR)
                .build();

        given(grantedAuthorityRepository.existsByUserEcasIdAndGroupIdAndRoleNameAndEnabledTrue(anyString(), anyLong(), any(RoleName.class)))
                .willReturn(false);

        assertTrue(uniqueGrantedAuthorityValidator.isValid(grantedAuthoritySpec, context));
    }

    @Test
    void should_fail_validation_for_sys_admin() {
        Group group = mockGroup("group_id", "group_name", null, ROOT);
        GrantedAuthoritySpec grantedAuthoritySpec = GrantedAuthoritySpec.builder()
                .userName(USERNAME)
                .groupId(group.getId())
                .roleName(SYS_ADMIN)
                .build();


        given(grantedAuthorityRepository.existsByUserEcasIdAndGroupIdAndRoleNameAndEnabledTrue(anyString(), anyLong(), any(RoleName.class)))
                .willReturn(true);
        given(groupRepository.findById(anyLong())).willReturn(Optional.of(group));
        given(context.buildConstraintViolationWithTemplate(ValidationMessage.USER_ALREADY_SYS_ADMIN.getMessage()))
                .willReturn(constraintViolationBuilder);

        assertFalse(uniqueGrantedAuthorityValidator.isValid(grantedAuthoritySpec, context));

        verify(context).buildConstraintViolationWithTemplate(ValidationMessage.USER_ALREADY_SYS_ADMIN.getMessage());
    }

    @Test
    void should_fail_validation_for_non_sys_admin() {
        Group group = mockGroup("group_id", "group_name", mockGroup(), ENTITY);
        GrantedAuthoritySpec grantedAuthoritySpec = GrantedAuthoritySpec.builder()
                .userName(USERNAME)
                .groupId(group.getId())
                .roleName(OPERATOR)
                .build();


        given(grantedAuthorityRepository.existsByUserEcasIdAndGroupIdAndRoleNameAndEnabledTrue(anyString(), anyLong(), any(RoleName.class)))
                .willReturn(true);
        given(groupRepository.findById(anyLong())).willReturn(Optional.of(group));
        given(context.buildConstraintViolationWithTemplate(ValidationMessage.formatUniqueGrantedAuthorityMessage(group.getType(), grantedAuthoritySpec.getRoleName())))
                .willReturn(constraintViolationBuilder);

        assertFalse(uniqueGrantedAuthorityValidator.isValid(grantedAuthoritySpec, context));

        verify(context).buildConstraintViolationWithTemplate(ValidationMessage.formatUniqueGrantedAuthorityMessage(group.getType(), grantedAuthoritySpec.getRoleName()));
    }

    @Test
    void should_throw_if_group_not_found() {
        GrantedAuthoritySpec grantedAuthoritySpec = GrantedAuthoritySpec.builder()
                .userName(USERNAME)
                .groupId(1L)
                .roleName(OPERATOR)
                .build();

        given(grantedAuthorityRepository.existsByUserEcasIdAndGroupIdAndRoleNameAndEnabledTrue(anyString(), anyLong(), any(RoleName.class)))
                .willReturn(true);
        given(groupRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(EtxWebException.class, () -> uniqueGrantedAuthorityValidator.isValid(grantedAuthoritySpec, context));
    }
}
