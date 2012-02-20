package org.motechproject.appointments.api.dao;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.appointments.api.model.AppointmentCalendar;
import org.motechproject.appointments.api.model.Visit;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/applicationAppointmentsAPI.xml"})
public class AllAppointmentCalendarsIT {

	@Autowired
	private AllAppointmentCalendars allAppointmentCalendars;

	@Test
	public void testAddAppointment() {
        DateTime now = DateUtil.now();

		Appointment appointment = new Appointment().dueDate(now).scheduledDate(now);
        Visit visit = new Visit().visitDate(now).appointment(appointment);
        AppointmentCalendar appointmentCalendar = new AppointmentCalendar().externalId("externalId").addVisit(visit);

        allAppointmentCalendars.saveAppointmentCalendar(appointmentCalendar);

        assertNotNull(appointmentCalendar.getId());

        allAppointmentCalendars.remove(appointmentCalendar);
	}

    @Test
    public void testFindByExternalId() {
        Visit visit1 = new Visit().title("Appointment 1");
        Visit visit2 = new Visit().title("Appointment 2");

        AppointmentCalendar appointmentCalendar = new AppointmentCalendar().externalId("foo").addVisit(visit1).addVisit(visit2);
        allAppointmentCalendars.saveAppointmentCalendar(appointmentCalendar);

        AppointmentCalendar savedCalender = allAppointmentCalendars.findByExternalId("foo");
        assertNotNull(savedCalender);
        assertEquals(appointmentCalendar.getId(), savedCalender.getId());

        allAppointmentCalendars.remove(savedCalender);
    }
}
