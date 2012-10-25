package org.motechproject.commons.date.util;

import org.joda.time.DateTime;
import org.joda.time.MutablePeriod;
import org.joda.time.Period;
import org.joda.time.ReadWritablePeriod;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.joda.time.format.PeriodParser;
import org.motechproject.commons.date.ParseException;

import java.util.Locale;

import static org.apache.commons.lang.StringUtils.isBlank;

public class JodaFormatter {

    private PeriodParser periodParser;
    private PeriodFormatter periodFormatter;

    private DateTimeFormatter dateTimeFormatter;

    public JodaFormatter() {

        PeriodFormatterBuilder periodFormatterBuilder = new PeriodFormatterBuilder().appendYears().appendSuffix(" year", " years")
            .appendMonths().appendSuffix(" month", " months")
            .appendWeeks().appendSuffix(" week", " weeks")
            .appendDays().appendSuffix(" day", " days")
            .appendHours().appendSuffix(" hour", " hours")
            .appendMinutes().appendSuffix(" minute", " minutes")
            .appendSeconds().appendSuffix(" second", " seconds");

        periodParser = periodFormatterBuilder.toParser();
        periodFormatter = periodFormatterBuilder.toFormatter();

        dateTimeFormatter = ISODateTimeFormat.dateTime();
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
        if (periodParser.parseInto(period, intervalString, 0, locale) > 0) {
            return period.toPeriod();
        }
        throw new ParseException(String.format("Could not parse %s into time interval.", intervalString));
    }

    public Period parsePeriod(String intervalString) {
        return parse(intervalString, Locale.getDefault());
    }

    /**
     * Format joda period as text, eg: "1 year"
     *
     * @param period time interval
     * @return
     */
    public String formatPeriod(Period period) {
        return periodFormatter.print(period);
    }

    public DateTime parseDateTime(String isoDateTime) {
        return dateTimeFormatter.parseDateTime(isoDateTime);
    }

    public String formatDateTime(DateTime dateTime) {
        return dateTimeFormatter.print(dateTime);
    }
}
