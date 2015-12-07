package org.motechproject.event.listener;

import org.motechproject.server.osgi.event.OsgiEventProxy;
import org.motechproject.server.osgi.util.PlatformConstants;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.message.GenericMessage;

public class EventConsumerStarter implements EventHandler {
    private MessageChannel controlChannel;

    @Autowired
    public EventConsumerStarter(MessageChannel controlChannel) {
        this.controlChannel = controlChannel;
    }

    @Override
    public void handleEvent(Event osgiEvent) {
        String subject = (String) osgiEvent.getProperty(OsgiEventProxy.SUBJECT_PARAM);
        if (subject.equals(PlatformConstants.MODULES_STARTUP_TOPIC)) {
            startActiveMQConsumers();
        }
    }

    private void startActiveMQConsumers() {
        controlChannel.send(new GenericMessage<>("@queueOutboundChannelAdapter.start()"));
        controlChannel.send(new GenericMessage<>("@topicOutboundChannelAdapter.start()"));
    }
}
