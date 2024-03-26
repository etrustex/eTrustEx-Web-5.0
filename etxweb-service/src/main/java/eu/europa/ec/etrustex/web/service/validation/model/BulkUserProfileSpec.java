package eu.europa.ec.etrustex.web.service.validation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;

import static eu.europa.ec.etrustex.web.util.validation.ValidationMessage.Constants.*;

@Data
@EqualsAndHashCode(callSuper = true)
@SuppressWarnings({"squid:S1068", "squid:S2160"})
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BulkUserProfileSpec extends UserProfileSpec{

    @NotNull(message = OPERATOR_ROLE_BLANK_ERROR_MSG)
    private Boolean isOperator;
    @NotNull(message = ADMIN_ROLE_BLANK_ERROR_MSG)
    private Boolean isAdmin;
}
