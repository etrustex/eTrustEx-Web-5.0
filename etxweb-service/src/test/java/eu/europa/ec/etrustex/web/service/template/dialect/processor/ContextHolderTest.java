package eu.europa.ec.etrustex.web.service.template.dialect.processor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.context.ITemplateContext;

import java.security.SecureRandom;
import java.util.Random;

import static eu.europa.ec.etrustex.web.common.template.TemplateContextConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ContextHolderTest {
    @Mock
    private ITemplateContext context;

    private ContextHolder contextHolder;

    private static final Random RANDOM = new SecureRandom();

    @BeforeEach
    public void init() {
        contextHolder = new ContextHolder(context);
    }

    @Test
    void should_validate_context() {
        given(context.containsVariable(anyString())).willReturn(true);
        assertDoesNotThrow(() -> contextHolder.validateContext());
    }

    @Test
    void should_not_validate_context_if_missing_status_tpl_var() {
        assertThrows(IllegalArgumentException.class, () -> contextHolder.validateContext());
    }

    @Test
    void should_not_validate_context_if_missing_recipient_tpl_var() {
        assertThrows(IllegalArgumentException.class, () -> contextHolder.validateContext());
    }

    @Test
    void should_not_validate_context_if_missing_sender_tpl_var() {
        assertThrows(IllegalArgumentException.class, () -> contextHolder.validateContext());
    }

    @Test
    void should_not_validate_context_if_missing_msg_id_tpl_var() {
        assertThrows(IllegalArgumentException.class, () -> contextHolder.validateContext());
    }

    @Test
    void should_get_recipient_group_id() {
        long recipientGroupId = RANDOM.nextLong();
        given(context.getVariable(TPL_VARIABLES_RECIPIENT_GROUP_ID.getValue())).willReturn(recipientGroupId);
        assertEquals(recipientGroupId, contextHolder.getRecipientGroupId());
    }

    @Test
    void should_get_sender_group_id() {
        long senderGroupId = RANDOM.nextLong();
        given(context.getVariable(TPL_VARIABLES_SENDER_GROUP_ID.getValue())).willReturn(senderGroupId);
        assertEquals(senderGroupId, contextHolder.getSenderGroupId());
    }

    @Test
    void should_get_msg_id() {
        long messageId = RANDOM.nextLong();
        given(context.getVariable(TPL_VARIABLES_MESSAGE_ID.getValue())).willReturn(messageId);
        assertEquals(messageId, contextHolder.getMessageId().longValue());
    }
}
