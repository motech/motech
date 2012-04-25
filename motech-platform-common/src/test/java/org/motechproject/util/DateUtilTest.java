package org.motechproject.util;

import junitx.framework.Assert;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Test;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.Time;
import org.motechproject.util.datetime.DateTimeSource;
import org.motechproject.util.datetime.DefaultDateTimeSource;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.motechproject.model.DayOfWeek.Monday;
import static org.motechproject.model.DayOfWeek.Wednesday;
import static org.motechproject.util.DateUtil.*;

public class DateUtilTest {
    @After
    public void tearDown() {
        DateTimeSourceUtil.SourceInstance = new DefaultDateTimeSource();
    }

    @Test
    public void shouldFindDaysToCalendarWeekEnd() {

        assertThat(daysToCalendarWeekEnd(newDate(2011, 11, 19), Wednesday.getValue()), is(3));
        assertThat(daysToCalendarWeekEnd(newDate(2011, 11, 20), Wednesday.getValue()), is(2));
        assertThat(daysToCalendarWeekEnd(newDate(2011, 11, 21), Wednesday.getValue()), is(1));
        assertThat(daysToCalendarWeekEnd(newDate(2011, 11, 22), Wednesday.getValue()), is(0));
        assertThat(daysToCalendarWeekEnd(newDate(2011, 11, 23), Wednesday.getValue()), is(6));
        assertThat(daysToCalendarWeekEnd(newDate(2011, 11, 24), Wednesday.getValue()), is(5));

        assertThat(daysToCalendarWeekEnd(newDate(2011, 11, 19), Monday.getValue()), is(1));
        assertThat(daysToCalendarWeekEnd(newDate(2011, 11, 20), Monday.getValue()), is(0));
        assertThat(daysToCalendarWeekEnd(newDate(2011, 11, 21), Monday.getValue()), is(6));
        assertThat(daysToCalendarWeekEnd(newDate(2011, 11, 22), Monday.getValue()), is(5));
        assertThat(daysToCalendarWeekEnd(newDate(2011, 11, 23), Monday.getValue()), is(4));
        assertThat(daysToCalendarWeekEnd(newDate(2011, 11, 24), Monday.getValue()), is(3));
    }

    @Test
    public void shouldReturnTheNumberOfYearsFromAGivenDate(){
        mockCurrentDate(new DateTime(2011, 9, 9, 9, 30, 0, 0));
        assertEquals(2, getDifferenceOfDatesInYears(newDate(2008, 12, 12).toDate()));
    }

    @Test
    public void shouldGetNextApplicableDateAfterGivenDateBasedOnApplicableWeekDays() {

        DateTime oct1Sat2011 = new DateTime(2011, 10, 1, 0, 0);
        DateTime oct2Sun2011 = new DateTime(2011, 10, 2, 2, 0);
        DateTime oct3Mon2011 = new DateTime(2011, 10, 3, 0, 0);
        DateTime oct4Tue2011 = new DateTime(2011, 10, 4, 5, 0);
        DateTime oct6Thu2011 = new DateTime(2011, 10, 6, 3, 0);

        List<DayOfWeek> applicableDays = asList(DayOfWeek.Monday, DayOfWeek.Wednesday, DayOfWeek.Friday);
        assertThat(nextApplicableWeekDay(oct1Sat2011, applicableDays), is(oct1Sat2011.dayOfYear().addToCopy(2)));
        assertThat(nextApplicableWeekDay(oct2Sun2011, applicableDays), is(oct2Sun2011.dayOfYear().addToCopy(1)));
        assertThat(nextApplicableWeekDay(oct3Mon2011, applicableDays), is(oct3Mon2011.dayOfYear().addToCopy(2)));
        assertThat(nextApplicableWeekDay(oct4Tue2011, applicableDays), is(oct4Tue2011.dayOfYear().addToCopy(1)));
        assertThat(nextApplicableWeekDay(oct6Thu2011, applicableDays), is(oct6Thu2011.dayOfYear().addToCopy(1)));

        DateTime oct7Fri2011 = new DateTime(2011, 10, 7, 0, 1);
        DateTime oct8Sat2011 = new DateTime(2011, 10, 8, 0, 3);
        DateTime oct9Sun2011 = new DateTime(2011, 10, 9, 0, 4);
        DateTime oct10Mon2011 = new DateTime(2011, 10, 10, 5, 5);

        applicableDays = asList(DayOfWeek.Sunday, DayOfWeek.Saturday);
        assertThat(nextApplicableWeekDay(oct6Thu2011, applicableDays), is(oct6Thu2011.dayOfYear().addToCopy(2)));
        assertThat(nextApplicableWeekDay(oct7Fri2011, applicableDays), is(oct7Fri2011.dayOfYear().addToCopy(1)));
        assertThat(nextApplicableWeekDay(oct8Sat2011, applicableDays), is(oct8Sat2011.dayOfYear().addToCopy(1)));
        assertThat(nextApplicableWeekDay(oct9Sun2011, applicableDays), is(oct9Sun2011.dayOfYear().addToCopy(6)));
        assertThat(nextApplicableWeekDay(oct10Mon2011, applicableDays), is(oct10Mon2011.dayOfYear().addToCopy(5)));

        DateTime feb25Sat2012 = new DateTime(2012, 2, 25, 0, 5);
        applicableDays = asList(DayOfWeek.Saturday);
        DateTime actualMar3Sat2012 = feb25Sat2012.dayOfYear().addToCopy(7);
        assertNotSame(feb25Sat2012, actualMar3Sat2012);
        assertThat(nextApplicableWeekDay(feb25Sat2012, applicableDays), is(actualMar3Sat2012));
    }

