package org.motechproject.server.alerts.service;

import org.motechproject.server.alerts.contract.UpdateCriteria;
import org.motechproject.server.alerts.domain.Alert;
import org.motechproject.server.alerts.domain.AlertCriteria;
import org.motechproject.server.alerts.domain.AlertStatus;
import org.motechproject.server.alerts.domain.AlertType;

import java.util.List;
import java.util.Map;

public interface AlertService {

    void create(String entityId, String name, String description, AlertType type, AlertStatus status, int priority, Map<String, String> data);

    List<Alert> search(AlertCriteria alertCriteria);

    Alert get(String id);

    void update(String alertId, UpdateCriteria updateCriteria);

}