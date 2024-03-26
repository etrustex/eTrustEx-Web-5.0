package eu.europa.ec.etrustex.web.service.validation.validator;

import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.service.security.GroupService;
import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintValidatorContext;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class EntityIsEmptyValidatorTest {
    @Mock
    private GroupService groupService;
    @Mock
    private ConstraintValidatorContext context;

    private EntityIsEmptyValidator groupIsEmptyValidator;

    @BeforeEach
    public void init() {
        groupIsEmptyValidator = new EntityIsEmptyValidator(groupService);
    }

    @Test
    void should_validate() {
        Group group = Group.builder().id(1L).type(GroupType.ENTITY).build();

        given(groupService.hasMessages(anyLong())).willReturn(false);
        assertTrue(groupIsEmptyValidator.isValid(group, context));
    }

    @Test
    void should_not_validate() {
        Group group = Group.builder().id(1L).type(GroupType.ENTITY).build();

        given(groupService.hasMessages(anyLong())).willReturn(true);
        given(context.buildConstraintViolationWithTemplate(anyString())).willReturn(new ConstraintValidatorContext.ConstraintViolationBuilder() {
            @Override
            public NodeBuilderDefinedContext addNode(String name) {
                return null;
            }

            @Override
            public NodeBuilderCustomizableContext addPropertyNode(String name) {
                return null;
            }

            @Override
            public LeafNodeBuilderCustomizableContext addBeanNode() {
                return null;
            }

            @Override
            public ContainerElementNodeBuilderCustomizableContext addContainerElementNode(String name, Class<?> containerType, Integer typeArgumentIndex) {
                return null;
            }

            @Override
            public NodeBuilderDefinedContext addParameterNode(int index) {
                return null;
            }

            @Override
            public ConstraintValidatorContext addConstraintViolation() {
                return null;
            }
        });
        assertFalse(groupIsEmptyValidator.isValid(group, context));
    }
}
