package eu.europa.ec.etrustex.web.service.template;

import java.util.Map;

public interface ThymeleafTemplateService {
    String process(Long templateId, Map<String, Object> templateVariables);
}
