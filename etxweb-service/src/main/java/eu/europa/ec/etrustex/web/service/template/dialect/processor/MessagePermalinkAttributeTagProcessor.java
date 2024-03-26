package eu.europa.ec.etrustex.web.service.template.dialect.processor;

import eu.europa.ec.etrustex.web.persistence.entity.redirect.AttachmentsDownloadRedirect;
import eu.europa.ec.etrustex.web.persistence.entity.redirect.InboxMessageDetailsRedirect;
import eu.europa.ec.etrustex.web.persistence.entity.redirect.Redirect;
import eu.europa.ec.etrustex.web.persistence.entity.redirect.SentMessageDetailsRedirect;
import eu.europa.ec.etrustex.web.service.redirect.RedirectService;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

public class MessagePermalinkAttributeTagProcessor extends AbstractAttributeTagProcessor {
    private static final String ATTR_NAME = "permalink";

    private enum AttributeValues {
        INBOX, SENT, DOWNLOAD
    }

    private static final int PRECEDENCE = 10000;
    private final RedirectService redirectService;

    public MessagePermalinkAttributeTagProcessor(final String dialectPrefix, RedirectService redirectService) {
        super(
                TemplateMode.HTML, // This processor will apply only to HTML mode
                dialectPrefix,     // Prefix to be applied to name for matching
                null,              // No tag name: match any tag name
                false,             // No prefix to be applied to tag name
                ATTR_NAME,         // Name of the attribute that will be matched
                true,              // Apply dialect prefix to attribute name
                PRECEDENCE,        // Precedence (inside dialect's own precedence)
                true);             // Remove the matched attribute afterwards
        this.redirectService = redirectService;
    }

    @Override
    protected void doProcess(
            final ITemplateContext context, final IProcessableElementTag tag,
            final AttributeName attributeName, final String attributeValue,
            final IElementTagStructureHandler structureHandler) {

        ContextHolder contextHolder = new ContextHolder(context);
        contextHolder.validateContext();

        Long recipientGroupId = contextHolder.getRecipientGroupId();
        Long senderGroupId = contextHolder.getSenderGroupId();
        Long messageId = contextHolder.getMessageId();

        Redirect redirect;

        switch (AttributeValues.valueOf(attributeValue.toUpperCase())) {
            case DOWNLOAD:
                redirect = AttachmentsDownloadRedirect
                        .builder()
                        .groupId(recipientGroupId)
                        .messageId(messageId)
                        .build();

                break;

            case SENT:
                redirect = SentMessageDetailsRedirect
                        .builder()
                        .groupId(senderGroupId)
                        .messageId(messageId)
                        .build();

                break;

            default:
                redirect = InboxMessageDetailsRedirect
                        .builder()
                        .groupId(recipientGroupId)
                        .messageId(messageId)
                        .build();

                break;
        }

        final String permalink = redirectService.createPermalink(redirect);

        structureHandler.setAttribute("href", permalink);

    }
}
