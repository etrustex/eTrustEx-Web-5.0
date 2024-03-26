/*
 * Copyright (c) 2023. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package eu.europa.ec.etrustex.web.util.exchange.model;

import eu.europa.ec.etrustex.web.util.exchange.model.keys.SymmetricKey;
import eu.europa.ec.etrustex.web.util.validation.CheckByteLength;
import eu.europa.ec.etrustex.web.util.validation.Patterns;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

import static eu.europa.ec.etrustex.web.util.validation.ValidationMessage.Constants.*;

@Data
@SuppressWarnings({"squid:S1068", "squid:S2160"})
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMessageRequestSpec {

    public static final int MAX_SUBJECT_BYTE_LENGTH = 4000;

    @NotEmpty(message = NOT_EMPTY_RECIPIENTS)
    @Valid
    private List<MessageSummarySpec> recipients;

    @NotNull(message = SUBJECT_REQUIRED_MESSAGE)
    @Size(min = 1, message = SUBJECT_LENGTH_ERROR_MESSAGE)
    @Pattern(regexp = Patterns.TRIMMED, message = SUBJECT_TRIM_MESSAGE)
    @CheckByteLength(value = MAX_SUBJECT_BYTE_LENGTH, message = SUBJECT_MAX_BYTE_LENGTH_ERROR_MESSAGE)
    private String subject;

    private String text;

    private String templateVariables;

    private SymmetricKey symmetricKey;
    private String iv;
    private Boolean highImportance;
}
