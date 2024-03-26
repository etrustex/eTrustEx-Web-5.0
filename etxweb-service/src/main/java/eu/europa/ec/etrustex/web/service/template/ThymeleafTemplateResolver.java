package eu.europa.ec.etrustex.web.service.template;

import eu.europa.ec.etrustex.web.persistence.entity.Template;
import eu.europa.ec.etrustex.web.persistence.repository.TemplateRepository;
import org.springframework.stereotype.Component;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.StringTemplateResolver;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.templateresource.StringTemplateResource;
import org.thymeleaf.util.Validate;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

/**
 * Implementation of {@link ITemplateResolver} that extends {@link StringTemplateResolver} and creates
 * {@link StringTemplateResource} instances for template resources from {@link Template} entities.
 * <p>
 * Sets {@link #getResolvablePatternSpec()} to * by default
 */
@Component
public class ThymeleafTemplateResolver extends StringTemplateResolver {
    private final TemplateRepository templateRepository;

    /**
     * Creates a new instance of this template resolver.
     *
     * @param templateRepository ({@link TemplateRepository}). The service that returns the {@link Template} entity
     */
    public ThymeleafTemplateResolver(TemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
        this.setResolvablePatterns(new HashSet<>(Collections.singletonList("*")));
    }


    /**
     * Computes the resolved template resource.
     *
     * @param configuration                the engine configuration.
     * @param ownerTemplate                the owner template, if the resource being computed is a fragment. Might be null.
     * @param templateId                   the template id to be resolved ({@link Template#getId()}).
     * @param templateResolutionAttributes the template resolution attributes, if any. Might be null.
     * @return the template resource, or null if this template cannot be resolved (or the resource does not exist).
     */
    @Override
    protected ITemplateResource computeTemplateResource(IEngineConfiguration configuration, String ownerTemplate, String templateId, Map<String, Object> templateResolutionAttributes) {
        Validate.notEmpty(templateId, "templateId cannot be null or empty");

        return templateRepository.findById(Long.parseLong(templateId))
                .map(value -> super.computeTemplateResource(configuration, ownerTemplate, value.getContent(), templateResolutionAttributes))
                .orElse(null);

    }
}
