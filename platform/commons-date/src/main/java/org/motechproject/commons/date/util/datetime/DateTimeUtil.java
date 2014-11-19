package org.motechproject.commons.date.util.datetime;

import org.joda.time.DateTime;

/**
 * Utility class for the <code>org.joda.time.DateTime</code> class
 */
public final class DateTimeUtil {

    private DateTimeUtil() {
    }

    public static final DateTime MIN_DATETIME = new DateTime(0);
    public static final DateTime IN_100_YEARS = DateTime.now().plusYears(100);
}
