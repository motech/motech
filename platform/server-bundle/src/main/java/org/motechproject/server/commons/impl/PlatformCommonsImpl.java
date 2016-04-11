package org.motechproject.server.commons.impl;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.server.commons.PlatformCommons;
import org.springframework.stereotype.Service;

import java.util.ResourceBundle;

/**
 * Default implementation of {@link PlatformCommons}.
 */
@Service("platformCommons")
public class PlatformCommonsImpl implements PlatformCommons {

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
