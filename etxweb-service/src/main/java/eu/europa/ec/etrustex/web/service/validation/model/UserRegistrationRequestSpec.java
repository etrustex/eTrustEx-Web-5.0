package eu.europa.ec.etrustex.web.service.validation.model;

import eu.europa.ec.etrustex.web.util.validation.Patterns;
import eu.europa.ec.etrustex.web.service.validation.annotation.CheckUserRegistrationRequestExistsForGroup;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static eu.europa.ec.etrustex.web.util.validation.ValidationMessage.Constants.*;

@EqualsAndHashCode(callSuper = true)
@Data
@SuppressWarnings({"squid:S1068", "squid:S2160"})
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@CheckUserRegistrationRequestExistsForGroup(message = "")
@ToString
public class UserRegistrationRequestSpec extends BaseUserRegistrationRequestSpec{
    @NotNull(message = GROUP_ID_NOT_NULL_ERROR_MSG)
    private String groupIdentifier;

    @Pattern(regexp = Patterns.TRIMMED, message = EMAIL_ADDRESS_TRIM_ERROR_MSG)
    @Pattern(regexp = Patterns.EMPTY + "|" + Patterns.VALID_EMAIL, message = EMAIL_ADDRESS_NOT_VALID_ERROR_MSG)
    @Size(max = 255, message = EMAIL_ADDRESS_LENGTH_ERROR_MSG)
    private String notificationEmail;

    private String requesterEcasId;
    private String requesterEmailAddress;

    private boolean isOperator;
    private boolean isAdmin;
}
