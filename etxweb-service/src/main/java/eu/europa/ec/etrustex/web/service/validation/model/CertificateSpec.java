package eu.europa.ec.etrustex.web.service.validation.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

import static eu.europa.ec.etrustex.web.util.validation.ValidationMessage.Constants.*;

@Data
@Builder
public class CertificateSpec {
    @NotNull(message = PUBLIC_KEY_NOT_NULL_MSG)
    private String publicKey;
    @NotNull(message = ENTITY_NOT_EMPTY_ERROR_MSG)
    private Long entityId;
    @NotNull(message = BUSINESS_NOT_NULL_ERROR_MSG)
    private Long businessId;
}
