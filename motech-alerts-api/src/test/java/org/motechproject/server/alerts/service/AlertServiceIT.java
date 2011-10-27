package org.motechproject.server.alerts.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.server.alerts.dao.AllAlerts;
import org.motechproject.server.alerts.domain.Alert;
import org.motechproject.server.alerts.domain.AlertStatus;
import org.motechproject.server.alerts.domain.AlertType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;

import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/applicationContextAlert.xml"})
public class AlertServiceIT {
    @Autowired
    AllAlerts allAlerts;
    @Autowired
    private AlertServiceImpl alertService;

    private Alert alert;

    @Before
    public void setUp() {
        List<Alert> list = allAlerts.listAlerts(null, null, null, null, 100);
        for (Alert alert : list) {
            allAlerts.remove(alert);
        }
    }

    @After
    public void tearDown
            () {
        if (alert != null) allAlerts.remove(alert);
    }

    @Test
    public void shouldRegisterSuccessfully() {
        HashMap<String, String> alertData = new HashMap<String, String>();
        alertData.put("Status", "Open");
        alertData.put("Note", "This is an Alert!");
        alert = new Alert("111", AlertType.CRITICAL, AlertStatus.NEW, 1, alertData);
        alertService.createAlert(alert);

        List<Alert> all = allAlerts.getAll();
        assertEquals(1, all.size());
        alert = all.get(0);
        assertEquals("111", alert.getExternalId());
        assertEquals("Open", alert.getData().get("Status"));
        assertEquals("This is an Alert!", alert.getData().get("Note"));
    }

    @Test
    public void shouldChangeStatus() {
        alert = new Alert("111", AlertType.CRITICAL, AlertStatus.NEW, 1, new HashMap<String, String>());
        alertService.createAlert(alert);
        alert = allAlerts.getAll().get(0);
        assertEquals(AlertStatus.NEW, alert.getStatus());

        alertService.changeStatus(alert.getId(), AlertStatus.CLOSED);

        alert = allAlerts.getAll().get(0);
        assertEquals(AlertStatus.CLOSED, alert.getStatus());
    }
}