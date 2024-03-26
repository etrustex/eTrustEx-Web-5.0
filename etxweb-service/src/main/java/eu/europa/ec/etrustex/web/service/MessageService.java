package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.entity.UserProfile;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.service.dto.FindMessageDto;
import eu.europa.ec.etrustex.web.service.pagination.MessagePage;
import eu.europa.ec.etrustex.web.service.validation.annotation.CheckForbiddenExtensions;
import eu.europa.ec.etrustex.web.service.validation.annotation.CheckWindowsCompatible;
import eu.europa.ec.etrustex.web.util.exchange.model.SendMessageRequestSpec;
import eu.europa.ec.etrustex.web.util.exchange.model.UpdateMessageRequestSpec;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface MessageService {

    Message findById(Long id);
    Message findByIdAndSenderGroupId(Long messageId, Long senderId);

    Optional<Message> findOptionalById(Long id);

    Message save(Message message);

    Message create(UserProfile userProfile);

    Message create(Long senderEntityId, String senderUserEcasId);

    void delete(Message message);

    void delete(Long messageId);

    @CheckForbiddenExtensions
    @CheckWindowsCompatible
    Message update(Long messageId, SendMessageRequestSpec sendMessageRequestSpec, String userName);

    void update(Long messageId, UpdateMessageRequestSpec updateMessageRequestSpec, String userName);

    boolean groupIsSender(Long messageId, Group sender);

    MessagePage getMessagesForUser(FindMessageDto findMessageDto, User user);

    @Transactional(readOnly = true)
    int countUnreadSent(Long senderEntityId, User user);

    Message getMessage(long messageId, Long senderId, String clientPublicKeyPem, User user);

    String getTemplateVarsAsString(Map<String, Object> templateVariables);

    List<Message> findBySenderGroupAndSentOnBefore(Group group, Date expiryDate, boolean filterDrafts);

    List<Message> findBySenderGroupAndSentOnBetween(Group group, Date startDate, Date endDate);

    void setUpdatedWithNewCertificateToTrueForProcessedMessages();

    void setProcessed(boolean value);

    long countUpdatedWithNewCertificateFailures();

    boolean isReadyToSend(Long id);
}
