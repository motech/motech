package org.motechproject.commons.date.util;

import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.motechproject.commons.date.model.DayOfWeek;
import org.motechproject.commons.date.model.Time;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.motechproject.commons.date.model.DayOfWeek.getDayOfWeek;

/**
 * Utility class for various classes from {@code org.joda.time} package.
 * Using this class for retrieving the current date and time will allow
 * mocking time, so should be always used throughout the platform instead
 * of calling the underlying date-time API directly.
 */
public final class DateUtil {

    /**
     * Returns current time as an instance of {@code DateTime}.
     *
     * @return the current time
     */
    public static DateTime now() {
        return DateTimeSourceUtil.now();
    }

    /**
     * Returns current time in UTC time zone.
     *
     * @return the current time as instance of {@code DateTime}
     */
    public static DateTime nowUTC() {
        return setTimeZoneUTC(now());
    }

    /**
     * Returns current local date.
     *
     * @return the current local date as an instance of {@code LocalDate}
     */
    public static LocalDate today() {
        return DateTimeSourceUtil.today();
    }

    /**
     * Returns tomorrow local date.
     *
     * @return the tomorrow local date as an instance of {@code LocalDate}
     */
    public static LocalDate tomorrow() {
        return today().plusDays(1);
    }

    /**
     * Creates new instance of {@code LocalDate}.
     *
     * @param year  the year to be stored in created instance
     * @param month  the month to be stored in created instance
     * @param day  the day to be stored in created instance
     * @return the instance of {@code LocalDate} with given year, month and day
     */
    public static LocalDate newDate(int year, int month, int day) {
        return new LocalDate(DateTimeSourceUtil.timeZone())
                .withYear(year)
                .withMonthOfYear(month)
                .withDayOfMonth(day);
    }

    /**
     * Creates new instance of {@code DateTime}.
     *
     * @param localDate  the date to be stored in created instance
     * @param hour  the hour to be stored in created instance
     * @param minute  the minute to be stored in created instance
     * @param second  the second to be stored in created instance
     * @return the instance of {@code DateTime} with given date and time
     */
    public static DateTime newDateTime(LocalDate localDate, int hour, int minute, int second) {
        final DateTimeZone zone = DateTimeSourceUtil.timeZone();
        LocalDateTime localDateTime = new LocalDateTime(zone)
                .withYear(localDate.getYear())
                .withMonthOfYear(localDate.getMonthOfYear())
                .withDayOfMonth(localDate.getDayOfMonth())
                .withHourOfDay(hour)
                .withMinuteOfHour(minute)
                .withSecondOfMinute(second)
                .withMillisOfSecond(0);

        if (zone.isLocalDateTimeGap(localDateTime)) {
            localDateTime = localDateTime.withHourOfDay(hour + 1);
        }

        return localDateTime.toDateTime();
    }

    /**
     * Sets time zone, for given {@code DateTime}, to default.
     *
     * @param dateTime  the {@code DateTime} to have time zone set
     * @return the {@code DateTime} with time zone set
     */
    public static DateTime setTimeZone(DateTime dateTime) {
        return dateTime == null ? null : dateTime.toDateTime(DateTimeSourceUtil.timeZone());
    }

    /**
     * Sets time zone, for given {@code DateTime}, to UTC time zone.
     *
     * @param dateTime  the {@code DateTime} to have time zone set
     * @return the {@code DateTime} with time zone set
     */
    public static DateTime setTimeZoneUTC(DateTime dateTime) {
        return dateTime == null ? null : dateTime.toDateTime(DateTimeZone.UTC);
    }

    /**
     * Creates new {@code DateTime} from given {@code Date}.
     *
     * @param date  the {@code Date} to be parsed to {@code DateTime}
     * @return the new {@code DateTime} instance
     */
    public static DateTime newDateTime(Date date) {
        return new DateTime(date.getTime(), DateTimeSourceUtil.timeZone()).withMillisOfSecond(0);
    }

    /**
     * Creates new {@code DateTime} from given {@code LocalDate}. Time is set to 00:00:00.
     *
     * @param date  the {@code Date} to be parsed to {@code DateTime}
     * @return the new {@code DateTime} instance
     */
    public static DateTime newDateTime(LocalDate date) {
        return newDateTime(date, 0, 0, 0);
    }

