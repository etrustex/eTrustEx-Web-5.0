/*
 * Copyright (c) 2023. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package eu.europa.ec.etrustex.web.exchange.validation.validator;

import eu.europa.ec.etrustex.web.exchange.model.groupconfiguration.SplashScreenGroupConfigurationSpec;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings({"java:S112", "java:S100"}) /* Suppress Define and throw a dedicated exception instead of using a generic one & method names false positive. */
class EmptyContentActiveConfigurationValidatorTest {
    EmptyContentActiveConfigurationValidator emptyContentActiveConfigurationValidator = new EmptyContentActiveConfigurationValidator();


    @Test
    void should_fail_active_and_blank() {
        SplashScreenGroupConfigurationSpec spec = SplashScreenGroupConfigurationSpec.builder()
                .active(true)
                .value("")
                .build();

        assertFalse(emptyContentActiveConfigurationValidator.isValid(spec, null));
    }

    @Test
    void should_not_fail_not_active_and_blank() {
        SplashScreenGroupConfigurationSpec spec = SplashScreenGroupConfigurationSpec.builder()
                .active(false)
                .value("")
                .build();

        assertTrue(emptyContentActiveConfigurationValidator.isValid(spec, null));
    }


    @Test
    void should_not_fail_not_active_and_not_blank() {
        SplashScreenGroupConfigurationSpec spec = SplashScreenGroupConfigurationSpec.builder()
                .active(true)
                .value("foo")
                .build();

        assertTrue(emptyContentActiveConfigurationValidator.isValid(spec, null));
    }
}