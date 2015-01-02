package org.motechproject.event.queue;

import org.apache.activemq.ScheduledMessage;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.apache.log4j.Logger;
import org.motechproject.event.MotechEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.MessageHeaders;
import org.springframework.integration.jms.DefaultJmsHeaderMapper;

import javax.jms.JMSException;
import javax.jms.Message;

/**
 * Sets the <code>AMQ_SCHEDULED_DELAY</code> header of the JMS message being sent based on the <code>MotechEventConfig</code>.
 * For the delay to work, set attribute schedulerSupport="true" in the broker element of the activemq.xml
 * Ref: http://activemq.apache.org/delay-and-schedule-message-delivery.html
 */
public class MotechEventHeaderMapper extends DefaultJmsHeaderMapper {

    private static final Logger LOGGER = Logger.getLogger(MotechEventHeaderMapper.class);
    private static final long MILLIS_PER_SEC = 1000L;

    @Autowired
    private MotechEventConfig motechEventConfig;

    /**
     * {@inheritDoc}. Additionally sets <code>AMQ_SCHEDULED_DELAY</code> using
     * <code>MotechEventConfig</code> variables.
     */
    @Override
    public void fromHeaders(MessageHeaders messageHeaders, Message message) {
        super.fromHeaders(messageHeaders, message);
        try {
            MotechEvent motechEvent = (MotechEvent) ((ActiveMQObjectMessage) message).getObject();
            Boolean isFailedMessage = (Boolean) motechEvent.getParameters().get(MotechEvent.PARAM_INVALID_MOTECH_EVENT);

            if (isFailedMessage != null && isFailedMessage) {
                long redeliveryCount = motechEvent.getMessageRedeliveryCount();
                Double delay = motechEventConfig.getMessageRedeliveryDelay() * MILLIS_PER_SEC *
                        ((Math.pow(2, redeliveryCount - 1)));
                LOGGER.debug("Redelivering " + motechEvent + " after " + delay + " millis.");
                message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, delay.longValue());
            }
        } catch (JMSException e) {
            LOGGER.error("Failed to set header", e);
        }
    }
}
