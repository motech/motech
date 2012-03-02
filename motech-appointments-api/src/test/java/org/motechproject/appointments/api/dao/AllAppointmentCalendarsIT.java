package org.motechproject.appointments.api.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.appointments.api.model.AppointmentCalendar;
import org.motechproject.appointments.api.model.Visit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
public class AllAppointmentCalendarsIT extends AppointmentsBaseIntegrationTest {

    @Autowired
    private AllAppointmentCalendars allAppointmentCalendars;

    @Test
    public void testSaveAppointmentCalender() {
        AppointmentCalendar appointmentCalendar = new AppointmentCalendar().externalId("externalId");

        allAppointmentCalendars.saveAppointmentCalendar(appointmentCalendar);

        assertNotNull(appointmentCalendar.getId());

        markForDeletion(appointmentCalendar);
    }

    @Test
    public void testFindByExternalId() {
        Visit visit1 = new Visit().name("Visit 1");
        Visit visit2 = new Visit().name("Visit 2");

        AppointmentCalendar appointmentCalendar = new AppointmentCalendar().externalId("foo").addVisit(visit1).addVisit(visit2);
        allAppointmentCalendars.saveAppointmentCalendar(appointmentCalendar);

        AppointmentCalendar savedCalender = allAppointmentCalendars.findByExternalId("foo");
        assertNotNull(savedCalender);
        assertEquals(appointmentCalendar.getId(), savedCalender.getId());

        markForDeletion(appointmentCalendar);
    }
}
