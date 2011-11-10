package org.motechproject.util.datetime;

import org.apache.commons.lang.StringUtils;

import java.util.logging.Logger;

public class DateTimeSourceFactory {
    public static String DATE_PROPERTIES_FILE = "/date.properties";
    private static Logger logger = Logger.getLogger(DateTimeSourceFactory.class.getName());

    public static DateTimeSource create() {
        return create(DATE_PROPERTIES_FILE);
    }

    static DateTimeSource create(String file) {
        FileBasePropertySource propertySource = new FileBasePropertySource(file);
        DateTimeConfiguration dateTimeConfiguration = new DateTimeConfiguration(propertySource);
        String testMode = dateTimeConfiguration.currentValueFor("test.mode");
        if (StringUtils.isNotEmpty(testMode) && Boolean.parseBoolean(testMode)) {
            logger.info(String.format("Using external date time source in timezone: %s", dateTimeConfiguration.timeZone().toTimeZone().getDisplayName()));
            return new ExternalDateTimeSource(dateTimeConfiguration);
        }
        logger.info(String.format("Using default date time source in timezone: %s", dateTimeConfiguration.timeZone().toTimeZone().getDisplayName()));
        return new DefaultDateTimeSource();
    }
}
