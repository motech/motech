package org.motechproject.util;

import org.joda.time.Period;
import org.junit.Test;
import org.motechproject.util.TimeIntervalParser;
import org.motechproject.valueobjects.ParseException;

import static org.junit.Assert.assertEquals;

public class TimeIntervalParserTest {
    @Test
    public void shouldParseIntervalInYear() throws Exception {
        assertYear(1, new TimeIntervalParser().parse("1 year"));

        assertYear(1, new TimeIntervalParser().parse("1 years"));

        assertYear(2, new TimeIntervalParser().parse("2 years"));

    }

    @Test
    public void shouldParseIntervalInMonth() throws Exception {
        assertEquals(5, new TimeIntervalParser().parse("5 months").getMonths());
        assertEquals(5, new TimeIntervalParser().parse("5 days").getDays());
        assertEquals(5, new TimeIntervalParser().parse("5 hours").getHours());
        assertEquals(5, new TimeIntervalParser().parse("5 minutes").getMinutes());
        assertEquals(5, new TimeIntervalParser().parse("5 seconds").getSeconds());
    }

    @Test(expected = ParseException.class)
    public void shouldFailParsingForInvalidFormat() throws Exception {
        new TimeIntervalParser().parse("five months").getMonths();
    }

    private void assertYear(int yearsExpected, Period oneYear) {
        assertEquals(yearsExpected,oneYear.getYears());
        assertEquals(0,oneYear.getMonths());
        assertEquals(0,oneYear.getDays());
        assertEquals(0,oneYear.getHours());
        assertEquals(0,oneYear.getMinutes());
        assertEquals(0,oneYear.getSeconds());
    }
}
