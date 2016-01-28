package org.motechproject.event.listener.impl;

import org.motechproject.event.listener.EventConsumerInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.jms.JmsMessageDrivenEndpoint;

/**
 * Default implementation of EventConsumerInfo interface
 *
 * @see org.motechproject.event.listener.EventConsumerInfo
 */
public class EventConsumerInfoImpl implements EventConsumerInfo {

    private JmsMessageDrivenEndpoint queueEndpoint;
    private JmsMessageDrivenEndpoint topicEndpoint;

    @Override
    public boolean isRunning() {
        return queueEndpoint.isRunning() && topicEndpoint.isRunning();
    }

    @Autowired
    @Qualifier("eventQueueJMSIn")
    public void setQueueEndpoint(JmsMessageDrivenEndpoint queueEndpoint) {
        this.queueEndpoint = queueEndpoint;
    }

    @Autowired
    @Qualifier("eventTopicJMSIn")
    public void setTopicEndpoint(JmsMessageDrivenEndpoint topicEndpoint) {
        this.topicEndpoint = topicEndpoint;
    }
}
