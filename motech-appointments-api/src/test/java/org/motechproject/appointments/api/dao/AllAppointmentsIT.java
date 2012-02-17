package org.motechproject.appointments.api.dao;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.appointments.api.model.Visit;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/applicationAppointmentsAPI.xml"})
public class AllAppointmentsIT {

	@Autowired
	private AllAppointments allAppointments;

	@Test
	public void testAddAppointment() {
        DateTime now = DateUtil.now();

        Visit visit = new Visit();
        visit.setVisitDate(now);

		Appointment app = new Appointment();
        app.setDueDate(now);
        app.setExternalId("foo");
        app.setScheduledDate(now);

        allAppointments.add(app);

        assertNotNull(app.getId());

        allAppointments.remove(app);
	}

    @Test
    public void testFindByExternalId() {
        Appointment app1 = new Appointment();
        app1.setExternalId("foo");
        app1.setTitle("Appointment 1");
        allAppointments.add(app1);

        Appointment app2 = new Appointment();
        app2.setTitle("Appointment 2");
        app2.setExternalId("foo");
        allAppointments.add(app2);

        List<Appointment> list = allAppointments.findByExternalId("foo");

        assertEquals(2, list.size());

        allAppointments.remove(app1);
        allAppointments.remove(app2);
    }
}
