/*
 * Copyright (c) 2018-2023. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package eu.europa.ec.etrustex.web.util.exchange.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@SuppressWarnings({"java:S1068"})
public class RecipientStatus implements Serializable {
    private Long messageId;
    @NotNull
    private String businessIdentifier;
    @NotNull
    private String entityIdentifier;

    /**
     * {@link Status#DELIVERED}: is available in the receiver's inbox
     * {@link Status#READ}: has been read by the receiver
     * {@link Status#FAILED}: has failed
     */
    private Status status;
    private Date statusModifiedDate;
}
