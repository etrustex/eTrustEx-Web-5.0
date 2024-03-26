package eu.europa.ec.etrustex.web.persistence.entity.exchange;

import lombok.*;

import javax.persistence.Embeddable;
import javax.persistence.Lob;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Data
@SuppressWarnings({"squid:S1068", "squid:S2160"})
@EqualsAndHashCode
@Builder
public class EncryptedPassword implements Serializable {
    @NotNull
    @Lob
    private String passwordB64;

    @Size(min = 1, max = 255, message = "ivB64 should be between 1 and 255 characters long.")
    @Deprecated
    private String ivB64;

    @Deprecated
    private boolean isRsaEncrypted;
}
