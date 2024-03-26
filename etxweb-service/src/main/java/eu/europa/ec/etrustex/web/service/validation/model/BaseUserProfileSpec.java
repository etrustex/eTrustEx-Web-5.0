package eu.europa.ec.etrustex.web.service.validation.model;

import eu.europa.ec.etrustex.web.util.validation.Patterns;
import eu.europa.ec.etrustex.web.util.validation.PostAuthorization;
import eu.europa.ec.etrustex.web.service.validation.annotation.CheckUserExistsInGroup;
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
@SuperBuilder
@CheckUserExistsInGroup(groups = PostAuthorization.class)
@AllArgsConstructor
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class BaseUserProfileSpec {

    @NotNull(message = EU_LOGIN_ID_NOT_NULL_ERROR_MSG)
    @Pattern(regexp = Patterns.TRIMMED, message = EU_LOGIN_ID_TRIM_ERROR_MSG)
    @Size(min = 1, max = 50, message = EU_LOGIN_ID_LENGTH_ERROR_MSG)
    @ToString.Include
    private String ecasId;

    @NotNull(message = GROUP_ID_NOT_NULL_ERROR_MSG)
    private Long groupId;
}
