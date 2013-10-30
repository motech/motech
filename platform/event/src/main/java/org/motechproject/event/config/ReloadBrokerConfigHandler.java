package org.motechproject.event.config;

import org.motechproject.event.MotechCachingConnectionFactory;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;

/**
 * The <code>ReloadBrokerConfigHandler</code> handles changes in the activemq broker.url variable.
 */
public class ReloadBrokerConfigHandler implements EventHandler {
    private final Logger logger = LoggerFactory.getLogger(ReloadBrokerConfigHandler.class);

    private MotechCachingConnectionFactory connectionFactory;

    @Autowired
    public ReloadBrokerConfigHandler(MotechCachingConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public void handleEvent(Event event) {
        try {
            Object brokerURL = event.getProperty("jms.broker.url");

            if (brokerURL != null) {
                connectionFactory.setBrokerUrl(brokerURL.toString());
                connectionFactory.initConnection();
            }
        } catch (JMSException e) {
            logger.error("Cannot init ActiveMQ connection.", e);
        }
    }
}
