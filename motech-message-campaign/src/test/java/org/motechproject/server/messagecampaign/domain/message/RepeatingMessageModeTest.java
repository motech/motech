package org.motechproject.server.messagecampaign.domain.message;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.motechproject.model.Time;
import org.motechproject.server.messagecampaign.builder.CampaignMessageBuilder;
import org.motechproject.testing.utils.BaseUnitTest;

import java.util.Date;
import java.util.List;

import static java.lang.Integer.valueOf;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.motechproject.server.messagecampaign.domain.message.RepeatingMessageMode.CALENDAR_WEEK_SCHEDULE;
import static org.motechproject.server.messagecampaign.domain.message.RepeatingMessageMode.WEEK_DAYS_SCHEDULE;
import static org.motechproject.util.DateUtil.newDate;
import static org.motechproject.util.DateUtil.newDateTime;

public class RepeatingMessageModeTest extends BaseUnitTest {

    @Test
    public void shouldReturnOffsetForCalendarWeekWithTuesdayAsStartOfWeek() {

        DateTime cycleStartDate = newDateTime(newDate(2011, 11, 2), 10, 11, 0);
        RepeatingCampaignMessage calendarStartAsTuesday = new CampaignMessageBuilder().repeatingCampaignMessageForCalendarWeek("mess", "Tuesday", null, "msg");

        int startIntervalOffset = 1;
        mockCurrentDate(date(2011, 11, 3));
        assertEquals(valueOf(startIntervalOffset), CALENDAR_WEEK_SCHEDULE.currentOffset(calendarStartAsTuesday, cycleStartDate, startIntervalOffset));

        mockCurrentDate(date(2011, 11, 7));
        startIntervalOffset = 5;
        assertEquals(valueOf(startIntervalOffset), CALENDAR_WEEK_SCHEDULE.currentOffset(calendarStartAsTuesday, cycleStartDate, startIntervalOffset));

        mockCurrentDate(date(2011, 11, 8));
        startIntervalOffset = 8;
        assertEquals(valueOf(1 + startIntervalOffset), CALENDAR_WEEK_SCHEDULE.currentOffset(calendarStartAsTuesday, cycleStartDate, startIntervalOffset));

        mockCurrentDate(date(2011, 11, 14));
        startIntervalOffset = 8;
        assertEquals(valueOf(1 + startIntervalOffset), CALENDAR_WEEK_SCHEDULE.currentOffset(calendarStartAsTuesday, cycleStartDate, startIntervalOffset));

        mockCurrentDate(date(2011, 11, 15));
        assertEquals(valueOf(2 + startIntervalOffset), CALENDAR_WEEK_SCHEDULE.currentOffset(calendarStartAsTuesday, cycleStartDate, startIntervalOffset));

        mockCurrentDate(date(2011, 11, 18));
        assertEquals(valueOf(2 + startIntervalOffset), CALENDAR_WEEK_SCHEDULE.currentOffset(calendarStartAsTuesday, cycleStartDate, startIntervalOffset));
    }

    @Test
    public void shouldReturnOffsetForCalendarWeekWithSundayAsStartOfWeek() {

        DateTime cycleStartDate = newDateTime(newDate(2011, 11, 2), 10, 11, 0);
        RepeatingCampaignMessage calendarStartAsSunday = repeatingCWCampaignMessage("mess", "Sunday", null, "msg");

        int startIntervalOffset = 1;
        mockCurrentDate(date(2011, 11, 3));
        assertEquals(valueOf(startIntervalOffset), CALENDAR_WEEK_SCHEDULE.currentOffset(calendarStartAsSunday, cycleStartDate, startIntervalOffset));

        startIntervalOffset = 8;

        mockCurrentDate(date(2011, 11, 7));
        assertEquals(valueOf(1 + startIntervalOffset), CALENDAR_WEEK_SCHEDULE.currentOffset(calendarStartAsSunday, cycleStartDate, startIntervalOffset));

        mockCurrentDate(date(2011, 11, 8));
        assertEquals(valueOf(1 + startIntervalOffset), CALENDAR_WEEK_SCHEDULE.currentOffset(calendarStartAsSunday, cycleStartDate, startIntervalOffset));

        mockCurrentDate(date(2011, 11, 13));
        assertEquals(valueOf(2 + startIntervalOffset), CALENDAR_WEEK_SCHEDULE.currentOffset(calendarStartAsSunday, cycleStartDate, startIntervalOffset));

        mockCurrentDate(date(2011, 11, 19));
        assertEquals(valueOf(2 + startIntervalOffset), CALENDAR_WEEK_SCHEDULE.currentOffset(calendarStartAsSunday, cycleStartDate, startIntervalOffset));

        mockCurrentDate(date(2011, 11, 21));
        assertEquals(valueOf(3 + startIntervalOffset), CALENDAR_WEEK_SCHEDULE.currentOffset(calendarStartAsSunday, cycleStartDate, startIntervalOffset));
    }

