package org.motechproject.event.listener;

import org.motechproject.server.osgi.event.OsgiEventProxy;
import org.motechproject.server.osgi.util.PlatformConstants;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.jms.JmsMessageDrivenEndpoint;

/**
 * Handles incoming events and starts ActiveMQ outbound channels.
 */
public class EventConsumerStarter implements EventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventConsumerStarter.class);

    private JmsMessageDrivenEndpoint queue;
    private JmsMessageDrivenEndpoint topic;

    /**
     * Receives an OSGi event with the proxy topic.
     * If event has {@link OsgiEventProxy#SUBJECT_PARAM subject} param
     * with specified {@link PlatformConstants#MODULES_STARTUP_TOPIC value},
     * then ActiveMQ outbound channels will be started.
     *
     * @param osgiEvent the event sent from OSGi
     */
    @Override
    public void handleEvent(Event osgiEvent) {
        String subject = (String) osgiEvent.getProperty(OsgiEventProxy.SUBJECT_PARAM);
        if (subject.equals(PlatformConstants.MODULES_STARTUP_TOPIC)) {
            startActiveMQConsumers();
        }
        LOGGER.info("ActiveMQ outbound channels started.");
    }

    private void startActiveMQConsumers() {
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

            if (!queue.isRunning()) {
                queue.start();
            }
            if (!topic.isRunning()) {
                topic.start();
            }
        }finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
    }

    @Autowired
    @Qualifier("eventQueueJMSIn")
    public void setQueue(JmsMessageDrivenEndpoint queue) {
        this.queue = queue;
    }

    @Autowired
    @Qualifier("eventTopicJMSIn")
    public void setTopic(JmsMessageDrivenEndpoint topic) {
        this.topic = topic;
    }
}
