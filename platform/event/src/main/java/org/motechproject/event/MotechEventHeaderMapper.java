package org.motechproject.event;

import org.apache.activemq.ScheduledMessage;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.MessageHeaders;
import org.springframework.integration.jms.DefaultJmsHeaderMapper;

import javax.jms.JMSException;
import javax.jms.Message;

/**
 * For the delay to work, set attribute schedulerSupport="true" in the broker element of the activemq.xml
 * Ref: http://activemq.apache.org/delay-and-schedule-message-delivery.html
 */
public class MotechEventHeaderMapper extends DefaultJmsHeaderMapper {

    private static Logger logger = Logger.getLogger(MotechEventHeaderMapper.class);

    @Autowired
    private MotechEventConfig motechEventConfig;

    @Override
    public void fromHeaders(MessageHeaders messageHeaders, Message message) {
        super.fromHeaders(messageHeaders, message);
        try {
            MotechEvent motechEvent = (MotechEvent) ((ActiveMQObjectMessage) message).getObject();
            Boolean isFailedMessage = (Boolean) motechEvent.getParameters().get(MotechEvent.PARAM_INVALID_MOTECH_EVENT);

            if (isFailedMessage != null && isFailedMessage) {
                long redeliveryCount = motechEvent.getMessageRedeliveryCount();
                Double delay = motechEventConfig.getMessageRedeliveryDelay() * 1000L * ((Math.pow(2, redeliveryCount - 1)));
                logger.debug("Redelivering " + motechEvent + " after " + delay + " millis.");
                message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, delay.longValue());
            }
        } catch (JMSException e) {
            logger.error("Failed to set header", e);
        }
    }
}
