package org.motechproject.core;

import org.junit.Test;
import org.motechproject.model.MotechScheduledEvent;
import org.motechproject.model.RunOnceSchedulableJob;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: rob
 * Date: 3/1/11
 * Time: 1:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestRunOnceSchedulableJob
{
    private String uuidStr = UUID.randomUUID().toString();
    private String uuidStr2 = UUID.randomUUID().toString();

    @Test
    public void newTest() throws Exception{
        RunOnceSchedulableJob job;
        MotechScheduledEvent scheduledEvent;
        scheduledEvent = new MotechScheduledEvent(uuidStr, "TestEvent", null);

        Calendar cal = Calendar.getInstance();
        Date currentDate = cal.getTime();
        cal.add(Calendar.DATE, -1);
        Date yesterday = cal.getTime();

        boolean exceptionThrown = false;
        try {
            job = new RunOnceSchedulableJob(null, currentDate);
        }
        catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);

        exceptionThrown = false;
        try {
            job = new RunOnceSchedulableJob(scheduledEvent, null);
        }
        catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);

        exceptionThrown = false;
        try {
            job = new RunOnceSchedulableJob(scheduledEvent, yesterday);
        }
        catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }

    @Test
    public void equalsTest() throws Exception{
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, +1);
        Date date = cal.getTime();

        cal.add(Calendar.DATE, +1);
        Date date2 = cal.getTime();

        MotechScheduledEvent scheduledEvent1 = new MotechScheduledEvent(uuidStr, "testEvent", null);
        MotechScheduledEvent scheduledEvent2 = new MotechScheduledEvent(uuidStr2, "testEvent", null);

        RunOnceSchedulableJob job1 = new RunOnceSchedulableJob(scheduledEvent1, date);
        RunOnceSchedulableJob job1Same = new RunOnceSchedulableJob(scheduledEvent1, date);
        RunOnceSchedulableJob job2 = new RunOnceSchedulableJob(scheduledEvent2, date);
        RunOnceSchedulableJob job3 = new RunOnceSchedulableJob(scheduledEvent1, date2);

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
