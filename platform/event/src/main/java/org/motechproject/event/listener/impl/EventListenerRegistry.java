package org.motechproject.event.listener.impl;

import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Implementation of the {@link EventListenerRegistryService} interface.
 * Acts as a registry for all scheduled event listeners.
 */
@Service
public class EventListenerRegistry implements EventListenerRegistryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventListenerRegistry.class);

    private EventListenerTree listenerTree = new EventListenerTree();

    public void registerListener(EventListener listener, List<String> subjects) {

        if (listener == null) {
            throw new IllegalArgumentException("Invalid attempt to register a null EventListener");
        }

        if (subjects == null) {
            throw new IllegalArgumentException("Invalid attempt to register for null subjects");
        }

        // Add the listener to the list of those interested in each event type
        for (String subject : subjects) {
            registerListener(listener, subject);
        }
    }

    public void registerListener(EventListener listener, String subject) {
        if (listener == null) {
            throw new IllegalArgumentException("Invalid attempt to register a null EventListener");
        }

        if (subject == null) {
            throw new IllegalArgumentException("Invalid attempt to register for null subject");
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("registering handler for " + subject + " to " + this.toString());
        }

        listenerTree.addListener(listener, subject);
    }

    public Set<EventListener> getListeners(String subject) {
        return listenerTree.getListeners(subject);
    }

    public boolean hasListener(String subject) {
        return listenerTree.hasListener(subject);
    }

    public int getListenerCount(String subject) {
        return listenerTree.getListenerCount(subject);
    }

    public void clearListenersForBean(String beanName) {
        listenerTree.removeAllListeners(beanName);
    }
}
