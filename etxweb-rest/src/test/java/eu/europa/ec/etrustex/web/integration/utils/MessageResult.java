package eu.europa.ec.etrustex.web.integration.utils;

import eu.europa.ec.etrustex.web.persistence.entity.Message;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.security.SecurityUserDetails;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class MessageResult {
    private SecurityUserDetails senderUserDetails;
    Message message;
    Group senderEntity;
    List<Group> recipientEntities;
}
