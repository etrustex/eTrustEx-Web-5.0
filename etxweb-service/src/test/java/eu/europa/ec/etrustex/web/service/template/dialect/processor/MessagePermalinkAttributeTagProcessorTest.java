package eu.europa.ec.etrustex.web.service.template.dialect.processor;

import eu.europa.ec.etrustex.web.persistence.entity.redirect.AttachmentsDownloadRedirect;
import eu.europa.ec.etrustex.web.persistence.entity.redirect.InboxMessageDetailsRedirect;
import eu.europa.ec.etrustex.web.persistence.entity.redirect.Redirect;
import eu.europa.ec.etrustex.web.persistence.entity.redirect.SentMessageDetailsRedirect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import static eu.europa.ec.etrustex.web.common.template.TemplateContextConstants.TPL_VARIABLES_SENDER_GROUP_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class MessagePermalinkAttributeTagProcessorTest extends AttributeTagProcessorTest {
    private static final String PERMALINK = "permalink";
    private enum AttributeValues {
        INBOX, SENT, DOWNLOAD
    }

    @Captor
    protected ArgumentCaptor<Redirect> redirectArgumentCaptor;

    MessagePermalinkAttributeTagProcessor messagePermalinkAttributeTagProcessor;

    @Override
    @BeforeEach
    public void init() {
        super.init();
        String dialectPrefix = "message";
        messagePermalinkAttributeTagProcessor = new MessagePermalinkAttributeTagProcessor(dialectPrefix, redirectService);
    }

    @Test
    void should_generate_link_tag_to_inbox() {
        given(redirectService.createPermalink(any(Redirect.class))).willReturn(PERMALINK);
        given(context.getVariable(TPL_VARIABLES_SENDER_GROUP_ID.getValue())).willReturn(RANDOM.nextLong());

        messagePermalinkAttributeTagProcessor.doProcess(context, tag, attributeName, AttributeValues.INBOX.toString(), structureHandler);

        verify(redirectService, times(1)).createPermalink(redirectArgumentCaptor.capture());

        verify(structureHandler, times(1)).setAttribute(
                attributeNameArgumentCaptor.capture(), attributeValueArgumentCaptor.capture());

        assertEquals("href", attributeNameArgumentCaptor.getValue());
        assertEquals(PERMALINK, attributeValueArgumentCaptor.getValue());
        assertTrue(redirectArgumentCaptor.getValue() instanceof InboxMessageDetailsRedirect);
    }

    @Test
    void should_generate_link_tag_to_inbox_download() {
        given(redirectService.createPermalink(any(Redirect.class))).willReturn(PERMALINK);
        given(context.getVariable(TPL_VARIABLES_SENDER_GROUP_ID.getValue())).willReturn(RANDOM.nextLong());

        messagePermalinkAttributeTagProcessor.doProcess(context, tag, attributeName, AttributeValues.DOWNLOAD.toString(), structureHandler);

        verify(redirectService, times(1)).createPermalink(redirectArgumentCaptor.capture());

        verify(structureHandler, times(1)).setAttribute(
                attributeNameArgumentCaptor.capture(), attributeValueArgumentCaptor.capture());

        assertEquals("href", attributeNameArgumentCaptor.getValue());
        assertEquals(PERMALINK, attributeValueArgumentCaptor.getValue());
        assertTrue(redirectArgumentCaptor.getValue() instanceof AttachmentsDownloadRedirect);
    }

    @Test
    void should_generate_link_tag_to_sent() {
        given(redirectService.createPermalink(any(Redirect.class))).willReturn(PERMALINK);

        messagePermalinkAttributeTagProcessor.doProcess(context, tag, attributeName, AttributeValues.SENT.toString(), structureHandler);

        verify(redirectService, times(1)).createPermalink(redirectArgumentCaptor.capture());

        verify(structureHandler, times(1)).setAttribute(
                attributeNameArgumentCaptor.capture(), attributeValueArgumentCaptor.capture());

        assertEquals("href", attributeNameArgumentCaptor.getValue());
        assertEquals(PERMALINK, attributeValueArgumentCaptor.getValue());
        assertTrue(redirectArgumentCaptor.getValue() instanceof SentMessageDetailsRedirect);
    }
}
