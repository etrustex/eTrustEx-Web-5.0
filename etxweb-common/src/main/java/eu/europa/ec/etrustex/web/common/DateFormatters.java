package eu.europa.ec.etrustex.web.common;

import eu.europa.ec.etrustex.web.util.exception.EtxWebException;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateFormatters {

    private DateFormatters() {
        throw new IllegalStateException("Utility class");
    }

    public static Format getYearMonthDayFormatter() {
      return new SimpleDateFormat("yyyy-MM-dd");
    }
    public static Date parseISODateTime(String date) {
        try {
            return (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")).parse(date);
        } catch (ParseException e) {
            throw new EtxWebException("Unable to parse date", e);
        }
    }
}
