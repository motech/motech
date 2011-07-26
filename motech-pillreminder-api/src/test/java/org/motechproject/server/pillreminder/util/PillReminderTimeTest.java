package org.motechproject.server.pillreminder.util;

import junit.framework.Assert;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.domain.Dosage;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class PillReminderTimeTest {

    private PillReminderTime pillReminderTime;

    @Before
    public void setUp() {
        pillReminderTime = new PillReminderTime();
    }

    @Test
    public void testPillWindowExpired() {
        DateTime tenHoursEarlier = new DateTime().minusHours(10);
        int twoHourWindow = 2;
        Dosage dosageStaringTenHoursEarlier = new Dosage();
        dosageStaringTenHoursEarlier.setStartTime(new Time(tenHoursEarlier.getHourOfDay(), tenHoursEarlier.getMinuteOfHour()));
        assertTrue(pillReminderTime.pillWindowExpired(dosageStaringTenHoursEarlier, twoHourWindow));
    }

    @Test
    public void testTimesPillRemindersSent() {
        DateTime oneHourEarlier = new DateTime().minusHours(1);
        int twoHourWindow = 2;
        int tenMinuteInterval = 10;
        Dosage dosageStaringOneHourEarlier = new Dosage();
        dosageStaringOneHourEarlier.setStartTime(new Time(oneHourEarlier.getHourOfDay(), oneHourEarlier.getMinuteOfHour()));
        assertEquals(6, pillReminderTime.timesPillRemindersSent(dosageStaringOneHourEarlier, twoHourWindow, tenMinuteInterval));
    }

    @Test
    public void testTimesPillRemindersSentWhenDosageStartTimeLaterThanNow() {
        DateTime oneHourLater = new DateTime().plusHours(1);
        int twoHourWindow = 2;
        int tenMinuteInterval = 10;
        Dosage dosageStaringOneHourLater = new Dosage();
        dosageStaringOneHourLater.setStartTime(new Time(oneHourLater.getHourOfDay(), oneHourLater.getMinuteOfHour()));
        assertEquals(0, pillReminderTime.timesPillRemindersSent(dosageStaringOneHourLater, twoHourWindow, tenMinuteInterval));
    }

    @Test
    public void testTimesPillRemainderWillBeSent() {
        int twoHourWindow = 2;
        int tenMinuteInterval = 10;
        assertEquals(12, pillReminderTime.timesPillRemainderWillBeSent(twoHourWindow, tenMinuteInterval));
    }
}
