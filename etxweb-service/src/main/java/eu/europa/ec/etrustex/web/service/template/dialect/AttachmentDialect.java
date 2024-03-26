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
package eu.europa.ec.etrustex.web.service.template.dialect;

import eu.europa.ec.etrustex.web.service.redirect.RedirectService;
import eu.europa.ec.etrustex.web.service.template.dialect.processor.ClientRefAttributeTagProcessor;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.standard.processor.StandardXmlNsTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;

import java.util.HashSet;
import java.util.Set;

public class AttachmentDialect extends AbstractProcessorDialect {

    private static final String DIALECT_NAME = "Attachment Dialect";
    private final RedirectService redirectService;


    public AttachmentDialect(RedirectService redirectService) {
        super(DIALECT_NAME, "attachment", StandardDialect.PROCESSOR_PRECEDENCE);
        this.redirectService = redirectService;
    }

    public Set<IProcessor> getProcessors(final String dialectPrefix) {
        final Set<IProcessor> processors = new HashSet<>();
        processors.add(new ClientRefAttributeTagProcessor(dialectPrefix, redirectService));
        processors.add(new StandardXmlNsTagProcessor(TemplateMode.HTML, dialectPrefix));

        return processors;
    }


}
