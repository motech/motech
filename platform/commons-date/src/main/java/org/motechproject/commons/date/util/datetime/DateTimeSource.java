package org.motechproject.commons.date.util.datetime;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * A datetime source for the application. Allows mocking of time.
 */
public interface DateTimeSource {

    /**
     * Returns the timezone we are in.
     * @return the timezone
     */
    DateTimeZone timeZone();

    /**
     * Returns the id of timezone we are in.
     * @return the zone id
     */
    ZoneId timeZoneId();

    /**
     * Used for retrieving the current date and time.
     * @return {@link org.joda.time.DateTime} representing the current date and time
     */
    DateTime now();

    /**
     * Used for retrieving the current date and time.
     * @return {@link java.time.LocalDateTime} representing the current date and time
     */
    LocalDateTime javaTimeNow();

    /**
     * Used for retrieving the current date.
     * @return {@link org.joda.time.DateTime} representing the current date
     */
    LocalDate today();
}
