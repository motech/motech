package org.motechproject.server.web.helper;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.stereotype.Component;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

/**
 * Helper class for creating UI suggestions for the user. Checks default locations for
 * available services.
 */
@Component
public class SuggestionHelper {

    public static final String DEFAULT_ACTIVEMQ_URL = "tcp://localhost:61616";

    /**
     * Suggests the ActiveMQ url.
     * @return The suggested url, or an empty string if the instance is not found.
     */
    public String suggestActivemqUrl() {
        Connection connection = null;
        boolean found = false;

        try {
            ConnectionFactory factory = new ActiveMQConnectionFactory(DEFAULT_ACTIVEMQ_URL);
            connection = factory.createConnection();
            connection.start();
        } catch (JMSException e) {
            found = false;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                    found = true;
                } catch (JMSException e) {
                    found = false;
                }
            }
        }

        return (found) ? DEFAULT_ACTIVEMQ_URL : "";
    }
}
