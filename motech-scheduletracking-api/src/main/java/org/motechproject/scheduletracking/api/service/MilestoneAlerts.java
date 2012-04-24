package org.motechproject.scheduletracking.api.service;

import org.joda.time.DateTime;
import org.motechproject.scheduletracking.api.domain.WindowName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MilestoneAlerts {
    private Map<String,List<DateTime>> alertTimings;

    public MilestoneAlerts() {
        alertTimings = new HashMap<String, List<DateTime>>();
    }

    public Map<String, List<DateTime>> getAlertTimings() {
        return alertTimings;
    }

    public List<DateTime> getEarliestWindowAlertTimings() {
        return alertTimings.get(WindowName.earliest.toString());
    }

    public List<DateTime> getDueWindowAlertTimings() {
        return alertTimings.get(WindowName.due.toString());
    }

    public List<DateTime> getLateWindowAlertTimings() {
        return alertTimings.get(WindowName.late.toString());
    }

    public List<DateTime> getMaxWindowAlertTimings() {
        return alertTimings.get(WindowName.max.toString());
    }
}
