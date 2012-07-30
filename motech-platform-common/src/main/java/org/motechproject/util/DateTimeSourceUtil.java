package org.motechproject.util;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.util.datetime.DateTimeSource;
import org.motechproject.util.datetime.DefaultDateTimeSource;

public class DateTimeSourceUtil {
    public static DateTimeSource sourceinstance = new DefaultDateTimeSource();

    public static DateTime now() {
        return sourceinstance.now();
    }

    public static LocalDate today() {
        return sourceinstance.today();
    }
}
