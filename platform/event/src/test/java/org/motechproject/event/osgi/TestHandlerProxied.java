package org.motechproject.event.osgi;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;

/*
This test handler mocks the situation when spring bean is proxied, for example when @Transactional annotation is used.
In test application context for the event module this bean is proxied manually.
 */
public class TestHandlerProxied {
    public static final String TEST_HANDLER_PROXIED_SUBJECT="test-handler-proxied-subject";

    @MotechListener(subjects = TEST_HANDLER_PROXIED_SUBJECT)
    public void handle(MotechEvent event) {
    }
}