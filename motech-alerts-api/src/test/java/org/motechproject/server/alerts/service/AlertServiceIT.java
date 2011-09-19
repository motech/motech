package org.motechproject.server.alerts.service;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.server.alerts.dao.AllAlerts;
import org.motechproject.server.alerts.domain.Alert;
import org.motechproject.server.alerts.domain.AlertStatus;
import org.motechproject.server.alerts.domain.AlertType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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

    @After
    public void tearDown() {
        if (alert != null) allAlerts.remove(alert);
    }

    @Test
    public void shouldRegisterSuccessfully() {
        Alert alert = new Alert("111", AlertType.CRITICAL, AlertStatus.NEW, 1);
        alertService.createAlert(alert);

        List<Alert> all = allAlerts.getAll();
        assertEquals(1, all.size());
        alert = all.get(0);
        assertEquals("111", alert.getExternalId());
    }

    @Test
    public void shouldChangeStatus() {
        Alert alert = new Alert("111", AlertType.CRITICAL, AlertStatus.NEW, 1);
        alertService.createAlert(alert);
        alert = allAlerts.getAll().get(0);
        assertEquals(AlertStatus.NEW, alert.getStatus());

        alertService.changeStatus(alert.getId(), AlertStatus.CLOSED);

        alert = allAlerts.getAll().get(0);
        assertEquals(AlertStatus.CLOSED, alert.getStatus());
    }
}