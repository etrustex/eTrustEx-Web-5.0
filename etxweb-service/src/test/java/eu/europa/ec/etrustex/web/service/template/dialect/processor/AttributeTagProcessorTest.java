package eu.europa.ec.etrustex.web.service.template.dialect.processor;

import eu.europa.ec.etrustex.web.service.redirect.RedirectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.StandardExpressionParser;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static eu.europa.ec.etrustex.web.common.template.TemplateContextConstants.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.thymeleaf.standard.expression.StandardExpressions.STANDARD_EXPRESSION_PARSER_ATTRIBUTE_NAME;
import static org.thymeleaf.standard.expression.StandardExpressions.STANDARD_VARIABLE_EXPRESSION_EVALUATOR_ATTRIBUTE_NAME;

@ExtendWith(MockitoExtension.class)
abstract class AttributeTagProcessorTest {
    @Mock
    protected RedirectService redirectService;
    @Mock
    protected ITemplateContext context;
    @Mock
    protected IProcessableElementTag tag;
    @Mock
    protected AttributeName attributeName;
    @Mock
    protected IElementTagStructureHandler structureHandler;

    @Captor
    protected ArgumentCaptor<String> attributeNameArgumentCaptor;

    @Captor
    protected ArgumentCaptor<String> attributeValueArgumentCaptor;

    protected Map<String, Object> executionAttributes;
    protected static final Random RANDOM = new SecureRandom();


    @BeforeEach
    public void init() {
        executionAttributes = new HashMap<>();
        executionAttributes.put(STANDARD_EXPRESSION_PARSER_ATTRIBUTE_NAME, new StandardExpressionParser());
        executionAttributes.put(STANDARD_VARIABLE_EXPRESSION_EVALUATOR_ATTRIBUTE_NAME, new StandardExpressionParser());

        given(context.containsVariable(anyString())).willReturn(true);

        when(context.getVariable(anyString())).thenAnswer(invocation -> {
            String paranm = (String) invocation.getArguments()[0];

            if (paranm.equals(TPL_VARIABLES_RECIPIENT_GROUP_ID.getValue()) || paranm.equals(TPL_VARIABLES_SENDER_GROUP_ID.getValue())) {
                return RANDOM.nextLong();
            }

            return paranm;
        });

        given(context.getVariable(TPL_VARIABLES_MESSAGE_ID.getValue())).willReturn(RANDOM.nextLong());
    }
}
