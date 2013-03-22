package org.motechproject.scheduletracking.api.service.impl;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.commons.date.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentActionService;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.commons.date.util.DateUtil.newDateTime;

public class EnrollmentActionServiceTest {

    @InjectMocks
    private EnrollmentActionService enrollmentActionService = new EnrollmentActionServiceImpl();

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
        String preferredAlertTime = "12:20";
        DateTime referenceDate = DateTime.now();
        String referenceTime = "16:10";
        DateTime enrollmentDate = DateTime.now();
        String enrollmentTime = "20:30";
        String startingMilestoneName = "startingMilestoneName";

        enrollmentActionService.enroll(externalId, scheduleName, preferredAlertTime, referenceDate, referenceTime, enrollmentDate, enrollmentTime, startingMilestoneName);

        ArgumentCaptor<EnrollmentRequest> captor = ArgumentCaptor.forClass(EnrollmentRequest.class);

        verify(scheduleTrackingService).enroll(captor.capture());

        EnrollmentRequest enrollmentRequest = captor.getValue();

        assertEquals(externalId, enrollmentRequest.getExternalId());
        assertEquals(scheduleName, enrollmentRequest.getScheduleName());
        assertEquals(new Time(preferredAlertTime), enrollmentRequest.getPreferredAlertTime());
        assertEquals(referenceDate.toLocalDate(), enrollmentRequest.getReferenceDate());
        assertEquals(new Time(referenceTime), enrollmentRequest.getReferenceTime());
        assertEquals(newDateTime(enrollmentDate.toLocalDate(), new Time(enrollmentTime)), enrollmentRequest.getEnrollmentDateTime());
        assertEquals(startingMilestoneName, enrollmentRequest.getStartingMilestoneName());

    }

    @Test
    public void shouldUnenrollFromEvent() {
        String externalId = "externalId";
        String scheduleName = "scheduleName";

        enrollmentActionService.unenroll(externalId, scheduleName);

        verify(scheduleTrackingService).unenroll(externalId, Arrays.asList(scheduleName));
    }
}