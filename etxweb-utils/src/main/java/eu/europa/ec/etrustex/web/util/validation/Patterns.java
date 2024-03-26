/*
 * Copyright (c) 2023. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package eu.europa.ec.etrustex.web.util.validation;

public final class Patterns {
    public static final String EMPTY = "^$";
    public static final String TRIMMED = EMPTY + "|^\\S$|(^\\S.*\\S$)";
    public static final String VALID_EMAIL = "^((([a-zA-Z0-9!#$%&'*+\\-\\/=?^_`{|}~][\\.]?)+)|(\"[^\"]+\"))@(([a-zA-Z0-9]-?)+\\.)+([a-zA-Z0-9]{2,})$";
    public static final String COMMA_SEPARATED_CAPS_AND_NUMBERS = "^([A-Z0-9\\.\\*]+,)*[A-Z0-9\\.\\*]+$";
    public static final String FILE_EXTENSION = ".*\\.([a-zA-Z0-9]+)$";
    public static final String ALPHA_NUM_HYPHEN_UNDERSCORE = "^[a-zA-Z0-9_-]*$";

    private Patterns() {
        throw new IllegalStateException("Utility class");
    }
}
