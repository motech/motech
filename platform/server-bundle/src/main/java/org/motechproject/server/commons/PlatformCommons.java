package org.motechproject.server.commons;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.commons.date.util.DateUtil;
import org.springframework.stereotype.Component;

import java.util.ResourceBundle;

/**
 * Class used with {@link org.motechproject.server.CommonsDataProvider} to provide common
 * values as a data source in Tasks module.
 */
@Component
public class PlatformCommons {

    public PlatformCommons() {

    }

    /**
     * Gets current MOTECH version
     * @return version of MOTECH
     */
    public String getMotechVersion() {
        return ResourceBundle.getBundle("webapp/messages.messages").getString("server.version");
    }

    /**
     * Gets current date as timestamp
     * @return current date
     */
    public DateTime getNow() {
        return DateUtil.now();
    }

    /**
     * Gets date of today
     * @return date of today
     */
    public LocalDate getToday() {
        return DateUtil.today();
    }
}
