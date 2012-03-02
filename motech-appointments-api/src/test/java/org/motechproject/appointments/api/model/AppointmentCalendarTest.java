package org.motechproject.appointments.api.model;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

public class AppointmentCalendarTest {

    private AppointmentCalendar appointmentCalendar;
    private Visit baselineVisit;
    private Visit week2Visit;

    @Before
    public void setUp() {
        baselineVisit = new Visit().name("baseline");
        week2Visit = new Visit().name("week2").appointment(new Appointment());
        
        appointmentCalendar = new AppointmentCalendar().externalId("externalId");
        appointmentCalendar.addVisit(baselineVisit).addVisit(week2Visit);
    }
    
    @Test
    public void shouldGetVisit(){
        assertEquals(baselineVisit, appointmentCalendar.getVisit("BaseLINE"));
        assertEquals(week2Visit, appointmentCalendar.getVisit("week2"));
        assertNull(appointmentCalendar.getVisit("week4"));
    }
}
