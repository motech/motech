package org.motechproject.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Motech Scheduled Event data carrier class,
 * Instance of this class with event specific data will be send by Motech Scheduler when a scheduled event is fired
 *
 * User:Igor (iopushnyev@2paths.com)
 * Date: 16/02/11
 * Time: 1:43 PM
 *
 */
public class MotechScheduledEvent {

    private String jobId;
    private String eventType;
    private HashMap<String, Object> parameters;

    /**
     * Constructor
     * @param jobId - ID of the scheduled job that generated this event.
     * @param eventType - event type: Pill Reminder, Appointment Reminder ...
     * @param parameters - a Map<String, Object> of additional parameters stored as job details
     */
    public MotechScheduledEvent(String jobId, String eventType, HashMap<String, Object> parameters) {
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
        return Collections.unmodifiableMap(parameters);
    }
}
