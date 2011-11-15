package org.motechproject.server.messagecampaign.domain.message;

import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.Arrays;

import static junit.framework.Assert.fail;
import static org.junit.Assert.assertThat;
import static org.motechproject.server.messagecampaign.domain.message.RepeatingCampaignMessage.DAILY_REPEAT_INTERVAL;

public class RepeatingCampaignMessageTest {

    @Test
    public void shouldCreateRepeatingCampaignMessageWithEitherRepeatIntervalOrWeekDaysApplicableAndNotBoth() {
        try {
            new RepeatingCampaignMessage(null, null);
            fail();
        } catch (IllegalArgumentException e) {
        }

        try {
            new RepeatingCampaignMessage("7", Arrays.asList("Monday", "Tuesday"));
            fail();
        } catch (IllegalArgumentException e) {
        }
        
        try {
            new RepeatingCampaignMessage(null, Arrays.asList("Monday", "Tuesday"));
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
        assertThat(new RepeatingCampaignMessage("2 Weeks", null).repeatIntervalInDays(), Matchers.is(14));
        assertThat(new RepeatingCampaignMessage("9 Days", null).repeatIntervalInDays(), Matchers.is(9));
    }

    @Test
    public void shouldReturn_1_WhenWeekDaysApplicableIsSet() {
        assertThat(new RepeatingCampaignMessage(null, Arrays.asList("Monday", "Tuesday")).repeatIntervalInDays(), Matchers.is(DAILY_REPEAT_INTERVAL));
    }
}
