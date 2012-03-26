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

    void changeStatus(String id, AlertStatus status);

    public void setDescription(String id, String description);

    void setPriority(String id, int priority);

    void setName(String id, String name);

    void setData(String id, String key, String value);

    void update(String alertId, UpdateCriteria updateCriteria);
}