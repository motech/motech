package org.motechproject.scheduletracking.api.service.impl;

import org.junit.Test;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.service.EnrollmentResponse;
import org.motechproject.util.DateUtil;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class EnrollmentResponseMapperTest {
    @Test
    public void shouldMapEnrollmentToEnrollmentResponse(){
        final Enrollment enrollment = new Enrollment("externalId", "scheduleName", null, DateUtil.newDateTime(2000, 2, 1, 0, 0, 0), DateUtil.newDateTime(2000, 2, 10, 0, 0, 0), new Time(10, 10), null);
        final EnrollmentResponse response = new EnrollmentResponseMapper().map(enrollment);
        assertThat(response.getExternalId(), is(equalTo(enrollment.getExternalId())));
        assertThat(response.getScheduleName(), is(equalTo(enrollment.getScheduleName())));
        assertThat(response.getReferenceDateTime(), is(equalTo(enrollment.getReferenceDateTime())));
        assertThat(response.getPreferredAlertTime(), is(equalTo(enrollment.getPreferredAlertTime())));
        assertThat(response.getEnrollmentDateTime(), is(equalTo(enrollment.getEnrollmentDateTime())));

        assertThat(new EnrollmentResponseMapper().map(null), is(equalTo(null)));
    }
}
