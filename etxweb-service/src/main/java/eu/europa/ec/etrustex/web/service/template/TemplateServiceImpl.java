package eu.europa.ec.etrustex.web.service.template;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.etrustex.web.common.template.TemplateType;
import eu.europa.ec.etrustex.web.exchange.model.TemplateSpec;
import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummary;
import eu.europa.ec.etrustex.web.persistence.entity.Template;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.persistence.repository.TemplateRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import eu.europa.ec.etrustex.web.service.EncryptionService;
import eu.europa.ec.etrustex.web.service.security.GroupService;
import eu.europa.ec.etrustex.web.service.util.FSUtils;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

import static eu.europa.ec.etrustex.web.common.template.TemplateContextConstants.*;

@Service
@RequiredArgsConstructor
public class TemplateServiceImpl implements TemplateService {
    private final TemplateRepository templateRepository;
    private final GroupService groupService;
    private final GroupRepository groupRepository;
    private final ThymeleafTemplateService thymeleafTemplateService;
    private final EncryptionService encryptionService;
    private final ObjectMapper objectMapper;

    @Override
    public List<Template> getDefaultTemplates() {
        return templateRepository.getDefaultTemplates();
    }

    @Override
    public Template getDefaultTemplateByType(TemplateType type) {
        return templateRepository.getDefaultTemplates(type);
    }

    @Override
    public Template getByGroupType(Long groupId, TemplateType type) {
        Group group = groupService.findById(groupId);
        return getByGroupType(group, type);
    }

    @Override
    public Template getByGroupType(Group group, TemplateType type) {
        Group target = (group.getType() == GroupType.ENTITY) ? group.getParent() : group;

        if (target.getTemplates() == null || target.getTemplates().isEmpty()) {
            return getDefaultTemplateByType(type);
        }

        return target.getTemplates().stream()
                .filter(template -> template.getType() == type)
                .findFirst()
                .orElseGet(() -> getDefaultTemplateByType(type));
    }

    @Override
    public Template save(Template template) {
        return templateRepository.save(template);
    }

    @Override
    @Transactional
    public Template saveOrUpdate(TemplateSpec templateSpec) {
        Group group = groupService.findById(templateSpec.getGroupId());
        Template existingTemplate = group.getTemplates().stream()
                .filter(t -> t.getType() == templateSpec.getType())
                .findFirst()
                .orElse(null);

        if (templateSpec.isUseDefault()) {
            setDefaultTemplate(group, existingTemplate);
            return getDefaultTemplateByType(templateSpec.getType());
        }

        // Create a new template
        Template newTemplate = Template.builder()
                .type(templateSpec.getType())
                .content(templateSpec.getContent())
                .groups(Collections.singletonList(group))
                .build();

        if (existingTemplate != null) {
            if (existingTemplate.getGroups().size() == 1) {
                // Update the existing template
                existingTemplate.setContent(templateSpec.getContent());
                templateRepository.save(existingTemplate);
            } else {
                // If the existing template is associated with multiple groups, create a new one
                newTemplate = templateRepository.save(newTemplate);
                group.getTemplates().set(group.getTemplates().indexOf(existingTemplate), newTemplate);
                existingTemplate = newTemplate;
            }
        } else {
            // No existing template found, create a new one
            existingTemplate = templateRepository.save(newTemplate);
            group.getTemplates().add(existingTemplate);
        }

        groupRepository.save(group);

        return existingTemplate;
    }

    private void setDefaultTemplate(Group group, Template existingTemplate) {
        if (existingTemplate == null) {
            return;
        }

        // Remove the template from the group and save the group
        group.getTemplates().remove(existingTemplate);
        groupRepository.save(group);

        existingTemplate.getGroups().remove(group);
        if (existingTemplate.getGroups().isEmpty()) {
            // If the template is not associated with any other groups, delete it
            templateRepository.delete(existingTemplate);
        }
    }


    @Override
    public void delete(Template template) {
        templateRepository.delete(template);
    }

    @Override
    public Template findById(Long id) {
        return templateRepository.findById(id).orElse(null);
    }

    @Override
    public Map<String, Object> getTemplateVariables(MessageSummary messageSummary) {
        return getTemplateVariables(messageSummary, null);
    }

