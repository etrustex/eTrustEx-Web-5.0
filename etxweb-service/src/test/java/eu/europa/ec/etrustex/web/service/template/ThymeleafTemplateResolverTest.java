package eu.europa.ec.etrustex.web.service.template;

import eu.europa.ec.etrustex.web.common.template.TemplateType;
import eu.europa.ec.etrustex.web.persistence.entity.Template;
import eu.europa.ec.etrustex.web.persistence.repository.TemplateRepository;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.templateresource.StringTemplateResource;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ThymeleafTemplateResolverTest {
    @Mock
    private TemplateRepository templateRepository;
    private String egreffeEmailTplHtml;
    private Map<String, Object> templateVariables;

    private ThymeleafTemplateResolver thymeleafTemplateResolver;


    @BeforeEach
    public void setUp() throws IOException {
        File egreffeEmailTpl = new File("build/resources/custom-templates/egreffe-email.html");
        egreffeEmailTplHtml = FileUtils.readFileToString(egreffeEmailTpl, StandardCharsets.UTF_8);
        thymeleafTemplateResolver = new ThymeleafTemplateResolver(templateRepository);

        templateVariables = new HashMap<>();
    }

    @Test
    void compute_template_resource_returns_instance_of_StringTemplateResource() {
        given(templateRepository.findById(1L)).willReturn(Optional.of(new Template(1L, TemplateType.BUSINESS_DELETION_NOTIFICATION, egreffeEmailTplHtml, Collections.emptyList())));

        ITemplateResource templateResource = thymeleafTemplateResolver.computeTemplateResource(any(), null, "1", templateVariables);

        assertThat(templateResource).isInstanceOf(StringTemplateResource.class);
    }

    @Test
    void should_throw_exception_when_templateId_is_null_or_empty() {
        IEngineConfiguration iEngineConfiguration = new SpringTemplateEngine().getConfiguration();
        assertThatThrownBy(() -> thymeleafTemplateResolver.computeTemplateResource(iEngineConfiguration, null, "", templateVariables))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("templateId cannot be null or empty");
    }
}
