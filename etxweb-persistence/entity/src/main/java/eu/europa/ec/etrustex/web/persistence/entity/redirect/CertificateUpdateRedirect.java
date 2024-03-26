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
public class CertificateUpdateRedirect extends Redirect {
    protected static final String CERTIFICATE_UPDATE_URL = "#/certificate-update/%s/%s/%s";

    @NotNull
    private Long userId;

    @NotNull
    private String groupIdentifier;

    @Override
    public String getTargetPath() {
        return String.format(CERTIFICATE_UPDATE_URL, getGroupId(), groupIdentifier, userId);
    }
}