    @Override
    public Map<String, Object> getTemplateVariables(MessageSummary messageSummary, User recipientUser) {
        Message message = messageSummary.getMessage();
        Map<String, Object> templateVars = getTemplateVariables(message);

        templateVars.put(TPL_VARIABLES_RECIPIENT_GROUP_ID.getValue(), messageSummary.getRecipient().getId());
        templateVars.put(TPL_VARIABLES_RECIPIENT_GROUP_NAME.getValue(), messageSummary.getRecipient().getName());
        templateVars.put(TPL_VARIABLES_STATUS.getValue(), messageSummary.getStatus().name());

        if (recipientUser != null) {
            templateVars.put(TPL_VARIABLES_RECIPIENT_USERNAME.getValue(), recipientUser.getName());
        }

        templateVars.put(TPL_VARIABLES_NUMBER_OF_FILES.getValue(), messageSummary.getMessage().getAttachmentTotalNumber());

        addStatusChangeDateAndTime(messageSummary.getAuditingEntity().getModifiedDate(), templateVars);

        return templateVars;
    }

    @Override
    public Map<String, Object> getTemplateVariables(Message message) {
        Map<String, Object> templateVars = new HashMap<>();

        if (StringUtils.isNotEmpty(message.getTemplateVariables())) {
            // TODO: ETRUSTEX-8450: remove try/catch nesting once no unencrypted template variables remain
            try {
                Map<String, Object> customTemplateVars = this.objectMapper.readValue(
                        message.getTemplateVariables(),
                        new TypeReference<Map<String, Object>>() {
                        });

                templateVars.putAll(customTemplateVars);
            } catch (JsonProcessingException e) {
                try {
                    String templateVarsString = encryptionService.getDecryptedTemplateVariables(message);
                    if (templateVarsString == null) {
                        throw new EtxWebException("Cannot convert json to TemplateVariable");
                    }
                    Map<String, Object> customTemplateVars = this.objectMapper.readValue(
                            templateVarsString,
                            new TypeReference<Map<String, Object>>() {
                            });

                    templateVars.putAll(customTemplateVars);
                } catch (JsonProcessingException ex) {
                    throw new EtxWebException("Cannot convert json to TemplateVariable");
                }
            }
        }

        if (!templateVars.containsKey(TPL_VARIABLES_MESSAGE_SUBJECT.getValue())) {
            templateVars.put(TPL_VARIABLES_MESSAGE_SUBJECT.getValue(), message.getSubject());
        }
        templateVars.put(TPL_VARIABLES_MESSAGE_ID.getValue(), message.getId());
        templateVars.put(TPL_VARIABLES_SENDER_NAME.getValue(), message.getSenderUserName());
        templateVars.put(TPL_VARIABLES_SENDER_GROUP_ID.getValue(), message.getSenderGroup().getId());
        templateVars.put(TPL_VARIABLES_SENDER_GROUP_NAME.getValue(), message.getSenderGroup().getName());

        templateVars.put(TPL_VARIABLES_TOTAL_FILES_SIZE.getValue(), message.getAttachmentsTotalByteLength());
        templateVars.put(TPL_VARIABLES_MESSAGE_SENT_ON.getValue(), message.getSentOn());

        if (templateVars.containsKey(TPL_VARIABLES_TOTAL_FILES_SIZE.getValue()) && templateVars.get(TPL_VARIABLES_TOTAL_FILES_SIZE.getValue()) != null) {
            Long fileSize = (Long) templateVars.get(TPL_VARIABLES_TOTAL_FILES_SIZE.getValue());
            templateVars.put(TPL_VARIABLES_TOTAL_FILES_SIZE.getValue(), FSUtils.format(fileSize, 2));
        }

        return templateVars;
    }

    @Override
    public String getSubject(MessageSummary messageSummary, Map<String, Object> templateVars, String subjectTemplate) {
        String subject;

        if (templateVars == null || templateVars.isEmpty() || templateVars.get("subject") == null) {
            subject = String.format(subjectTemplate, messageSummary.getStatus().name(), messageSummary.getMessage().getSubject());
        } else {
            subject = templateVars.get("subject").toString();
        }

        return subject;
    }

    @Override
    public String process(Long templateId, Map<String, Object> templateVariables) {
        return thymeleafTemplateService.process(templateId, templateVariables);
    }

    private void addStatusChangeDateAndTime(Date statusChangeDate, Map<String, Object> templateVars) {
        String datePattern = "MM/dd/yyyy";
        String timePattern = "HH:mm:ss";
        SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);
        SimpleDateFormat timeFormatter = new SimpleDateFormat(timePattern);

        if (statusChangeDate == null) {
            statusChangeDate = new Date();
        }
        templateVars.put(TPL_VARIABLES_STATUS_CHANGE_DATE.getValue(), dateFormatter.format(statusChangeDate));
        templateVars.put(TPL_VARIABLES_STATUS_CHANGE_TIME.getValue(), timeFormatter.format(statusChangeDate));
    }
}
