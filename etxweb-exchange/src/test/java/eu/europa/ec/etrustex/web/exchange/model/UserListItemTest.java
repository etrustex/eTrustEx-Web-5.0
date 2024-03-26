/*
 * Copyright (c) 2018-2023. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package eu.europa.ec.etrustex.web.exchange.model;

import eu.europa.ec.etrustex.web.util.exchange.model.GroupType;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserListItemTest {
    @Test
    void testConstructor() {
        String ecasId = "ecasId";
        String name = "name";
        String euLoginEmailAddress = "euLoginEmailAddress";
        String alternativeEmail = "alternativeEmail";
        Boolean alternativeEmailUsed = true;
        Long groupId = 1L;
        String groupIdentifier = "groupIdentifier";
        String groupName = "groupName";
        GroupType groupType = GroupType.ENTITY;
        Date createdDate = Calendar.getInstance().getTime();
        String createdBy = "createdBy";
        Date modifiedDate = Calendar.getInstance().getTime();
        String modifiedBy = "modifiedBy";
        boolean newMessageNotification = true;
        boolean statusNotification = false;
        boolean  retentionWarningNotification = true;
        boolean status = false;
        boolean isOperator = true;
        boolean isAdmin = false;

        UserListItem userListItem = new UserListItem(ecasId, name, euLoginEmailAddress, alternativeEmail, alternativeEmailUsed, groupId,
                groupIdentifier, groupName, groupType, createdDate, createdBy, modifiedDate, modifiedBy,
                newMessageNotification, statusNotification, retentionWarningNotification, status, isOperator, isAdmin);

        assertEquals(ecasId, userListItem.getEcasId());
        assertEquals(name, userListItem.getName());
        assertEquals(euLoginEmailAddress, userListItem.getEuLoginEmailAddress());
        assertEquals(alternativeEmail, userListItem.getAlternativeEmail());
        assertEquals(alternativeEmailUsed, userListItem.getAlternativeEmailUsed());
        assertEquals(groupId, userListItem.getGroupId());
        assertEquals(groupIdentifier, userListItem.getGroupIdentifier());
        assertEquals(groupName, userListItem.getGroupName());
        assertEquals(groupType, userListItem.getGroupType());
        assertEquals(createdDate, userListItem.getCreatedDate());
        assertEquals(createdBy, userListItem.getCreatedBy());
        assertEquals(modifiedDate, userListItem.getModifiedDate());
        assertEquals(modifiedBy, userListItem.getModifiedBy());
        assertEquals(newMessageNotification, userListItem.getNewMessageNotification());
        assertEquals(statusNotification, userListItem.getStatusNotification());
        assertEquals(retentionWarningNotification, userListItem.getRetentionWarningNotification());
        assertEquals(status, userListItem.getStatus());
        assertEquals(isOperator, userListItem.getIsOperator());
        assertEquals(isAdmin, userListItem.getIsAdmin());

    }
}