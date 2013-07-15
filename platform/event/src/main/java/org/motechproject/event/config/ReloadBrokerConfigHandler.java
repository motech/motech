package org.motechproject.event.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.motechproject.event.MotechCachingConnectionFactory;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.Connection;
import javax.jms.JMSException;

public class ReloadBrokerConfigHandler implements EventHandler{
    private final Logger logger = LoggerFactory.getLogger(ReloadBrokerConfigHandler.class);

    @Autowired
    private MotechCachingConnectionFactory connectionFactory;

    @Override
    public void handleEvent(Event event) {
        Connection connection = null;
        try {
            ActiveMQConnectionFactory activeMQConnectionFactory = (ActiveMQConnectionFactory) connectionFactory.getTargetConnectionFactory();
            Object brokerURL = event.getProperty("broker.url");
            if (brokerURL != null) {
                activeMQConnectionFactory.setBrokerURL(brokerURL.toString());
                connection = activeMQConnectionFactory.createConnection();
                connection.start();
            }
        } catch (JMSException e) {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e1) {
                    logger.error("Cannot close ActiveMQ connection.");
                }
            }
        }
    }
}
