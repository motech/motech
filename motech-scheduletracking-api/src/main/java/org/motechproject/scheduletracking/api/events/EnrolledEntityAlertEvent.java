package org.motechproject.scheduletracking.api.events;

import org.motechproject.model.MotechEvent;
import org.motechproject.scheduletracking.api.service.EventKeys;

import java.util.HashMap;
import java.util.Map;

public class EnrolledEntityAlertEvent {
    private Map<String, Object> map;
    private static final String SCHEDULE_NAME_KEY = "ScheduleName";
    private static final String ENROLLMENT_ID = "EnrollmentId";
    public static final String JOB_ID_KEY = "JobID";

    public EnrolledEntityAlertEvent(MotechEvent motechEvent) {
        map = motechEvent.getParameters();
    }

    public EnrolledEntityAlertEvent(String scheduleName, String externalId) {
        map = new HashMap<String, Object>();
        map.put(SCHEDULE_NAME_KEY, scheduleName);
        map.put(ENROLLMENT_ID, externalId);
        map.put(JOB_ID_KEY, externalId);
    }

    public MotechEvent toMotechEvent() {
        return new MotechEvent(EventKeys.ENROLLED_ENTITY_REGULAR_ALERT, map);
    }

    public String scheduleName() {
        return (String) map.get(SCHEDULE_NAME_KEY);
    }

    public String enrollmentId() {
        return (String) map.get(ENROLLMENT_ID);
    }
}
