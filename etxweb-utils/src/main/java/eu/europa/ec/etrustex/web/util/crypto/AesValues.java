/*
 * Copyright (c) 2023. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package eu.europa.ec.etrustex.web.util.crypto;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AesValues {
    CHUNK_SIZE(1024*1024*10),
    TAG_LENGTH_BITS(128),
    TAG_LENGTH_BYTES( 16),
    AES_KEY_LENGTH_BITS(256),
    AES_KEY_LENGTH_BYTES(32),
    /*
     * Recommended to use 12 bytes length
     * https://crypto.stackexchange.com/questions/41601/aes-gcm-recommended-iv-size-why-12-bytes
     */
    IV_LENGTH_BYTES(12);

    private final int value;

    @JsonValue
    public int value() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
