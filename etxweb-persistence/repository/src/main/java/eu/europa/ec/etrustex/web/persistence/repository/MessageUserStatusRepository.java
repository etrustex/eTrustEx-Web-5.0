package eu.europa.ec.etrustex.web.persistence.repository;

import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.entity.MessageUserStatus;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageUserStatusRepository extends CrudRepository<MessageUserStatus, Long> {
    boolean existsByMessageAndUserAndSenderGroupId(Message message, User user, Long senderGroupId);

    void deleteByUser(User user);
}
