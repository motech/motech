package org.motechproject.event.listener;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import java.util.Enumeration;

public class ActiveMQQueueExplorer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActiveMQQueueExplorer.class);

    private Connection connection;
    private Session session;

    public ActiveMQQueueExplorer(CachingConnectionFactory connectionFactory) throws JMSException {
        try {
            ActiveMQConnectionFactory activeMQConnectionFactory = (ActiveMQConnectionFactory) connectionFactory.getTargetConnectionFactory();
            connection = activeMQConnectionFactory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        } catch (JMSException e) {
            if (connection != null) connection.close();
        }
    }

    public int queueSize(ActiveMQQueue queue) throws JMSException {
        QueueBrowser browser = session.createBrowser(queue);
        int i = 0;
        try {
            Enumeration messages = browser.getEnumeration();
            while (messages.hasMoreElements()) {
                messages.nextElement();
                i++;
            }
        } finally {
            browser.close();
        }
        return i;
    }

    public void clear(ActiveMQQueue queue) throws JMSException {
        MessageConsumer consumer = null;
        try {
            int iterated = 0;
            int count = 0;
            consumer = session.createConsumer(queue);
            while (queueSize(queue) != 0) {
                iterated++;
                if (iterated > 100) throw new RuntimeException("Could not clear the queue");

                Message message = consumer.receive(1000);
                if (message != null) count++;
                LOGGER.info("Cleared a message");
            }
            LOGGER.info(String.format("Cleared %d messages", count));
        } finally {
            if (consumer != null) consumer.close();
        }
    }

    public void close() throws JMSException {
        connection.close();
    }
}
