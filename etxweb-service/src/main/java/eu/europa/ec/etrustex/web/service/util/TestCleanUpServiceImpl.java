package eu.europa.ec.etrustex.web.service.util;

import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.repository.MessageRepository;
import eu.europa.ec.etrustex.web.persistence.repository.MessageUserStatusRepository;
import eu.europa.ec.etrustex.web.persistence.repository.redirect.RedirectRepository;
import eu.europa.ec.etrustex.web.persistence.repository.security.GroupRepository;
import eu.europa.ec.etrustex.web.service.MessageService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class TestCleanUpServiceImpl implements TestCleanUpService {
    private final GroupRepository groupRepository;
    private MessageRepository messageRepository;
    private final MessageService messageService;
    private final RedirectRepository redirectRepository;
    private final MessageUserStatusRepository messageUserStatusRepository;


    /**
     * Deletes all messages (sent & received) for an entity
     *
     * @param parentGroupIdentifier The business identifier
     * @param groupIdentifier       The entity identifier
     */
    @Override
    @Transactional
    public void cleanUpAfterTests(String parentGroupIdentifier, String groupIdentifier) {
        Group entity = groupRepository.findFirstByIdentifierAndParentIdentifier(groupIdentifier, parentGroupIdentifier);

        messageRepository.findBySenderGroupId(entity.getId())
                .forEach(this::deleteMessage);

        redirectRepository.deleteAll(redirectRepository.findByGroupId(entity.getId()));
    }

    private void deleteMessage(Message message) {
        deleteMessageUserStatus(message);
        messageService.delete(message);
    }

    private void deleteMessageUserStatus(Message message) {
        if (message.getMessageUserStatuses() != null) {
            messageUserStatusRepository.deleteAll(message.getMessageUserStatuses());
        }

    }
}
