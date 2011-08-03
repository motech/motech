package org.motechproject.server.pillreminder.util;


import org.apache.commons.lang.time.DateUtils;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.domain.Dosage;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;


@RunWith(PowerMockRunner.class)
@PrepareForTest(Util.class)
public class PillReminderTimeUtilsTest {

    private PillReminderTimeUtils pillReminderTimeUtils;

    @Before
    public void setUp() {
        pillReminderTimeUtils = new PillReminderTimeUtils();
    }

    @Test
    public void timesSentInAnHourFromDosageStartTime() {
        mockStatic(Util.class);
        DateTime now = new DateTime(2011, 1, 1, 10, 20, 0, 0);
        when(Util.currentDateTime()).thenReturn(now);

        DateTime oneHourEarlier = now.minusHours(1);
        int twoHourWindow = 2;
        int tenMinuteInterval = 10;
        Dosage dosageStaringOneHourEarlier = new Dosage();
        dosageStaringOneHourEarlier.setStartTime(new Time(oneHourEarlier.getHourOfDay(), oneHourEarlier.getMinuteOfHour()));
        assertEquals(6, pillReminderTimeUtils.timesPillRemindersSent(dosageStaringOneHourEarlier, twoHourWindow, tenMinuteInterval));
    }

    @Test
    public void timesSentInHalfHourHourFromDosageStartTime() {
        mockStatic(Util.class);
        DateTime now = new DateTime(2011, 1, 1, 10, 20, 0, 0);
        when(Util.currentDateTime()).thenReturn(now);

        DateTime halfHourEarlier = now.minusMinutes(30);
        int twoHourWindow = 2;
        int tenMinuteInterval = 10;
        Dosage dosageStaringOneHourEarlier = new Dosage();
        dosageStaringOneHourEarlier.setStartTime(new Time(halfHourEarlier.getHourOfDay(), halfHourEarlier.getMinuteOfHour()));
        assertEquals(3, pillReminderTimeUtils.timesPillRemindersSent(dosageStaringOneHourEarlier, twoHourWindow, tenMinuteInterval));
    }

    @Test
    public void timesSentInAnHourFromDosageStartTimeWhenCrossingOverToNextDay() {
        mockStatic(Util.class);
        DateTime now = new DateTime(2011, 1, 2, 0, 10, 0, 0);
        when(Util.currentDateTime()).thenReturn(now);

        int twoHourWindow = 2;

        DateTime halfHourEarlier = now.minusHours(1);
        int tenMinuteInterval = 10;
        Dosage dosageStaringOneHourEarlier = new Dosage();
        dosageStaringOneHourEarlier.setStartTime(new Time(halfHourEarlier.getHourOfDay(), halfHourEarlier.getMinuteOfHour()));
        assertEquals(6, pillReminderTimeUtils.timesPillRemindersSent(dosageStaringOneHourEarlier, twoHourWindow, tenMinuteInterval));
    }


    @Test
    public void timesSentOutsidePillWindow() {
        mockStatic(Util.class);
        DateTime now = new DateTime(2011, 1, 2, 0, 10, 0, 0);
        when(Util.currentDateTime()).thenReturn(now);

        int twoHourWindow = 2;
        int tenMinuteInterval = 10;

        DateTime halfHourEarlier = now.minusHours(2).minusMinutes(30);

        Dosage dosageStaringOneHourEarlier = new Dosage();
        dosageStaringOneHourEarlier.setStartTime(new Time(halfHourEarlier.getHourOfDay(), halfHourEarlier.getMinuteOfHour()));
        assertEquals(12, pillReminderTimeUtils.timesPillRemindersSent(dosageStaringOneHourEarlier, twoHourWindow, tenMinuteInterval));
    }

    @Test
    public void testIsDosageTakenShouldReturnFalseWhenTheDosageWasNotConsumedInTheCurrentPillWindow() {
        Dosage dosageNotYetConsumed = new Dosage();
        DateTime oneHourBack = new DateTime().minusHours(1);
        dosageNotYetConsumed.setStartTime(new Time(oneHourBack.getHourOfDay(), oneHourBack.getMinuteOfHour()));
        dosageNotYetConsumed.setCurrentDosageDate(DateUtils.addDays(new Date(), -1));
        assertFalse(pillReminderTimeUtils.isDosageTaken(dosageNotYetConsumed, 4));
    }

    @Test
    public void testIsDosageTakenShouldReturnTrueWhenTheDosageWasConsumedInTheCurrentPillWindow() {
        Dosage dosageConsumed = new Dosage();
        DateTime oneHourBack = new DateTime().minusHours(1);
        dosageConsumed.setStartTime(new Time(oneHourBack.getHourOfDay(), oneHourBack.getMinuteOfHour()));
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
