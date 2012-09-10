package org.motechproject.event;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MotechEventConfig {

    /**
     * Maximum number of times a message would be re-delivered in case of InvalidMotechEventException
     */
    @Value("${motech.message.max.redelivery.count:3}")
    private int messageMaxRedeliveryCount;

    /**
     * Delay (in seconds) between successive re-deliveries of messages in case of InvalidMotechEventException
     * If delay=d and first exception was raised at time=t, then successive
     * redelivery times are t+d, t+(d*2), t+(d*4), t+(d*8), t+(d*16), t+(d*32),
     * and so on, till maximum redelivery count is reached.
     */
    @Value("${motech.message.redelivery.delay:1}")
    private long messageRedeliveryDelay;

    public int getMessageMaxRedeliveryCount() {
        return messageMaxRedeliveryCount;
    }

    public long getMessageRedeliveryDelay() {
        return messageRedeliveryDelay;
    }
}
