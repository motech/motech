package org.motechproject.scheduletracking.api.events;

import org.motechproject.model.MotechEvent;
import org.motechproject.scheduletracking.api.domain.Alert;
import org.motechproject.scheduletracking.api.service.EventDataKeys;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MilestoneEvent {
    private Alert alert;
    public static final String WINDOW_NAME = "window.name";
    public static final String MILESTONE_NAME = "milestone.name";

    public MilestoneEvent(Alert alert) {
        this.alert = alert;
    }

    public MotechEvent toMotechEvent() {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(WINDOW_NAME, alert.getWindowName());
        parameters.put(MILESTONE_NAME, alert.getMilestoneName());
        Set<Map.Entry<String,String>> entries = alert.getData().entrySet();
        for (Map.Entry<String,String> entry : entries) {
            parameters.put(entry.getKey(), entry.getValue());
        }
        return new MotechEvent(EventDataKeys.ENROLLED_ENTITY_MILESTONE_ALERT, parameters);
    }
}
