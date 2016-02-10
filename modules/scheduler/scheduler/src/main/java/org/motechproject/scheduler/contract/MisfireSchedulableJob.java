package org.motechproject.scheduler.contract;

import org.joda.time.DateTime;
import org.motechproject.event.MotechEvent;

public abstract class MisfireSchedulableJob extends EndingSchedulableJob {

    private boolean useOriginalFireTimeAfterMisfire;

    protected MisfireSchedulableJob() {
        super();
        this.useOriginalFireTimeAfterMisfire = false;
    }

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
