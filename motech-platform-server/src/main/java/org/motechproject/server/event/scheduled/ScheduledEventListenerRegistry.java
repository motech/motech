package org.motechproject.server.event.scheduled;

import org.motechproject.event.EventType;
import org.motechproject.server.event.scheduled.ScheduledEventListener;

import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.ArrayList;


/**
 * This class acts as a registry for all scheduled event listeners. One can register themselves to listen for
 * a specific set of event types.
 */
public class ScheduledEventListenerRegistry {

    private static Object mutex = new Object();
    private static ScheduledEventListenerRegistry instance = null;

    // Central registry for scheduled event listeners
    private static ConcurrentHashMap<String, List<ScheduledEventListener>> eventListeners = null;

    /**
     * Singleton constructor
     */
    private ScheduledEventListenerRegistry() {
        eventListeners = new ConcurrentHashMap<String, List<ScheduledEventListener>>();
    }


    /**
     * Retrieve an instance of the Scheduled Event Listener Registry Singleton
     * @return singleton instance
     */
    public static ScheduledEventListenerRegistry getInstance() {
        // Do we need to initialized the singleton
        if (instance == null) {
            synchronized (mutex) {
                // Do we still need to initialize the singleton or did someone do it for us while we
                // were waiting for the lock?
                if (instance == null) {
                    instance = new ScheduledEventListenerRegistry();
                }
            }
        }

        return instance;
    }

    /**
     * Register an event listener to be notified when events of a given type are received via the Server JMS Event Queue
     *
     * @param listener the listener instance
     * @param eventTypes the event types that a listener is interested in
     */
    public void registerListener(ScheduledEventListener listener, List<EventType> eventTypes) {

        List<ScheduledEventListener> listeners = null;
        // Add the listener to the list of those interested in each event type
        for (int i = 0; i < eventTypes.size(); i++) {
            // Check if there are any other listeners for this event
            if (eventListeners.contains(eventTypes.get(i).getKey())) {
                listeners = eventListeners.get(eventTypes.get(i).getKey());
            } else {
                listeners = new ArrayList<ScheduledEventListener>();
            }

            listeners.add(listener); // Add the listener to the list
            eventListeners.put(eventTypes.get(i).getKey(), listeners); // Add it back to the collection
        }
    }

    /**
     * Retrieve a list of event listeners for a given event type. If there are no listeners, null is returned.
     * @param type The event type that you are seeking listeners for
     * @return A list of scheduled event listeners that are interested in that event
     */
    public List<ScheduledEventListener> getListeners(EventType type) {
        List<ScheduledEventListener> listeners =  null;
        if (eventListeners.contains(type.getKey())) {
            listeners = eventListeners.get(type.getKey());
        }

        return listeners;

    }

}
