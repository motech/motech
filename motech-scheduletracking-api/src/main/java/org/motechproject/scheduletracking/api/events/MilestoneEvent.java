package org.motechproject.scheduletracking.api.events;

import org.motechproject.model.MotechEvent;
import org.motechproject.scheduletracking.api.domain.Alert;
import org.motechproject.scheduletracking.api.events.constants.EventDataKey;
import org.motechproject.scheduletracking.api.events.constants.EventSubject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MilestoneEvent {
    private Alert alert;

    public MilestoneEvent(Alert alert) {
        this.alert = alert;
    }

    public MotechEvent toMotechEvent() {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(EventDataKey.WINDOW_NAME, alert.getWindowName());
        parameters.put(EventDataKey.MILESTONE_NAME, alert.getMilestoneName());
        Set<Map.Entry<String,String>> entries = alert.getData().entrySet();
        for (Map.Entry<String,String> entry : entries) {
            parameters.put(entry.getKey(), entry.getValue());
        }
        return new MotechEvent(EventSubject.ENROLLED_ENTITY_MILESTONE_ALERT, parameters);
    }
}
