package org.motechproject.scheduler.contract;

import org.motechproject.event.MotechEvent;

import java.io.Serializable;

/**
 * Represents Job that can be scheduled
 */
public abstract class SchedulableJob implements Serializable {

    private boolean uiDefined;

    protected SchedulableJob(boolean uiDefined) {
        this.uiDefined = uiDefined;
    }

    public abstract MotechEvent getMotechEvent();

    public abstract boolean isIgnorePastFiresAtStart();

    public boolean isUiDefined() {
        return uiDefined;
    }

    public void setUiDefined(boolean uiDefined) {
        this.uiDefined = uiDefined;
    }
}
