package org.motechproject.commons.date.util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.junit.Test;
import org.motechproject.commons.date.ParseException;

import static org.junit.Assert.assertEquals;

public class JodaFormatterTest {

    @Test
    public void shouldParseIntervalInYear() throws Exception {
        assertYear(1, new JodaFormatter().parsePeriod("1 year"));

        assertYear(1, new JodaFormatter().parsePeriod("1 years"));

        assertYear(2, new JodaFormatter().parsePeriod("2 years"));
    }

    @Test
    public void shouldParseIntervalInMonth() throws Exception {
        assertEquals(5, new JodaFormatter().parsePeriod("5 months").getMonths());
        assertEquals(5, new JodaFormatter().parsePeriod("5 days").getDays());
        assertEquals(5, new JodaFormatter().parsePeriod("5 hours").getHours());
        assertEquals(5, new JodaFormatter().parsePeriod("5 minutes").getMinutes());
        assertEquals(5, new JodaFormatter().parsePeriod("5 seconds").getSeconds());
    }

    @Test(expected = ParseException.class)
    public void shouldFailParsingForInvalidFormat() throws Exception {
        new JodaFormatter().parsePeriod("five months").getMonths();
    }

    private void assertYear(int yearsExpected, Period oneYear) {
        assertEquals(yearsExpected, oneYear.getYears());
        assertEquals(0, oneYear.getMonths());
        assertEquals(0, oneYear.getDays());
        assertEquals(0,oneYear.getHours());
        assertEquals(0,oneYear.getMinutes());
        assertEquals(0, oneYear.getSeconds());
    }

    @Test
    public void shouldFormatPeriod() {
        assertEquals("5 months", new JodaFormatter().formatPeriod(Period.months(5)));

        assertEquals("2 days", new JodaFormatter().formatPeriod(Period.days(2)));

        assertEquals("1 minute", new JodaFormatter().formatPeriod(Period.minutes(1)));
    }

    @Test
    public void shouldParseDateTime() {
        assertEquals(0, new JodaFormatter().parseDateTime("1970-01-01T00:00:00.000Z").getMillis());

        assertEquals(0, new JodaFormatter().parseDateTime("1970-01-01T05:30:00.000+05:30").getMillis());
    }

    @Test
    public void shouldPrintDateTime() {
        assertEquals("1970-01-01T00:00:00.000Z", new JodaFormatter().formatDateTime(new DateTime(0).toDateTime(DateTimeZone.UTC)));
    }
}
