package org.motechproject.scheduletracking.api.service.impl;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.commons.date.model.Time;
import org.motechproject.scheduletracking.api.events.constants.EventDataKeys;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.commons.date.util.DateUtil.newDateTime;

public class EnrollmentActionServiceTest {

    @InjectMocks
    private EnrollmentActionService enrollmentActionService = new EnrollmentActionService();

    @Mock
    ScheduleTrackingService scheduleTrackingService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldEnrollFromEvent() {
        String externalId = "externalId";
        String scheduleName = "scheduleName";
        Time preferredAlertTime = new Time(12, 20);
        LocalDate referenceDate = new LocalDate();
        Time referenceTime = new Time(16, 10);
        LocalDate enrollmentDate = new LocalDate();
        Time enrollmentTime = new Time(20, 30);
        String startingMilestoneName = "startingMilestoneName";
        Map<String, String> metadata = new HashMap<>();
        metadata.put("key", "value");

        Map<String, Object> eventData = new HashMap<>();
        eventData.put(EventDataKeys.EXTERNAL_ID, externalId);
        eventData.put(EventDataKeys.SCHEDULE_NAME, scheduleName);
        eventData.put(EventDataKeys.PREFERRED_ALERT_TIME, preferredAlertTime);
        eventData.put(EventDataKeys.REFERENCE_DATE, referenceDate);
        eventData.put(EventDataKeys.REFERENCE_TIME, referenceTime);
        eventData.put(EventDataKeys.ENROLLMENT_DATE, enrollmentDate);
        eventData.put(EventDataKeys.ENROLLMENT_TIME, enrollmentTime);
        eventData.put(EventDataKeys.MILESTONE_NAME, startingMilestoneName);

        enrollmentActionService.enroll(eventData);

        ArgumentCaptor<EnrollmentRequest> captor = ArgumentCaptor.forClass(EnrollmentRequest.class);

        verify(scheduleTrackingService).enroll(captor.capture());

        EnrollmentRequest enrollmentRequest = captor.getValue();

        assertEquals(eventData.get(EventDataKeys.EXTERNAL_ID), enrollmentRequest.getExternalId());
        assertEquals(eventData.get(EventDataKeys.SCHEDULE_NAME), enrollmentRequest.getScheduleName());
        assertEquals(eventData.get(EventDataKeys.PREFERRED_ALERT_TIME), enrollmentRequest.getPreferredAlertTime());
        assertEquals(eventData.get(EventDataKeys.REFERENCE_DATE), enrollmentRequest.getReferenceDate());
        assertEquals(eventData.get(EventDataKeys.REFERENCE_TIME), enrollmentRequest.getReferenceTime());
        assertEquals(newDateTime(enrollmentDate, enrollmentTime), enrollmentRequest.getEnrollmentDateTime());
        assertEquals(eventData.get(EventDataKeys.MILESTONE_NAME), enrollmentRequest.getStartingMilestoneName());

    }

    @Test
    public void shouldUnenrollFromEvent() {
        String externalId = "externalId";
        String scheduleName = "scheduleName";

        Map<String, Object> eventData = new HashMap<>();
        eventData.put(EventDataKeys.EXTERNAL_ID, externalId);
        eventData.put(EventDataKeys.SCHEDULE_NAME, scheduleName);

        enrollmentActionService.unenroll(eventData);

        verify(scheduleTrackingService).unenroll(externalId, Arrays.asList(scheduleName));
    }
}