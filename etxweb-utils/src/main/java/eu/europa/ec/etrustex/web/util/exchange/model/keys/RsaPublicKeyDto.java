/*
 * Copyright (c) 2018-2023. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package eu.europa.ec.etrustex.web.util.exchange.model.keys;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;

@Data
@Builder
@SuppressWarnings({"squid:S1068", "squid:S2160"})
public class RsaPublicKeyDto implements Serializable {
    private String algorithm;
    private String format;
    private String pem;
    private BigInteger modulus;
    private BigInteger publicExponent;
}
