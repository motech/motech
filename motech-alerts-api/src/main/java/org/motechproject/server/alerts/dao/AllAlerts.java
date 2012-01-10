package org.motechproject.server.alerts.dao;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.server.alerts.domain.Alert;
import org.motechproject.server.alerts.domain.AlertStatus;
import org.motechproject.server.alerts.domain.AlertType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AllAlerts extends MotechBaseRepository<Alert> {
    @Autowired
    public AllAlerts(@Qualifier("alertDbConnector") CouchDbConnector db) {
        super(Alert.class, db);
    }

    private List<Alert> getAllAlerts(int limit) {
        return getAll(limit);
    }

    @GenerateView
    public List<Alert> findByExternalId(String externalId) {
        return queryView("by_externalId", externalId);
    }

    @GenerateView
    public List<Alert> findByAlertType(AlertType alertType) {
        return queryView("by_alertType", alertType.toString());
    }

    @GenerateView
    public List<Alert> findByStatus(AlertStatus alertStatus) {
        return queryView("by_status", alertStatus.toString());
    }

    @GenerateView
    public List<Alert> findByPriority(int priority) {
        return queryView("by_priority", priority);
    }

    public List<Alert> listAlerts(String externalId, AlertType alertType, AlertStatus alertStatus, Integer alertPriority, int limit) {
        List<Alert> alerts = null;

        if (noFilters(externalId, alertType, alertStatus, alertPriority)) {
            List<Alert> allAlerts = getAllAlerts(limit);
            Collections.sort(allAlerts);
            return allAlerts;
        }

        if (externalId != null)
            alerts = addToAlertList(alerts, findByExternalId(externalId));

        if (alertType != null)
            alerts = addToAlertList(alerts, findByAlertType(alertType));

        if (alertStatus != null)
            alerts = addToAlertList(alerts, findByStatus(alertStatus));

        if (alertPriority != null)
            alerts = addToAlertList(alerts, findByPriority(alertPriority));

        Collections.sort(alerts);
        return alerts;
    }

    private List<Alert> addToAlertList(List<Alert> alerts, List<Alert> alertListToRetain) {
        if (noFilterAppliedYet(alerts))
            alerts = new ArrayList<Alert>(alertListToRetain);
        else
            alerts.retainAll(alertListToRetain);

        return alerts;
    }

    private boolean noFilterAppliedYet(List<Alert> alerts) {
        return alerts == null;
    }

    private boolean noFilters(String externalId, AlertType alertType, AlertStatus alertStatus, Integer alertPriority) {
        return externalId == null && alertType == null && alertStatus == null && alertPriority == null;
    }
}