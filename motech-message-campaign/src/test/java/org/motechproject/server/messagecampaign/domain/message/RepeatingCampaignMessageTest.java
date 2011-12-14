package org.motechproject.server.messagecampaign.domain.message;

import org.junit.Test;
import org.motechproject.server.messagecampaign.BaseUnitTest;
import org.motechproject.server.messagecampaign.builder.CampaignMessageBuilder;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class RepeatingCampaignMessageTest extends BaseUnitTest {

    @Test
    public void shouldReturnRepeatIntervalInDays_WhenRepeatIntervalIsNotNull() {
        assertThat(new RepeatingCampaignMessage("2 Weeks", "0:0").repeatIntervalInDaysForOffset(), is(14));
        assertThat(new RepeatingCampaignMessage("9 Days", "0:0").repeatIntervalInDaysForOffset(), is(9));
    }

    @Test
    public void shouldReturnAs_1_ForScheduleInterval_WhenWeekDaysApplicableIsSet() {
        assertThat(repeatMessageWithDaysApplicable(asList("Monday")).repeatIntervalForSchedule(), is(RepeatingCampaignMessage.DAILY_REPEAT_INTERVAL));
    }

    @Test
    public void shouldReturn_7_ForOffsetInterval_WhenWeekDaysApplicableIsSet() {
        assertThat(repeatMessageWithDaysApplicable(asList("Monday", "Tuesday")).repeatIntervalInDaysForOffset(), is(RepeatingCampaignMessage.WEEKLY_REPEAT_INTERVAL));
    }

    @Test
    public void shouldReturnIsApplicableAsTrueIfTheRepeatIntervalIsSet() {
        assertThat(builder().repeatingCampaignMessageForInterval("name", "2 Weeks", "msgKey").isApplicable(), is(true));
        assertThat(builder().repeatingCampaignMessageForInterval("name", "9 Days", "key2").isApplicable(), is(true));
    }

    @Test
    public void shouldReturnIsApplicableAsTrueIfTheCurrentDayMatches_ApplicableWeeksDays() {

        mockCurrentDate(date(2011, 11, 15));
        assertThat(repeatMessageWithDaysApplicable(asList("Monday", "Tuesday")).isApplicable(), is(true));
        mockCurrentDate(date(2011, 11, 16));
        assertThat(repeatMessageWithDaysApplicable(asList("Monday", "Tuesday", "Wednesday")).isApplicable(), is(true));
        mockCurrentDate(date(2011, 11, 17));
        assertThat(repeatMessageWithDaysApplicable(asList("Monday", "Tuesday", "Wednesday")).isApplicable(), is(false));
    }

    private CampaignMessageBuilder builder() {
        return new CampaignMessageBuilder();
    }

    private RepeatingCampaignMessage repeatMessageWithDaysApplicable(List<String> weekDays) {
        return builder().repeatingCampaignMessageForDaysApplicable("name", weekDays, "msgKey");
    }

}
