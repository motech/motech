package org.motechproject.event;

/**
 * This interface defines event types and their unique lookup keys. The class classes are used to by both the
 * event scheduler and event routers to ensure that events are handled appropriate by various listeners.
 */
public interface EventType {

    /**
     * Retrieve the name of the event
     * @return name of the event type
     */
    public String getName();

    /**
     * Retrieve the unique event lookup key
     * @return lookup key
     */
    public String getKey();

}
