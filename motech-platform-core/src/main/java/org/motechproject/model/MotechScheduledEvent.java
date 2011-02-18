package org.motechproject.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Motech Scheduled Event data carrier class,
 * Instance of this class with event specific data will be send by Motech Scheduler when a scheduled event is fired
 *
 * This class is immutable
 *
 * User:Igor (iopushnyev@2paths.com)
 * Date: 16/02/11
 * Time: 1:43 PM
 *
 */
public final class MotechScheduledEvent {

    public static final String EVENT_TYPE_KEY_NAME = "eventType";

    private String jobId;
    private String eventType;
    private HashMap<String, Object> parameters;

    /**
     * Constructor
     * @param jobId - ID of the scheduled job that generated this event.
     * @param eventType - event type: Pill Reminder, Appointment Reminder ...
     * @param parameters - a Map<String, Object> of additional parameters
     *
     * @throws IllegalArgumentException if given jobId or entityType is null
     */
    public MotechScheduledEvent(String jobId, String eventType, HashMap<String, Object> parameters) {

        if (jobId == null) {
            throw new IllegalArgumentException("jobId can not be null");
        }

        if (eventType == null) {
            throw new IllegalArgumentException("eventType can not be null");
        }

        this.jobId = jobId;
        this.eventType = eventType;
        this.parameters = parameters;
    }

    public String getJobId() {
        return jobId;
    }

    public String getEventType() {
        return eventType;
    }

    public Map<String, Object> getParameters() {
        if (parameters == null ) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(parameters);
    }
}
