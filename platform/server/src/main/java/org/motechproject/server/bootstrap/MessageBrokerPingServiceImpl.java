package org.motechproject.server.bootstrap;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

@Service("messageBrokerPingService")
public class MessageBrokerPingServiceImpl implements MessageBrokerPingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageBrokerPingServiceImpl.class);

    private ActiveMQConnectionFactory factory;

    private Connection connection;

    private Session session;

    private Destination destination;

    private MessageProducer producer;

    private MessageConsumer consumer;

    @Override
    public boolean pingBroker(String queueUrl) {
        return test(queueUrl);
    }

    private void connect(final String queueUrl) throws JMSException {
        factory = new ActiveMQConnectionFactory(queueUrl);
        LOGGER.info("Connecting with broker using connector url {}.", queueUrl);
        connection = factory.createConnection();
        LOGGER.info("Connected with broker using connector url {}.", queueUrl);
        connection.start();
        LOGGER.info("Connection started.");
    }

    private void init() throws JMSException {

        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        LOGGER.info("Established a session with broker");

        destination = session.createQueue("MOTECH-TEST");
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

    private void disconnect() {
        if (consumer != null || session != null || connection != null) {
            try {
                consumer.close();
                session.close();
                connection.close();
            } catch (JMSException systemError) {
                LOGGER.error("System error occured due to {}", systemError);
            }
        }

    }

    public boolean test(final String queueUrl) {
        boolean acknowledgement;
        try {
            connect(queueUrl);
            init();
            acknowledgement = sendAndReceive();

        } catch (JMSException brokerPingTestFailedException) {
            LOGGER.info("Connection to message broker failed due to {}",
                    brokerPingTestFailedException);
            acknowledgement = false;
        } finally {
            disconnect();
        }
        return acknowledgement;
    }

    public static void main(String args[]) {
        MessageBrokerPingService messageBrokerPingService = new MessageBrokerPingServiceImpl();
        System.out.print(messageBrokerPingService.pingBroker("tcp://localhost:616161"));
    }
}
