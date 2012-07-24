package org.motechproject.scheduletracking.api.events.constants;

/**
 * Keys for accessing the Event Payload
 */
public final class EventDataKeys {

    // MilestoneEvent
    public static final String WINDOW_NAME = "window_name";
    public static final String MILESTONE_NAME = "milestone_name";
    public static final String SCHEDULE_NAME = "schedule_name";
    public static final String EXTERNAL_ID = "external_id";
    public static final String REFERENCE_DATE = "reference_date";

    // DefaultmentCaptureEvent
    public static final String ENROLLMENT_ID = "enrollment_id";

    private EventDataKeys() {
    }
}
