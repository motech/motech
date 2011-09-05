package org.motechproject.server.alerts.service;

import org.motechproject.server.alerts.domain.Alert;
import org.motechproject.server.alerts.domain.AlertStatus;
import org.motechproject.server.alerts.domain.AlertType;

import java.util.List;

public interface AlertService {
    void register(String externalId, AlertType type, AlertStatus status, Integer priority);
    List<Alert> getBy(String externalId, AlertType type, AlertStatus status, Integer priority, int limit);
    void changeStatus(String id, AlertStatus status);
}