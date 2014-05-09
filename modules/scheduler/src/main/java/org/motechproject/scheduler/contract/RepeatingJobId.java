package org.motechproject.scheduler.contract;

import org.motechproject.event.MotechEvent;
import org.motechproject.scheduler.contract.JobId;

public class RepeatingJobId extends JobId {

    public static final String SUFFIX_REPEATJOBID = "-repeat";

    public RepeatingJobId(String subject, String id) {
        super(subject, id, SUFFIX_REPEATJOBID);
    }

    public RepeatingJobId(MotechEvent repeatingEvent) {
        super(repeatingEvent, SUFFIX_REPEATJOBID);
    }
}
