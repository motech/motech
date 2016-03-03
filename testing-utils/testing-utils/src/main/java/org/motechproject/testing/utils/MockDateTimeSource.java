package org.motechproject.testing.utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.motechproject.commons.date.util.datetime.DateTimeSource;

/**
 * A mock date time source, which is used by time faking. This will
 * be injected into the static class holding the date time source and thus change
 * the result of current date/time lookups going through the date utils provided by Motech.
 */
public class MockDateTimeSource implements DateTimeSource {
    private DateTime dateTime;

    public MockDateTimeSource(LocalDate localDate) {
        this(localDate.toDateTime(LocalTime.MIDNIGHT));
    }

    public MockDateTimeSource(DateTime dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public DateTimeZone timeZone() {
        return dateTime.getZone();
    }

    @Override
    public DateTime now() {
        return dateTime;
    }

    @Override
    public LocalDate today() {
        return dateTime.toLocalDate();
    }
}
