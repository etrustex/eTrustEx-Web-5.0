package eu.europa.ec.etrustex.web.service.validation.model;

import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import eu.europa.ec.etrustex.web.util.validation.Patterns;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static eu.europa.ec.etrustex.web.util.validation.ValidationMessage.Constants.*;

@Data
@Builder
public class SysDeleteUserProfileSpec {
    public static final String ROLE_NAME_BLANK_ERROR_MSG = "Role name cannot be empty";

    @NotNull(message = EU_LOGIN_ID_NOT_NULL_ERROR_MSG)
    @Pattern(regexp = Patterns.TRIMMED, message = EU_LOGIN_ID_TRIM_ERROR_MSG)
    @Size(min = 1, max = 50, message = EU_LOGIN_ID_LENGTH_ERROR_MSG)
    private String ecasId;

    @NotNull(message = GROUP_ID_NOT_NULL_ERROR_MSG)
    private Long groupId;

    @NotNull(message = ROLE_NAME_BLANK_ERROR_MSG)
    private RoleName roleName;
}


