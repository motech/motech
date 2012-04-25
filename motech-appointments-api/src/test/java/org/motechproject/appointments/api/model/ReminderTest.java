package org.motechproject.appointments.api.model;

import org.joda.time.DateTime;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.motechproject.util.DateUtil.newDateTime;

public class ReminderTest {

    @Test
    public void shouldTestEquality() {
        DateTime startDate = newDateTime(2012, 2, 1, 12, 3, 4);
        Reminder reminder1 = new Reminder().startDate(startDate.plusDays(2).toDate()).endDate(startDate.plusDays(2).toDate());
        Reminder reminder1Dup = new Reminder().startDate(startDate.plusDays(2).toDate()).endDate(startDate.plusDays(2).toDate());
        Reminder reminder2 = new Reminder().startDate(startDate.plusWeeks(5).toDate()).endDate(startDate.plusWeeks(8).toDate()).repeatCount(3).intervalSeconds(100000);
        Reminder reminder2Dup = new Reminder().startDate(startDate.plusWeeks(5).toDate()).endDate(startDate.plusWeeks(8).toDate()).repeatCount(3)
                .intervalSeconds(100000);

        assertTrue(reminder1.equals(reminder1Dup));
        assertTrue(reminder2.equals(reminder2Dup));

        assertTrue(new Reminder().equals(new Reminder()));
        assertTrue(new Reminder().startDate(startDate.plusDays(2).toDate()).equals(new Reminder().startDate(startDate.plusDays(2).toDate())));
        assertTrue(new Reminder().endDate(startDate.plusDays(2).toDate()).equals(new Reminder().endDate(startDate.plusDays(2).toDate())));

        assertFalse(new Reminder().repeatCount(2).equals(new Reminder()));
        assertFalse(new Reminder().startDate(startDate.plusDays(2).toDate()).equals(new Reminder().startDate(startDate.plusDays(3).toDate())));
        assertFalse(new Reminder().endDate(startDate.plusDays(2).toDate()).equals(new Reminder().endDate(startDate.plusDays(4).toDate())));
        assertFalse(new Reminder().intervalSeconds(100).equals(new Reminder().intervalSeconds(22)));
    }
}
