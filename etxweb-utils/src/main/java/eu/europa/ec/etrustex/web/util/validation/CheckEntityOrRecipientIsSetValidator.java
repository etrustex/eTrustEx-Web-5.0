/*
 * Copyright (c) 2023. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package eu.europa.ec.etrustex.web.util.validation;

import eu.europa.ec.etrustex.web.util.exchange.model.MessageSummarySpec;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
public class CheckEntityOrRecipientIsSetValidator implements ConstraintValidator<CheckEntityOrRecipientIsSet, MessageSummarySpec> {
    @Override
    public boolean isValid(MessageSummarySpec value, ConstraintValidatorContext context) {
        return value.getEntitySpec() != null || value.getRecipientId() != null;
    }
}
