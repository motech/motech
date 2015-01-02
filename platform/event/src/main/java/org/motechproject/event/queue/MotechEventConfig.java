package org.motechproject.event.queue;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Accesses the <code>MotechEventConfig</code> variables.
 */
@Component
public class MotechEventConfig {

    @Value("${motech.message.max.redelivery.count:3}")
    private int messageMaxRedeliveryCount;

    @Value("${motech.message.redelivery.delay:1}")
    private long messageRedeliveryDelay;

    /**
     * Returns maximum number of times a message would be re-delivered
     * in case of any exception.
     *
     * @return the maximum number of message redelivery
     */
    public int getMessageMaxRedeliveryCount() {
        return messageMaxRedeliveryCount;
    }

    /**
     * Returns delay (in seconds) between successive re-deliveries of messages in case of any exception.
     * If delay=d and first exception was raised at time=t, then successive
     * redelivery times are t+d, t+(d*2), t+(d*4), t+(d*8), t+(d*16), t+(d*32),
     * and so on, till maximum redelivery count is reached.
     *
     * @return the message redelivery delay
     */
    public long getMessageRedeliveryDelay() {
        return messageRedeliveryDelay;
    }
}
