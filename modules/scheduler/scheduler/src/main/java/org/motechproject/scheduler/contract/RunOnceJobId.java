package org.motechproject.scheduler.contract;

import org.motechproject.event.MotechEvent;
import org.motechproject.scheduler.contract.JobId;

public class RunOnceJobId extends JobId {

    public static final String SUFFIX_RUNONCEJOBID = "-runonce";

    public RunOnceJobId(String subject, String id) {
        super(subject, id, SUFFIX_RUNONCEJOBID);
    }

    public RunOnceJobId(MotechEvent runOnceEvent) {
        super(runOnceEvent, SUFFIX_RUNONCEJOBID);
    }
}
