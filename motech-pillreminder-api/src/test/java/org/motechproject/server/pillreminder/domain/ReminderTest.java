package org.motechproject.server.pillreminder.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ReminderTest {

    @Test
    public void shouldTestAccessors(){
        Reminder reminder = new Reminder();
        reminder.setHour(1);
        assertEquals(new Integer(1), reminder.getHour());
        reminder.setMinute(60);
        assertEquals(new Integer(60), reminder.getMinute());
        reminder.setId("1234");
        assertEquals("1234",reminder.getId());
        reminder.setRepeatInterval(5);
        assertEquals(new Integer(5),reminder.getRepeatInterval());
        reminder.setRepeatSize(5);
        assertEquals(new Integer(5),reminder.getRepeatSize());
    }
}
