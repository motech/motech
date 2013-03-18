package org.motechproject.scheduletracking.api.events;

import org.motechproject.event.MotechEvent;
import org.motechproject.scheduletracking.api.events.constants.EventDataKeys;
import org.motechproject.scheduletracking.api.events.constants.EventSubjects;

import java.util.HashMap;
import java.util.Map;

public class UnenrolledUserEvent {
    private String externalId;
    private String scheduleName;

    public UnenrolledUserEvent(String externalId, String scheduleName) {
        this.externalId = externalId;
        this.scheduleName = scheduleName;
    }

    public UnenrolledUserEvent(Map<String, Object> parameters) {
        this.externalId = parameters.get(EventDataKeys.EXTERNAL_ID).toString();
        this.scheduleName = parameters.get(EventDataKeys.SCHEDULE_NAME).toString();
    }

    public String getExternalId() {
        return externalId;
    }

    public String getScheduleName() {
        return scheduleName;
    }

    public MotechEvent toMotechEvent() {
        Map<String, Object> param = new HashMap<>();
        param.put(EventDataKeys.EXTERNAL_ID, externalId);
        param.put(EventDataKeys.SCHEDULE_NAME, scheduleName);
        return new MotechEvent(EventSubjects.USER_UNENROLLED, param);
    }
}
