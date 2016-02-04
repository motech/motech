package org.motechproject.scheduler.util;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

public final class CustomDateParser {

    private static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm Z");

    public static DateTime parseToDateTime(String date) {
        return date != null ? FORMATTER.parseDateTime(date) : null;
    }

    public static LocalDate parseToLocalDate(String date) {
        return date != null ? FORMATTER.parseLocalDate(date) : null;
    }

    public static String parseToString(DateTime dateTime) {
        return FORMATTER.print(dateTime.toInstant());
    }

    public static String parseToString(Date date) {
        return parseToString(new DateTime(date));
    }

    private CustomDateParser() {
    }
}
