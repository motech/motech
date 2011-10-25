package org.motechproject.server.pillreminder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventKeys {
	private final static Logger log = LoggerFactory.getLogger(EventKeys.class);

	public final static String PILLREMINDER_ID_KEY = "PillReminderID";
	public final static String DOSAGE_ID_KEY = "DosageID";
	public final static String EXTERNAL_ID_KEY = "ExternalID";
    public final static String SCHEDULE_JOB_ID_KEY = "JobID";
    public final static String BASE_SUBJECT = "org.motechproject.server.pillreminder.";

    public final static String PILLREMINDER_REMINDER_EVENT_SUBJECT_SCHEDULER = BASE_SUBJECT + "scheduler-reminder";
    public final static String PILLREMINDER_REMINDER_EVENT_SUBJECT = BASE_SUBJECT + "pill-reminder";
    public static final String PILLREMINDER_TIMES_SENT = "times-reminders-sent";
    public static final String PILLREMINDER_TOTAL_TIMES_TO_SEND = "times-reminders-to-be-sent";
    public static final String PILLREMINDER_RETRY_INTERVAL = "retry-interval";

}
