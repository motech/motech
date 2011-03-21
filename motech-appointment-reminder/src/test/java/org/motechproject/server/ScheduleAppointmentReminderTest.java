package org.motechproject.server;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.server.event.EventListenerRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/testApplicationContext.xml" })
public class ScheduleAppointmentReminderTest {
	@Autowired
	private ScheduleAppointmentReminderHandler handler;
	@Autowired
	private EventListenerRegistry registry;
	@Test
	public void testSubscription() {
		registry.registerListener(handler, Arrays.asList(ScheduleAppointmentReminderEventType.getInstance()));
		System.out.print(handler.getIdentifier());
	}

}
