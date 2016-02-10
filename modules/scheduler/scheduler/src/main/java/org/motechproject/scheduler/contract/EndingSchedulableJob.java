package org.motechproject.scheduler.contract;

import org.apache.commons.lang.ObjectUtils;
import org.joda.time.DateTime;
import org.motechproject.event.MotechEvent;

public abstract class EndingSchedulableJob extends SchedulableJob {

    private DateTime endDate;

    protected EndingSchedulableJob() {
        this(null, null, null, false, false);
    }

    protected EndingSchedulableJob(MotechEvent motechEvent, DateTime startDate, DateTime endDate, boolean uiDefined,
                                   boolean ignorePastFiresAtStart) {
        super(motechEvent, startDate, uiDefined, ignorePastFiresAtStart);
        this.endDate = endDate;
    }

    public DateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(DateTime endDate) {
        this.endDate = endDate;
    }

    protected boolean equals(EndingSchedulableJob other) {
        return ObjectUtils.equals(endDate, other.endDate)
                && super.equals(other);
    }
}
