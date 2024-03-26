/*
 * Copyright (c) 2023. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package eu.europa.ec.etrustex.web.common.template;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TemplateTypeTest {

    @Test
    void testToString() {
        assertEquals("message-status-notification", TemplateType.MESSAGE_STATUS_NOTIFICATION.toString());
    }
}