package eu.europa.ec.etrustex.web.integration.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import eu.europa.ec.etrustex.web.common.exchange.ExchangeMode;
import eu.europa.ec.etrustex.web.persistence.entity.Attachment;
import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.Channel;
import eu.europa.ec.etrustex.web.persistence.entity.security.User;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import eu.europa.ec.etrustex.web.util.exchange.model.keys.SymmetricKey;

import java.security.PublicKey;


@SuppressWarnings({"squid:S107"})
public interface MessageUtils {

    void sendMessage(Message message, SecurityUserDetails userDetails, SymmetricKey encryptedSymmetricKey, String iv, String encryptedText, String encryptedTemplates, boolean isSigned, Long... recipientEntityIds) ;

    void cleanUp();

    MessageResult createMessageWithNewConfigurations(SecurityUserDetails businessAdminUserDetails, User senderUser, int numberOfRecipients, boolean withNotifications, boolean andSend, boolean isSenderSystemEntity) ;
    MessageResult createMessageWithNewConfigurations(SecurityUserDetails businessAdminUserDetails, User senderUser, int numberOfRecipients, boolean withNotifications, boolean andSend) ;

    Attachment createMessageAttachment(SecurityUserDetails userDetails, Message message) ;

    void uploadAttachment(Attachment attachment, SecurityUserDetails userDetails) ;

    Message createAndSendMessage(SecurityUserDetails userDetails, Long... recipientEntityIds) throws JsonProcessingException;

    Message createAndSendMessage(SecurityUserDetails userDetails, boolean isSigned, Long... recipientEntityIds) throws JsonProcessingException;

    Channel createChannel(Long businessId, SecurityUserDetails userDetails) ;

    void createExchangeRule(Long channelId, Long groupId, ExchangeMode exchangeMode, SecurityUserDetails userDetails) ;

    PublicKey getServerPublicKey();

    long getTotalNumberOfMessages();
}
