package org.motechproject.util;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.motechproject.model.DayOfWeek;

import static junit.framework.Assert.assertEquals;

public class DateUtilTest {
    @Test
    public void shouldReturnADateInThePastOnTheSpecifiedDayAfterTheSpecifedDate_SameDay() {
        LocalDate pastDate = DateUtil.pastDateWith(DayOfWeek.Tuesday, DateUtil.newDate(2011, 10, 25), DateUtil.newDate(2011, 10, 25));
        assertEquals(DateUtil.newDate(2011, 10, 25), pastDate);
    }

    @Test
    public void shouldReturnADateInThePastOnTheSpecifiedDayAfterTheSpecifedDate_DifferenceOfOneWeek() {
        LocalDate pastDate = DateUtil.pastDateWith(DayOfWeek.Tuesday, DateUtil.newDate(2011, 10, 18), DateUtil.newDate(2011, 10, 27));
        assertEquals(DateUtil.newDate(2011, 10, 25), pastDate);
    }

    @Test
    public void shouldReturnADateInThePastOnTheSpecifiedDayAfterTheSpecifedDate_DifferenceOfMoreThanOneWeek() {
        LocalDate pastDate = DateUtil.pastDateWith(DayOfWeek.Tuesday, DateUtil.newDate(2011, 10, 5), DateUtil.newDate(2011, 10, 27));
        assertEquals(DateUtil.newDate(2011, 10, 25), pastDate);
    }

    @Test
    public void shouldReturnADateInThePastOnTheSpecifiedDayAfterTheSpecifedDate_DifferenceOfLessThanOneWeek() {
        LocalDate pastDate = DateUtil.pastDateWith(DayOfWeek.Tuesday, DateUtil.newDate(2011, 10, 15), DateUtil.newDate(2011, 10, 17));
        assertEquals(DateUtil.newDate(2011, 10, 11), pastDate);
    }
}
