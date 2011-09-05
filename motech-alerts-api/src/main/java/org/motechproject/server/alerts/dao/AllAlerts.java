package org.motechproject.server.alerts.dao;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
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
        initStandardDesignDocument();
    }

    @View(name = "all", map = "function(doc) {if (doc.type == 'Alert') {emit(null, doc._id);}}")
    private List<Alert> getAll(int limit) {
        ViewQuery q = createQuery("all").limit(limit).includeDocs(true);
        return db.queryView(q, Alert.class);
    }

    @View(name = "findByExternalId", map = "function(doc) {if (doc.type == 'Alert') {emit(doc.externalId, doc._id);}}")
    private List<Alert> findByExternalId(String externalId) {
        ViewQuery q = createQuery("findByExternalId").key(externalId).includeDocs(true);
        return db.queryView(q, Alert.class);
    }

    @View(name = "findByAlertType", map = "function(doc) {if (doc.type == 'Alert') {emit(doc.alertType, doc._id);}}")
    private List<Alert> findByAlertType(AlertType alertType) {
        ViewQuery q = createQuery("findByAlertType").key(alertType).includeDocs(true);
        return db.queryView(q, Alert.class);
    }

    @View(name = "findByAlertStatus", map = "function(doc) {if (doc.type == 'Alert') {emit(doc.status, doc._id);}}")
    private List<Alert> findByAlertStatus(AlertStatus alertStatus) {
        ViewQuery q = createQuery("findByAlertStatus").key(alertStatus).includeDocs(true);
        return db.queryView(q, Alert.class);
    }

    @View(name = "findByAlertPriority", map = "function(doc) {if (doc.type == 'Alert') {emit(doc.priority, doc._id);}}")
    private List<Alert> findByAlertPriority(int priority) {
        ViewQuery q = createQuery("findByAlertPriority").key(priority).includeDocs(true);
        return db.queryView(q, Alert.class);
    }

    public List<Alert> listAlerts(String externalId, AlertType alertType, AlertStatus alertStatus, Integer alertPriority, int limit) {
        List<Alert> alerts = null;

        if (noFilters(externalId, alertType, alertStatus, alertPriority)) {
            List<Alert> allAlerts = getAll(limit);
            Collections.sort(allAlerts);
            return allAlerts;
        }

        if (externalId != null)
            alerts = addToAlertList(alerts, findByExternalId(externalId));

        if (alertType != null)
            alerts = addToAlertList(alerts, findByAlertType(alertType));

        if (alertStatus != null)
            alerts = addToAlertList(alerts, findByAlertStatus(alertStatus));

        if (alertPriority != null)
            alerts = addToAlertList(alerts, findByAlertPriority(alertPriority));

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