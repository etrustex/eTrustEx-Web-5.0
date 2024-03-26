/*
 * Copyright (c) 2023. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package eu.europa.ec.etrustex.web.util.exchange.model;

import eu.europa.ec.etrustex.web.util.validation.CheckUniqueFileNames;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

import static eu.europa.ec.etrustex.web.util.validation.ValidationMessage.Constants.FILE_NAME_REPEATED_ERROR_MSG;
import static eu.europa.ec.etrustex.web.util.validation.ValidationMessage.Constants.NOT_EMPTY_ATTACHMENTS;

@Data
@SuppressWarnings({"squid:S1068", "squid:S2160"})
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@CheckUniqueFileNames(message = FILE_NAME_REPEATED_ERROR_MSG)
@EqualsAndHashCode(callSuper = true)
public class SendMessageRequestSpec extends UpdateMessageRequestSpec implements MessageRequestSpecWithAttachments {
    private Long attachmentsTotalByteLength;

    @Min(value = 1, message = NOT_EMPTY_ATTACHMENTS)
    private Integer attachmentTotalNumber;

    @NotNull(message = NOT_EMPTY_ATTACHMENTS)
    @NotEmpty(message = NOT_EMPTY_ATTACHMENTS)
    private List<@Valid AttachmentSpec> attachmentSpecs;
}
