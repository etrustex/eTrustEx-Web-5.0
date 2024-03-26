/*
 * Copyright (c) 2023. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.impl;

import org.junit.jupiter.api.Test;

import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.mockForbiddenExtensionsGroupConfiguration;
import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.mockGroup;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SuppressWarnings({"java:S112", "java:S100"}) /* Suppress Define and throw a dedicated exception instead of using a generic one & method names false positive. */
class ForbiddenExtensionsGroupConfigurationTest {

    @Test
    void should_get_ForbiddenExtensions() {
        ForbiddenExtensionsGroupConfiguration forbiddenExtensionsGroupConfiguration = mockForbiddenExtensionsGroupConfiguration(mockGroup(), "");

        assertThat(forbiddenExtensionsGroupConfiguration.getForbiddenExtensions()).isEmpty();

        forbiddenExtensionsGroupConfiguration = mockForbiddenExtensionsGroupConfiguration(mockGroup(), "FOO,BAR");

        assertThat(forbiddenExtensionsGroupConfiguration.getForbiddenExtensions().size()).isEqualTo(2);
    }

    @Test
    void should_get_enum_value() {
        ForbiddenExtensionsGroupConfiguration.DefaultForbiddenExtensions bz = ForbiddenExtensionsGroupConfiguration.DefaultForbiddenExtensions.BZ;

        assertEquals(bz, ForbiddenExtensionsGroupConfiguration.DefaultForbiddenExtensions.getValue(ForbiddenExtensionsGroupConfiguration.DefaultForbiddenExtensions.BZ.toString()));
    }

    @Test
    void should_get_null_if_enum_value_does_not_exit() {
        assertNull(ForbiddenExtensionsGroupConfiguration.DefaultForbiddenExtensions.getValue("foo"));
    }

    @Test
    void testDefaultForbiddenExtensionsToString() {
        ForbiddenExtensionsGroupConfiguration.DefaultForbiddenExtensions bz = ForbiddenExtensionsGroupConfiguration.DefaultForbiddenExtensions.BZ;

        assertEquals(ForbiddenExtensionsGroupConfiguration.DefaultForbiddenExtensions.BZ.name(), bz.toString());
    }
}