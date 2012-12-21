package org.motechproject.eventlogging.osgi;

import org.motechproject.event.MotechEvent;
import org.motechproject.eventlogging.service.EventLoggingService;

import java.util.HashSet;
import java.util.Set;

public class TestEventLoggingService implements EventLoggingService {

    private boolean isLogged = false;

    @Override
    public synchronized void logEvent(MotechEvent event) {
        isLogged = true;
        this.notifyAll();
    }

    @Override
    public Set<String> getLoggedEventSubjects() {
        Set<String> subjects = new HashSet<>();
        subjects.add(EventLoggingBundleIT.TEST_EVENT_SUBJECT);
        return subjects;
    }

    public boolean isLogged() {
        return isLogged;
    }
}