    /**
     * Extracts time from given {@code DateTime} and converts it to an instance of {@code Time}.
     *
     * @param dateTime  the {@code DateTime} storing the time
     * @return the new instance of {@code Time}
     */
    public static Time time(DateTime dateTime) {
        return new Time(dateTime.getHourOfDay(), dateTime.getMinuteOfHour());
    }

    /**
     * Creates new {@code LocalDate} from given {@code Date}.
     *
     * @param date  the {@code Date} to be parsed to {@code LocalDate}
     * @return the new instance of {@code LocalTime}, null if {@code date} was null
     */
    public static LocalDate newDate(Date date) {
        if (date == null) {
            return null;
        }
        return new LocalDate(date.getTime(), DateTimeSourceUtil.timeZone());
    }

    /**
     * Creates new {@code LocalDate} from given {@code DateTime}.
     *
     * @param dateTime  the {@code DateTime} to be parsed to {@code LocalDate}
     * @return the new instance of {@code LocalTime}, null if {@code date} was null
     */
    public static LocalDate newDate(DateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return newDate(dateTime.getYear(), dateTime.getMonthOfYear(), dateTime.getDayOfMonth());
    }

    /**
     * Counts the days passed between given date and given day of week.
     *
     * @param localDate  the given date
     * @param dayOfWeek  the given day of week
     * @return the number of days passed between {@code localDate} and a {@link org.motechproject.commons.date.model.DayOfWeek}
     */
    public static int daysPast(LocalDate localDate, DayOfWeek dayOfWeek) {
        int count = 0;
        LocalDate date = localDate;
        while (date.getDayOfWeek() != dayOfWeek.getValue()) {
            date = date.minusDays(1);
            count++;
        }
        return count;
    }

    /**
     * Checks whether first date is on or before second one.
     *
     * @param firstDate  the date what should be on or before
     * @param secondDate  the date to compare to
     * @return false if first date is after second, true otherwise
     */
    public static boolean isOnOrBefore(DateTime firstDate, DateTime secondDate) {
        return firstDate.equals(secondDate) || firstDate.isBefore(secondDate);
    }

    /**
     * Checks whether first date is on or after second one.
     *
     * @param firstDate  the date what should be on or after
     * @param secondDate  the date to compare to
     * @return false if first date is before second, true otherwise
     */
    public static boolean isOnOrAfter(DateTime firstDate, DateTime secondDate) {
        return firstDate.equals(secondDate) || firstDate.isAfter(secondDate);
    }

    /**
     * Checks if first date is in period between second and third.
     *
     * @param reference  the date to be checked
     * @param start  the start of the period
     * @param end  the end of the period
     * @return true if first date is in range, false otherwise
     */
    public static boolean inRange(DateTime reference, DateTime start, DateTime end) {
        return (isOnOrAfter(reference, start)) && (isOnOrBefore(reference, end));
    }

    /**
     * Returns number of days left until weekend.
     *
     * @param date  the date from which days should be counted
     * @param calendarWeekStartDay  the day at which weekend starts
     * @return days left until weekend
     */
    public static int daysToCalendarWeekEnd(LocalDate date, int calendarWeekStartDay) {
        final int daysInWeek = 7;
        int currentDayOfWeek = date.get(DateTimeFieldType.dayOfWeek());
        int calendarWeekEndDay = (calendarWeekStartDay + daysInWeek - 1) % 7;
        int intervalBetweenWeekEndAndCurrentDay = calendarWeekEndDay - currentDayOfWeek;
        return intervalBetweenWeekEndAndCurrentDay >= 0 ? intervalBetweenWeekEndAndCurrentDay :
                intervalBetweenWeekEndAndCurrentDay + 7;
    }

    /**
     * Creates new {@code DateTime} from given {@code LocalDate} and {@code Time}.
     *
     * @param localDate  the {@code LocalDate} to be stored
     * @param time  the {@code Time} to be stored
     * @return the new {@code DateTime} instance
     */
    public static DateTime newDateTime(LocalDate localDate, Time time) {
        return newDateTime(localDate, time.getHour(), time.getMinute(), 0);
    }

