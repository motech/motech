package org.motechproject.server.pillreminder;

import org.motechproject.model.MotechEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class EventKeys
{
	private final static Logger log = LoggerFactory.getLogger(EventKeys.class);

    public final static String CALL_HOUR_KEY = "CallHour";
    public final static String CALL_MINUTE_KEY = "CallMinute";
    public final static String PHONE_NUMBER_KEY = "PhoneNumber";
    public final static String PARTY_ID_KEY = "PartyID";
    public final static String REMINDER_ID_KEY = "ReminderID";    
    public final static String SCHEDULE_JOB_ID_KEY = "JobID";
    public final static String LANGUAGE_KEY = "Language";
    public final static String BASE_SUBJECT = "org.motechproject.server.pillreminder.";

    public final static String PILLREMINDER_CREATED_SUBJECT = BASE_SUBJECT + "reminder.created";
    public final static String PILLREMINDER_UPDATED_SUBJECT = BASE_SUBJECT + "reminder.updated";
    public final static String PILLREMINDER_DELETED_SUBJECT = BASE_SUBJECT + "reminder.deleted";

    public static String getScheduleJobIdKey(MotechEvent event)
    {
        return getStringValue(event, EventKeys.SCHEDULE_JOB_ID_KEY);
    }

    public static Integer getCallHourKey(MotechEvent event)
    {
        return getIntegerValue(event, EventKeys.CALL_HOUR_KEY);
    }

    public static Integer getCallMinuteKey(MotechEvent event)
    {
        return getIntegerValue(event, EventKeys.CALL_MINUTE_KEY);
    }

    public static String getPhoneNumberKey(MotechEvent event)
    {
        return getStringValue(event, EventKeys.PHONE_NUMBER_KEY);
    }

    public static String getPartyID(MotechEvent event)
    {
        return getStringValue(event, EventKeys.PARTY_ID_KEY);
    }

    public static String getLanguageKey(MotechEvent event)
    {
    	return getStringValue(event, EventKeys.LANGUAGE_KEY);
    }

    public static String getStringValue(MotechEvent event, String key)
    {
        String ret = null;
        try {
            ret = (String) event.getParameters().get(key);
        } catch (ClassCastException e) {
            log.warn("Event: " + event + " Key: " + key + " is not a String");
        }

        return ret;
    }

    public static Integer getIntegerValue(MotechEvent event, String key)
    {
        Integer ret = null;
        try {
            ret = (Integer)event.getParameters().get(key);
        } catch (ClassCastException e) {
            log.warn("Event: " + event + " Key: " + key + " is not an Integer");
        }

        return ret;
    }
}
