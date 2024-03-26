package eu.europa.ec.etrustex.web.service.validation.model;

import eu.europa.ec.etrustex.web.common.exchange.ExchangeMode;
import eu.europa.ec.etrustex.web.service.validation.annotation.*;
import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import eu.europa.ec.etrustex.web.util.validation.Patterns;
import eu.europa.ec.etrustex.web.util.validation.PostAuthorization;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static eu.europa.ec.etrustex.web.util.validation.ValidationMessage.Constants.*;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@CheckTypeDependantUpdateGroupFields
@CheckUniqueGroupIdentifier(message = UNIQUE_GROUP_IDENTIFIER_ERROR_MSG, groups = {PostAuthorization.class})
@UniqueGroupName(message = UNIQUE_GROUP_NAME_ERROR_MSG, groups = {PostAuthorization.class})
@ToString(onlyExplicitlyIncluded = true)
@SuppressWarnings("java:S1068")
public class GroupSpec {
    private Long id;

    @Pattern(regexp = Patterns.TRIMMED, message = GROUP_IDENTIFIER_TRIM_ERROR_MSG)
    @Pattern(regexp = Patterns.ALPHA_NUM_HYPHEN_UNDERSCORE, message = GROUP_IDENTIFIER_VALID_ERROR_MSG)
    @Size(min = 1, max = 255, message = GROUP_IDENTIFIER_LENGTH_ERROR_MSG)
    @NotNull(message = GROUP_IDENTIFIER_LENGTH_ERROR_MSG)
    @ToString.Include
    private String identifier;

    @Pattern(regexp = Patterns.TRIMMED, message = DISPLAY_NAME_TRIM_ERROR_MSG)
    @Size(min = 1, max = 100, message = DISPLAY_NAME_LENGTH_ERROR_MSG)
    @NotNull(message = DISPLAY_NAME_LENGTH_ERROR_MSG)
    @ToString.Include
    private String displayName;

    @Size(min = 1, max = 255, message = DESCRIPTION_LENGTH_ERROR_MSG)
    @NotNull(message = DESCRIPTION_LENGTH_ERROR_MSG)
    private String description;

    @NotNull(message = STATUS_NOT_NULL_ERROR_MSG)
    private boolean isActive;

    private boolean isSystem;

    private String endpoint;

    private GroupType type;

    @CheckRecipientPreferencesExist(message = RECIPIENT_PREFERENCES_ID_DOES_NOT_EXIST_MSG, groups = {PostAuthorization.class})
    private Long recipientPreferencesId;

    private Long senderPreferencesId;

    private Long parentGroupId;

    @CheckEmailList(message = EMAIL_ADDRESS_LIST_NOT_VALID_ERROR_MSG)
    private String newMessageNotificationEmailAddresses;

    @CheckEmailList(message = EMAIL_ADDRESS_LIST_NOT_VALID_ERROR_MSG, limit = 0)
    private String registrationRequestNotificationEmailAddresses;

    @Size(max = 255, message = EMAIL_ADDRESS_LENGTH_ERROR_MSG)
    @Pattern(regexp = Patterns.EMPTY + "|" + Patterns.VALID_EMAIL, message = EMAIL_ADDRESS_NOT_VALID_ERROR_MSG)
    private String statusNotificationEmailAddress;

    @CheckEmailList(message = EMAIL_ADDRESS_LIST_NOT_VALID_ERROR_MSG, limit = 0)
    private String retentionWarningNotificationEmailAddresses;

    private boolean individualStatusNotifications;

    private boolean addToChannel;
    private Long channelId;
    private ExchangeMode entityRole;
}
