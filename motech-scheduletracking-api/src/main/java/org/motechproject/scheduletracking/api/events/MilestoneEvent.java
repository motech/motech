package org.motechproject.scheduletracking.api.events;

import org.motechproject.model.MotechEvent;
import org.motechproject.scheduletracking.api.events.constants.EventDataKey;
import org.motechproject.scheduletracking.api.events.constants.EventSubject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MilestoneEvent {

    private String windowName;
    private String milestoneName;
    private String scheduleName;
    private String enrollmentId;

    private Map<String, String> data = new HashMap<String, String>();

    public MilestoneEvent(String enrollmentId, String scheduleName, String milestoneName, String windowName) {
        this.scheduleName = scheduleName;
        this.milestoneName = milestoneName;
        this.windowName = windowName;
        this.enrollmentId = enrollmentId;
    }

    public MotechEvent toMotechEvent() {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(EventDataKey.WINDOW_NAME, windowName);
        parameters.put(EventDataKey.MILESTONE_NAME, milestoneName);
        parameters.put(EventDataKey.ENROLLMENT_ID, milestoneName);
        Set<Map.Entry<String,String>> entries = data.entrySet();
        for (Map.Entry<String,String> entry : entries)
            parameters.put(entry.getKey(), entry.getValue());
        return new MotechEvent(EventSubject.MILESTONE_ALERT, parameters);
    }
}
