package org.motechproject.server.alerts.dao;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.server.alerts.domain.Alert;
import org.motechproject.server.alerts.domain.AlertStatus;
import org.motechproject.server.alerts.domain.AlertType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/applicationContextAlert.xml"})
public class AllAlertsIT {
	
	@Autowired
	private AllAlerts allAlerts;
	
	private Alert alert = new Alert();
	
	@Before
	public void setUp() {
		alert.setName("Test Alert");
		alert.setDescription("Test alert Description");
		alert.setDateTime(new DateTime());
		alert.setExternalId("1212");
		alert.setPriority(2);
		alert.setStatus(AlertStatus.NEW);
		alert.setAlertType(AlertType.HIGH);
		allAlerts.add(alert);
	}
	
	@After
	public void tearDown() {
		//allAlerts.remove(alert);
	}

	@Test
	public void testListAlerts() {
		List<Alert> listAlerts = allAlerts.listAlerts("1212", AlertType.HIGH, null, null);
		assertNotNull(listAlerts);
		
	}

}
