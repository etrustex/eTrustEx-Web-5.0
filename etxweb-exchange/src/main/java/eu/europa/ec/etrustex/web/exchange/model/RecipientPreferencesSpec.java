package eu.europa.ec.etrustex.web.exchange.model;

import eu.europa.ec.etrustex.web.util.cia.Confidentiality;
import eu.europa.ec.etrustex.web.util.validation.PostAuthorization;
import eu.europa.ec.etrustex.web.exchange.validation.annotation.CheckConfidentialityAndPublicKey;
import eu.europa.ec.etrustex.web.exchange.validation.annotation.CheckPublicOrLimitedHigh;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

import static eu.europa.ec.etrustex.web.util.validation.ValidationMessage.Constants.*;

@Data
@SuppressWarnings({"squid:S1068", "squid:S2160"})
@Builder
@AllArgsConstructor
@NoArgsConstructor
@CheckConfidentialityAndPublicKey(message = INVALID_PUBLIC_KEY_MSG, groups = {PostAuthorization.class})
public class RecipientPreferencesSpec {
    @NotNull(message = CONFIDENTIALITY_NOT_NULL_MSG)
    @CheckPublicOrLimitedHigh(message = ALLOWED_CONFIDENTIALITY_VALUES_MSG)
    private Confidentiality confidentiality;
    private String publicKey;
    private String publicKeyFileName;

}
