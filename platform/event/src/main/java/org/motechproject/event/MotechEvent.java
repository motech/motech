package org.motechproject.event;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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
    private static final long serialVersionUID = -6710829948064847678L;

    private UUID id;
    private boolean invalid;
    private boolean discarded;
    private boolean broadcast;
    private int redeliveryCount;
    private String subject;
    private String messageDestination;
    private Map<String, Object> parameters;

    public MotechEvent() {
    }

    /**
     * Constructs a MotechEvent with the given subject.
     *
     * @param subject the subject of the event
     *
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
     * Returns whether event is invalid
     *
     * @return event invalid
     */
    public boolean isInvalid() {
        return invalid;
    }

    /**
     * Sets event as a invalid
     *
     * @param value
     */
    public void setInvalid(boolean value) {
        invalid = value;
    }

    /**
     * Returns whether event is discarded
     *
     * @return event discarded
     */
    public boolean isDiscarded() {
        return discarded;
    }

    /**
     * Sets event as discarded
     *
     * @param value
     */
    public void setDiscarded(boolean value) {
        discarded = value;
    }

    /**
     * Returns whether event is a broadcast event
     *
     * @return broadcast
     */
    public boolean isBroadcast() {
        return broadcast;
    }

    /**
     * Sets event as a broadcast event
     *
     * @param value
     */
    public void setBroadcast(boolean value) {
        broadcast = value;
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
     * Returns the id of the Motech listener that this event is meant for
     *
     * @return message destination
     */
    public String getMessageDestination() {
        return messageDestination;
    }

    /**
     * Sets the id of the Motech listener that this event is meant for
     *
     * @param value
     */
    public void setMessageDestination(String value) {
        messageDestination = value;
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

        return Objects.equals(invalid, that.invalid) &&
                Objects.equals(discarded, that.discarded) &&
                Objects.equals(broadcast, that.broadcast) &&
                Objects.equals(redeliveryCount, that.redeliveryCount) &&
                Objects.equals(subject, that.subject) &&
                Objects.equals(messageDestination, that.messageDestination) &&
                Objects.equals(parameters, that.parameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(invalid,
                discarded,
                broadcast,
                redeliveryCount,
                subject,
                messageDestination,
                parameters);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("MotechEvent");
        sb.append("{id=").append(id);
        sb.append(", subject='").append(subject).append('\'');
        sb.append(", redelivery-count=").append(redeliveryCount);
        sb.append(", invalid=").append(invalid);
        sb.append(", discarded=").append(discarded);
        sb.append(", broadcast=").append(broadcast);
        sb.append(", destination='").append(messageDestination).append('\'');
        sb.append(", parameters=").append(parameters);
        sb.append('}');
        return sb.toString();
    }

    /**
     * Returns the <code>redeliveryCount</code>.
     * This is incremented by the event system if the delivery fails, so it is equal to the number of failed deliveries.
     * Any exception from the handler is counted as failure in this context. It cannot be larger than {@link org.motechproject.event.messaging.MotechEventConfig#messageMaxRedeliveryCount}
     *
     * @return the number of message redeliveries
     */
    public int getMessageRedeliveryCount() {
        return redeliveryCount;
    }

    public void setMessageRedeliveryCount(int value) {
        redeliveryCount = value;
    }

    /**
     * Increments the <code>redeliveryCount</code>.
     * It is invoked by the event system if the delivery fails. If it is null, sets the value to 0.
     */
    public void incrementMessageRedeliveryCount() {
        redeliveryCount++;
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
