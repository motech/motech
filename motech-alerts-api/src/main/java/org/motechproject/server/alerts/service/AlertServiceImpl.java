package org.motechproject.server.alerts.service;

import org.ektorp.DocumentNotFoundException;
import org.motechproject.server.alerts.contract.UpdateCriteria;
import org.motechproject.server.alerts.contract.UpdateCriterion;
import org.motechproject.server.alerts.repository.AllAlerts;
import org.motechproject.server.alerts.domain.Alert;
import org.motechproject.server.alerts.domain.AlertCriteria;
import org.motechproject.server.alerts.domain.AlertStatus;
import org.motechproject.server.alerts.domain.AlertType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public class AlertServiceImpl implements AlertService {

    private AllAlerts allAlerts;
    private AlertFilter alertFilter;

    final Logger logger = LoggerFactory.getLogger(AlertServiceImpl.class);

    @Autowired
    public AlertServiceImpl(AllAlerts allAlerts, AlertFilter alertFilter) {
        this.allAlerts = allAlerts;
        this.alertFilter = alertFilter;
    }

    @Override
    public void create(String entityId, String name, String description, AlertType type, AlertStatus status, int priority, Map<String, String> data) {
        allAlerts.add(new Alert(entityId, name, description, type, status, priority, data));
    }

    @Override
    public List<Alert> search(AlertCriteria alertCriteria) {
        return alertFilter.search(alertCriteria);
    }

    @Override
    public Alert get(String id) {
        try {
            return allAlerts.get(id);
        } catch (DocumentNotFoundException e) {
            logger.error(String.format("No Alert found for the given id: %s.", id), e);
            return null;
        }
    }

    @Override
    public void update(String alertId, UpdateCriteria updateCriteria) {
        Alert alert = get(alertId);
        Map<UpdateCriterion, Object> all = updateCriteria.getAll();
        for (UpdateCriterion updateCriterion : all.keySet()) {
            AlertUpdater.get(updateCriterion).update(alert, all.get(updateCriterion));
        }
        allAlerts.update(alert);
    }
}
