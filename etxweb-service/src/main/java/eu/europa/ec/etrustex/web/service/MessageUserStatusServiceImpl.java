package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.entity.MessageUserStatus;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.persistence.repository.MessageUserStatusRepository;
import eu.europa.ec.etrustex.web.util.exchange.model.Status;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
@Slf4j
public class MessageUserStatusServiceImpl implements MessageUserStatusService {

    private final MessageUserStatusRepository messageUserStatusRepository;

    @Override
    public MessageUserStatus createReadMessageUserStatusIfNotExists(Message message, User user, Status status) {
        if (messageUserStatusRepository.existsByMessageAndUserAndSenderGroupId(message, user, message.getSenderGroup().getId())) {
            return null;
        }

        return messageUserStatusRepository.save(MessageUserStatus.builder()
                .user(user)
                .message(message)
                .senderGroupId(message.getSenderGroup().getId())
                .status(status)
                .build());
    }
}
