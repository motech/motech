package org.motechproject.server.event;

import java.util.List;
import java.util.Set;
import org.motechproject.metrics.MetricsAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventListenerRegistry implements EventListenerRegistryService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private EventListenerTree listenerTree = new EventListenerTree();

    @Autowired
    private MetricsAgent metricsAgent;

    public EventListenerRegistry() {
    }

    public EventListenerRegistry(MetricsAgent metricsAgent) {
        this.metricsAgent = metricsAgent;
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

        final long startTime = metricsAgent.startTimer();
        listenerTree.addListener(listener, subject);
        metricsAgent.stopTimer("motech.listener-registry.addListener", startTime);
    }

    public Set<EventListener> getListeners(String subject) {
        final long startTime = metricsAgent.startTimer();
        Set<EventListener> ret = listenerTree.getListeners(subject);
        metricsAgent.stopTimer("motech.listener-registry.getListeners", startTime);

        return ret;
    }

    public boolean hasListener(String subject) {

        final long startTime = metricsAgent.startTimer();
        boolean ret = listenerTree.hasListener(subject);
        metricsAgent.stopTimer("motech.listener-registry.hasListener", startTime);

        return ret;
    }

    public int getListenerCount(String subject) {

        final long startTime = metricsAgent.startTimer();
        int ret = listenerTree.getListenerCount(subject);
        metricsAgent.stopTimer("motech.listener-registry.hasListener", startTime);

        return ret;
    }

    public void clearListenersForBean(String beanName) {
        listenerTree.removeAllListeners(beanName);
    }
}
