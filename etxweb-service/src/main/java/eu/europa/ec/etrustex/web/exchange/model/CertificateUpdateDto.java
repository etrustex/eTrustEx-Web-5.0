package eu.europa.ec.etrustex.web.exchange.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@EqualsAndHashCode
@Builder
public class CertificateUpdateDto {
    private Long recipientEntityId;
    private String encryptedPrivateKey;
    private String hashedPublicKey;
    private String randomBits;
    private String iv;

}
