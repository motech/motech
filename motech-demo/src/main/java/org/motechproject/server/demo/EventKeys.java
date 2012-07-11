package org.motechproject.server.demo;

import org.motechproject.scheduler.domain.MotechEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public final class EventKeys {
    private EventKeys() { } 

    private static final Logger LOG = LoggerFactory.getLogger("org.motechproject.server.demo");

    public static final String PHONE_KEY = "PhoneNumber";
    public static final String BASE_SUBJECT = "org.motechproject.server.demo.";
    public static final String CALL_EVENT_SUBJECT = BASE_SUBJECT + "call";

    public static String getPhoneNumber(MotechEvent event) {
        return getStringValue(event, EventKeys.PHONE_KEY);
    }

    public static String getStringValue(MotechEvent event, String key) {
        String ret = null;
        try {
            ret = (String) event.getParameters().get(key);
        } catch (ClassCastException e) {
            LOG.warn("Event: " + event + " Key: " + key + " is not a String");
        }

        return ret;
    }
}
