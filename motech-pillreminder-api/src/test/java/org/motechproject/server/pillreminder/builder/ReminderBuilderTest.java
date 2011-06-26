package org.motechproject.server.pillreminder.builder;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.server.pillreminder.contract.ReminderRequest;
import org.motechproject.server.pillreminder.domain.Reminder;

import static org.junit.Assert.assertTrue;

public class ReminderBuilderTest {

    private ReminderBuilder builder;

    @Before
    public void setUp() {
        builder = new ReminderBuilder();
    }

    @Test
    public void shouldReturnAReminderBuiltFromReminderRequest() {
        Integer hour = 1;
        Integer minute = 30;
        Integer repeatSize = 5;
        Integer repeatInterval = 300;
        ReminderRequest reminderRequest = new ReminderRequest(hour, minute, repeatSize, repeatInterval);

        Reminder reminder = builder.createFrom(reminderRequest);
        Reminder expectedReminder = new Reminder(hour, minute, repeatSize, repeatInterval);
        assertTrue(expectedReminder.equals(reminder));
    }
}
