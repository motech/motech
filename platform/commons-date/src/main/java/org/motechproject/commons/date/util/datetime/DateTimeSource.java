package org.motechproject.commons.date.util.datetime;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;

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
     * Used for retrieving the current date and time.
     * @return {@link org.joda.time.DateTime} representing the current date and time
     */
    DateTime now();

    /**
     * Used for retrieving the current date.
     * @return {@link org.joda.time.DateTime} representing the current date
     */
    LocalDate today();
}