    @Test
    public void shouldGetNextApplicableDateIncludingFromDateBasedOnApplicableWeekDays() {

           DateTime oct1Sat2011 = new DateTime(2011, 10, 1, 0, 0);
           DateTime oct2Sun2011 = new DateTime(2011, 10, 2, 2, 0);
           DateTime oct3Mon2011 = new DateTime(2011, 10, 3, 0, 0);
           DateTime oct4Tue2011 = new DateTime(2011, 10, 4, 5, 0);
           DateTime oct6Thu2011 = new DateTime(2011, 10, 6, 3, 0);

           List<DayOfWeek> applicableDays = asList(DayOfWeek.Monday, DayOfWeek.Wednesday, DayOfWeek.Friday);
           assertThat(nextApplicableWeekDayIncludingFromDate(oct1Sat2011, applicableDays), is(oct1Sat2011.dayOfYear().addToCopy(2)));
           assertThat(nextApplicableWeekDayIncludingFromDate(oct2Sun2011, applicableDays), is(oct2Sun2011.dayOfYear().addToCopy(1)));
           assertThat(nextApplicableWeekDayIncludingFromDate(oct3Mon2011, applicableDays), is(oct3Mon2011));
           assertThat(nextApplicableWeekDayIncludingFromDate(oct4Tue2011, applicableDays), is(oct4Tue2011.dayOfYear().addToCopy(1)));
           assertThat(nextApplicableWeekDayIncludingFromDate(oct6Thu2011, applicableDays), is(oct6Thu2011.dayOfYear().addToCopy(1)));
    }

    @Test
    public void shouldReturnEndOfDayForGivenTime() {
        DateTime time = new DateTime(2010, 10, 10, 12, 22, 22, 223);
        assertEquals(new DateTime(2010, 10, 10, 23, 59, 59, 999), DateUtil.endOfDay(time.toDate()));
    }

    @Test
    public void shouldReturnWhetherGivenDateTimeFallsUnderSpecifiedInclusiveRange() {
        DateTime reference = new DateTime(2010, 10, 10, 12, 22, 22, 223);
        assertTrue(DateUtil.inRange(reference, new DateTime(2010, 10, 9, 12, 22, 22, 223), new DateTime(2010, 10, 11, 12, 22, 22, 223)));
        assertTrue(DateUtil.inRange(reference, new DateTime(2010, 10, 10, 12, 22, 22, 223), new DateTime(2010, 10, 11, 12, 22, 22, 223)));
        assertTrue(DateUtil.inRange(reference, new DateTime(2010, 10, 9, 12, 22, 22, 223), new DateTime(2010, 10, 10, 12, 22, 22, 223)));
        assertFalse(DateUtil.inRange(reference, new DateTime(2010, 10, 9, 12, 22, 22, 223), new DateTime(2010, 10, 10, 12, 22, 22, 222)));
        assertFalse(DateUtil.inRange(reference, new DateTime(2010, 10, 10, 12, 22, 22, 224), new DateTime(2010, 10, 10, 12, 22, 22, 223)));
        assertTrue(DateUtil.inRange(reference, new DateTime(2010, 10, 9, 12, 22, 22, 224), new DateTime(2010, 10, 10, 12, 22, 22, 223)));

    }

    @Test
    public void shouldReturnTimeFromDateTime() {
        assertThat(DateUtil.time(new DateTime(2012, 12, 2, 19, 9, 38)), is(new Time(19, 9)));
        assertThat(DateUtil.time(new DateTime(2012, 12, 2, 1, 58, 57)), is(new Time(1, 58)));
    }

    @Test
    public void shouldThrowExceptionIfApplicableDaysIsEmptyForNextApplicableDate() {
        try {
            nextApplicableWeekDayIncludingFromDate(new DateTime(2011, 10, 6, 3, 0), Collections.<DayOfWeek>emptyList());
            Assert.fail("expected invalid argument exception");
        } catch (IllegalArgumentException iae) {}

        try {
            nextApplicableWeekDay(new DateTime(2011, 10, 6, 3, 0), Collections.<DayOfWeek>emptyList());
            Assert.fail("expected invalid argument exception");
        } catch (IllegalArgumentException iae) {}
    }

    private void mockCurrentDate(final DateTime currentDate) {
        DateTimeSourceUtil.SourceInstance = new DateTimeSource() {

            @Override
            public DateTimeZone timeZone() {
                return currentDate.getZone();
            }

            @Override
            public DateTime now() {
                return currentDate;
            }

            @Override
            public LocalDate today() {
                return currentDate.toLocalDate();
            }
        };
    }
}
