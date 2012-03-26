package org.motechproject.server.alerts.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.server.alerts.contract.UpdateCriteria;
import org.motechproject.server.alerts.repository.AllAlerts;
import org.motechproject.server.alerts.domain.Alert;
import org.motechproject.server.alerts.domain.AlertStatus;
import org.motechproject.server.alerts.domain.AlertCriteria;
import org.motechproject.server.alerts.domain.AlertType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/applicationContextAlert.xml"})
public class AlertServiceImplIT {

    @Autowired
    AllAlerts allAlerts;
    @Autowired
    private AlertServiceImpl alertService;

    private List<String> externalIds;

    @Before
    public void setUp() {
        externalIds = new ArrayList<String>();
    }

    @After
    public void tearDown() {
        for (String externalId : externalIds)
            allAlerts.remove(alertService.search(new AlertCriteria().byExternalId(externalId)).get(0));
    }

    @Test
    public void shouldRegisterSuccessfully() {
        HashMap<String, String> alertData = new HashMap<String, String>();
        alertData.put("Status", "Open");
        alertData.put("Note", "This is an Alert!");
        String externalId = UUID.randomUUID().toString();
        createAlert(externalId, AlertType.CRITICAL, null, AlertStatus.NEW, 1, "description", alertData);

        Alert alert = alertService.search(new AlertCriteria().byExternalId(externalId)).get(0);
        assertNotNull(alert);
        assertEquals(externalId, alert.getExternalId());
        assertEquals("Open", alert.getData().get("Status"));
        assertEquals("This is an Alert!", alert.getData().get("Note"));
    }

    @Test
    public void shouldChangeStatus() {
        String externalId = UUID.randomUUID().toString();
        createAlert(externalId, AlertType.CRITICAL, null, AlertStatus.NEW, 1, "description", new HashMap<String, String>());
        Alert alert = alertService.search(new AlertCriteria().byExternalId(externalId)).get(0);
        assertEquals(AlertStatus.NEW, alert.getStatus());

        alertService.changeStatus(alert.getId(), AlertStatus.CLOSED);

        alert = alertService.search(new AlertCriteria().byExternalId(externalId)).get(0);
        assertEquals(AlertStatus.CLOSED, alert.getStatus());
    }

    @Test
    public void shouldChangeDescription() {
        String externalId = UUID.randomUUID().toString();
        createAlert(externalId, AlertType.CRITICAL, null, AlertStatus.NEW, 1, "description", new HashMap<String, String>());
        Alert alert = alertService.search(new AlertCriteria().byExternalId(externalId)).get(0);
        assertEquals("description", alert.getDescription());

        alertService.setDescription(alert.getId(), "new description");

        alert = alertService.search(new AlertCriteria().byExternalId(externalId)).get(0);
        assertEquals("new description", alert.getDescription());
    }

    @Test
    public void shouldSetPriority() {
        String externalId = UUID.randomUUID().toString();
        createAlert(externalId, AlertType.CRITICAL, null, AlertStatus.NEW, 1, "description", new HashMap<String, String>());
        Alert alert = alertService.search(new AlertCriteria().byExternalId(externalId)).get(0);
        assertEquals(1, alert.getPriority());

        alertService.setPriority(alert.getId(), 2);

        alert = alertService.search(new AlertCriteria().byExternalId(externalId)).get(0);
        assertEquals(2, alert.getPriority());
    }

    @Test
    public void shouldSetName() {
        String externalId = UUID.randomUUID().toString();
        createAlert(externalId, AlertType.CRITICAL, null, AlertStatus.NEW, 1, "description", new HashMap<String, String>());
        Alert alert = alertService.search(new AlertCriteria().byExternalId(externalId)).get(0);

        alertService.setName(alert.getId(), "new name");

        alert = alertService.search(new AlertCriteria().byExternalId(externalId)).get(0);
        assertEquals("new name", alert.getName());
    }

    @Test
    public void shouldUpdateAlert() {
        String externalId = UUID.randomUUID().toString();
        createAlert(externalId, AlertType.CRITICAL, "name", AlertStatus.NEW, 1, "description", new HashMap<String, String>());
        Alert alert = alertService.search(new AlertCriteria().byExternalId(externalId)).get(0);

        HashMap<String, String> newData = new HashMap<String, String>();
        newData.put("newKey", "newValue");

        UpdateCriteria updateCriteria = new UpdateCriteria().status(AlertStatus.READ)
                                            .name("newName").description("newDescription")
                                            .priority(2).data(newData);
        alertService.update(alert.getId(), updateCriteria);

        Alert updatedAlert = alertService.search(new AlertCriteria().byExternalId(externalId)).get(0);
        assertEquals(AlertStatus.READ, updatedAlert.getStatus());
        assertEquals("newName", updatedAlert.getName());
        assertEquals("newDescription", updatedAlert.getDescription());
        assertEquals(2, updatedAlert.getPriority());
        assertEquals(newData, updatedAlert.getData());
    }

    private void createAlert(String externalId, AlertType critical, String name, AlertStatus aNew, int priority, String description, HashMap<String, String> alertData) {
        alertService.create(externalId, name, description, critical, aNew, priority, alertData);
        externalIds.add(externalId);
    }
}