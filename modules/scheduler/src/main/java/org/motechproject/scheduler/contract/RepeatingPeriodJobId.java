package org.motechproject.scheduler.contract;

import org.motechproject.event.MotechEvent;

public class RepeatingPeriodJobId extends JobId {

    public static final String SUFFIX_REPEATPERIODJOBID = "-period";

    public RepeatingPeriodJobId(String subject, String id) {
        super(subject, id, SUFFIX_REPEATPERIODJOBID);
    }

    public RepeatingPeriodJobId(MotechEvent repeatingEvent) {
        super(repeatingEvent, SUFFIX_REPEATPERIODJOBID);
    }
}
