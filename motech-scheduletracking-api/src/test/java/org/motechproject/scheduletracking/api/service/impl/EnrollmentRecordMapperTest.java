package org.motechproject.scheduletracking.api.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.WindowName;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.util.DateUtil;

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
        final Enrollment enrollment = new Enrollment("externalId", "scheduleName", "milestoneX", DateUtil.newDateTime(2000, 2, 1, 0, 0, 0), DateUtil.newDateTime(2000, 2, 10, 0, 0, 0), new Time(10, 10), null, null);
        final EnrollmentRecord record = enrollmentRecordMapper.map(enrollment);

        assertRecordMatchesEnrollment(record, enrollment);

        assertThat(new EnrollmentRecordMapper(enrollmentService).map(null), is(equalTo(null)));
    }

    @Test
    public void shouldMapEnrollmentToEnrollmentResponseAndPopulateWindowDates(){
        Enrollment enrollment = new Enrollment("external_id_1", "schedule_1", "milestoneX", null, null, null, null, null);

        when(enrollmentService.getStartOfWindowForCurrentMilestone(enrollment, WindowName.earliest)).thenReturn(newDateTime(2011, 12, 1, 0, 0, 0));
        when(enrollmentService.getStartOfWindowForCurrentMilestone(enrollment, WindowName.due)).thenReturn(newDateTime(2011, 12, 8, 0, 0, 0));
        when(enrollmentService.getStartOfWindowForCurrentMilestone(enrollment, WindowName.late)).thenReturn(newDateTime(2011, 12, 15, 0, 0, 0));
        when(enrollmentService.getStartOfWindowForCurrentMilestone(enrollment, WindowName.max)).thenReturn(newDateTime(2011, 12, 22, 0, 0, 0));

        EnrollmentRecord record = enrollmentRecordMapper.mapWithDates(enrollment);

        assertRecordMatchesEnrollment(record, enrollment);

        assertThat(record.getStartOfEarliestWindow(), is(newDateTime(2011, 12, 1, 0, 0, 0)));
        assertThat(record.getStartOfDueWindow(), is(newDateTime(2011, 12, 8, 0, 0, 0)));
        assertThat(record.getStartOfLateWindow(), is(newDateTime(2011, 12, 15, 0, 0, 0)));
        assertThat(record.getStartOfMaxWindow(), is(newDateTime(2011, 12, 22, 0, 0, 0)));
    }

    private void assertRecordMatchesEnrollment(EnrollmentRecord actualRecord, Enrollment expectedEnrollment) {
        assertThat(actualRecord.getExternalId(), is(equalTo(expectedEnrollment.getExternalId())));
        assertThat(actualRecord.getScheduleName(), is(equalTo(expectedEnrollment.getScheduleName())));
        assertThat(actualRecord.getReferenceDateTime(), is(equalTo(expectedEnrollment.getReferenceDateTime())));
        assertThat(actualRecord.getPreferredAlertTime(), is(equalTo(expectedEnrollment.getPreferredAlertTime())));
        assertThat(actualRecord.getEnrollmentDateTime(), is(equalTo(expectedEnrollment.getEnrollmentDateTime())));
        assertThat(actualRecord.getCurrentMilestoneName(), is(equalTo(expectedEnrollment.getCurrentMilestoneName())));
    }
}
