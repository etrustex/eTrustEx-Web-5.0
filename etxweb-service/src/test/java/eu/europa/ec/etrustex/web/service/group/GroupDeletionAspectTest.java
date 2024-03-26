package eu.europa.ec.etrustex.web.service.group;

import eu.europa.ec.etrustex.web.persistence.entity.Attachment;
import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.SenderPreferences;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.repository.AttachmentRepository;
import eu.europa.ec.etrustex.web.persistence.repository.MessageRepository;
import eu.europa.ec.etrustex.web.persistence.repository.MessageSummaryRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import eu.europa.ec.etrustex.web.service.jobs.DeleteGroupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"java:S100"}) /* Suppress method names false positive. */
class GroupDeletionAspectTest {
    @Mock
    MessageRepository messageRepository;
    @Mock
    AttachmentRepository attachmentRepository;
    @Mock
    private MessageSummaryRepository messageSummaryRepository;
    @Mock
    private GroupRepository groupRepository;

    @Mock
    private DeleteGroupService deleteGroupService;

    GroupDeletionAspect groupDeletionAspect;

    @BeforeEach
    void setUp() {
        groupDeletionAspect = new GroupDeletionAspect(messageRepository, attachmentRepository, groupRepository, messageSummaryRepository, deleteGroupService);
    }

    @Test
    void should_delete_entity_without_attachments() {
        Group group = mockGroup();
        SenderPreferences senderPreferences = SenderPreferences.builder().id(12L).build();
        group.setSenderPreferences(senderPreferences);

        Message message = mockMessage();
        Attachment attachment = mockAttachment(message);

        groupDeletionAspect.deleteGroup(group);
        verify(attachmentRepository, times(0)).delete(attachment);
    }

    @Test
    void should_delete_entity_with_attachments() {
        Group group = mockGroup();
        SenderPreferences senderPreferences = SenderPreferences.builder().id(12L).build();
        group.setSenderPreferences(senderPreferences);

        Message message = mockMessage();

        given(groupRepository.getTotalRecordsFromMessageAndMessageSummaryByGroupId(group.getId())).willReturn(0);
        given(messageRepository.findBySenderGroupAndAttachmentTotalNumberIsNull(group)).willReturn(Collections.nCopies(3, message).stream());

        groupDeletionAspect.deleteGroup(group);
        verify(attachmentRepository, times(3)).deleteByMessage(message);
    }

    @Test
    void should_disable_entity_for_deletion() {
        Group group = mockGroup();
        SenderPreferences senderPreferences = SenderPreferences.builder().id(12L).build();
        group.setSenderPreferences(senderPreferences);

        Message message = mockMessage();

        given(groupRepository.getTotalRecordsFromMessageAndMessageSummaryByGroupId(group.getId())).willReturn(1);

        groupDeletionAspect.deleteGroup(group);
        verify(attachmentRepository, times(0)).deleteByMessage(message);

    }

}
