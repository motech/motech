package org.motechproject.scheduler.domain;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.event.MotechEvent;
import org.motechproject.scheduler.contract.RunOnceSchedulableJob;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestRunOnceSchedulableJob {
    private String uuidStr = UUID.randomUUID().toString();
    private String uuidStr2 = UUID.randomUUID().toString();

    private MotechEvent motechEvent1;
    private MotechEvent motechEvent2;

    private DateTime currentDate;

    @Before
    public void setUp() {
        Map<String, Object> params = new HashMap<>();
        params.put("JobID", uuidStr);
        motechEvent1 = new MotechEvent("TestEvent", params);

        params = new HashMap<>();
        params.put("JobID", uuidStr2);
        motechEvent2 = new MotechEvent("TestEvent", params);

        currentDate = DateUtil.now();
        currentDate = currentDate.minusDays(1);
    }

    @Test
    public void equalsTest() throws Exception {
        DateTime now = DateUtil.now();
        DateTime date = now.plusDays(1);
        DateTime date2 = now.plusDays(2);

        RunOnceSchedulableJob job1 = new RunOnceSchedulableJob(motechEvent1, date);
        RunOnceSchedulableJob job1Same = new RunOnceSchedulableJob(motechEvent1, date);
        RunOnceSchedulableJob job2 = new RunOnceSchedulableJob(motechEvent2, date);
        RunOnceSchedulableJob job3 = new RunOnceSchedulableJob(motechEvent1, date2);

        assertTrue(job1.equals(job1));
        assertTrue(job1.equals(job1Same));

        assertFalse(job1.equals(null));
        assertFalse(job1.equals(motechEvent1));

        // Same date, different event
        assertFalse(job1.equals(job2));

        // Same event different date
        assertFalse(job1.equals(job3));
    }
}
