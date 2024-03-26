/*
 * Copyright (c) 2023. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package eu.europa.ec.etrustex.web.util.exchange.model;

import eu.europa.ec.etrustex.web.util.cia.Confidentiality;
import eu.europa.ec.etrustex.web.util.cia.Integrity;
import eu.europa.ec.etrustex.web.util.exchange.model.keys.SymmetricKey;
import eu.europa.ec.etrustex.web.util.validation.CheckEntityOrRecipientIsSet;
import eu.europa.ec.etrustex.web.util.validation.PostAuthorization;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Builder
@CheckEntityOrRecipientIsSet(groups = PostAuthorization.class)
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings({"squid:S1068", "squid:S2160"})
public class MessageSummarySpec implements Serializable {
    private SymmetricKey symmetricKey;

    private Long recipientId;
    private EntitySpec entitySpec;

    private String clientReference;

    @NotNull(message = "Confidentiality cannot be null.")
    @Builder.Default
    private Confidentiality confidentiality = Confidentiality.PUBLIC;
    @NotNull(message = "Integrity cannot be null.")
    @Builder.Default
    private Integrity integrity = Integrity.MODERATE;

    private String signature;
}
