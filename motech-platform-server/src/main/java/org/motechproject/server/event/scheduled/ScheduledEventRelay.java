package org.motechproject.server.event.scheduled;

import org.motechproject.event.EventTypeRegistry;
import org.motechproject.model.MotechScheduledEvent;
import java.util.List;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

/**
 * This class handled incoming scheduled events and relays those events to the appropriate event listeners
 */
public class ScheduledEventRelay {

    private ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 50);

    public ScheduledEventRelay() {

    }

    /**
     * Relay an event to all the listeners of that event.
     * @param event event being relayed
     */
    public void relayEvent(MotechScheduledEvent event) {

        // Retrieve a list of listeners for the given event type
        List<ScheduledEventListener> listeners = ScheduledEventListenerRegistry.getInstance().getListeners( EventTypeRegistry.getInstance().getEventType(event.getEventType()) );

        final MotechScheduledEvent providedEvent = event; // Copy the event to be provided to the listeners

        // Iterate through the list of listeners of this event and execute handle method
        // with the message.
        for( Iterator<ScheduledEventListener> iter = listeners.iterator(); iter.hasNext(); ) {
            final ScheduledEventListener listener = iter.next(); // create an instance of the event listener
            executor.execute(new Runnable(){
                    public void run() {
                        listener.handle(providedEvent);
                    }
                });
        }
    }

}
