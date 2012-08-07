package org.motechproject.scheduler.domain;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestCronSchedulabeJob {
    private String uuidStr = UUID.randomUUID().toString();
    private String uuidStr2 = UUID.randomUUID().toString();

    private MotechEvent motechEvent1;
    private MotechEvent motechEvent2;

    @Before
    public void setUp() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("JobID", uuidStr);
        motechEvent1 = new MotechEvent("TestEvent", params);

        params = new HashMap<String, Object>();
        params.put("JobID", uuidStr2);
        motechEvent2 = new MotechEvent("TestEvent", params);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_NullEvent() throws Exception {
        new CronSchedulableJob(null, "0/5 0 * * * ?");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_NullCronExpression() throws Exception {
        new CronSchedulableJob(motechEvent1, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_InvalidCronExpression() throws Exception {
        new CronSchedulableJob(motechEvent1, "");
    }

    @Test
    public void equalsTest() throws Exception {
        String cron1 = "0/5 0 * * * ?";
        String cron2 = "5 0 * * * ?";

        CronSchedulableJob job1 = new CronSchedulableJob(motechEvent1, cron1);
        CronSchedulableJob job1Same = new CronSchedulableJob(motechEvent1, cron1);
        CronSchedulableJob job2 = new CronSchedulableJob(motechEvent2, cron1);
        CronSchedulableJob job3 = new CronSchedulableJob(motechEvent1, cron2);

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
