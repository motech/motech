package org.motechproject.event;

import java.util.concurrent.ConcurrentHashMap;


/**
 * A central registry for all event types
 */
public class EventTypeRegistry {

    private static Object mutex = new Object();
    private static EventTypeRegistry instance = null;

    // Central registry for scheduled event listeners
    private static ConcurrentHashMap<String, EventType> eventTypes = null;

    /**
     * Singleton constructor
     */
    private EventTypeRegistry() {
        eventTypes = new ConcurrentHashMap<String, EventType>();
    }


    /**
     * Retrieve an instance of the Scheduled Event Listener Registry Singleton
     * @return singleton instance
     */
    public static EventTypeRegistry getInstance() {
        // Do we need to initialized the singleton
        if (instance == null) {
            synchronized (mutex) {
                // Do we still need to initialize the singleton or did someone do it for us while we
                // were waiting for the lock?
                if (instance == null) {
                    instance = new EventTypeRegistry();
                }
            }
        }

        return instance;
    }

    /**
     * Register an event type so that it can be referenced by others
     *
     * @param type The event type being registered
     */
    public void setEventType(EventType type) {
        eventTypes.put(type.getKey(), type);
    }

    /**
     * Register an event type so that it can be referenced by others
     *
     * @param types The event types being registered
     */
    public void setEventType(EventType[] types) {
        for(int i = 0; i < types.length; i++) {
            this.setEventType(types[i]);
        }
    }

    /**
     * Retrieve a concrete instance of the event type you are looking for
     * @param key The event type that you are looking for
     * @return A concrete instance of the event type that 
     */
    public EventType getEventType(String key) {
        EventType type = null;
        if (eventTypes.contains(key)) {
            type = eventTypes.get(key);
        }

        return type;

    }
}
