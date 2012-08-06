package org.motechproject.scheduler.gateway;

import org.motechproject.scheduler.domain.MotechEvent;

/**
 * \ingroup scheduler
 *
 *A gateway interface to a Spring Integration message channel.
 * This interface configured in schedulerFiredEventChannelAdapter.xml
 *
 * @author Igor (iopushnyev@2paths.com)
 * Date: 23/02/11
 *
 */
public interface SchedulerFireEventGateway {

    /**
     * Sends the given MotechEvent message as a payload to the message channel
     *  defined in the Spring Integration configuration file.
     *
     * @param motechEvent
     */
    void sendEventMessage(MotechEvent motechEvent);
}
