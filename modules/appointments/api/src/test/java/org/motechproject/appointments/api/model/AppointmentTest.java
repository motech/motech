package org.motechproject.appointments.api.model;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.commons.date.util.DateUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class AppointmentTest {

    @Test
    public void shouldSetIDOnAppointment() {
        assertTrue(StringUtils.isNotEmpty(new Appointment().id()));
    }

    @Test
    public void shouldSetOriginalDueDateOnAppointment() {
        DateTime now = DateUtil.now();
        Appointment appointment = new Appointment().dueDate(now);

        assertEquals(now, appointment.originalDueDate());
        assertEquals(now, appointment.dueDate());
    }

    @Test
    public void shouldAdjustDueDateOnAppointment() {
        DateTime today = DateUtil.now();
        DateTime tomorrow = today.plusDays(1);
        Appointment appointment = new Appointment().dueDate(today);

        List<Reminder> reminders = Arrays.asList(new Reminder());
        appointment.adjustDueDate(tomorrow, reminders);
        assertEquals(reminders, appointment.reminders());
        assertEquals(1, appointment.reminders().size());
        assertEquals(today, appointment.originalDueDate());
        assertEquals(tomorrow, appointment.dueDate());
    }

    @Test
    public void shouldTestIsSame() {
        DateTime today = DateUtil.now();
        Reminder reminder1 = new Reminder().startDate(today.plusDays(2).toDate()).endDate(today.plusDays(2).toDate());
        Reminder reminder2 = new Reminder().startDate(today.plusWeeks(5).toDate()).endDate(today.plusWeeks(8).toDate()).repeatCount(3)
                      .intervalSeconds(100000);

        Appointment appointment = new Appointment().dueDate(today.plus(2)).confirmedDate(today.plus(4))
                .adjustDueDate(today.plusDays(3), asList(reminder1, reminder2));
        appointment.addData("key", 3);
        Appointment duplicateAppointment = new Appointment().dueDate(today.plus(2)).confirmedDate(today.plus(4))
                .adjustDueDate(today.plusDays(3), asList(reminder1, reminder2));
        duplicateAppointment.addData("key", 3);

        assertTrue(appointment.isSame(duplicateAppointment));
        assertTrue(new Appointment().isSame(new Appointment()));
        assertTrue(new Appointment().dueDate(today).isSame(new Appointment().dueDate(today)));
        assertTrue(new Appointment().confirmedDate(today.plusWeeks(2)).isSame(new Appointment().confirmedDate(today.plusWeeks(2))));
        assertTrue(new Appointment().reminders(asList(reminder1)).isSame(new Appointment().reminders(asList(reminder1))));
        assertTrue(new Appointment().addData(new HashMap()).isSame(new Appointment()));

        assertFalse(new Appointment().dueDate(today).isSame(new Appointment()));
        assertFalse(new Appointment().confirmedDate(today.plusWeeks(5)).isSame(new Appointment().dueDate(today)));
        assertFalse(new Appointment().reminders(asList(reminder2)).isSame(new Appointment().reminders(asList(reminder1))));
        assertFalse(new Appointment().reminders(asList(reminder2)).isSame(new Appointment().reminders(asList(reminder1))));
        assertFalse(new Appointment().addData("key", "value").isSame(new Appointment()));
    }
}
