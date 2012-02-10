package org.motechproject.util;

import org.joda.time.*;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.Time;

import java.util.Date;
import java.util.List;

import static org.motechproject.model.DayOfWeek.getDayOfWeek;

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
                .withHourOfDay(hour).withMinuteOfHour(minute).withSecondOfMinute(second).withMillisOfSecond(0);
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

    public static int daysPast(LocalDate date, DayOfWeek dayOfWeek) {
        int count = 0;
        while (date.getDayOfWeek() != dayOfWeek.getValue()) {
            date = date.minusDays(1);
            count++;
        }
        return count;
    }

    public static boolean isOnOrBefore(LocalDate firstDate, LocalDate secondDate) {
        return firstDate.equals(secondDate) || firstDate.isBefore(secondDate);
    }

    public static boolean isOnOrAfter(LocalDate firstDate, LocalDate secondDate) {
        return firstDate.equals(secondDate) || firstDate.isAfter(secondDate);
    }

    public static int daysToCalendarWeekEnd(LocalDate date, int calendarWeekStartDay) {
        int currentDayOfWeek = date.get(DateTimeFieldType.dayOfWeek());
        int calendarWeekEndDay = (calendarWeekStartDay + 6) % 7;
        int intervalBetweenWeekEndAndCurrentDay = calendarWeekEndDay - currentDayOfWeek;
        return intervalBetweenWeekEndAndCurrentDay >= 0 ? intervalBetweenWeekEndAndCurrentDay :
                intervalBetweenWeekEndAndCurrentDay + 7;
    }

    public static DateTime newDateTime(LocalDate localDate, Time time) {
        return newDateTime(localDate, time.getHour(), time.getMinute(), 0);
    }

    public static int getDifferenceOfDatesInYears(Date startDate) {
        Period period = new Period(newDate(startDate), today(), PeriodType.yearMonthDay());
        return period.getYears();
    }
    
    public static DateTime endOfDay(Date dateTime) {
        return new DateTime(dateTime).withTime(23, 59, 59, 999);
    }

    public static DateTime nextApplicableWeekDay(DateTime fromDate, List<DayOfWeek> applicableDays) {
        return nextApplicableWeekDayIncludingFromDate(fromDate.dayOfMonth().addToCopy(1), applicableDays);
    }
    
    public static DateTime nextApplicableWeekDayIncludingFromDate(DateTime fromDate, List<DayOfWeek> applicableDays) {
        int dayOfWeek = fromDate.getDayOfWeek();
        int noOfDaysToNearestCycleDate = 0;
        int WEEK_MAX_DAY = DayOfWeek.Sunday.getValue();
        for (int currentDayOfWeek = dayOfWeek, dayCount = 0; dayCount <= WEEK_MAX_DAY; dayCount++) {
            if (applicableDays.contains(getDayOfWeek(currentDayOfWeek))) {
                noOfDaysToNearestCycleDate = dayCount;
                break;
            }
            if (currentDayOfWeek == WEEK_MAX_DAY) currentDayOfWeek = 1;
            else currentDayOfWeek++;
        }
        return fromDate.dayOfMonth().addToCopy(noOfDaysToNearestCycleDate);
    }
}