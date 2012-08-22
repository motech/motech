package org.motechproject.server.alerts.domain;

import java.util.HashMap;
import java.util.Map;

public enum AlertUpdater {

    statusUpdater {
        @Override
        public Alert update(Alert alert, Object newValue) {
            alert.setStatus((AlertStatus) newValue);
            return alert;
        }
    }, nameUpdater {
        @Override
        public Alert update(Alert alert, Object newValue) {
            alert.setName((String) newValue);
            return alert;
        }
    }, descriptionUpdater {
        @Override
        public Alert update(Alert alert, Object newValue) {
            alert.setDescription((String) newValue);
            return alert;
        }
    }, priorityUpdater {
        @Override
        public Alert update(Alert alert, Object newValue) {
            alert.setPriority((Integer) newValue);
            return alert;
        }
    }, dataUpdater {
        @Override
        public Alert update(Alert alert, Object newValue) {
            alert.getData().putAll((Map<String, String>) newValue);
            return alert;
        }
    };

    public abstract Alert update(Alert alert, Object newValue);

    private static HashMap<UpdateCriterion, AlertUpdater> updateCriterionMap = new HashMap<UpdateCriterion, AlertUpdater>();

    static {
        updateCriterionMap.put(UpdateCriterion.status, AlertUpdater.statusUpdater);
        updateCriterionMap.put(UpdateCriterion.name, AlertUpdater.nameUpdater);
        updateCriterionMap.put(UpdateCriterion.description, AlertUpdater.descriptionUpdater);
        updateCriterionMap.put(UpdateCriterion.priority, AlertUpdater.priorityUpdater);
        updateCriterionMap.put(UpdateCriterion.data, AlertUpdater.dataUpdater);
    }

    public static AlertUpdater get(UpdateCriterion updateCriterion) {
        return updateCriterionMap.get(updateCriterion);
    }
}
