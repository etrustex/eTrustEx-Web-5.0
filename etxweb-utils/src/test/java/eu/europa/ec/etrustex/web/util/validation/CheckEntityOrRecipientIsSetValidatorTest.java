/*
 * Copyright (c) 2023. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package eu.europa.ec.etrustex.web.util.validation;

import eu.europa.ec.etrustex.web.util.exchange.model.EntitySpec;
import eu.europa.ec.etrustex.web.util.exchange.model.MessageSummarySpec;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CheckEntityOrRecipientIsSetValidatorTest {
    private final CheckEntityOrRecipientIsSetValidator checkEntityOrRecipientIsSetValidator = new CheckEntityOrRecipientIsSetValidator();


    @Test
    void isValid() {
        assertTrue(checkEntityOrRecipientIsSetValidator.isValid(MessageSummarySpec.builder()
                        .entitySpec(EntitySpec.builder().build())
                        .recipientId(1L)
                        .build(),
                null));

        assertFalse(checkEntityOrRecipientIsSetValidator.isValid(MessageSummarySpec.builder().build(), null));
    }
}