package org.motechproject.server.alerts.service;

import org.ektorp.DocumentNotFoundException;
import org.motechproject.server.alerts.dao.AllAlerts;
import org.motechproject.server.alerts.domain.Alert;
import org.motechproject.server.alerts.domain.AlertStatus;
import org.motechproject.server.alerts.domain.AlertType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class AlertServiceImpl implements AlertService {
    private AllAlerts allAlerts;

    final Logger logger = LoggerFactory.getLogger(AlertServiceImpl.class);

    @Autowired
    public AlertServiceImpl(AllAlerts allAlerts) {
        this.allAlerts = allAlerts;
    }

    @Override
    public void register(String externalId, AlertType type, AlertStatus status, Integer priority) {
        Alert alert = new Alert();
        alert.setExternalId(externalId);
        alert.setAlertType(type);
        alert.setStatus(status);
        alert.setPriority(priority);

        allAlerts.add(alert);
    }

    @Override
    public List<Alert> getBy(String externalId, AlertType type, AlertStatus status, Integer priority, int limit) {
        return allAlerts.listAlerts(externalId, type, status, priority, limit);
    }

    @Override
    public void changeStatus(String id, AlertStatus status) {
        Alert alert = null;
        try {
            alert = allAlerts.get(id);
        } catch (DocumentNotFoundException e) {
            logger.error(String.format("No Alert found for the given id: {0}.", id), e);
            return;
        }
        alert.setStatus(status);
        allAlerts.update(alert);
    }
}
