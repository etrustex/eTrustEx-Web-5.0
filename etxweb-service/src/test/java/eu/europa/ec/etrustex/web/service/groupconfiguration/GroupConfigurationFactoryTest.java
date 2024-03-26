/*
 * Copyright (c) 2023. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package eu.europa.ec.etrustex.web.service.groupconfiguration;

import eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.GroupConfiguration;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import org.junit.jupiter.api.Test;

import java.util.List;

import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.mockBusiness;
import static eu.europa.ec.etrustex.web.persistence.entity.EntityTestUtils.mockGroup;
import static eu.europa.ec.etrustex.web.util.test.PrivateConstructorTester.testPrivateConstructor;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings({"java:S112", "java:S100"}) /* Suppress Define and throw a dedicated exception instead of using a generic one & method names false positive. */
class GroupConfigurationFactoryTest {

    @Test
    void constructor_should_be_private() throws NoSuchMethodException {
        testPrivateConstructor(GroupConfigurationFactory.class.getDeclaredConstructor());
    }

    @SuppressWarnings("rawtypes")
    @Test
    void should_create_default_GroupConfigurations_for_all_entity_implementations() {
        Group mockGroup = mockGroup();

        List<GroupConfiguration> groupConfigurations = GroupConfigurationFactory.defaultGroupConfigurations(mockGroup);

        assertEquals(1, groupConfigurations.size());

        for (GroupConfiguration groupConfiguration : groupConfigurations) {
            assertEquals(groupConfiguration.getClass().getSimpleName(), groupConfiguration.getDtype());
            assertEquals(mockGroup, groupConfiguration.getGroup());
        }
    }

    @SuppressWarnings("rawtypes")
    @Test
    void should_create_default_GroupConfigurations_for_all_business_implementations() {
        Group mockBusiness = mockBusiness();

        List<GroupConfiguration> groupConfigurations = GroupConfigurationFactory.defaultGroupConfigurations(mockBusiness);

        assertEquals(17, groupConfigurations.size());

        for (GroupConfiguration groupConfiguration : groupConfigurations) {
            assertEquals(groupConfiguration.getClass().getSimpleName(), groupConfiguration.getDtype());
            assertEquals(mockBusiness, groupConfiguration.getGroup());
        }
    }
}