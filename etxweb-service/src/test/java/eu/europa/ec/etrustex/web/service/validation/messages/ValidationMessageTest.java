package eu.europa.ec.etrustex.web.service.validation.messages;

import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import eu.europa.ec.etrustex.web.util.validation.ValidationMessage;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static eu.europa.ec.etrustex.web.util.exchange.model.GroupType.ENTITY;
import static eu.europa.ec.etrustex.web.util.exchange.model.RoleName.OPERATOR;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ValidationMessageTest {

    @Test
    void formatUniqueGrantedAuthorityMessage() {
        RoleName roleName = OPERATOR;
        GroupType groupType = ENTITY;
        assertEquals(String.format(ValidationMessage.Constants.USER_ALREADY_HAS_GRANTED_AUTHORITY_VALUE, roleName, StringUtils.capitalize(groupType.toString().toLowerCase())),
                ValidationMessage.formatUniqueGrantedAuthorityMessage(groupType, roleName));
    }

    @Test
    void formatUserExistsInGroupMessage() {
        GroupType groupType = ENTITY;
        assertEquals(String.format(ValidationMessage.Constants.USER_EXISTS_IN_GROUP_VALUE, StringUtils.capitalize(groupType.toString())),
                ValidationMessage.formatUserExistsInGroupMessage(groupType));
    }
}