    @Test
    public void shouldThrowIllegalArguementExceptionIfCyleStartDateIsAheadCurrentDay() {
        DateTime cycleStartDate = newDateTime(newDate(2011, 11, 2), 10, 11, 0);
        RepeatingCampaignMessage calendarStartAsTuesday = repeatingCWCampaignMessage("mess", "Sunday", null, "msg");

        mockCurrentDate(date(2011, 11, 1));
        try {
            CALENDAR_WEEK_SCHEDULE.currentOffset(calendarStartAsTuesday, cycleStartDate, 4);
            Assert.fail("should fail for the current date");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "cycleStartDate cannot be in future");
        }
    }

    @Test
    public void shouldCheckHasEnded_IfNextToCurrentApplicableDateTimeIsBeyondEndTime() {

        RepeatingCampaignMessage message = repeatingCWCampaignMessage("mess", "Sunday", asList("Monday", "Wednesday", "Friday"), "msgKey")
                .deliverTime(new Time(10, 30));
        Date sat11Feb2012EndDate = date("11-02-2012 12:11:20 Sat");
        RepeatingMessageMode cwSchedule = CALENDAR_WEEK_SCHEDULE;

        /* > 10:30 - will fall on Thursday internal - so will send message on thursday itself (so on Friday it woud fail)
         * < 10:30 - message will be sent on friday (not on thursday)
         */

        //before 10:30
        mockCurrentDate(new DateTime(date("8-02-2012 06:11:10 Wed")));
        assertFalse(cwSchedule.hasEnded(message, sat11Feb2012EndDate));

        mockCurrentDate(new DateTime(date("9-02-2012 06:11:10 Thu")));
        assertFalse(cwSchedule.hasEnded(message, sat11Feb2012EndDate));

        mockCurrentDate(new DateTime(date("10-02-2012 06:11:10 Fri")));
        assertTrue(cwSchedule.hasEnded(message, sat11Feb2012EndDate));

        mockCurrentDate(new DateTime(date("11-02-2012 06:11:10 Sat")));
        assertTrue(cwSchedule.hasEnded(message, sat11Feb2012EndDate));

        //after 10:30
        mockCurrentDate(new DateTime(date("8-02-2012 11:11:10 Wed")));
        assertFalse(cwSchedule.hasEnded(message, sat11Feb2012EndDate));

        mockCurrentDate(new DateTime(date("9-02-2012 11:11:10 Thu")));
        assertTrue(cwSchedule.hasEnded(message, sat11Feb2012EndDate));

        mockCurrentDate(new DateTime(date("10-02-2012 11:12:10 Fri")));
        assertFalse(cwSchedule.hasEnded(message, sat11Feb2012EndDate));

        mockCurrentDate(new DateTime(date("11-02-2012 11:11:10 Sat")));
        assertTrue(cwSchedule.hasEnded(message, sat11Feb2012EndDate));
    }

    @Test
    public void shouldCheckHasEnded_IfNextToCurrentApplicableDateTimeIsBeyondEndTime_AndNowIsWithin_10_30_Range() {

        RepeatingCampaignMessage message = repeatingCWCampaignMessage("mess", "Sunday", asList("Monday", "Wednesday", "Friday"), "msgKey")
                .deliverTime(new Time(10, 30));
        Date sat11Feb2012EndDate = date("11-02-2012 12:11:20 Sat");
        RepeatingMessageMode cwSchedule = CALENDAR_WEEK_SCHEDULE;

        /* =< 10:30 - message will be sent on friday, so programEnded is set on Friday (not on thursday)
         * > 10:30 - will fall on Thursday interval - so will send message on thursday itself (so on Friday it woud fail)
         */

        // on 10:30 exactly - this is a exception to normal cases where
        mockCurrentDate(new DateTime(date("9-02-2012 10:30:00 Thu")));
        assertFalse(cwSchedule.hasEnded(message, sat11Feb2012EndDate));

        mockCurrentDate(new DateTime(date("10-02-2012 10:30:00 Fri")));
        assertTrue(cwSchedule.hasEnded(message, sat11Feb2012EndDate));

        // < 10:30
        mockCurrentDate(new DateTime(date("9-02-2012 10:29:59 Thu")));
        assertFalse(cwSchedule.hasEnded(message, sat11Feb2012EndDate));

        mockCurrentDate(new DateTime(date("10-02-2012 10:29:59 Fri")));
        assertTrue(cwSchedule.hasEnded(message, sat11Feb2012EndDate));

        // > 10:30
        mockCurrentDate(new DateTime(date("9-02-2012 10:30:01 Thu")));
        assertTrue(cwSchedule.hasEnded(message, sat11Feb2012EndDate));

        mockCurrentDate(new DateTime(date("10-02-2012 10:30:01 Fri")));
        assertFalse(cwSchedule.hasEnded(message, sat11Feb2012EndDate));

        // wed and satruday
        mockCurrentDate(new DateTime(date("8-02-2012 10:30:10 Wed")));
        assertFalse(cwSchedule.hasEnded(message, sat11Feb2012EndDate));

        mockCurrentDate(new DateTime(date("11-02-2012 10:30:00 Sat")));
        assertTrue(cwSchedule.hasEnded(message, sat11Feb2012EndDate));

    }

    @Test
    public void shouldReturnIsEndedForWeekSchedule_IfNextToCurrentApplicableDateIsBeyondEndDate(){

        RepeatingCampaignMessage message = new CampaignMessageBuilder().repeatingCampaignMessageForDaysApplicable("WeekType", asList("Monday", "Wednesday"), "msgKey");

        RepeatingMessageMode weekSchedule = WEEK_DAYS_SCHEDULE;
        Date sat17Feb2012EndDate = date("17-02-2012 22:11:20 Fri");

        mockCurrentDate(new DateTime(date("14-02-2012 12:00:00 Mon")));
        assertFalse(weekSchedule.hasEnded(message, sat17Feb2012EndDate));

        mockCurrentDate(new DateTime(date("14-02-2012 12:00:00 Tue")));
        assertFalse(weekSchedule.hasEnded(message, sat17Feb2012EndDate));

        mockCurrentDate(new DateTime(date("15-02-2012 12:00:00 Wed")));
        assertTrue(weekSchedule.hasEnded(message, sat17Feb2012EndDate));

        mockCurrentDate(new DateTime(date("17-02-2012 22:11:20 Fri")));
        assertTrue(weekSchedule.hasEnded(message, sat17Feb2012EndDate));
    }

    @Test
    @Ignore
    //
    // TODO: Test should be fixed once the isEnded and applicableNextDayWithin24Hours is fixed
    ///
    ///
    public void shouldReturnIsEndedForRepeatInterval_IfNextToCurrentApplicableDateIsBeyondEndDate(){

        RepeatingCampaignMessage message = new CampaignMessageBuilder().repeatingCampaignMessageForInterval("WeekType", "9 Days", "msgKey", "0:0");

        RepeatingMessageMode weekSchedule = RepeatingMessageMode.REPEAT_INTERVAL;
        Date sat17Feb2012EndDate = date("17-02-2012 22:11:20 Fri");

        mockCurrentDate(new DateTime(date("7-02-2012 12:00:00")));
        assertFalse(weekSchedule.hasEnded(message, sat17Feb2012EndDate));

        mockCurrentDate(new DateTime(date("8-02-2012 12:00:00")));
        assertFalse(weekSchedule.hasEnded(message, sat17Feb2012EndDate));

        mockCurrentDate(new DateTime(date("9-02-2012 12:00:00")));
        assertFalse(weekSchedule.hasEnded(message, sat17Feb2012EndDate));

        mockCurrentDate(new DateTime(date("17-02-2012 12:00:00")));
        assertTrue(weekSchedule.hasEnded(message, sat17Feb2012EndDate));

        mockCurrentDate(new DateTime(date("17-02-2012 22:11:20 Fri")));
        assertTrue(weekSchedule.hasEnded(message, sat17Feb2012EndDate));
    }

    private RepeatingCampaignMessage repeatingCWCampaignMessage(String name, String startDayOfWeek, List<String> applicableWeekDays, String messageKey) {
        return new CampaignMessageBuilder().repeatingCampaignMessageForCalendarWeek(name, startDayOfWeek, applicableWeekDays, messageKey);
    }
}
