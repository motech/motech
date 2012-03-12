package org.motechproject.appointments.api.model;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.util.DateUtil;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

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
}
