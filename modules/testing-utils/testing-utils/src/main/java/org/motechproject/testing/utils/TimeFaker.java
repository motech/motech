package org.motechproject.testing.utils;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.commons.date.util.DateTimeSourceUtil;
import org.motechproject.commons.date.util.datetime.DefaultDateTimeSource;

public final class TimeFaker {

    private TimeFaker() {
        // static utility class
    }

    public static void fakeNow(DateTime now) {
        DateTimeSourceUtil.setSourceInstance(new MockDateTimeSource(now));
    }

    public static void fakeToday(LocalDate today) {
        DateTimeSourceUtil.setSourceInstance(new MockDateTimeSource(today));
    }

    public static void stopFakingTime() {
        DateTimeSourceUtil.setSourceInstance(new DefaultDateTimeSource());
    }
}
