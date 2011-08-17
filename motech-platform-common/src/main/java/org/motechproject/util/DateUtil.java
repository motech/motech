package org.motechproject.util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.motechproject.MotechException;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

public class DateUtil {

    private static DateTimeZone dateTimeZone;

    public static DateTime now() {
        DateTimeZone timeZone = getTimeZone();
        return new DateTime(timeZone);
    }

    public static LocalDate today() {
        return new LocalDate(getTimeZone());
    }

    public static LocalDate newDate(int year, int month, int day) {
        return new LocalDate(getTimeZone()).withYear(year).withMonthOfYear(month).withDayOfMonth(day);
    }

    public static DateTime newDateTime(LocalDate localDate, int hour, int minute, int second) {
        return new DateTime(getTimeZone()).
                withYear(localDate.getYear()).withMonthOfYear(localDate.getMonthOfYear()).withDayOfMonth(localDate.getDayOfMonth())
                .withHourOfDay(hour).withMinuteOfHour(minute).withSecondOfMinute(second);
    }

    public static LocalDate newDate(Date date) {
        if (date == null) return null;
        return new LocalDate(date.getTime(), getTimeZone());
    }

    private static DateTimeZone getTimeZone() {
        if (dateTimeZone != null) return dateTimeZone;

        try {
            Properties dateProperties = new Properties();
            dateProperties.load(DateUtil.class.getResourceAsStream("/date.properties"));
            String timeZoneString = dateProperties.getProperty("timezone");
            dateTimeZone = DateTimeZone.forTimeZone(TimeZone.getTimeZone(timeZoneString));
        } catch (IOException e) {
            throw new MotechException("Error while loading timezone from date.properties", e);
        }
        return null;
    }
}
