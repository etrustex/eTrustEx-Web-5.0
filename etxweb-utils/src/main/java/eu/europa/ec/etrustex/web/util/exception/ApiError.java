/*
 * Copyright (c) 2018-2023. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package eu.europa.ec.etrustex.web.util.exception;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Builder
@Getter
@SuppressWarnings({"java:S1068"})
public class ApiError implements Serializable {
    private int code;
    private String type;
    private String message;
    private String description;
}
