/*
 * Copyright (c) 2023. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package eu.europa.ec.etrustex.web.persistence.converter;

import eu.europa.ec.etrustex.web.util.exception.EtxWebException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SymmetricKeyExtractor {
    private SymmetricKeyExtractor() {
        throw new EtxWebException("Utility class");
    }


    public static String extractSymmetricKey(String symmetricKey) {
        if (symmetricKey != null && symmetricKey.startsWith("{\"symmetricKey\":")) {
            Pattern pattern = Pattern.compile("\\{\"symmetricKey\":(.+)}$");
            Matcher matcher = pattern.matcher(symmetricKey);
            if (matcher.matches()) {
                return matcher.group(1);
            }
        }

        return symmetricKey;
    }
}
