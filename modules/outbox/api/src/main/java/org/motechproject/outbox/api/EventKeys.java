package org.motechproject.outbox.api;

import org.motechproject.event.MotechEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EventKeys {

    private EventKeys() {

    }

    private static Logger log = LoggerFactory.getLogger(EventKeys.class);

    public static final String CALL_HOUR_KEY = "CallHour";
    public static final String CALL_MINUTE_KEY = "CallMinute";
    public static final String PHONE_NUMBER_KEY = "PhoneNumber";
    public static final String EXTERNAL_ID_KEY = "ExternalID";
    public static final String SCHEDULE_JOB_ID_KEY = "JobID";
    public static final String LANGUAGE_KEY = "Language";

    public static final String OUTBOX_MAX_PENDING_MESSAGES_EVENT_SUBJECT = "org.motechproject.server.outbox.max-pending-messages";

    public static final String SCHEDULE_EXECUTION_SUBJECT = "org.motechproject.server.outbox.schedule-execution";
    public static final String UNSCHEDULE_EXECUTION_SUBJECT = "org.motechproject.server.outbox.unschedule-execution";
    public static final String EXECUTE_OUTBOX_SUBJECT = "org.motechproject.server.outbox.execute";

    public static final String INCOMPLETE_OUTBOX_CALL_SUBJECT = "org.motechproject.server.outbox.call.incomplete";
    public static final String COMPLETED_OUTBOX_CALL_SUBJECT = "org.motechproject.server.outbox.call.completed";

    public static String getScheduleJobIdKey(MotechEvent event) {
        return getStringValue(event, EventKeys.SCHEDULE_JOB_ID_KEY);
    }

    public static Integer getCallHourKey(MotechEvent event) {
        return getIntegerValue(event, EventKeys.CALL_HOUR_KEY);
    }

    public static Integer getCallMinuteKey(MotechEvent event) {
        return getIntegerValue(event, EventKeys.CALL_MINUTE_KEY);
    }

    public static String getPhoneNumberKey(MotechEvent event) {
        return getStringValue(event, EventKeys.PHONE_NUMBER_KEY);
    }

    public static String getExternalID(MotechEvent event) {
        return getStringValue(event, EventKeys.EXTERNAL_ID_KEY);
    }

    public static String getLanguageKey(MotechEvent event) {
        return getStringValue(event, EventKeys.LANGUAGE_KEY);
    }

    public static String getStringValue(MotechEvent event, String key) {
        String ret = null;
        try {
            ret = (String) event.getParameters().get(key);
        } catch (ClassCastException e) {
            log.warn("Event: " + event + " Key: " + key + " is not a String");
        }

        return ret;
    }

    public static Integer getIntegerValue(MotechEvent event, String key) {
        Integer ret = null;
        try {
            ret = (Integer) event.getParameters().get(key);
        } catch (ClassCastException e) {
            log.warn("Event: " + event + " Key: " + key + " is not an Integer");
        }

        return ret;
    }
}
