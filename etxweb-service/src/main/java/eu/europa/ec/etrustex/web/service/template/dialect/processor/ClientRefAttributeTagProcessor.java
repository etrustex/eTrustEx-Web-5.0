/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2016, The THYMELEAF team (http://www.thymeleaf.org)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 * =============================================================================
 */
package eu.europa.ec.etrustex.web.service.template.dialect.processor;

import eu.europa.ec.etrustex.web.service.redirect.RedirectService;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;


public class ClientRefAttributeTagProcessor extends AbstractAttributeTagProcessor {
    private static final String ATTR_NAME = "clientRef";
    private static final int PRECEDENCE = 10000;
    private final RedirectService redirectService;


    public ClientRefAttributeTagProcessor(final String dialectPrefix, RedirectService redirectService) {
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

        final Long recipientGroupId = contextHolder.getRecipientGroupId();
        final Long messageId = contextHolder.getMessageId();

        final String clientReference = contextHolder.parseExpressionAsString(attributeValue);

        final String permalink = redirectService.createAttachmentPermalink(recipientGroupId, messageId, clientReference);

        structureHandler.setAttribute("href", permalink);

    }


}
