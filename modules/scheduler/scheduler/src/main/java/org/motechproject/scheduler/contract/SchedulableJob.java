package org.motechproject.scheduler.contract;

import org.motechproject.event.MotechEvent;

import java.io.Serializable;

/**
 * Represents Job that can be scheduled
 */
public abstract class SchedulableJob implements Serializable {

    private boolean programmed;

    public abstract MotechEvent getMotechEvent();

    public abstract boolean isIgnorePastFiresAtStart();

    public boolean isProgrammed() {
        return programmed;
    }

    public void setProgrammed(boolean programmed) {
        this.programmed = programmed;
    }
}
