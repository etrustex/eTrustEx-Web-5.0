/*
 * Copyright (c) 2023. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package eu.europa.ec.etrustex.web.util.exchange.model;

public enum GroupType {
    /**
     * BUSINESS: Businesses are used to define a client and group all the entities belonging to that client
     */
    BUSINESS,

    /**
     * ENTITY: Entities participate to ExchangeRules as
     * members and are used as senders/receivers of messages
     */
    ENTITY,

    /**
     * ROOT: the type of the root group, used to assing a group to user with
     * Role SYS_ADMIN
     */
    ROOT
}
