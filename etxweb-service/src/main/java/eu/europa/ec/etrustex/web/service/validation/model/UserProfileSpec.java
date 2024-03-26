package eu.europa.ec.etrustex.web.service.validation.model;

import eu.europa.ec.etrustex.web.util.validation.Patterns;
import eu.europa.ec.etrustex.web.service.validation.annotation.CheckNotificationPreferences;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static eu.europa.ec.etrustex.web.util.validation.ValidationMessage.Constants.*;

@Data
@SuperBuilder
@CheckNotificationPreferences
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
@ToString(onlyExplicitlyIncluded = true)
public class UserProfileSpec extends BaseUserProfileSpec {

    @Pattern(regexp = Patterns.TRIMMED, message = NAME_TRIM_ERROR_MSG)
    @NotNull(message = NAME_NOT_NULL_ERROR_MSG)
    @Size(min = 1, max = 255, message = NAME_LENGTH_ERROR_MSG)
    @ToString.Include
    private String name;

    private boolean alternativeEmailUsed;

    @Pattern(regexp = Patterns.TRIMMED, message = EMAIL_ADDRESS_TRIM_ERROR_MSG)
    @Pattern(regexp = Patterns.EMPTY + "|" + Patterns.VALID_EMAIL, message = EMAIL_ADDRESS_NOT_VALID_ERROR_MSG)
    @Size(max = 255, message = EMAIL_ADDRESS_LENGTH_ERROR_MSG)
    private String alternativeEmail;

    private String euLoginEmailAddress;

    private boolean newMessageNotification;
    private boolean statusNotification;
    private boolean retentionWarningNotification;
}


