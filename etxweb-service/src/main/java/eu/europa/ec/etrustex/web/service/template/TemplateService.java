package eu.europa.ec.etrustex.web.service.template;

import eu.europa.ec.etrustex.web.common.template.TemplateType;
import eu.europa.ec.etrustex.web.exchange.model.TemplateSpec;
import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummary;
import eu.europa.ec.etrustex.web.persistence.entity.Template;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;

import java.util.List;
import java.util.Map;

public interface TemplateService {
    List<Template> getDefaultTemplates();

    Template getDefaultTemplateByType(TemplateType type);

    Template getByGroupType(Long groupId, TemplateType type);

    Template getByGroupType(Group group, TemplateType type);

    Template save(Template template);

    Template saveOrUpdate(TemplateSpec templateSpec);

    void delete(Template template);

    Template findById(Long id);

    Map<String, Object> getTemplateVariables(MessageSummary messageSummary);

    Map<String, Object> getTemplateVariables(MessageSummary messageSummary, User recipientUser);

    Map<String, Object> getTemplateVariables(Message message);

    String getSubject(MessageSummary messageSummary, Map<String, Object> templateVarsString, String subjectTemplate);

    String process(Long templateId, Map<String, Object> templateVariables);
}
