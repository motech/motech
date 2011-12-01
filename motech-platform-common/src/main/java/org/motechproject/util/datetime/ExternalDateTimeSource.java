package org.motechproject.util.datetime;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;

public class ExternalDateTimeSource implements DateTimeSource {
    private DateTimeZone timeZone;
    private DateTimeConfiguration configuration;

    public ExternalDateTimeSource(DateTimeConfiguration configuration) {
        this.configuration = configuration;
        timeZone = configuration.timeZone();
    }

    @Override
    public DateTimeZone timeZone() {
        return timeZone;
    }

    @Override
    public DateTime now() {
        DateTime dateTime = new DateTime(timeZone);
        LocalDate today = today();
        return dateTime.withYear(today.getYear()).withMonthOfYear(today.getMonthOfYear()).withDayOfMonth(today.getDayOfMonth()).
                withHourOfDay(dateTime.getHourOfDay()).withMinuteOfHour(dateTime.getMinuteOfHour()).withSecondOfMinute(dateTime.getSecondOfMinute());
    }

    @Override
    public LocalDate today() {
        String configuredToday = configuration.currentValueFor(DateTimeConfiguration.TODAY_PROPERTY_NAME);
        if (StringUtils.isEmpty(configuredToday)) return new LocalDate(timeZone());

        LocalDate configuredDate = LocalDate.parse(configuredToday);
        return new LocalDate(timeZone).withYear(configuredDate.getYear()).withMonthOfYear(configuredDate.getMonthOfYear()).withDayOfMonth(configuredDate.getDayOfMonth());
    }
}
