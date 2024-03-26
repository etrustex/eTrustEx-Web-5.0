/*
 * Copyright (c) 2023. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package eu.europa.ec.etrustex.web.util.exception;

public class EtxWebException extends RuntimeException {
    public EtxWebException(String errorMessage) {
        super(errorMessage);
    }

    public EtxWebException(Throwable throwable) {
        super(throwable);
    }

    public EtxWebException(String errorMessage, Throwable throwable) {
        super(errorMessage, throwable);
    }
}
