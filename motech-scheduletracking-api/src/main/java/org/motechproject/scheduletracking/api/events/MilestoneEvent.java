package org.motechproject.scheduletracking.api.events;

import org.motechproject.model.MotechEvent;
import org.motechproject.scheduletracking.api.domain.AlertEvent;
import org.motechproject.scheduletracking.api.events.constants.EventDataKey;
import org.motechproject.scheduletracking.api.events.constants.EventSubject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MilestoneEvent {
    private AlertEvent alertEvent;

    public MilestoneEvent(AlertEvent alertEvent) {
        this.alertEvent = alertEvent;
    }

    public MotechEvent toMotechEvent() {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(EventDataKey.WINDOW_NAME, alertEvent.getWindowName());
        parameters.put(EventDataKey.MILESTONE_NAME, alertEvent.getMilestoneName());
        Set<Map.Entry<String,String>> entries = alertEvent.getData().entrySet();
        for (Map.Entry<String,String> entry : entries) {
            parameters.put(entry.getKey(), entry.getValue());
        }
        return new MotechEvent(EventSubject.ENROLLED_ENTITY_MILESTONE_ALERT, parameters);
    }
}
