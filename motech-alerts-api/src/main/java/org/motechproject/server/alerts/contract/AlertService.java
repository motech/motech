package org.motechproject.server.alerts.contract;

import org.motechproject.server.alerts.domain.Alert;
import org.motechproject.server.alerts.domain.AlertStatus;
import org.motechproject.server.alerts.domain.AlertType;

import java.util.List;
import java.util.Map;

/**
 * \defgroup alerts Alerts
 */

/**
 * \ingroup alerts
 * Interface to handle alerts
 */
public interface AlertService {
    /**
     * Creates an alert
     *
     * @param entityId    External id for alert
     * @param name        Name of the alert
     * @param description Description of alert
     * @param type        Alert type like critical, high, medium and low
     * @param status      Status of the alert like new, read and closed
     * @param priority    priority of the alert specified by integer values
     * @param data        Extra information of the alert stored as property => value pairs
     */
    void create(String entityId, String name, String description, AlertType type, AlertStatus status, int priority, Map<String, String> data);

    /**
     * Searches for alerts by the given criteria
     *
     * @param alertCriteria Criteria which is to be used to search for alerts
     * @return List of matched alerts for the given search criteria.
     */
    List<Alert> search(AlertCriteria alertCriteria);

    /**
     * Fetches an alert by id
     *
     * @param id Id of the alert
     * @return Alert object with the given id if found
     */
    Alert get(String id);

    /**
     * Updates an alert by alert id
     *
     * @param alertId        Id of the alert to be updated
     * @param updateCriteria criteria which specifies the fields to be updated and their new values
     */
    void update(String alertId, UpdateCriteria updateCriteria);
}
