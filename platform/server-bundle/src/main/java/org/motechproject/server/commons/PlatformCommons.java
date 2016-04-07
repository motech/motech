package org.motechproject.server.commons;


import org.joda.time.DateTime;
import org.joda.time.LocalDate;

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
