package org.motechproject.server.alerts.domain;

import java.util.ArrayList;
import java.util.List;

public class AlertCriteria {

    private String externalId;
    private AlertStatus alertStatus;
    private AlertType alertType;
    private int alertPriority;

    public enum Criterion {
        externalId, alertStatus, alertType, alertPriority
    }

    private List<Criterion> orderedFilters;

    public AlertCriteria() {
        orderedFilters = new ArrayList<Criterion>();
    }

    public List<Criterion> getFilters() {
        return orderedFilters;
    }

    public AlertCriteria byExternalId(String externalId) {
        this.externalId = externalId;
        this.orderedFilters.add(Criterion.externalId);
        return this;
    }

    public String externalId() {
        return this.externalId;
    }

    public AlertCriteria byStatus(AlertStatus alertStatus) {
        this.alertStatus = alertStatus;
        this.orderedFilters.add(Criterion.alertStatus);
        return this;
    }

    public AlertStatus alertStatus() {
        return this.alertStatus;
    }

    public AlertCriteria byType(AlertType alertType) {
        this.alertType = alertType;
        this.orderedFilters.add(Criterion.alertType);
        return this;
    }

    public AlertType alertType() {
        return this.alertType;
    }

    public AlertCriteria byPriority(int alertPriority) {
        this.alertPriority = alertPriority;
        this.orderedFilters.add(Criterion.alertPriority);
        return this;
    }

    public int alertPriority() {
        return this.alertPriority;
    }
}
