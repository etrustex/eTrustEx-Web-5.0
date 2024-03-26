package eu.europa.ec.etrustex.web.service.template.dialect.processor;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;

import static eu.europa.ec.etrustex.web.common.template.TemplateContextConstants.*;
import static org.thymeleaf.util.Validate.isTrue;

final class ContextHolder {
    private final ITemplateContext context;
    public static final String CONTEXT_MUST_CONTAIN_VARIABLE = "context must contain variable '";

    ContextHolder(ITemplateContext context) {
        this.context = context;
    }

    void validateContext() {
        isTrue(context.containsVariable(TPL_VARIABLES_STATUS.getValue()), CONTEXT_MUST_CONTAIN_VARIABLE + TPL_VARIABLES_STATUS + "'. E.g.: context.setVariable(status, status);");
        isTrue(context.containsVariable(TPL_VARIABLES_RECIPIENT_GROUP_ID.getValue()), CONTEXT_MUST_CONTAIN_VARIABLE + TPL_VARIABLES_RECIPIENT_GROUP_ID + "'. E.g.: context.setVariable(recipientGroupId, recipientGroupId);");
        isTrue(context.containsVariable(TPL_VARIABLES_SENDER_GROUP_ID.getValue()), CONTEXT_MUST_CONTAIN_VARIABLE + TPL_VARIABLES_SENDER_GROUP_ID + "'. E.g.: context.setVariable(senderGroupId, senderGroupId);");
        isTrue(context.containsVariable(TPL_VARIABLES_MESSAGE_ID.getValue()), CONTEXT_MUST_CONTAIN_VARIABLE + TPL_VARIABLES_MESSAGE_ID + "'. E.g.: context.setVariable(messageId, messageId);");
    }

    String parseExpressionAsString(final String attributeValue) {
        final IEngineConfiguration configuration = context.getConfiguration();
        final IStandardExpressionParser parser = StandardExpressions.getExpressionParser(configuration);
        final IStandardExpression expression = parser.parseExpression(context, attributeValue);

        return (String) expression.execute(context);
    }

    Long getRecipientGroupId() {
        return Long.valueOf(context.getVariable(TPL_VARIABLES_RECIPIENT_GROUP_ID.getValue()).toString());
    }

    Long getSenderGroupId() {
        return Long.valueOf(context.getVariable(TPL_VARIABLES_SENDER_GROUP_ID.getValue()).toString());
    }

    Long getMessageId() {
        return Long.valueOf(context.getVariable(TPL_VARIABLES_MESSAGE_ID.getValue()).toString());
    }
}
