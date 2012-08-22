package org.motechproject.event;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Motech Scheduled Event data carrier class,
 * Instance of this class with event specific data will be send by Motech Scheduler when a scheduled event is fired
 * <p></p>
 * This class is immutable
 */
public final class MotechEvent implements Serializable {
    public static final String EVENT_TYPE_KEY_NAME = "eventType";

    private UUID id;
    private String subject;
    private Map<String, Object> parameters;
    private Date endTime;
    private boolean isLastEvent;

    /**
     * Constructor with subject only (parameters can be added interactively)
     *
     * @param subject - event destination
     * @throws IllegalArgumentException
     */
    public MotechEvent(String subject) {
        if (subject == null) {
            throw new IllegalArgumentException("subject can not be null");
        }

        if (subject.contains("*")) {
            throw new IllegalArgumentException("subject can not contain wildcard: " + subject);
        }

        if (subject.contains("..")) {
            throw new IllegalArgumentException("subject can not contain empty path segment: " + subject);
        }

        this.subject = subject;
    }

    /**
     * Constructor
     *
     * @param subject    - event type: Pill Reminder, Appointment Reminder ...
     * @param parameters - a Map<String, Object> of additional parameters
     * @throws IllegalArgumentException
     */
    public MotechEvent(String subject, Map<String, Object> parameters) {
        this(subject);
        this.parameters = parameters;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    /**
     * Sets empty HashMap if parameters=null
     *
     * @return
     */
    public Map<String, Object> getParameters() {
        if (parameters == null) {
            parameters = new HashMap<String, Object>();
        }
        return parameters;
    }

    public Date getEndTime() {
        return endTime;
    }

    public MotechEvent setEndTime(Date endDate) {
        this.endTime = endDate;
        return this;
    }

    public boolean isLastEvent() {
        return isLastEvent;
    }

    public MotechEvent setLastEvent(boolean lastEvent) {
        isLastEvent = lastEvent;
        return this;
    }

    public MotechEvent copy(String subject, Map<String, Object> parameters) {
        MotechEvent event = new MotechEvent(subject, parameters);
        event.setEndTime(clone(this.endTime));
        event.setLastEvent(isLastEvent());
        return event;
    }

    private Date clone(Date date) {
        return date != null ? (Date) date.clone() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MotechEvent that = (MotechEvent) o;

        if (!subject.equals(that.subject)) {
            return false;
        }
        if (parameters != null ? !parameters.equals(that.parameters) : that.parameters != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = 31 * subject.hashCode();
        result = 31 * result + (parameters != null ? parameters.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MotechEvent{" +
                "subject='" + subject + '\'' +
                ", parameters=" + parameters +
                '}';
    }
}
