package org.motechproject.mds.config;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.motechproject.mds.config.TimeUnit.DAYS;
import static org.motechproject.mds.config.TimeUnit.HOURS;
import static org.motechproject.mds.config.TimeUnit.MONTHS;
import static org.motechproject.mds.config.TimeUnit.UNKNOWN;
import static org.motechproject.mds.config.TimeUnit.WEEKS;
import static org.motechproject.mds.config.TimeUnit.YEARS;
import static org.motechproject.mds.config.TimeUnit.fromString;

public class TimeUnitTest {

    @Test
    public void shouldReturnCorrectTimeInMillis() throws Exception {
        assertEquals(0L, UNKNOWN.inMillis());
        assertEquals(3600000L, HOURS.inMillis());
        assertEquals(86400000L, DAYS.inMillis());
        assertEquals(604800000L, WEEKS.inMillis());
        assertEquals(2678400000L, MONTHS.inMillis());
        assertEquals(31536000000L, YEARS.inMillis());
    }

    @Test
    public void shouldConvertStringToAppopriateTimeUnit() throws Exception {
        assertMode(UNKNOWN, "UNKNOWN", "unknown", "UnKnOwN", "uNkNoWn", "unkNOWN", "UNKnown");
        assertMode(HOURS, "HOURS", "hours", "HoUrS", "hOuRs", "houRS", "HOUrs");
        assertMode(DAYS, "DAYS", "days", "DaYs", "dAyS", "daYS", "DAys");
        assertMode(WEEKS, "WEEKS", "weeks", "WeEkS", "wEeKs", "weeKS", "WEEks");
        assertMode(MONTHS, "MONTHS", "months", "MoNtHs", "mOnThS", "monTHS", "MONths");
        assertMode(YEARS, "YEARS", "years", "YeArS", "yEaRs", "yeaRS", "YEArs");

        // for other vlues the UNKNOWN mode should be returned
        assertMode(UNKNOWN, "     ", "", null, "string", "some value", "h", "d", "w", "m", "y");
    }

    private void assertMode(TimeUnit mode, String... values) {
        for (String value : values) {
            assertEquals(mode, fromString(value));
        }
    }

}
