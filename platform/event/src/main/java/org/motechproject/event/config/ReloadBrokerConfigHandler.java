package org.motechproject.event.config;

import org.motechproject.event.queue.MotechCachingConnectionFactory;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;

/**
 * Handles changes in the ActiveMQ config.
 */
public class ReloadBrokerConfigHandler implements EventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReloadBrokerConfigHandler.class);

    private MotechCachingConnectionFactory connectionFactory;

    /**
     * @param connectionFactory the factory which inits connection to ActiveMQ.
     */
    @Autowired
    public ReloadBrokerConfigHandler(MotechCachingConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    /**
     * Handles changes in the ActiveMQ broker.url variable.
     *
     * @param event the event that occurred.
     */
    @Override
    public void handleEvent(Event event) {
        try {
            Object brokerURL = event.getProperty("jms.broker.url");

            if (brokerURL != null) {
                connectionFactory.setBrokerUrl(brokerURL.toString());
                connectionFactory.initConnection();
            }
        } catch (JMSException e) {
            LOGGER.error("Cannot init ActiveMQ connection.", e);
        }
    }
}
