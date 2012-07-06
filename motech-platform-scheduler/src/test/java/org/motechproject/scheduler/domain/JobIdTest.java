package org.motechproject.scheduler.domain;

import org.junit.Test;
import org.motechproject.scheduler.MotechSchedulerService;

import static junit.framework.Assert.assertEquals;

public class JobIdTest {
    private static final String SUBJECT_VALUE = "sub1";
    private static final String JOB_ID_VALUE = "id1";

    @Test
    public void value() {
        JobId jobId = new JobId(SUBJECT_VALUE, JOB_ID_VALUE, false);
        assertEquals(String.format("%s-%s", SUBJECT_VALUE, JOB_ID_VALUE), jobId.value());
    }

    @Test
    public void repeatingId() {
        JobId jobId = new JobId(SUBJECT_VALUE, JOB_ID_VALUE, true);
        assertEquals(String.format("%s-%s%s", SUBJECT_VALUE, JOB_ID_VALUE, JobId.REPEAT_JOB_SUFFIX), jobId.repeatingId());
    }

    @Test
    public void initializeUsingMotechEvent() {
        MotechEvent motechEvent = new MotechEvent(SUBJECT_VALUE);
        motechEvent.getParameters().put(MotechSchedulerService.JOB_ID_KEY, JOB_ID_VALUE);
        JobId jobId = new JobId(motechEvent, false);
        assertEquals(String.format("%s-%s", SUBJECT_VALUE, JOB_ID_VALUE), jobId.value());

        jobId = new JobId(motechEvent, true);
        assertEquals(String.format("%s-%s%s", SUBJECT_VALUE, JOB_ID_VALUE, JobId.REPEAT_JOB_SUFFIX), jobId.repeatingId());
    }
}
