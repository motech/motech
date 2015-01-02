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
            String errorMessage = "Invalid attempt to register a null EventListener";
            LOGGER.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        if (subjects == null) {
            String errorMessage = "Invalid attempt to register for null subjects";
            LOGGER.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        // Add the listener to the list of those interested in each event type
        for (String subject : subjects) {
            registerListener(listener, subject);
        }
    }

    public void registerListener(EventListener listener, String subject) {
        if (listener == null) {
            String errorMessage = "Invalid attempt to register a null EventListener";
            LOGGER.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        if (subject == null) {
            String errorMessage = "Invalid attempt to register for null subject";
            LOGGER.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
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
