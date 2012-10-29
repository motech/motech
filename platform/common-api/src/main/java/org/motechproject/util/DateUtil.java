package org.motechproject.util;

import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.Time;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.motechproject.model.DayOfWeek.getDayOfWeek;

public final class DateUtil {

    private DateUtil() {

    }

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
        return new LocalDate(DateTimeSourceUtil.timeZone())
                .withYear(year)
                .withMonthOfYear(month)
                .withDayOfMonth(day);
    }

    public static DateTime newDateTime(LocalDate localDate, int hour, int minute, int second) {
        return new DateTime(DateTimeSourceUtil.timeZone())
                .withYear(localDate.getYear())
                .withMonthOfYear(localDate.getMonthOfYear())
                .withDayOfMonth(localDate.getDayOfMonth())
                .withHourOfDay(hour)
                .withMinuteOfHour(minute)
                .withSecondOfMinute(second)
                .withMillisOfSecond(0);
    }

    public static DateTime setTimeZone(DateTime dateTime) {
        return dateTime == null ? dateTime : dateTime.toDateTime(DateTimeSourceUtil.timeZone());
    }

    public static DateTime newDateTime(Date date) {
        return new DateTime(date.getTime(), DateTimeSourceUtil.timeZone()).withMillisOfSecond(0);
    }

    public static DateTime newDateTime(LocalDate date) {
        return newDateTime(date, 0, 0, 0);
    }

    public static Time time(DateTime dateTime) {
        return new Time(dateTime.getHourOfDay(), dateTime.getMinuteOfHour());
    }

    public static LocalDate newDate(Date date) {
        if (date == null) {
            return null;
        }
        return new LocalDate(date.getTime(), DateTimeSourceUtil.timeZone());
    }

    public static LocalDate newDate(DateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return newDate(dateTime.getYear(), dateTime.getMonthOfYear(), dateTime.getDayOfMonth());
    }

    public static int daysPast(LocalDate localDate, DayOfWeek dayOfWeek) {
        int count = 0;
        LocalDate date = localDate;
        while (date.getDayOfWeek() != dayOfWeek.getValue()) {
            date = date.minusDays(1);
            count++;
        }
        return count;
    }

    public static boolean isOnOrBefore(DateTime firstDate, DateTime secondDate) {
        return firstDate.equals(secondDate) || firstDate.isBefore(secondDate);
    }

    public static boolean isOnOrAfter(DateTime firstDate, DateTime secondDate) {
        return firstDate.equals(secondDate) || firstDate.isAfter(secondDate);
    }

    public static boolean inRange(DateTime reference, DateTime start, DateTime end) {
        return (isOnOrAfter(reference, start)) && (isOnOrBefore(reference, end));
    }

    public static int daysToCalendarWeekEnd(LocalDate date, int calendarWeekStartDay) {
        final int daysInWeek = 7;
        int currentDayOfWeek = date.get(DateTimeFieldType.dayOfWeek());
        int calendarWeekEndDay = (calendarWeekStartDay + daysInWeek - 1) % 7;
        int intervalBetweenWeekEndAndCurrentDay = calendarWeekEndDay - currentDayOfWeek;
        return intervalBetweenWeekEndAndCurrentDay >= 0 ? intervalBetweenWeekEndAndCurrentDay :
                intervalBetweenWeekEndAndCurrentDay + 7;
    }

    public static DateTime newDateTime(LocalDate localDate, Time time) {
        return newDateTime(localDate, time.getHour(), time.getMinute(), 0);
    }

    public static DateTime newDateTime(int year, int month, int day, Time time) {
        return newDateTime(newDate(year, month, day), time.getHour(), time.getMinute(), 0);
    }

    public static DateTime newDateTime(int year, int month, int day) {
        return newDateTime(newDate(year, month, day), 0, 0, 0);
    }

    public static DateTime newDateTime(int year, int month, int day, int hour, int minute, int second) {
        return newDateTime(newDate(year, month, day), hour, minute, second);
    }

    public static int getDifferenceOfDatesInYears(Date startDate) {
        Period period = new Period(newDate(startDate), today(), PeriodType.yearMonthDay());
        return period.getYears();
    }

    public static DateTime endOfDay(Date dateTime) {
        final int hour = 23;
        final int minute = 59;
        final int second = 59;
        final int millis = 999;
        return new DateTime(dateTime).withTime(hour, minute, second, millis);
    }

    public static DateTime nextApplicableWeekDay(DateTime fromDate, List<DayOfWeek> applicableDays) {
        return nextApplicableWeekDayIncludingFromDate(fromDate.dayOfMonth().addToCopy(1), applicableDays);
    }

    public static DateTime nextApplicableWeekDayIncludingFromDate(DateTime fromDate, List<DayOfWeek> applicableDays) {
        if (CollectionUtils.isEmpty(applicableDays)) {
            throw new IllegalArgumentException("Applicable Days should not be empty");
        }

        int dayOfWeek = fromDate.getDayOfWeek();
        int noOfDaysToNearestCycleDate = 0;
        int weekMaxDay = DayOfWeek.Sunday.getValue();
        int currentDayOfWeek = dayOfWeek;
        for (int dayCount = 0; dayCount <= weekMaxDay; dayCount++) {
            if (applicableDays.contains(getDayOfWeek(currentDayOfWeek))) {
                noOfDaysToNearestCycleDate = dayCount;
                break;
            }
            if (currentDayOfWeek == weekMaxDay) {
                currentDayOfWeek = 1;
            } else {
                currentDayOfWeek++;
            }
        }
        return fromDate.dayOfMonth().addToCopy(noOfDaysToNearestCycleDate);
    }

    public static List<DateTime> greaterThanOrEqualTo(final DateTime date, final List<DateTime> dates){
        List<DateTime> list = new ArrayList<>(dates.size());

        for (DateTime dt : dates) {
            if (isOnOrAfter(dt, date)) {
                list.add(dt);
            }
        }

        return list;
    }

    public static List<DateTime> lessThan(final DateTime date, final List<DateTime> dates) {
        List<DateTime> list = new ArrayList<>(dates.size());

        for (DateTime dt : dates) {
            if (dt.isBefore(date)) {
                list.add(dt);
            }
        }

        return list;
    }
}
