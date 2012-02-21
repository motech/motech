package org.motechproject.appointments.api.model;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;

public class AppointmentTest {

    @Test
    public void shouldSetIDOnAppointment() {
        assertTrue(StringUtils.isNotEmpty(new Appointment().id()));
    }
}
