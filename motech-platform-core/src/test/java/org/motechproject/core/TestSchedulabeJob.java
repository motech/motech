package org.motechproject.core;

import org.junit.Test;
import org.motechproject.model.MotechScheduledEvent;
import org.motechproject.model.SchedulableJob;

import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: rob
 * Date: 3/1/11
 * Time: 1:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestSchedulabeJob
{
    private String uuidStr = UUID.randomUUID().toString();
    private String uuidStr2 = UUID.randomUUID().toString();

    @Test
    public void newTest() throws Exception{
        SchedulableJob job;
        MotechScheduledEvent scheduledEvent;
        scheduledEvent = new MotechScheduledEvent(uuidStr, "TestEvent", null);

        boolean exceptionThrown = false;
        try {
            job = new SchedulableJob(null, "0/5 0 * * * ?");
        }
        catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);

        exceptionThrown = false;
        try {
            job = new SchedulableJob(scheduledEvent, null);
        }
        catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);

        exceptionThrown = false;
        try {
            job = new SchedulableJob(scheduledEvent, "");
        }
        catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }

    @Test
    public void equalsTest() throws Exception{
        String cron1 = "0/5 0 * * * ?";
        String cron2 = "5 0 * * * ?";

        MotechScheduledEvent scheduledEvent1 = new MotechScheduledEvent(uuidStr, "testEvent", null);
        MotechScheduledEvent scheduledEvent2 = new MotechScheduledEvent(uuidStr2, "testEvent", null);

        SchedulableJob job1 = new SchedulableJob(scheduledEvent1, cron1);
        SchedulableJob job1Same = new SchedulableJob(scheduledEvent1, cron1);
        SchedulableJob job2 = new SchedulableJob(scheduledEvent2, cron1);
        SchedulableJob job3 = new SchedulableJob(scheduledEvent1, cron2);

        assertTrue(job1.equals(job1));
        assertTrue(job1.equals(job1Same));

        assertFalse(job1.equals(null));
        assertFalse(job1.equals(scheduledEvent1));

        // Same date, different event
        assertFalse(job1.equals(job2));

        // Same event different date
        assertFalse(job1.equals(job3));
    }
}
