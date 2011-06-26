package org.motechproject.server.pillreminder.domain;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ReminderTest {

    @Test
    public void shouldTestEquality() {
        Reminder reminder = new Reminder(2,30,4,30);
        Reminder anotherReminder = new Reminder(1,30,3,35);
        Reminder similarReminder = new Reminder(2,30,4,30);

        assertFalse(reminder.equals(null));
        assertFalse(reminder.equals(""));
        assertFalse(reminder.equals(anotherReminder));

        assertTrue(reminder.equals(reminder));
        assertTrue(reminder.equals(similarReminder));
    }
}
