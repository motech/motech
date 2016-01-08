package org.motechproject.commons.date.model;

import org.joda.time.LocalDate;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.motechproject.commons.date.model.DayOfWeek.getDayOfWeek;

public class DayOfWeekTest {

    @Test
    public void shouldGetValueOfDays() {
        assertEquals(1, DayOfWeek.Monday.getValue());
        assertEquals(2, DayOfWeek.Tuesday.getValue());
        assertEquals(3, DayOfWeek.Wednesday.getValue());
        assertEquals(4, DayOfWeek.Thursday.getValue());
        assertEquals(5, DayOfWeek.Friday.getValue());
        assertEquals(6, DayOfWeek.Saturday.getValue());
        assertEquals(7, DayOfWeek.Sunday.getValue());
    }

    @Test
    public void shouldReturnDayOfWeekForTheSpecifiedNumber() {
        assertEquals(DayOfWeek.Monday, DayOfWeek.getDayOfWeek(1));
        assertEquals(DayOfWeek.Tuesday, DayOfWeek.getDayOfWeek(2));
        assertEquals(DayOfWeek.Wednesday, DayOfWeek.getDayOfWeek(3));
        assertEquals(DayOfWeek.Thursday, DayOfWeek.getDayOfWeek(4));
        assertEquals(DayOfWeek.Friday, DayOfWeek.getDayOfWeek(5));
        assertEquals(DayOfWeek.Saturday, DayOfWeek.getDayOfWeek(6));
        assertEquals(DayOfWeek.Sunday, DayOfWeek.getDayOfWeek(7));
    }

    @Test
    public void shouldReturnDayOfWeekForTheSpecifiedDate() {
        DayOfWeek dayOfWeek = DayOfWeek.getDayOfWeek(new LocalDate(2011, 10, 5));
        assertEquals(DayOfWeek.Wednesday, dayOfWeek);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenSpecifiedNonExistingDay() {
        DayOfWeek.getDayOfWeek(8);
    }

    @Test
    public void shouldReturnDaysOfWeekStartingToday() {
        List<DayOfWeek> days = daysStarting(DayOfWeek.Saturday, 2);
        assertEquals(3, days.size());
        assertTrue(days.contains(DayOfWeek.Saturday));
        assertTrue(days.contains(DayOfWeek.Sunday));
        assertTrue(days.contains(DayOfWeek.Monday));
    }

    @Test
    public void shouldReturnDaysOfWeek_IfOnlyToday() {
        List<DayOfWeek> days = daysStarting(DayOfWeek.Saturday, 0);
        assertEquals(1, days.size());
        assertTrue(days.contains(DayOfWeek.Saturday));
    }

    @Test
    public void shouldParseDayOfWeek() {
        assertEquals(DayOfWeek.Sunday, DayOfWeek.parse("sun"));
        assertEquals(DayOfWeek.Sunday, DayOfWeek.parse("Sun"));
        assertEquals(DayOfWeek.Sunday, DayOfWeek.parse("Sunday"));
    }

    @Test
    public void shouldVerifyCronDayOfWeek() {
        assertEquals(1, DayOfWeek.Sunday.getCronValue());
        assertEquals(2, DayOfWeek.Monday.getCronValue());
        assertEquals(3, DayOfWeek.Tuesday.getCronValue());
        assertEquals(4, DayOfWeek.Wednesday.getCronValue());
        assertEquals(5, DayOfWeek.Thursday.getCronValue());
        assertEquals(6, DayOfWeek.Friday.getCronValue());
        assertEquals(7, DayOfWeek.Saturday.getCronValue());
    }

    private static List<DayOfWeek> daysStarting(DayOfWeek day, int numberOfDays) {
        List<DayOfWeek> days = new ArrayList<>();
        for (int i = 0; i <= numberOfDays; i++) {
            days.add(getDayOfWeek(new LocalDate().withDayOfWeek(day.getValue()).plusDays(i)));
        }
        return days;
    }
}