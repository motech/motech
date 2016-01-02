package org.motechproject.server.util;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

/**
 * Created by atish on 9/7/15.
 */
public final class MessageBrokerPingUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageBrokerPingUtil.class);

    private ActiveMQConnectionFactory factory;

    private Connection connection;

    private Session session;

    private Destination destination;

    private MessageProducer producer;

    private MessageConsumer consumer;


    private MessageBrokerPingUtil() {

    }

    private static class SingletonHelper {
        private static final MessageBrokerPingUtil INSTANCE = new MessageBrokerPingUtil();
    }

    public static MessageBrokerPingUtil getInstance() {
        return SingletonHelper.INSTANCE;
    }

    private void connect(final String queueUrl) throws JMSException {
        factory = new ActiveMQConnectionFactory(queueUrl);
        LOGGER.info(String.format("Connecting with broker using connector url %s.", queueUrl));
        connection = factory.createConnection();
        LOGGER.info(String.format("Connected with broker using connector url %s.", queueUrl));
        connection.start();
        LOGGER.info("Connection started.");
    }

    private void init() throws JMSException {

        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        LOGGER.info("Established a session with broker");

        destination = session.createQueue("TEST");
        LOGGER.info("Created a Destination TEST to send TEST message.");

        producer = session.createProducer(destination);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        LOGGER.info("Created a Producer to send PING message without persisting PING message in store.");
        consumer = session.createConsumer(destination);
        LOGGER.info("Created a Consumer to receive PING message.");

    }

    private boolean sendAndReceive() throws JMSException {

        boolean acknowledgementReceived = false;
        TextMessage messageToSend = session.createTextMessage("PING");
        producer.send(messageToSend);
        LOGGER.info("Producer sent PING message to broker.");
        LOGGER.info("Consumer waiting for broker to receive message.");
        Message acknowledgement = consumer.receive(8000);
        if (acknowledgement instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) acknowledgement;
            String text = textMessage.getText();
            if (text.equals("PING")) {
                LOGGER.info("Consumer received PING from broker.");
                acknowledgementReceived = true;
                LOGGER.info("Broker connection verified.");
            }
        }
        return acknowledgementReceived;
    }

    private void disconnect() throws JMSException {
        consumer.close();
        session.close();
        connection.close();
    }

    public boolean test(final String queueUrl) {
        boolean acknowledgement;
        try {
            connect(queueUrl);
            init();
            acknowledgement = sendAndReceive();
            disconnect();
        } catch (JMSException brokerPingTestFailedException) {
            LOGGER.info(String.format("Connection to message broker failed due to %s",
                    brokerPingTestFailedException.toString()));
            acknowledgement = false;
        }
        return acknowledgement;
    }

}
