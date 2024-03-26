/*
 * Copyright (c) 2023. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package eu.europa.ec.etrustex.web.util.exchange.model;

import eu.europa.ec.etrustex.web.util.validation.Patterns;
import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

import static eu.europa.ec.etrustex.web.util.validation.ValidationMessage.Constants.*;

@Value
@Builder
public class EntitySpec implements Serializable {
    @Pattern(regexp = Patterns.TRIMMED, message = GROUP_IDENTIFIER_TRIM_ERROR_MSG)
    @Pattern(regexp = Patterns.ALPHA_NUM_HYPHEN_UNDERSCORE, message = GROUP_IDENTIFIER_VALID_ERROR_MSG)
    @Size(min = 1, max = 255, message = GROUP_IDENTIFIER_LENGTH_ERROR_MSG)
    @NotNull(message = GROUP_IDENTIFIER_LENGTH_ERROR_MSG)
    String businessIdentifier;

    @Pattern(regexp = Patterns.TRIMMED, message = GROUP_IDENTIFIER_TRIM_ERROR_MSG)
    @Pattern(regexp = Patterns.ALPHA_NUM_HYPHEN_UNDERSCORE, message = GROUP_IDENTIFIER_VALID_ERROR_MSG)
    @Size(min = 1, max = 255, message = GROUP_IDENTIFIER_LENGTH_ERROR_MSG)
    @NotNull(message = GROUP_IDENTIFIER_LENGTH_ERROR_MSG)
    String entityIdentifier;
}
