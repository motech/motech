package org.motechproject.scheduler.contract;

import org.joda.time.DateTime;
import org.motechproject.event.MotechEvent;

/**
 * Represents a job that can use its original fire time after a misfire.
 */
public abstract class MisfireSchedulableJob extends EndingSchedulableJob {

    private boolean useOriginalFireTimeAfterMisfire;

    protected MisfireSchedulableJob(MotechEvent motechEvent, DateTime startDate, DateTime endDate, boolean uiDefined,
                                    boolean ignorePastFiresAtStart, boolean useOriginalFireTimeAfterMisfire) {
        super(motechEvent, startDate, endDate, uiDefined, ignorePastFiresAtStart);
        this.useOriginalFireTimeAfterMisfire = useOriginalFireTimeAfterMisfire;
    }

    public boolean isUseOriginalFireTimeAfterMisfire() {
        return useOriginalFireTimeAfterMisfire;
    }

    public void setUseOriginalFireTimeAfterMisfire(boolean useOriginalFireTimeAfterMisfire) {
        this.useOriginalFireTimeAfterMisfire = useOriginalFireTimeAfterMisfire;
    }

    protected boolean equals(MisfireSchedulableJob other) {
        return useOriginalFireTimeAfterMisfire == other.isUseOriginalFireTimeAfterMisfire()
                && super.equals(other);
    }
}
