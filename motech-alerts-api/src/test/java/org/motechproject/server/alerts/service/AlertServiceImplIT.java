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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/applicationContextAlert.xml"})
public class AlertServiceImplIT {

    @Autowired
    AllAlerts allAlerts;
    @Autowired
    private AlertServiceImpl alertService;

    private List<Alert> createdAlerts;

    @Before
    public void setUp() {
        createdAlerts = new ArrayList<Alert>();
    }

    @After
    public void tearDown() {
        for (Alert alert : createdAlerts)
            allAlerts.remove(allAlerts.get(alert.getId()));
    }

    @Test
    public void shouldRegisterSuccessfully() {
        HashMap<String, String> alertData = new HashMap<String, String>();
        alertData.put("Status", "Open");
        alertData.put("Note", "This is an Alert!");
        createAlert(UUID.randomUUID().toString(), AlertType.CRITICAL, AlertStatus.NEW, 1, alertData);

        List<Alert> all = allAlerts.getAll();
        assertEquals(1, all.size());
        Alert alert = all.get(0);
        assertEquals("111", alert.getExternalId());
        assertEquals("Open", alert.getData().get("Status"));
        assertEquals("This is an Alert!", alert.getData().get("Note"));
    }

    @Test
    public void shouldChangeStatus() {
        createAlert(UUID.randomUUID().toString(), AlertType.CRITICAL, AlertStatus.NEW, 1, new HashMap<String, String>());
        Alert alert = allAlerts.getAll().get(0);
        assertEquals(AlertStatus.NEW, alert.getStatus());

        alertService.changeStatus(alert.getId(), AlertStatus.CLOSED);

        alert = allAlerts.getAll().get(0);
        assertEquals(AlertStatus.CLOSED, alert.getStatus());
    }

    private void createAlert(String externalId, AlertType critical, AlertStatus aNew, int priority, HashMap<String, String> alertData) {
        alertService.create(externalId, null, null, critical, aNew, priority, alertData);
        createdAlerts.add(alertService.get(externalId));
    }
}