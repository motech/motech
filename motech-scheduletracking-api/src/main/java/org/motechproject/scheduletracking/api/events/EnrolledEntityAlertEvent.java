package org.motechproject.scheduletracking.api.events;

import org.motechproject.model.MotechEvent;
import org.motechproject.scheduletracking.api.events.constants.EventDataKey;
import org.motechproject.scheduletracking.api.events.constants.EventSubject;

import java.util.HashMap;
import java.util.Map;

public class EnrolledEntityAlertEvent {
	private Map<String, Object> map;

	public EnrolledEntityAlertEvent(MotechEvent motechEvent) {
		map = motechEvent.getParameters();
	}

	public EnrolledEntityAlertEvent(String scheduleName, String externalId) {
		map = new HashMap<String, Object>();
		map.put(EventDataKey.SCHEDULE_NAME, scheduleName);
		map.put(EventDataKey.ENROLLMENT_ID, externalId);
		map.put(EventDataKey.JOB_ID, externalId);
	}

	public MotechEvent toMotechEvent() {
		return new MotechEvent(EventSubject.ENROLLED_ENTITY_REGULAR_ALERT, map);
	}

	public String scheduleName() {
		return (String) map.get(EventDataKey.SCHEDULE_NAME);
	}

	public String enrollmentId() {
		return (String) map.get(EventDataKey.ENROLLMENT_ID);
	}
}
