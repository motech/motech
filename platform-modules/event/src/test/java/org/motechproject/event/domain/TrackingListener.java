package org.motechproject.event.domain;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrackingListener implements EventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrackingListener.class);

    private int count = 0;
    private String identifier;

    public TrackingListener(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public void handle(MotechEvent event) {
        LOGGER.info(String.format("Handled event %s by %s", event.getSubject(), identifier));
        count = count + 1;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    public int getCount() {
        return count;
    }
}
