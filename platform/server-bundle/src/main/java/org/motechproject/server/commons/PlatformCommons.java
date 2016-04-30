package org.motechproject.server.commons;


import org.joda.time.DateTime;
import org.joda.time.LocalDate;

/**
 * This class, exposed as OSGi service, provides basic information from the server, such as
 * MOTECH version or current time. It is also used with {@link org.motechproject.server.CommonsDataProvider}
 * to provide common values as a data source in Tasks module.
 */
public interface PlatformCommons {

    /**
     * Gets current MOTECH version
     * @return version of MOTECH
     */
    String getMotechVersion();

    /**
     * Gets current date as timestamp
     * @return current date
     */
    DateTime getNow();

    /**
     * Gets date of today
     * @return date of today
     */
    LocalDate getToday();
}
