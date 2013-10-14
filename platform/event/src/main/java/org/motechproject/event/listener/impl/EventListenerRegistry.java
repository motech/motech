package org.motechproject.event.listener.impl;

import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.event.listener.EventListenerTree;
import org.motechproject.event.osgi.MetricsServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Implementation of {@Link EventListenerRegistryService} interface.
 * Acts as a registry for all scheduled event listeners
 */

@Service
public class EventListenerRegistry implements EventListenerRegistryService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private EventListenerTree listenerTree = new EventListenerTree();

    @Autowired
    private MetricsServiceManager metricsManager;

    public EventListenerRegistry() {
    }

    public EventListenerRegistry(MetricsServiceManager metricsManager) {
        this.metricsManager = metricsManager;
    }

    public void registerListener(EventListener listener, List<String> subjects) {

        if (listener == null) {
            String errorMessage = "Invalid attempt to register a null EventListener";
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        if (subjects == null) {
            String errorMessage = "Invalid attempt to register for null subjects";
            log.error(errorMessage);
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
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        if (subject == null) {
            String errorMessage = "Invalid attempt to register for null subject";
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        if (log.isDebugEnabled()) {
            log.debug("registering handler for " + subject + " to " + this.toString());
        }

        if (metricsManager.isServiceAvailable()) {
            final long startTime = metricsManager.getService().startTimer();
            listenerTree.addListener(listener, subject);
            metricsManager.getService().stopTimer("motech.listener-registry.addListener", startTime);
        } else {
            listenerTree.addListener(listener, subject);
        }
    }

    public Set<EventListener> getListeners(String subject) {
        Set<EventListener> ret;

        if (metricsManager.isServiceAvailable()) {
            final long startTime = metricsManager.getService().startTimer();
            ret = listenerTree.getListeners(subject);
            metricsManager.getService().stopTimer("motech.listener-registry.getListeners", startTime);
        } else {
           ret = listenerTree.getListeners(subject);
        }

        return ret;
    }

    public boolean hasListener(String subject) {
        boolean ret;

        if (metricsManager.isServiceAvailable()) {
            final long startTime = metricsManager.getService().startTimer();
            ret = listenerTree.hasListener(subject);
            metricsManager.getService().stopTimer("motech.listener-registry.hasListener", startTime);
        } else {
            ret = listenerTree.hasListener(subject);
        }

        return ret;
    }

    public int getListenerCount(String subject) {
        int ret;

        if (metricsManager.isServiceAvailable()) {
            final long startTime = metricsManager.getService().startTimer();
            ret = listenerTree.getListenerCount(subject);
            metricsManager.getService().stopTimer("motech.listener-registry.hasListener", startTime);
        } else {
            ret = listenerTree.getListenerCount(subject);
        }

        return ret;
    }

    public void clearListenersForBean(String beanName) {
        listenerTree.removeAllListeners(beanName);
    }
}
