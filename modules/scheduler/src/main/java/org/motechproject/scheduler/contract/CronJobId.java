package org.motechproject.scheduler.contract;

import org.motechproject.event.MotechEvent;
import org.motechproject.scheduler.contract.JobId;

public class CronJobId extends JobId {

    private static final String SUFFIX_CRONJOBID = "";

    public CronJobId(String subject, String id) {
        super(subject, id, SUFFIX_CRONJOBID);
    }

    public CronJobId(MotechEvent event) {
        super(event, SUFFIX_CRONJOBID);
    }
}
