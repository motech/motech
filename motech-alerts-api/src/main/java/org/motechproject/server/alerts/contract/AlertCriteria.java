package org.motechproject.server.alerts.contract;

import org.joda.time.DateTime;
import org.motechproject.server.alerts.domain.AlertStatus;
import org.motechproject.server.alerts.domain.AlertType;
import org.motechproject.server.alerts.domain.Criterion;

import java.util.ArrayList;
import java.util.List;

/**
 * \ingroup alerts
 * Maintains the criteria by which search for alerts to be done
 */
public class AlertCriteria {
    private String externalId;
    private AlertStatus alertStatus;
    private AlertType alertType;
    private DateTime fromDate;
    private DateTime toDate;
    private int alertPriority;

    private List<Criterion> orderedFilters;

    /**
     * Instantiates alert criteria
     */
    public AlertCriteria() {
        orderedFilters = new ArrayList<Criterion>();
    }

    /**
     * Gets all the search criteria that have been added so far
     *
     * @return List of search criteria
     */
    public List<Criterion> getFilters() {
        return orderedFilters;
    }

    /**
     * Adds criterion to search by external id of alert
     *
     * @param externalId external id of the alert to be searched for
     * @return Instance with the current criterion added to it.
     */
    public AlertCriteria byExternalId(String id) {
        this.externalId = id;
        this.orderedFilters.add(Criterion.externalId);
        return this;
    }

    public String externalId() {
        return this.externalId;
    }

    /**
     * Adds criterion to search by status of alert
     *
     * @param alertStatus status of the alert to be searched for
     * @return Instance with the current criterion added to it.
     */
    public AlertCriteria byStatus(AlertStatus status) {
        this.alertStatus = status;
        this.orderedFilters.add(Criterion.alertStatus);
        return this;
    }

    public AlertStatus alertStatus() {
        return this.alertStatus;
    }

    /**
     * Adds criterion to search by type of alert
     *
     * @param alertType type of the alert to be searched for
     * @return Instance with the current criterion added to it.
     */
    public AlertCriteria byType(AlertType type) {
        this.alertType = type;
        this.orderedFilters.add(Criterion.alertType);
        return this;
    }

    public AlertType alertType() {
        return this.alertType;
    }

    /**
     * Adds criterion to search by priority of alert
     *
     * @param alertPriority priority of the alert to be searched for
     * @return Instance with the current criterion added to it.
     */
    public AlertCriteria byPriority(int priority) {
        this.alertPriority = priority;
        this.orderedFilters.add(Criterion.alertPriority);
        return this;
    }

    public int alertPriority() {
        return this.alertPriority;
    }

    /**
     * Adds criterion to search by the date of alert in the given range
     *
     * @param fromDate Start date of the alerts to be searched for
     * @param toDate   End date of the alerts to be searched for
     * @return Instance with the current criterion added to it.
     */
    public AlertCriteria byDateRange(DateTime from, DateTime to) {
        this.fromDate = from;
        this.toDate = to;
        this.orderedFilters.add(Criterion.dateRange);
        return this;
    }

    public DateTime fromDate() {
        return fromDate;
    }

    public DateTime toDate() {
        return toDate;
    }
}
