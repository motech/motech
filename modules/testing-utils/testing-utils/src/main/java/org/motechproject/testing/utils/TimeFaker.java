package org.motechproject.testing.utils;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.commons.date.util.DateTimeSourceUtil;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.commons.date.util.datetime.DefaultDateTimeSource;

/**
 * This is a utility that allows faking time during tests in Motech. It modifies the instance of
 * {@link org.motechproject.commons.date.util.datetime.DateTimeSource} that is held by {@link DateTimeSourceUtil},
 * which is used by {@link org.motechproject.commons.date.util.DateUtil}. It means that the date and time will be mocked
 * for code that relies on using {@link org.motechproject.commons.date.util.DateUtil} to fetch the current date and time.
 * That's why all code in Motech should use {@link org.motechproject.commons.date.util.DateUtil} for getting the current
 * time instant, instead of relying on the regular Joda API.
 */
public final class TimeFaker {

    private TimeFaker() {
        // static utility class
    }

    /**
     * Fakes the time that will be returned by calls to {@link DateUtil#now()}.
     * @param now the date time that should be returned as "now"
     */
    public static void fakeNow(DateTime now) {
        DateTimeSourceUtil.setSourceInstance(new MockDateTimeSource(now));
    }

    /**
     * Fakes the time that will be returned by calls to {@link DateUtil#today()}.
     * @param today the date that should be returned as "today"
     */
    public static void fakeToday(LocalDate today) {
        DateTimeSourceUtil.setSourceInstance(new MockDateTimeSource(today));
    }

    /**
     * Stops faking time by replacing the mocked {@link org.motechproject.commons.date.util.datetime.DateTimeSource}
     * with the default one.
     */
    public static void stopFakingTime() {
        DateTimeSourceUtil.setSourceInstance(new DefaultDateTimeSource());
    }
}
