package org.motechproject.scheduletracking.api.events;

import org.junit.Test;
import org.motechproject.model.MotechEvent;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduletracking.api.events.constants.EventDataKey;
import org.motechproject.scheduletracking.api.events.constants.EventSubject;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class DefaultmentCaptureEventTest {
    @Test
    public void shouldCreateMotechEvent() {
        String enrollmentId = "enrollmentId";
        String jobId = "jobId";

        DefaultmentCaptureEvent defaultmentCaptureEvent = new DefaultmentCaptureEvent(enrollmentId, jobId);
        MotechEvent motechEvent = defaultmentCaptureEvent.toMotechEvent();

        assertEquals(EventSubject.DEFAULTMENT_CAPTURE, motechEvent.getSubject());
        Map<String, Object> parameters = motechEvent.getParameters();
        assertEquals(enrollmentId, parameters.get(EventDataKey.ENROLLMENT_ID));
        assertEquals(jobId, parameters.get(MotechSchedulerService.JOB_ID_KEY));
    }
}
