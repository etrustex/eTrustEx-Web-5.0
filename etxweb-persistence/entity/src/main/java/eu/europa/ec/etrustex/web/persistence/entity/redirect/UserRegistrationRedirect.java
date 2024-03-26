package eu.europa.ec.etrustex.web.persistence.entity.redirect;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

@Entity
@Data
@SuppressWarnings({"squid:S1068", "squid:S2160"})
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserRegistrationRedirect extends Redirect {
    protected static final String USER_REGISTRATION_URL = "#/user-registration/%s/%s";

    @NotNull
    private String emailAddress;

    @NotNull
    private String groupIdentifier;

    @Override
    public String getTargetPath() {
        return String.format(USER_REGISTRATION_URL, getGroupId(), groupIdentifier);
    }
}

