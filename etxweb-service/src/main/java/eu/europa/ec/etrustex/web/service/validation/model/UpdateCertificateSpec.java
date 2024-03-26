package eu.europa.ec.etrustex.web.service.validation.model;

import eu.europa.ec.etrustex.web.util.validation.Patterns;
import eu.europa.ec.etrustex.web.service.validation.annotation.CheckUserExistsForGroup;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static eu.europa.ec.etrustex.web.util.validation.ValidationMessage.Constants.*;

@Data
@Builder
@CheckUserExistsForGroup
public class UpdateCertificateSpec{
    @NotNull(message = EU_LOGIN_ID_NOT_NULL_ERROR_MSG)
    @Pattern(regexp = Patterns.TRIMMED, message = EU_LOGIN_ID_TRIM_ERROR_MSG)
    @Size(min = 1, max = 50, message = EU_LOGIN_ID_LENGTH_ERROR_MSG)
    private String euLoginId;
    @NotNull(message = ENTITY_NOT_EMPTY_ERROR_MSG)
    private Long entityId;
    @NotNull(message = BUSINESS_NOT_NULL_ERROR_MSG)
    private Long businessId;
}
