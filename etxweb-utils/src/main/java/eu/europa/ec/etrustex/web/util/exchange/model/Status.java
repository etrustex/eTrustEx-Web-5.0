/*
 * Copyright (c) 2023. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package eu.europa.ec.etrustex.web.util.exchange.model;

public enum Status {
    /**
     * SENT: Message has been sent (saved in DB)
     */
    SENT,

    /**
     * DELIVERED: in WEB-WEB and NODE-WEB flows, when the message summary is created
     */
    DELIVERED,

    /**
     * READ: has been read by the receiver
     */
    READ,

    /**
     * FAILED: has failed after {@link this.SENT} or after {@link this.DELIVERED}
     */
    FAILED,

    /**
     * DRAFT: Message has been saved to be sent later
     */
    DRAFT,

    /**
     * MULTIPLE: When messageSummaries have different statuses
     */
    MULTIPLE
}
