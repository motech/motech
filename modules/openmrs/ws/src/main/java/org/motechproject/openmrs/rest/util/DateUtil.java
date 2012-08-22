package org.motechproject.openmrs.rest.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateUtil {

    private DateUtil() { }

    private static final SimpleDateFormat OPENMRS_DATA_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    public static String formatToOpenMrsDate(Date date) {
        return OPENMRS_DATA_FORMAT.format(date.getTime());
    }

    public static Date parseOpenMrsDate(String date) throws ParseException {
        return OPENMRS_DATA_FORMAT.parse(date);
    }

}
