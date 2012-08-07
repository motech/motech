package org.motechproject.scheduler.domain;

import org.motechproject.scheduler.MotechSchedulerService;

import java.io.Serializable;

public class RunOnceJobId extends JobId{

    private static final String SUFFIX_RUNONCEJOBID = "-runonce";

    public RunOnceJobId(String subject, String id) {
        super(subject, id, SUFFIX_RUNONCEJOBID);
    }

    public RunOnceJobId(MotechEvent runOnceEvent) {
        super(runOnceEvent, SUFFIX_RUNONCEJOBID);
    }
}
