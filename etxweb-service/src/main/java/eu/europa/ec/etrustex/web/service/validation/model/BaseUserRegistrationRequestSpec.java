package eu.europa.ec.etrustex.web.service.validation.model;

import eu.europa.ec.etrustex.web.util.validation.Patterns;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static eu.europa.ec.etrustex.web.util.validation.ValidationMessage.Constants.*;

@Data
@SuppressWarnings({"squid:S1068", "squid:S2160"})
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BaseUserRegistrationRequestSpec {
    @NotNull(message = EU_LOGIN_ID_NOT_NULL_ERROR_MSG)
    @Pattern(regexp = Patterns.TRIMMED, message = EU_LOGIN_ID_TRIM_ERROR_MSG)
    @Size(min = 1, max = 50, message = EU_LOGIN_ID_LENGTH_ERROR_MSG)
    private String ecasId;

    @NotNull(message = GROUP_ID_NOT_EMPTY_ERROR_MSG)
    private Long groupId;

}
