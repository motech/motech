package org.motechproject.server.messagecampaign.domain.message;

import org.junit.Test;

import static java.util.Arrays.asList;
import static junit.framework.Assert.fail;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.motechproject.server.messagecampaign.TestUtils.date;
import static org.motechproject.server.messagecampaign.TestUtils.mockCurrentDate;

public class RepeatingCampaignMessageTest {

    @Test
    public void shouldCreateRepeatingCampaignMessageWithEitherRepeatIntervalOrWeekDaysApplicableAndNotBoth() {
        try {
            new RepeatingCampaignMessage(null, null);
            fail();
        } catch (IllegalArgumentException e) {
        }

        try {
            new RepeatingCampaignMessage("7", asList("Monday", "Tuesday"));
            fail();
        } catch (IllegalArgumentException e) {
        }
        
        try {
            new RepeatingCampaignMessage(null, asList("Monday", "Tuesday"));
        } catch (IllegalArgumentException e) {
            fail();
        }

        try {
            new RepeatingCampaignMessage("7", null);
        } catch (IllegalArgumentException e) {
            fail();
        }
    }

    @Test
    public void shouldReturnRepeatIntervalInDays_WhenRepeatIntervalIsNotNull() {
        assertThat(new RepeatingCampaignMessage("2 Weeks", null).repeatIntervalInDaysForOffset(), is(14));
        assertThat(new RepeatingCampaignMessage("9 Days", null).repeatIntervalInDaysForOffset(), is(9));
    }

    @Test
    public void shouldReturnAs_1_ForScheduleInterval_WhenWeekDaysApplicableIsSet() {
        assertThat(new RepeatingCampaignMessage(null, asList("Monday", "Tuesday")).repeatIntervalForSchedule(), is(RepeatingCampaignMessage.DAILY_REPEAT_INTERVAL));
    }

    @Test
    public void shouldReturn_7_ForOffsetInterval_WhenWeekDaysApplicableIsSet() {
        assertThat(new RepeatingCampaignMessage(null, asList("Monday", "Tuesday")).repeatIntervalInDaysForOffset(), is(RepeatingCampaignMessage.WEEKLY_REPEAT_INTERVAL));
    }

    @Test
    public void shouldReturnIsApplicableAsTrueIfTheRepeatIntervalIsSet() {
        assertThat(new RepeatingCampaignMessage("2 Weeks", null).isApplicable(), is(true));
        assertThat(new RepeatingCampaignMessage("9 Days", null).isApplicable(), is(true));
    }

    @Test
    public void shouldReturnIsApplicableAsTrueIfTheCurrentDayMatches_ApplicableWeeksDays() {

        mockCurrentDate(date(2011, 11, 15));
        assertThat(new RepeatingCampaignMessage(null, asList("Monday", "Tuesday")).isApplicable(), is(true));
        mockCurrentDate(date(2011, 11, 16));
        assertThat(new RepeatingCampaignMessage(null, asList("Monday", "Tuesday", "Wednesday")).isApplicable(), is(true));
        mockCurrentDate(date(2011, 11, 17));
        assertThat(new RepeatingCampaignMessage(null, asList("Monday", "Tuesday", "Wednesday")).isApplicable(), is(false));
    }

}
