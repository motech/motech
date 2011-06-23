package com.motechproject.server.pillreminder.domain;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static com.motechproject.server.pillreminder.util.TestUtil.getDate;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ReminderTest {

    @Test
    public void shouldTestEquality() {
        Reminder reminder = new Reminder(getDate(2011, 1, 1));

        assertFalse(reminder.equals(null));
        assertFalse(reminder.equals(""));
        assertFalse(reminder.equals(new Reminder()));

        assertTrue(reminder.equals(reminder));
        assertTrue(reminder.equals(new Reminder(getDate(2011, 1, 1))));
    }

}
