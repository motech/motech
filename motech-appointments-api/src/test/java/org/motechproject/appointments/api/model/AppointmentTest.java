package org.motechproject.appointments.api.model;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.util.DateUtil;

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

        appointment.adjustDueDate(tomorrow);
        assertEquals(today, appointment.originalDueDate());
        assertEquals(tomorrow, appointment.dueDate());
    }
}
