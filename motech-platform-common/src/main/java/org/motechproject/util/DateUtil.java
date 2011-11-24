package org.motechproject.util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.LocalDate;

import java.util.Date;

public class DateUtil {
    public static DateTime now() {
        return DateTimeSourceUtil.now();
    }

    public static LocalDate today() {
        return DateTimeSourceUtil.today();
    }

    public static LocalDate tomorrow() {
        return today().plusDays(1);
    }

    public static LocalDate newDate(int year, int month, int day) {
        return new LocalDate(DateTimeSourceUtil.SourceInstance.timeZone()).withYear(year).withMonthOfYear(month).withDayOfMonth(day);
    }

    public static DateTime newDateTime(LocalDate localDate, int hour, int minute, int second) {
        return new DateTime(DateTimeSourceUtil.SourceInstance.timeZone()).
                withYear(localDate.getYear()).withMonthOfYear(localDate.getMonthOfYear()).withDayOfMonth(localDate.getDayOfMonth())
                .withHourOfDay(hour).withMinuteOfHour(minute).withSecondOfMinute(second);
    }

    public static DateTime setTimeZone(DateTime dateTime) {
        return dateTime.toDateTime(DateTimeSourceUtil.SourceInstance.timeZone());
    }

    public static DateTime newDateTime(Date date) {
        return new DateTime(date.getTime(), DateTimeSourceUtil.SourceInstance.timeZone()).withMillisOfSecond(0);
    }

    public static LocalDate newDate(Date date) {
        if (date == null) return null;
        return new LocalDate(date.getTime(), DateTimeSourceUtil.SourceInstance.timeZone());
    }

    public static int daysToCalendarWeekEnd(LocalDate date, int calendarWeekStartDay) {
        int currentDayOfWeek = date.get(DateTimeFieldType.dayOfWeek());
        int calendarWeekEndDay = (calendarWeekStartDay + 6) % 7;
        int intervalBetweenWeekEndAndCurrentDay = calendarWeekEndDay - currentDayOfWeek;
        return intervalBetweenWeekEndAndCurrentDay >= 0 ? intervalBetweenWeekEndAndCurrentDay :
                intervalBetweenWeekEndAndCurrentDay + 7;
        }
}