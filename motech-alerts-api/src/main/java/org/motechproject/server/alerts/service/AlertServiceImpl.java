package org.motechproject.server.alerts.service;

import org.motechproject.server.alerts.dao.AllAlerts;
import org.motechproject.server.alerts.domain.Alert;
import org.motechproject.server.alerts.domain.AlertStatus;
import org.motechproject.server.alerts.domain.AlertType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class AlertServiceImpl implements AlertService {
    private AllAlerts allAlerts;

    @Autowired
    public AlertServiceImpl(AllAlerts allAlerts) {
        this.allAlerts = allAlerts;
    }

    @Override
    public List<Alert> getBy(String externalId, AlertType type, AlertStatus status, Integer priority, int limit) {
        return allAlerts.listAlerts(externalId, type, status, priority, limit);
    }
}
