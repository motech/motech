package org.motechproject.commons.date.util.datetime;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Default implementation of {@code DateTimeSource}.
 */
public class DefaultDateTimeSource implements DateTimeSource {

    private DateTimeZone timeZone;
    private ZoneId zoneId;

    public DefaultDateTimeSource() {
        TimeZone tz = Calendar.getInstance().getTimeZone();
        this.timeZone = DateTimeZone.forTimeZone(tz);

        this.zoneId = ZonedDateTime.now().getZone();
    }

    @Override
    public DateTimeZone timeZone() {
        return timeZone;
    }

    @Override
    public ZoneId timeZoneId() {
        return zoneId;
    }

    @Override
    public DateTime now() {
        return new DateTime(timeZone);
    }

    @Override
    public LocalDateTime javaTimeNow() {
        return LocalDateTime.now(zoneId);
    }

    @Override
    public LocalDate today() {
        return new LocalDate(timeZone);
    }
}
