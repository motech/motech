package org.motechproject.util;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.motechproject.model.DayOfWeek.Monday;
import static org.motechproject.model.DayOfWeek.Wednesday;
import static org.motechproject.util.DateUtil.daysToCalendarWeekEnd;
import static org.motechproject.util.DateUtil.newDate;

public class DateUtilTest {

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
}
