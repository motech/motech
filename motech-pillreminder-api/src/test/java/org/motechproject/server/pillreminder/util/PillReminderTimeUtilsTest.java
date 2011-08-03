package org.motechproject.server.pillreminder.util;


import org.apache.commons.lang.time.DateUtils;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.domain.Dosage;

import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class PillReminderTimeUtilsTest {

    private PillReminderTimeUtils pillReminderTimeUtils;

    @Before
    public void setUp() {
        pillReminderTimeUtils = new PillReminderTimeUtils();
    }
    @Test
    public void testTimesPillRemindersSent() {
        DateTime oneHourEarlier = new DateTime().minusHours(1);
        int twoHourWindow = 2;
        int tenMinuteInterval = 10;
        Dosage dosageStaringOneHourEarlier = new Dosage();
        dosageStaringOneHourEarlier.setStartTime(new Time(oneHourEarlier.getHourOfDay(), oneHourEarlier.getMinuteOfHour()));
        assertEquals(6, pillReminderTimeUtils.timesPillRemindersSent(dosageStaringOneHourEarlier, twoHourWindow, tenMinuteInterval));
    }

    @Test
    public void testTimesPillRemindersSentWhenDosageStartTimeLaterThanNow() {
//        TODO : test fails when run between 11:00 and 12:00 pm
        DateTime oneHourLater = new DateTime().plusHours(1);
        int twoHourWindow = 2;
        int tenMinuteInterval = 10;
        Dosage dosageStaringOneHourLater = new Dosage();
        dosageStaringOneHourLater.setStartTime(new Time(oneHourLater.getHourOfDay(), oneHourLater.getMinuteOfHour()));
        assertEquals(0, pillReminderTimeUtils.timesPillRemindersSent(dosageStaringOneHourLater, twoHourWindow, tenMinuteInterval));
    }

    @Test
    public void testIsDosageTakenShouldRetrunFalseWhenTheDosageWasNotConsumedInTheCurrentPillWindow(){
        Dosage dosageNotYetConsumed = new Dosage();
        DateTime oneHourBack = new DateTime().minusHours(1);
        dosageNotYetConsumed.setStartTime(new Time(oneHourBack.getHourOfDay(),oneHourBack.getMinuteOfHour()));
        dosageNotYetConsumed.setCurrentDosageDate(DateUtils.addDays(new Date(), -1));
        assertFalse(pillReminderTimeUtils.isDosageTaken(dosageNotYetConsumed, 4));
    }

    @Test
    public void testIsDosageTakenShouldRetrunTrueWhenTheDosageWasConsumedInTheCurrentPillWindow(){
        Dosage dosageConsumed = new Dosage();
        DateTime oneHourBack = new DateTime().minusHours(1);
        dosageConsumed.setStartTime(new Time(oneHourBack.getHourOfDay(),oneHourBack.getMinuteOfHour()));
        dosageConsumed.setCurrentDosageDate(DateUtils.addSeconds(new Date(), -1));
        assertTrue(pillReminderTimeUtils.isDosageTaken(dosageConsumed, 4));
    }

    @Test
    public void testTimesPillRemainderWillBeSent() {
        int twoHourWindow = 2;
        int tenMinuteInterval = 10;
        assertEquals(12, pillReminderTimeUtils.timesPillRemainderWillBeSent(twoHourWindow, tenMinuteInterval));
    }

}