    /**
     * Creates new {@code DateTime} from given information.
     *
     * @param year  the year to be stored
     * @param month  the month to be stored
     * @param day  the day to be stored
     * @param time  the time to be stored
     * @return the new {@code DateTime} instance
     */
    public static DateTime newDateTime(int year, int month, int day, Time time) {
        return newDateTime(newDate(year, month, day), time.getHour(), time.getMinute(), 0);
    }

    /**
     * Creates new {@code DateTime} from given information. Time is set to 00:00:00.
     *
     * @param year  the year to be stored
     * @param month  the month to be stored
     * @param day  the day to be stored
     * @return the new {@code DateTime} instance
     */
    public static DateTime newDateTime(int year, int month, int day) {
        return newDateTime(newDate(year, month, day), 0, 0, 0);
    }

    /**
     * Creates new {@code DateTime} from given information.
     *
     * @param year  the year to be stored
     * @param month  the month to be stored
     * @param day  the day to be stored
     * @param hour  the hour to be stored
     * @param minute  the minute to be stored
     * @param second  the second to be stored
     * @return the new {@code DateTime} instance
     */
    public static DateTime newDateTime(int year, int month, int day, int hour, int minute, int second) {
        return newDateTime(newDate(year, month, day), hour, minute, second);
    }

    /**
     * Returns difference, in years, between given date and today.
     *
     * @param startDate  the date from which year are counted
     * @return difference in years
     */
    public static int getDifferenceOfDatesInYears(Date startDate) {
        Period period = new Period(newDate(startDate), today(), PeriodType.yearMonthDay());
        return period.getYears();
    }

    /**
     * Creates {@code DateTime} with time set to 23:59:59:999 and date equal to the given one.
     *
     * @param dateTime  the {@code Date} to be stored
     * @return the new {@code DateTime} instance
     */
    public static DateTime endOfDay(Date dateTime) {
        final int hour = 23;
        final int minute = 59;
        final int second = 59;
        final int millis = 999;
        return new DateTime(dateTime).withTime(hour, minute, second, millis);
    }

    /**
     * Returns first next applicable week day.
     *
     * @param fromDate  the date from which next day should be searched
     * @param applicableDays  list of applicable days
     * @return next applicable week day
     */
    public static DateTime nextApplicableWeekDay(DateTime fromDate, List<DayOfWeek> applicableDays) {
        return nextApplicableWeekDayIncludingFromDate(fromDate.dayOfMonth().addToCopy(1), applicableDays);
    }

    /**
     * Returns first next applicable week day(including current day).
     *
     * @param fromDate  the date from which next day should be searched
     * @param applicableDays  list of applicable days
     * @return next applicable week day
     */
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

    /**
     * Filters given dates and returns only those that are past or at the given date.
     *
     * @param date  the date to be used as filter
     * @param dates  dates to be filtered
     * @return  list of date that are equal or greater than given date
     */
    public static List<DateTime> greaterThanOrEqualTo(final DateTime date, final List<DateTime> dates) {
        List<DateTime> list = new ArrayList<>(dates.size());

        for (DateTime dt : dates) {
            if (isOnOrAfter(dt, date)) {
                list.add(dt);
            }
        }

        return list;
    }

    /**
     * Filters given dates and returns only those that are before the given date.
     *
     * @param date  the date to be used as filter
     * @param dates  dates to be filtered
     * @return  list of date that are before the given date
     */
    public static List<DateTime> lessThan(final DateTime date, final List<DateTime> dates) {
        List<DateTime> list = new ArrayList<>(dates.size());

        for (DateTime dt : dates) {
            if (dt.isBefore(date)) {
                list.add(dt);
            }
        }

        return list;
    }

    /**
     * Returns list of days equal to numberOfDays from given day.
     *
     * @param day  the day of week from which list should begin
     * @param numberOfDays  the number of days which should be included in the list
     * @return  the list of days
     */
    public static List<DayOfWeek> daysStarting(DayOfWeek day, int numberOfDays) {
        List<DayOfWeek> days = new ArrayList<>();
        for (int i = 0; i <= numberOfDays; i++) {
            days.add(getDayOfWeek(DateUtil.today().withDayOfWeek(day.getValue()).plusDays(i)));
        }
        return days;
    }

    /**
     * This is a utility class and should not be instantiated
     */
    private DateUtil() {
    }
}
