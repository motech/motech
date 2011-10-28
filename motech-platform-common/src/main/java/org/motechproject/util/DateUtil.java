package org.motechproject.util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.motechproject.MotechException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

public class DateUtil {

    private static DateTimeZone dateTimeZone;
    private static Properties dateProperties;

    public static DateTime now() {
        DateTimeZone timeZone = getTimeZone();
        return new DateTime(timeZone);
    }

    public static LocalDate today() {
        if (getTestMode()) return getTestDate();
        return new LocalDate(getTimeZone());
    }

    public static LocalDate tomorrow() {
        return today().plusDays(1);
    }

    public static LocalDate newDate(int year, int month, int day) {
        return new LocalDate(getTimeZone()).withYear(year).withMonthOfYear(month).withDayOfMonth(day);
    }

    public static DateTime newDateTime(LocalDate localDate, int hour, int minute, int second) {
        return new DateTime(getTimeZone()).
                withYear(localDate.getYear()).withMonthOfYear(localDate.getMonthOfYear()).withDayOfMonth(localDate.getDayOfMonth())
                .withHourOfDay(hour).withMinuteOfHour(minute).withSecondOfMinute(second);
    }

    public static DateTime setTimeZone(DateTime dateTime) {
        return dateTime.toDateTime(getTimeZone());
    }

    public static DateTime newDateTime(Date date) {
        return new DateTime(date.getTime(), getTimeZone()).withMillisOfSecond(0);
    }

    public static LocalDate newDate(Date date) {
        if (date == null) return null;
        return new LocalDate(date.getTime(), getTimeZone());
    }

    private static DateTimeZone getTimeZone() {
        if (dateTimeZone != null) return dateTimeZone;
        try {
            Properties properties = getProperties(false);
            if (properties == null) return DateTimeZone.getDefault();

            String timeZoneString = properties.getProperty("timezone");
            dateTimeZone = DateTimeZone.forTimeZone(TimeZone.getTimeZone(timeZoneString));
        } catch (IOException e) {
            throw new MotechException("Error while loading timezone from date.getProperties", e);
        }
        return dateTimeZone;
    }

    private static Boolean getTestMode() {
        try {
            Properties properties = getProperties(true);
            if (properties == null) return false;
            return Boolean.parseBoolean(properties.getProperty("test.mode"));
        } catch (IOException e) {
            throw new MotechException("Error while loading test.mode from ivr.getProperties", e);
        }
    }

    private static LocalDate getTestDate() {
        try {
            Properties properties = getProperties(true);
            return LocalDate.parse(properties.getProperty("dateutil.today"));
        } catch (IOException e) {
            throw new MotechException("Error while loading dateutil.today from ivr.getProperties", e);
        }
    }

    private static Properties getProperties(boolean reload) throws IOException {
        if (!reload && dateProperties != null) return dateProperties;

        dateProperties = new Properties();
        InputStream fileStream = new FileInputStream(DateUtil.class.getResource("/date.properties").getFile());

        dateProperties.load(fileStream);
        fileStream.close();
        return dateProperties;
    }
}