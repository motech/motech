package org.motechproject.event.listener;

import org.motechproject.server.osgi.event.OsgiEventProxy;
import org.motechproject.server.osgi.util.PlatformConstants;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Handles incoming events and starts ActiveMQ outbound channels.
 */
public class EventConsumerStarter implements EventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventConsumerStarter.class);

    private ControlBusGateway controlBusGateway;

    @Autowired
    public EventConsumerStarter(ControlBusGateway controlBusGateway) {
        this.controlBusGateway = controlBusGateway;
    }

    /**
     * Receives an OSGi event with the proxy topic.
     * If event has {@link OsgiEventProxy#SUBJECT_PARAM subject} param
     * with specified {@link PlatformConstants#MODULES_STARTUP_TOPIC value},
     * then ActiveMQ outbound channels will be started.
     *
     * @param osgiEvent the event sent from OSGi
     */
    @Override
    public void handleEvent(Event osgiEvent) {
        String subject = (String) osgiEvent.getProperty(OsgiEventProxy.SUBJECT_PARAM);
        if (subject.equals(PlatformConstants.MODULES_STARTUP_TOPIC)) {
            startActiveMQConsumers();
        }
        LOGGER.info("ActiveMQ outbound channels started.");
    }

    private void startActiveMQConsumers() {
        controlBusGateway.sendCommand("@queueOutboundChannelAdapter.start()");
        controlBusGateway.sendCommand("@topicOutboundChannelAdapter.start()");
    }
}
