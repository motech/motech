package org.motechproject.scheduletracking.api.events;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.commons.date.model.Time;
import org.motechproject.event.MotechEvent;
import org.motechproject.scheduletracking.api.events.constants.EventDataKeys;
import org.motechproject.scheduletracking.api.events.constants.EventSubjects;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class EnrolledUserEventTest {

    @Test
    public void shouldCreateEnrollEvent() {
        String externalId = "externalId";
        String scheduleName = "scheduleName";
        String startingMilestoneName = "startingMilestoneName";
        Time preferredAlertTime = new Time(5, 20);
        DateTime referenceDateTime = DateTime.now();
        DateTime enrollmentDateTime = DateTime.now().plusDays(1);
        Map<String, String> metadata = new HashMap<>();
        metadata.put("key1", "value1");
        metadata.put("key2", "value2");

        EnrolledUserEvent enrolledUserEvent = new EnrolledUserEvent(externalId, scheduleName, preferredAlertTime, referenceDateTime, enrollmentDateTime, startingMilestoneName);
        MotechEvent event = enrolledUserEvent.toMotechEvent();

        assertEquals(EventSubjects.USER_ENROLLED, event.getSubject());
        assertEquals(enrolledUserEvent.getExternalId(), event.getParameters().get(EventDataKeys.EXTERNAL_ID));
        assertEquals(enrolledUserEvent.getScheduleName(), event.getParameters().get(EventDataKeys.SCHEDULE_NAME));
        assertEquals(enrolledUserEvent.getStartingMilestoneName(), event.getParameters().get(EventDataKeys.MILESTONE_NAME));
        assertEquals(enrolledUserEvent.getPreferredAlertTime(), event.getParameters().get(EventDataKeys.PREFERRED_ALERT_TIME));
        assertEquals(enrolledUserEvent.getReferenceDate(), event.getParameters().get(EventDataKeys.REFERENCE_DATE));
        assertEquals(enrolledUserEvent.getReferenceTime(), event.getParameters().get(EventDataKeys.REFERENCE_TIME));
        assertEquals(enrolledUserEvent.getEnrollmentDate(), event.getParameters().get(EventDataKeys.ENROLLMENT_DATE));
        assertEquals(enrolledUserEvent.getEnrollmentTime(), event.getParameters().get(EventDataKeys.ENROLLMENT_TIME));
    }

}
