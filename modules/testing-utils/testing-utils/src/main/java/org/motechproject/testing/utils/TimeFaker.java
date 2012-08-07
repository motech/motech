package org.motechproject.testing.utils;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.util.DateTimeSourceUtil;
import org.motechproject.util.datetime.DefaultDateTimeSource;

public class TimeFaker {

    public static void fakeNow(DateTime now) {
        DateTimeSourceUtil.setSourceInstance(new FakeDateTimeSource(now));
    }

    public static void fakeToday(LocalDate today) {
        DateTimeSourceUtil.setSourceInstance(new FakeDateTimeSource(today));
    }

    public static void stopFakingTime() {
        DateTimeSourceUtil.setSourceInstance(new DefaultDateTimeSource());
    }
}
