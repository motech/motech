package org.motechproject.commons.date.util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.motechproject.commons.date.util.datetime.DateTimeSource;
import org.motechproject.commons.date.util.datetime.DefaultDateTimeSource;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Utility class for {@code DateTimeSource}.
 */
public final class DateTimeSourceUtil {

    private static DateTimeSource sourceInstance = new DefaultDateTimeSource();

    /**
     * This is a utility class and should not be instantiated
     */
    private DateTimeSourceUtil() {

    }

    /**
     * Returns current time as an instance of {@code DateTime}.
     *
     * @return the current time
     */
    public static DateTime now() {
        return sourceInstance.now();
    }

    /**
     * Returns current time as an instance of {@code LocalDateTime}
     *
     * @return the current time
     */
    public static LocalDateTime javaTimeNow() {
        return sourceInstance.javaTimeNow();
    }

    /**
     * Returns current local date.
     *
     * @return the current local date as an instance of {@code LocalDate}
     */
    public static LocalDate today() {
        return sourceInstance.today();
    }

    /**
     * Returns time zone used by class.
     *
     * @return time zone used by class
     */
    public static DateTimeZone timeZone() {
        return sourceInstance.timeZone();
    }

    /**
     * Returns time zone id used by class.
     *
     * @return time zone id used by class
     */
    public static ZoneId timeZoneId() {
        return sourceInstance.timeZoneId();
    }

    public static void setSourceInstance(DateTimeSource sourceInstance) {
        DateTimeSourceUtil.sourceInstance = sourceInstance;
    }
}

