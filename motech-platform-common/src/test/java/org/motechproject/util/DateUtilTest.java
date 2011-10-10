package org.motechproject.util;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.motechproject.model.DayOfWeek;

import static junit.framework.Assert.assertEquals;

public class DateUtilTest {
    @Test
    public void shouldReturnDateOnASpecifiedDayInThePast() {
        LocalDate pastDate = DateUtil.pastDateWith(DateUtil.newDate(2011, 10, 5), DayOfWeek.Monday, 4);
        assertEquals(DateUtil.newDate(2011, 9, 26), pastDate);
    }

    @Test
    public void shouldReturnDateOnASpecifiedDayInThePast2() {
        LocalDate pastDate = DateUtil.pastDateWith(DateUtil.newDate(2011, 10, 10), DayOfWeek.Wednesday, 4);
        assertEquals(DateUtil.newDate(2011, 10, 5), pastDate);
    }
}
