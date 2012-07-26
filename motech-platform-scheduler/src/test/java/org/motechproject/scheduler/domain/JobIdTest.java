package org.motechproject.scheduler.domain;

import org.junit.Test;
import org.motechproject.scheduler.MotechSchedulerService;

import static junit.framework.Assert.assertEquals;

public class JobIdTest {
    private static final String SUBJECT_VALUE = "sub1";
    private static final String JOB_ID_VALUE = "id1";

    @Test
    public void value() {
        JobId jobId = new CronJobId(SUBJECT_VALUE, JOB_ID_VALUE);
        assertEquals(String.format("%s-%s", SUBJECT_VALUE, JOB_ID_VALUE), jobId.value());
    }

    @Test
    public void repeatingId() {
        JobId jobId = new RepeatingJobId(SUBJECT_VALUE, JOB_ID_VALUE);
        assertEquals(String.format("%s-%s%s", SUBJECT_VALUE, JOB_ID_VALUE, RepeatingJobId.SUFFIX_REPEATJOBID), jobId.value());
    }

    @Test
    public void initializeUsingMotechEvent() {
        MotechEvent motechEvent = new MotechEvent(SUBJECT_VALUE);
        motechEvent.getParameters().put(MotechSchedulerService.JOB_ID_KEY, JOB_ID_VALUE);
        JobId jobId = new CronJobId(motechEvent);
        assertEquals(String.format("%s-%s", SUBJECT_VALUE, JOB_ID_VALUE), jobId.value());

        jobId = new RepeatingJobId(motechEvent);
        assertEquals(String.format("%s-%s%s", SUBJECT_VALUE, JOB_ID_VALUE, RepeatingJobId.SUFFIX_REPEATJOBID), jobId.value());
    }
}
