package eu.europa.ec.etrustex.web.service.template;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.etrustex.web.common.template.TemplateType;
import eu.europa.ec.etrustex.web.exchange.model.TemplateSpec;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummary;
import eu.europa.ec.etrustex.web.persistence.entity.Template;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.repository.TemplateRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import eu.europa.ec.etrustex.web.service.EncryptionService;
import eu.europa.ec.etrustex.web.service.security.GroupService;
import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import eu.europa.ec.etrustex.web.util.exchange.model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static eu.europa.ec.etrustex.web.common.template.TemplateContextConstants.TPL_VARIABLES_MESSAGE_SUBJECT;
import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TemplateServiceImplTest {
    private static final String VALUE = "value";
    private static final String SUBJECT = "SUBJECT";

    @Mock
    private TemplateRepository templateRepository;
    @Mock
    private GroupService groupService;
    @Mock
    private EncryptionService encryptionService;
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private ThymeleafTemplateService thymeleafTemplateService;
    @Mock
    private ObjectMapper objectMapper;
    private TemplateService templateService;


    private static final String SUBJECT_FOR_NEW_STATUS = "New %1$s status in Etrustex Web for message %2$s";

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        this.templateService = new TemplateServiceImpl(templateRepository, groupService, groupRepository, thymeleafTemplateService, encryptionService, objectMapper);
    }

    @Test
    void should_save() {
        Template template = mockTemplate(100L);

        given(this.templateRepository.save(any())).willReturn(template);

        assertThat(this.templateService.save(any())).isEqualTo(template);
    }

    @Test
    void should_save_or_update_existing() {
        Template template = mockTemplate(100L);
        Group group = mockGroup();

        template.setType(TemplateType.BUSINESS_DELETION_NOTIFICATION);
        template.setGroups((Collections.singletonList(group)));

        List<Template> templates = new ArrayList<>();
        templates.add(template);
        group.setTemplates(templates);

        given(groupService.findById(any())).willReturn(group);
        given(templateRepository.save(any())).willReturn(template);

        TemplateSpec spec = TemplateSpec.builder()
                .groupId(group.getId())
                .type(template.getType())
                .content(template.getContent())
                .build();
        assertThat(templateService.saveOrUpdate(spec)).isEqualTo(template);
    }

    @Test
    void should_save_or_update_new_one() {
        Template template = mockTemplate(100L);
        Group group = mockGroup();

        template.setType(TemplateType.BUSINESS_DELETION_NOTIFICATION);

        group.setTemplates(new ArrayList<>());

        given(groupService.findById(any())).willReturn(group);
        given(templateRepository.save(any())).willReturn(template);

        TemplateSpec spec = TemplateSpec.builder()
                .groupId(group.getId())
                .type(template.getType())
                .content(template.getContent())
                .build();
        assertThat(templateService.saveOrUpdate(spec)).isEqualTo(template);
    }

    @Test
    void should_save_or_update_multiple_groups() {
        Template template = mockTemplate(100L);
        Group group = mockGroup();

        template.setType(TemplateType.BUSINESS_DELETION_NOTIFICATION);
        template.setGroups((Arrays.asList(group, mockGroup())));

        List<Template> templates = new ArrayList<>();
        templates.add(template);
        group.setTemplates(templates);

        given(groupService.findById(any())).willReturn(group);
        given(templateRepository.save(any())).willReturn(template);

        TemplateSpec spec = TemplateSpec.builder()
                .groupId(group.getId())
                .type(template.getType())
                .content(template.getContent())
                .build();
        assertThat(templateService.saveOrUpdate(spec)).isEqualTo(template);
    }

    @Test
    void should_save_or_update_with_default() {
        Template template = mockTemplate(100L);
        Group group = mockGroup();

        template.setType(TemplateType.BUSINESS_DELETION_NOTIFICATION);

        List<Group> groups = new ArrayList<>();
        groups.add(group);
        template.setGroups(groups);

        List<Template> templates = new ArrayList<>();
        templates.add(template);
        group.setTemplates(templates);

        given(groupService.findById(any())).willReturn(group);
        given(templateRepository.getDefaultTemplates(any())).willReturn(template);

        TemplateSpec spec = TemplateSpec.builder()
                .groupId(group.getId())
                .type(template.getType())
                .useDefault(true)
                .build();
        assertThat(templateService.saveOrUpdate(spec)).isEqualTo(template);
    }

    @Test
    void should_delete() {
        Template template = mockTemplate(100L);
        this.templateService.delete(template);

        verify(templateRepository, times(1)).delete(template);
    }

    @Test
    void should_find_by_id() {
        Template template = mockTemplate(100L);
        given(this.templateRepository.findById(any())).willReturn(Optional.of(template));

        assertThat(this.templateService.findById(any())).isEqualTo(template);
    }

    @Test
    void should_get_subject() {
        MessageSummary messageSummary = mockMessageSummary(Status.DELIVERED);
        assertThat(templateService.getSubject(messageSummary, null, SUBJECT_FOR_NEW_STATUS)).contains("status in Etrustex Web for message");
    }

    @Test
    void should_get_subject_if_tpl_vars_not_empty() throws JsonProcessingException {
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("subject", SUBJECT);
        templateVariables.put("key", VALUE);
        MessageSummary messageSummary = mockMessageSummary(Status.DELIVERED);
        messageSummary.getMessage().setTemplateVariables(objectMapper.writeValueAsString(templateVariables));

        assertThat(templateService.getSubject(messageSummary, templateVariables, SUBJECT_FOR_NEW_STATUS)).contains(SUBJECT);
    }

    @Test
    void should_process() {
        given(thymeleafTemplateService.process(any(), any())).willReturn("Template_processed");
        assertThat(templateService.process(any(), any())).isEqualTo("Template_processed");
    }

    @Test
    void should_get_tpl_vars_if_empty() {
        MessageSummary messageSummary = mockMessageSummary(Status.DELIVERED);
        assertThat(templateService.getTemplateVariables(messageSummary).entrySet().size()).isEqualTo(13);
    }

    @Test
    void should_get_tpl_vars_if_not_empty() throws JsonProcessingException {
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put(TPL_VARIABLES_MESSAGE_SUBJECT.getValue(), SUBJECT);
        templateVariables.put("key", VALUE);
        MessageSummary messageSummary = mockMessageSummary(Status.DELIVERED);
        messageSummary.getMessage().setTemplateVariables(objectMapper.writeValueAsString(templateVariables));

        assertThat(templateService.getTemplateVariables(messageSummary).entrySet().size()).isEqualTo(14);
        assertThat(templateService.getTemplateVariables(messageSummary)).containsEntry("key", VALUE);
    }

    @Test
    void should_throw_EtxWebException_if_tpl_vars_wrong() {
        MessageSummary messageSummary = mockMessageSummary(Status.DELIVERED);
        messageSummary.getMessage().setTemplateVariables("foo");

        assertThrows(EtxWebException.class, () -> templateService.getTemplateVariables(messageSummary));
    }
}
