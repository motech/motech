package org.motechproject.server.messagecampaign.domain.message;

import org.junit.Test;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.motechproject.server.messagecampaign.builder.CampaignMessageRecordBuilder.*;
import static org.motechproject.server.messagecampaign.domain.message.RepeatingMessageMode.*;

public class RepeatingMessageModeTest {

    @Test
    public void shouldReturnModeBasedOnScheduleParameter() {

        assertEquals(REPEAT_INTERVAL , findMode(createRepeatingMessageRecordWithInterval("n", "msgKey", "9 Weeks")));
        assertEquals(WEEK_DAYS_SCHEDULE , findMode(createRepeatingMessageRecordWithWeekApplicableDays("n", "msgKey", asList("Friday"))));
        assertEquals(CALENDAR_WEEK_SCHEDULE , findMode(createRepeatingMessageRecordWithCalendarWeek("n", "msgKey", "Sunday", asList("Friday"))));
        assertEquals(CALENDAR_WEEK_SCHEDULE , findMode(createRepeatingMessageRecordWithCalendarWeek("n", "msgKey", "Sunday", null)));
    }
}
