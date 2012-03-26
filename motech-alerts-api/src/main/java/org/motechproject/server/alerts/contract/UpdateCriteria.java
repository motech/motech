package org.motechproject.server.alerts.contract;

import org.motechproject.server.alerts.domain.AlertStatus;

import java.util.HashMap;
import java.util.Map;

public class UpdateCriteria {

    private Map<UpdateCriterion, Object> allCriteria = new HashMap<UpdateCriterion, Object>();

    public UpdateCriteria() {
    }

    public Map<UpdateCriterion, Object> getAll() {
        return allCriteria;
    }

    public UpdateCriteria status(AlertStatus newStatus) {
        allCriteria.put(UpdateCriterion.status, newStatus);
        return this;
    }

    public UpdateCriteria name(String newName) {
        allCriteria.put(UpdateCriterion.name, newName);
        return this;
    }

    public UpdateCriteria description(String newDescription) {
        allCriteria.put(UpdateCriterion.description, newDescription);
        return this;
    }

    public UpdateCriteria priority(int newPriority) {
        allCriteria.put(UpdateCriterion.priority, newPriority);
        return this;
    }

    public UpdateCriteria data(HashMap<String, String> newData) {
        allCriteria.put(UpdateCriterion.data, newData);
        return this;
    }

}
