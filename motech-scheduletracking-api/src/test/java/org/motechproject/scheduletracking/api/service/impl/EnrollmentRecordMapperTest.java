package org.motechproject.scheduletracking.api.service.impl;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.WindowName;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.util.DateUtil;

import java.util.List;

import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.util.DateUtil.newDateTime;

public class EnrollmentRecordMapperTest {

    private EnrollmentRecordMapper enrollmentRecordMapper;

    @Mock
    private EnrollmentService enrollmentService;

    @Before
    public void setup() {
        initMocks(this);
        enrollmentRecordMapper = new EnrollmentRecordMapper(enrollmentService);
    }

    @Test
    public void shouldMapEnrollmentToEnrollmentResponse(){
        final Enrollment enrollment = new Enrollment("externalId", "scheduleName", null, DateUtil.newDateTime(2000, 2, 1, 0, 0, 0), DateUtil.newDateTime(2000, 2, 10, 0, 0, 0), new Time(10, 10), null);
        final EnrollmentRecord record = enrollmentRecordMapper.map(enrollment);
        assertThat(record.getExternalId(), is(equalTo(enrollment.getExternalId())));
        assertThat(record.getScheduleName(), is(equalTo(enrollment.getScheduleName())));
        assertThat(record.getReferenceDateTime(), is(equalTo(enrollment.getReferenceDateTime())));
        assertThat(record.getPreferredAlertTime(), is(equalTo(enrollment.getPreferredAlertTime())));
        assertThat(record.getEnrollmentDateTime(), is(equalTo(enrollment.getEnrollmentDateTime())));

        assertThat(new EnrollmentRecordMapper(enrollmentService).map(null), is(equalTo(null)));
    }


    @Test
    public void shouldMapEnrollmentToEnrollmentResponseAndPopulateWindowDates(){
        Enrollment enrollment = new Enrollment("external_id_1", "schedule_1", null, null, null, null, null);

        when(enrollmentService.getStartOfWindowForCurrentMilestone(enrollment, WindowName.earliest)).thenReturn(newDateTime(2011, 12, 1, 0, 0, 0));
        when(enrollmentService.getStartOfWindowForCurrentMilestone(enrollment, WindowName.due)).thenReturn(newDateTime(2011, 12, 8, 0, 0, 0));
        when(enrollmentService.getStartOfWindowForCurrentMilestone(enrollment, WindowName.late)).thenReturn(newDateTime(2011, 12, 15, 0, 0, 0));
        when(enrollmentService.getStartOfWindowForCurrentMilestone(enrollment, WindowName.max)).thenReturn(newDateTime(2011, 12, 22, 0, 0, 0));

        EnrollmentRecord record = enrollmentRecordMapper.mapWithDates(enrollment);

        assertEquals("external_id_1", record.getExternalId());
        assertEquals("schedule_1", record.getScheduleName());
        assertEquals(newDateTime(2011, 12, 1, 0, 0, 0), record.getStartOfEarliestWindow());
        assertEquals(newDateTime(2011, 12, 8, 0, 0, 0), record.getStartOfDueWindow());
        assertEquals(newDateTime(2011, 12, 15, 0, 0, 0), record.getStartOfLateWindow());
        assertEquals(newDateTime(2011, 12, 22, 0, 0, 0), record.getStartOfMaxWindow());
    }
}
