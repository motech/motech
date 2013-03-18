package org.motechproject.scheduletracking.api.events.constants;

/**
 * Event subjects published by the schedule tracking module
 */
public final class EventSubjects {
    private static final String BASE_SUBJECT = "org.motechproject.scheduletracking.api.";

    public static final String MILESTONE_ALERT = BASE_SUBJECT + "milestone.alert";
    public static final String DEFAULTMENT_CAPTURE = BASE_SUBJECT + "defaultment.capture";
    public static final String USER_ENROLLED = BASE_SUBJECT + "user.enrolled";
    public static final String USER_UNENROLLED = BASE_SUBJECT + "user.unenrolled";

    private EventSubjects() {
    }
}
