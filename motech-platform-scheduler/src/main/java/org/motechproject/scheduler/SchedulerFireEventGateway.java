package org.motechproject.scheduler;

import org.motechproject.model.MotechScheduledEvent;

/**
 *A gateway interface to a Spring Integration message channel.
 * This interface configured in schedulerFiredEventChannelAdapter.xml
 *
 * @author Igor (iopushnyev@2paths.com)
 * Date: 23/02/11
 *
 */
public interface SchedulerFireEventGateway {

    /**
     * Sends the given MotechScheduledEvent message as a payload to the message channel
     *  defined in the Spring Integration configuration file.
     *
     * @param motechScheduledEvent
     */
    public void sendEventMessage(MotechScheduledEvent motechScheduledEvent);
}
