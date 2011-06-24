package com.motechproject.server.pillreminder.domain;

import org.junit.Test;

import static com.motechproject.server.pillreminder.util.TestUtil.newDate;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ReminderTest {

    @Test
    public void shouldTestEquality() {
        Reminder reminder = new Reminder(newDate(2011, 1, 1));

        assertFalse(reminder.equals(null));
        assertFalse(reminder.equals(""));
        assertFalse(reminder.equals(new Reminder()));

        assertTrue(reminder.equals(reminder));
        assertTrue(reminder.equals(new Reminder(newDate(2011, 1, 1))));
    }

}
