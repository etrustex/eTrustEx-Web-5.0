/*
 * Copyright (c) 2023. eTrustEx Web Software
 *  Copyright (c) 2018 European Commission
 *  Licensed under the EUPL v1.2
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package eu.europa.ec.etrustex.web.common;

import eu.europa.ec.etrustex.web.util.exception.EtxWebException;
import org.junit.jupiter.api.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static eu.europa.ec.etrustex.web.util.test.PrivateConstructorTester.testPrivateConstructor;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings({"java:S112", "java:S100"}) /* Suppress Define and throw a dedicated exception instead of using a generic one & method names false positive. */
class DateFormattersTest {
    @Test
    void testConstructorIsPrivate() throws NoSuchMethodException {
        testPrivateConstructor(DateFormatters.class.getDeclaredConstructor());
    }

    @Test
    void should_parse_iso_date_time() {
        Date now = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        String nowStr = dateFormat.format(now);
        Date date = DateFormatters.parseISODateTime(nowStr);
        assertEquals(now, date);
    }

    @Test
    void should_fail_to_parse_iso_date_time() {
        assertThrows(EtxWebException.class, () -> DateFormatters.parseISODateTime("2023-02-31'T'10:10:10.SSS'Z'"));
    }
}