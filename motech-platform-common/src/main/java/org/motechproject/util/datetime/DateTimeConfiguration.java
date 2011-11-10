package org.motechproject.util.datetime;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.motechproject.MotechException;
import org.motechproject.util.DateUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.TimeZone;

public class DateTimeConfiguration {
    private PropertySource propertySource;
    static final String TIMEZONE_PROPERTY_NAME = "timezone";
    static final String TODAY_PROPERTY_NAME = "dateutil.today";
    private DateTimeZone timeZone;

    public DateTimeConfiguration(PropertySource propertySource) {
        this.propertySource = propertySource;
        String timeZoneString = propertySource.getProperty(TIMEZONE_PROPERTY_NAME);
        timeZone = StringUtils.isEmpty(timeZoneString) ? DateTimeZone.getDefault() : DateTimeZone.forTimeZone(TimeZone.getTimeZone(timeZoneString));
    }

    public DateTimeZone timeZone() {
        return timeZone;
    }

    public String currentValueFor(String propertyName) {
        return propertySource.getProperty(propertyName);
    }
}