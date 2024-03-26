package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.entity.MessageUserStatus;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.util.exchange.model.Status;

public interface MessageUserStatusService {
    MessageUserStatus createReadMessageUserStatusIfNotExists(Message message, User user, Status status);
}
