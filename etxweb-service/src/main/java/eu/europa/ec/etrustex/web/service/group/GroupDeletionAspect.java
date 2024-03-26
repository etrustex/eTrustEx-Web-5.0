package eu.europa.ec.etrustex.web.service.group;

import eu.europa.ec.etrustex.web.persistence.entity.MessageSummary;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.repository.AttachmentRepository;
import eu.europa.ec.etrustex.web.persistence.repository.MessageRepository;
import eu.europa.ec.etrustex.web.persistence.repository.MessageSummaryRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import eu.europa.ec.etrustex.web.service.jobs.DeleteGroupService;
import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import lombok.AllArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Aspect
@AllArgsConstructor
public class GroupDeletionAspect {

    private final MessageRepository messageRepository;
    private final AttachmentRepository attachmentRepository;
    private final GroupRepository groupRepository;
    private final MessageSummaryRepository messageSummaryRepository;
    private final DeleteGroupService deleteGroupService;

    @Before(value = "execution(* eu.europa.ec.etrustex.web.service.security.GroupService.deleteGroup(..)) && args(group)", argNames = "group")
    public void deleteGroup(Group group) {
        deleteGroupService.removeGroupConfigurations(group);
        boolean isEmpty = groupRepository.getTotalRecordsFromMessageAndMessageSummaryByGroupId(group.getId()) == 0;
        if (isEmpty) {
            messageRepository.findBySenderGroupAndAttachmentTotalNumberIsNull(group).forEach(message -> {
                attachmentRepository.deleteByMessage(message);
                messageRepository.delete(message);
            });

        } else {
            disableMessageSummariesForDeletion(group);
            disableEntityForDeletion(group);
        }

    }

    private void disableEntityForDeletion(Group group) {
        if (group.getType().equals(GroupType.ENTITY)) {
            group.setActive(false);
            group.setPendingDeletion(true);
            groupRepository.save(group);
        }
    }

    private void disableMessageSummariesForDeletion(Group group) {
        List<MessageSummary> messageSummaries = messageSummaryRepository.findByRecipient(group);
        messageSummaries.forEach(messageSummary -> {
            messageSummary.setActive(false);
        });

        messageSummaryRepository.saveAll(messageSummaries);
    }
}
