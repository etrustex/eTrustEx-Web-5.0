package eu.europa.ec.etrustex.web.service.template;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.expression.ThymeleafEvaluationContext;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ThymeleafTemplateServiceImpl implements ThymeleafTemplateService {
    private final TemplateEngine templateEngine;
    private final ApplicationContext applicationContext;
    private final ConversionService conversionService;


    @Override
    public String process(Long templateId, Map<String, Object> templateVariables) {
        Context context = new Context(LocaleContextHolder.getLocale(), templateVariables);

        final ThymeleafEvaluationContext evaluationContext = new ThymeleafEvaluationContext(applicationContext, conversionService);
        context.setVariable(ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME, evaluationContext);

        return templateEngine.process(String.valueOf(templateId), context);
    }
}
