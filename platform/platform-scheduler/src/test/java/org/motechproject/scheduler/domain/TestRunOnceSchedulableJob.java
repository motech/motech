package org.motechproject.scheduler.domain;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
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

    private Date currentDate;

    @Before
    public void setUp() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("JobID", uuidStr);
        motechEvent1 = new MotechEvent("TestEvent", params);

        params = new HashMap<String, Object>();
        params.put("JobID", uuidStr2);
        motechEvent2 = new MotechEvent("TestEvent", params);

        Calendar cal = Calendar.getInstance();
        currentDate = cal.getTime();
        cal.add(Calendar.DATE, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void newConstructor_NullEvent() throws Exception {
        new RunOnceSchedulableJob(null, currentDate);
    }

    @Test(expected = IllegalArgumentException.class)
    public void newConstructor_NullDate() throws Exception {
        new RunOnceSchedulableJob(motechEvent1, null);
    }

    @Test
    public void equalsTest() throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, +1);
        Date date = cal.getTime();

        cal.add(Calendar.DATE, +1);
        Date date2 = cal.getTime();

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
