package org.motechproject.mds.config;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Duration;
import org.joda.time.Hours;
import org.joda.time.Months;
import org.joda.time.ReadablePeriod;
import org.joda.time.Seconds;
import org.joda.time.Weeks;
import org.joda.time.Years;

import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.motechproject.commons.date.util.DateUtil.nowUTC;


/**
 * The <code>TimeUnit</code> specifies what time unit should be used to specify time when the
 * module trash should be cleaned. This enum is related with the property
 * {@link org.motechproject.mds.util.Constants.Config#MDS_TIME_UNIT}.
 * <p/>
 * Each value from this enum can be converted to long value that presents time interval in
 * milliseconds. For example the {@link #HOURS} value is equal to {@value 3.6E6}.
 * <p/>
 * The {@link #UNKNOWN} value should not be used in code as appropriate value. It was added to
 * ensure that the {@link #fromString(String)} method will not return {@value null} value.
 */
public enum TimeUnit {
    UNKNOWN(Seconds.ZERO), HOURS(Hours.ONE), DAYS(Days.ONE), WEEKS(Weeks.ONE), MONTHS(Months.ONE),
    YEARS(Years.ONE);

    private long millis;

    TimeUnit(ReadablePeriod period) {
        DateTime date = nowUTC();
        millis = new Duration(date, date.plus(period)).getMillis();
    }

    public long inMillis() {
        return millis;
    }

    /**
     * Converts the given string to appropriate time unit. This method will never return
     * {@value null} value. If the appropriate unit doesn't exists then the {@link #UNKNOWN} unit
     * will be returned.
     *
     * @param string the string representation of the time unit.
     * @return the appropriate time unit if exists; otherwize {@link #UNKNOWN}
     */
    public static TimeUnit fromString(String string) {
        TimeUnit result = UNKNOWN;

        if (isNotBlank(string)) {
            for (TimeUnit mode : TimeUnit.values()) {
                if (mode.name().equalsIgnoreCase(string)) {
                    result = mode;
                    break;
                }
            }
        }

        return result;
    }
}
