package org.motechproject.event;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Motech Scheduled Event data carrier class.
 *
 * It contains a subject, to which listeners can subscribe and a payload in the form of a map of parameters.
 * Instance of this class with event specific data will be sent by Motech Scheduler when a scheduled event is fired.
 * <p></p>
 * This class is immutable
 */
public class MotechEvent implements Serializable {
    public static final String EVENT_TYPE_KEY_NAME = "eventType";
    public static final String PARAM_REDELIVERY_COUNT = "motechEventRedeliveryCount";
    public static final String PARAM_INVALID_MOTECH_EVENT = "invalidMotechEvent";
    public static final String PARAM_DISCARDED_MOTECH_EVENT = "discardedMotechEvent";

    private static final long serialVersionUID = -6710829948064847678L;

    private UUID id;
    private String subject;
    private Map<String, Object> parameters;

    public MotechEvent() {
    }

    /**
     * Constructs a MotechEvent with the given subject.
     *
     * @param subject the subject of the event
     * @throws IllegalArgumentException if the subject is null or contains <code>'*', '..'</code>
     */
    public MotechEvent(String subject) {
        this(subject, null);
    }

    /**
     * Constructs a MotechEvent with the given subject and parameters.
     *
     * @param subject the subject of the event
     * @param parameters the map of additional parameters
     * @throws IllegalArgumentException if the subject is null or contains <code>'*', '..'</code>
     */
    public MotechEvent(String subject, Map<String, Object> parameters) {
        validateSubject(subject);
        this.subject = subject;
        this.parameters = parameters;
    }

    /**
     * Returns the universally unique identifier
     *
     * @return the id
     */
    public UUID getId() {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the universally unique identifier
     */
    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * Returns the name of the subject.
     *
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Returns the parameters, if null returns
     * empty <code>HashMap</code>.
     *
     * @return the map of the parameters
     */
    public Map<String, Object> getParameters() {
        if (parameters == null) {
            parameters = new HashMap<>();
        }
        return parameters;
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
        final StringBuffer sb = new StringBuffer();
        sb.append("MotechEvent");
        sb.append("{id=").append(id);
        sb.append(", subject='").append(subject).append('\'');
        sb.append(", parameters=").append(parameters);
        sb.append('}');
        return sb.toString();
    }

    /**
     * Returns the <code>motechEventRedeliveryCount</code> from the parameters.
     * This is incremented by the event system if the delivery fails, so it is equal to the number of failed deliveries.
     * Any exception from the handler is counted as failure in this context. It cannot be larger than {@link org.motechproject.event.queue.MotechEventConfig#messageMaxRedeliveryCount}
     *
     * @return the number of message redeliveries
     */
    public int getMessageRedeliveryCount() {
        Object redeliverCount = this.getParameters().get(MotechEvent.PARAM_REDELIVERY_COUNT);
        if (redeliverCount instanceof Integer) {
            return ((Integer) redeliverCount).intValue();
        }
        return 0;
    }

    /**
     * Increments the <code>motechEventRedeliveryCount</code> from the parameters.
     * It is invoked by the event system if the delivery fails. If it is null, sets the value to 0.
     */
    public void incrementMessageRedeliveryCount() {
        Object redeliverCount = this.getParameters().get(MotechEvent.PARAM_REDELIVERY_COUNT);
        if (redeliverCount == null) {
            redeliverCount = 0;
        }
        this.getParameters().put(MotechEvent.PARAM_REDELIVERY_COUNT, ((Integer) redeliverCount).intValue() + 1);
    }

    private void validateSubject(String subject) {
        if (subject == null) {
            throw new IllegalArgumentException("subject can not be null");
        }

        if (subject.contains("*")) {
            throw new IllegalArgumentException("subject can not contain wildcard: " + subject);
        }

        if (subject.contains("..")) {
            throw new IllegalArgumentException("subject can not contain empty path segment: " + subject);
        }
    }
}
