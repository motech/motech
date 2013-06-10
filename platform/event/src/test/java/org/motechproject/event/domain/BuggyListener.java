package org.motechproject.event.domain;

import org.motechproject.event.MotechEvent;

public class BuggyListener extends TrackingListener {


    private int times;

    public BuggyListener(int numberOfTimesExceptionThrown) {
        super("buggy-listener");
        times = numberOfTimesExceptionThrown;
    }

    @Override
    public void handle(MotechEvent event) {
        super.handle(event);
        if (getCount() <= times) {
            throw new TestListenerException();
        }
    }

    @Override
    public String getIdentifier() {
        return "buggy-listener";
    }

}
