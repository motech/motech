package org.motechproject.server;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.dao.PatientDao;
import org.motechproject.event.EventTypeRegistry;
import org.motechproject.model.Patient;
import org.motechproject.server.event.EventListener;
import org.motechproject.server.event.EventListenerRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/ApplicationContext.xml", "/testApplicationContext.xml" })
public class ScheduleAppointmentReminderTest {
	@Autowired
	private ScheduleAppointmentReminderHandler listener;
	@Autowired
	private EventListenerRegistry listenerRegistry;
//	@Autowired
//	private PatientDao patientDao;
	@Test
	public void testSubscription() {
		listenerRegistry.registerListener(listener,Arrays.asList(ScheduleAppointmentReminderEventType.getInstance()));
		listenerRegistry.getListeners(ScheduleAppointmentReminderEventType.getInstance());
		List<EventListener> listeners = listenerRegistry.getListeners(ScheduleAppointmentReminderEventType.getInstance());
		assertNotNull(listeners);
		assertEquals(1, listeners.size());
	}
//    @Test
//    public void testGetAll() throws Exception {
//
//        String id = "1234";
//
//        Patient patient = new Patient();
//        patient.setId(id);
//        patientDao.add(patient);
//
//        patient = patientDao.get(id);
//        System.out.print(patient);
//        assertNotNull(patient);
//        
//        patientDao.remove(patient);
//
//    }
}
