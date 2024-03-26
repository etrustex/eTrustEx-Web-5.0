package eu.europa.ec.etrustex.web.service.validation.model;

import eu.europa.ec.etrustex.web.util.exchange.model.RoleName;
import eu.europa.ec.etrustex.web.util.validation.PostAuthorization;
import eu.europa.ec.etrustex.web.service.validation.annotation.CheckUniqueGrantedAuthority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@SuppressWarnings({"squid:S1068", "squid:S2160"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@CheckUniqueGrantedAuthority(groups = {PostAuthorization.class})
public class GrantedAuthoritySpec {
    public static final String USER_NAME_BLANK_ERROR_MSG = "Username cannot be empty";
    public static final String GROUP_ID_BLANK_ERROR_MSG = "Group id cannot be empty";
    public static final String ROLE_NAME_BLANK_ERROR_MSG = "Role name cannot be empty";

    @NotBlank(message = USER_NAME_BLANK_ERROR_MSG)
    private String userName;

    @NotNull(message = GROUP_ID_BLANK_ERROR_MSG)
    private Long groupId;

    @NotNull(message = ROLE_NAME_BLANK_ERROR_MSG)
    private RoleName roleName;

    private boolean enabled;
}
