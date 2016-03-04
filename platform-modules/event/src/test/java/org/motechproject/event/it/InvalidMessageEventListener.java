package org.motechproject.event.it;

import org.joda.time.DateTime;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.annotations.MotechListener;

import java.util.ArrayList;
import java.util.List;

public class InvalidMessageEventListener implements EventListener {

    private static final String MESSAGE_REDELIVERY_TEST = "MESSAGE_REDELIVERY_TEST";

    private MotechEvent motechEvent;
    private List<DateTime> handledTimes = new ArrayList<>();

    @Override
    public String getIdentifier() {
        return MESSAGE_REDELIVERY_TEST;
    }

    @MotechListener(subjects = MESSAGE_REDELIVERY_TEST)
    public synchronized void handle(MotechEvent motechEvent) {
        this.motechEvent = motechEvent;
        handledTimes.add(new DateTime());
        throw new RuntimeException("Message redelivery test.");
    }

    public MotechEvent getMotechEvent() {
        return motechEvent;
    }

    public List<DateTime> getHandledTimes() {
        return handledTimes;
    }
}
