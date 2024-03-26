/*
 * Copyright (c) 2023. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package eu.europa.ec.etrustex.web.util;

import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;

public class UTFStringsUtils {

    public static final String STRIP_CHARS = " ,.;:!?¿¡";
    public static final String ELLIPSIS = "…";
    private UTFStringsUtils() {
        throw new EtxWebException("Utility class");
    }
    public static String abbreviateToByteLength(String input, int byteLength) {
        if(input != null && getByteLength(input) > byteLength) {

            StringBuilder sb = new StringBuilder();
            int outLength = 0;
            int nextIndex = 0;
            String currentChar = "";
            String nextChar;
            do {
                nextChar = input.substring(nextIndex, nextIndex+1);
                nextIndex++;

                sb.append(currentChar);
                outLength += getByteLength(currentChar);

                currentChar = nextChar;

            } while (outLength + getByteLength(nextChar) <= byteLength - 3);

            String shortened = sb.toString();
            String stripped = StringUtils.stripEnd(shortened, STRIP_CHARS);

            return stripped + ELLIPSIS;
        }
        return input;

    }

    public static int getByteLength(String s) {
        return s.getBytes(StandardCharsets.UTF_8).length;
    }
}
