package org.motechproject.server.messagecampaign.userspecified;

import org.junit.Test;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.motechproject.server.messagecampaign.builder.CampaignMessageRecordBuilder.*;

public class CampaignMessageRecordTest {

    @Test
    public void shouldValidateRecordReturnTrue_IfRepeatIntervalIsSet_AndApplicableDaysOrCalendarWeekIsNotSet() {

        assertEquals(true, createRepeatingMessageRecordWithInterval("Message 1", "message-key", "1 Weeks").validate());
        assertEquals(true, createRepeatingMessageRecordWithInterval("Message 1", "message-key", "2 Days").validate());
    }

    @Test
    public void shouldValidateRecordReturnTrue_IfApplicableDaysOrCalendarWeekIsSet_AndRepeatIntervalIsNotSet() {

        CampaignMessageRecord record = createRepeatingMessageRecordWithWeekApplicableDays("Message 1", "message-key", asList("Mon", "Wed", "Fri"));
        assertEquals(true, record.validate());
        record = createRepeatingMessageRecordWithCalendarWeek("Message 1", "message-key", "Sun", asList("Mon", "Wed", "Fri"));
        assertEquals(true, record.validate());
    }

    @Test
    public void shouldValidateRecordReturnFalse_IfRepeatIntervalIsSet_AndApplicableDaysOrCalendarWeekIsSet() {

        CampaignMessageRecord record = new CampaignMessageRecord().weekDaysApplicable(asList("Mon", "Wed", "Fri"))
                                            .repeatInterval("1 Weeks");
        assertEquals(false, record.validate());
    }

    @Test
    public void shouldValidateRecordReturnFalse_IfRepeatInterval_AndApplicableDaysAndCalendarWeekIsNotSet() {

        CampaignMessageRecord record = new CampaignMessageRecord();
        assertEquals(false, record.validate());
    }

    @Test
    public void shouldValidateRecordReturnFalse_IfRepeatInterval_AndApplicableDaysAndCalendarWeekIsSet() {

        CampaignMessageRecord record = new CampaignMessageRecord().weekDaysApplicable(asList("Mon", "Wed", "Fri"));
                    record.calendarStartOfWeek("Sun").repeatInterval("1 Weeks");
        assertEquals(false, record.validate());
    }
}
