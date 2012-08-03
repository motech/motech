package org.motechproject.testing.utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.motechproject.util.datetime.DateTimeSource;

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

