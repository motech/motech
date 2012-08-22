package org.motechproject.util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.motechproject.util.datetime.DateTimeSource;
import org.motechproject.util.datetime.DefaultDateTimeSource;

public final class DateTimeSourceUtil {
    private static DateTimeSource sourceInstance = new DefaultDateTimeSource();

    private DateTimeSourceUtil() {

    }

    public static DateTime now() {
        return sourceInstance.now();
    }

    public static LocalDate today() {
        return sourceInstance.today();
    }

    public static DateTimeZone timeZone() {
        return sourceInstance.timeZone();
    }

    public static void setSourceInstance(DateTimeSource sourceInstance) {
        DateTimeSourceUtil.sourceInstance = sourceInstance;
    }
}

