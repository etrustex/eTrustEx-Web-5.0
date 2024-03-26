/*
 * Copyright (c) 2018-2023. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package eu.europa.ec.etrustex.web.util.exchange.model.keys;

import com.fasterxml.jackson.annotation.JsonView;
import eu.europa.ec.etrustex.web.util.exchange.filter.DetailsViewFilter;
import eu.europa.ec.etrustex.web.util.exchange.filter.MessageSummaryViewFilter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings({"squid:S1068", "squid:S2160"})
public class SymmetricKey implements Serializable {
    /**
     * PLAIN. No encryption. Not used anymore. To be removed once old web decommissioned and retention policies remove usages
     * ENCRYPTED. No encryption. Not used anymore. To be removed once old web decommissioned and retention policies remove usages
     * RSA_OAEP_E2E. The Symmetric key is asymmetrically encrypted with Recipient Public Key
     * RSA_OAEP_SERVER. The Symmetric key is asymmetrically encrypted with Server Public Key
     */
    public enum EncryptionMethod {PLAIN, ENCRYPTED, RSA_OAEP_E2E, RSA_OAEP_SERVER}

    /**
     * the random bits used for the symmetric encryption, depending on the encryptionMethod, these
     * bits are either saved in clear text or encrypted with asymmetric encryption, using the public key of the
     * recipient. In either case they are encoded b64
     */
    @JsonView({DetailsViewFilter.class, MessageSummaryViewFilter.class})
    private String randomBits;
    /**
     * defines how the random bits have been encrypted. PLAIN allows anyone to decode and is used
     * for non-encrypted transmissions.
     */
    @JsonView({DetailsViewFilter.class, MessageSummaryViewFilter.class})
    private EncryptionMethod encryptionMethod;
}
