package org.motechproject.event.osgi;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;

import java.util.ArrayList;
import java.util.List;

public class TestEventListnerOsgi {

    public static final String TEST_SUBJECT_OSGI = "TestSubjectOSGI";
    private List<String> receivedEvents = new ArrayList<>();

    @MotechListener(subjects = TEST_SUBJECT_OSGI)
    public synchronized void testSubjectOsgiHandler(MotechEvent event) {
        receivedEvents.add(event.getSubject());
        this.notify();
    }

    public List<String> getReceivedEvents() {
        return receivedEvents;
    }
}
