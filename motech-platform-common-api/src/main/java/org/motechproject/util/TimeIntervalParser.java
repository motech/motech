package org.motechproject.util;

import org.joda.time.MutablePeriod;
import org.joda.time.Period;
import org.joda.time.ReadWritablePeriod;
import org.joda.time.format.PeriodFormatterBuilder;
import org.joda.time.format.PeriodParser;
import org.motechproject.valueobjects.ParseException;

import java.util.Locale;

import static org.apache.commons.lang.StringUtils.isBlank;

public class TimeIntervalParser {
    private PeriodParser parser;

    public TimeIntervalParser() {

        parser = new PeriodFormatterBuilder().appendYears().appendSuffix(" year", " years")
                .appendMonths().appendSuffix(" month", " months")
                .appendWeeks().appendSuffix(" week", " weeks")
                .appendDays().appendSuffix(" day", " days")
                .appendDays().appendSuffix(" day", " days")
                .appendHours().appendSuffix(" hour", " hours")
                .appendMinutes().appendSuffix(" minute", " minutes")
                .appendSeconds().appendSuffix(" second", " seconds").toParser();
    }

    /**
     * Parse time interval in different units, eg: "1 year"
     *
     * @param intervalString time interval
     *                       format <number> <unit>
     *                       number: integer
     *                       unit :  year, month, week, day, hour, minute, second (can use plural forms also)
     *                       currently compound units like 1 year and 2 months are not supported
     * @return
     */
    public Period parse(String intervalString, Locale locale) {
        if (isBlank(intervalString)) {
            return new Period(0);
        }
        ReadWritablePeriod period = new MutablePeriod();
        if (parser.parseInto(period, intervalString, 0, locale) > 0) {
            return period.toPeriod();
        }
        throw new ParseException(String.format("Could not parse %s into time interval.", intervalString));
    }

    public Period parse(String intervalString) {
        return parse(intervalString, Locale.getDefault());
    }
}
