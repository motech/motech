package org.motechproject.util;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.util.datetime.DateTimeSource;
import org.motechproject.util.datetime.DateTimeSourceFactory;

public class DateTimeSourceUtil {
    public static DateTimeSource SourceInstance = DateTimeSourceFactory.create();

    public static DateTime now() {
        return SourceInstance.now();
    }

    public static LocalDate today() {
        return SourceInstance.today();
    }
}
