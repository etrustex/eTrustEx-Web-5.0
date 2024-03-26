/*
 * Copyright (c) 2023. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package eu.europa.ec.etrustex.web.service.groupconfiguration;

import eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.GroupConfiguration;
import eu.europa.ec.etrustex.web.persistence.entity.groupconfiguration.impl.*;
import eu.europa.ec.etrustex.web.persistence.entity.security.Group;
import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;

import java.util.ArrayList;
import java.util.List;

public class GroupConfigurationFactory {
    private GroupConfigurationFactory() {
        throw new IllegalStateException("static Factory class");
    }

    @SuppressWarnings("rawtypes")
    static List<GroupConfiguration> defaultGroupConfigurations(Group group) {
        return group.getType() == GroupType.BUSINESS ? defaultBusinessConfigurations(group) : defaultEntityConfigurations(group);
    }

    @SuppressWarnings("rawtypes")
    static List<GroupConfiguration> defaultEntityConfigurations(Group entity) {
        List<GroupConfiguration> entityConfigurations = new ArrayList<>();

        entityConfigurations.add(RetentionPolicyEntityConfiguration.builder()
                .dtype(RetentionPolicyEntityConfiguration.class.getSimpleName())
                .group(entity)
                .integerValue(0)
                .active(false)
                .build());

        return entityConfigurations;
    }

    @SuppressWarnings("rawtypes")
    static List<GroupConfiguration> defaultBusinessConfigurations(Group business) {
        List<GroupConfiguration> businessConfigurations = new ArrayList<>();

        businessConfigurations.add(DisableEncryptionGroupConfiguration.builder()
                .dtype(DisableEncryptionGroupConfiguration.class.getSimpleName())
                .group(business)
                .active(false)
                .build());

        businessConfigurations.add(EnforceEncryptionGroupConfiguration.builder()
                .dtype(EnforceEncryptionGroupConfiguration.class.getSimpleName())
                .group(business)
                .active(false)
                .build());

        businessConfigurations.add(FileSizeLimitationGroupConfiguration.builder()
                .dtype(FileSizeLimitationGroupConfiguration.class.getSimpleName())
                .group(business)
                .integerValue(0)
                .active(false)
                .build());

        businessConfigurations.add(ForbiddenExtensionsGroupConfiguration.builder()
                .dtype(ForbiddenExtensionsGroupConfiguration.class.getSimpleName())
                .group(business)
                .stringValue("")
                .build());

        businessConfigurations.add(LogoGroupConfiguration.builder()
                .dtype(LogoGroupConfiguration.class.getSimpleName())
                .group(business)
                .stringValue("")
                .build());


        businessConfigurations.add(NotificationsEmailFromGroupConfiguration.builder()
                .dtype(NotificationsEmailFromGroupConfiguration.class.getSimpleName())
                .group(business)
                .stringValue("")
                .build());

        businessConfigurations.add(NumberOfFilesLimitationGroupConfiguration.builder()
                .dtype(NumberOfFilesLimitationGroupConfiguration.class.getSimpleName())
                .group(business)
                .integerValue(0)
                .active(false)
                .build());

        businessConfigurations.add(RetentionPolicyGroupConfiguration.builder()
                .dtype(RetentionPolicyGroupConfiguration.class.getSimpleName())
                .group(business)
                .integerValue(84)
                .active(true)
                .build());

        businessConfigurations.add(RetentionPolicyNotificationGroupConfiguration.builder()
                .dtype(RetentionPolicyNotificationGroupConfiguration.class.getSimpleName())
                .group(business)
                .integerValue(7)
                .active(true)
                .build());

        businessConfigurations.add(SignatureGroupConfiguration.builder()
                .dtype(SignatureGroupConfiguration.class.getSimpleName())
                .group(business)
                .active(false)
                .build());

        businessConfigurations.add(SplashScreenGroupConfiguration.builder()
                .dtype(SplashScreenGroupConfiguration.class.getSimpleName())
                .group(business)
                .stringValue("")
                .build());

        businessConfigurations.add(SupportEmailGroupConfiguration.builder()
                .dtype(SupportEmailGroupConfiguration.class.getSimpleName())
                .group(business)
                .active(true)
                .stringValue("")
                .build());

        businessConfigurations.add(TotalFileSizeLimitationGroupConfiguration.builder()
                .dtype(TotalFileSizeLimitationGroupConfiguration.class.getSimpleName())
                .group(business)
                .integerValue(0)
                .active(false)
                .build());

        businessConfigurations.add(UnreadMessageReminderConfiguration.builder()
                .dtype(UnreadMessageReminderConfiguration.class.getSimpleName())
                .group(business)
                .integerValue(0)
                .active(false)
                .build());

        businessConfigurations.add(WelcomeEmailGroupConfiguration.builder()
                .dtype(WelcomeEmailGroupConfiguration.class.getSimpleName())
                .group(business)
                .active(true)
                .build());

        businessConfigurations.add(WindowsCompatibleFilenamesGroupConfiguration.builder()
                .dtype(WindowsCompatibleFilenamesGroupConfiguration.class.getSimpleName())
                .group(business)
                .active(false)
                .build());

        return businessConfigurations;
    }
}
