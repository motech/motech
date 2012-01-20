package org.motechproject.scheduletracking.api.service;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.model.CronSchedulableJob;
import org.motechproject.model.Time;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.Milestone;
import org.motechproject.scheduletracking.api.domain.Schedule;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.motechproject.scheduletracking.api.repository.AllTrackedSchedules;
import org.motechproject.util.DateUtil;
import org.motechproject.valueobjects.WallTime;
import org.motechproject.valueobjects.WallTimeUnit;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.scheduletracking.api.utility.DateTimeUtil.wallTimeOf;

public class ScheduleTrackingServiceImplTest {
	@Mock
	private AllTrackedSchedules allTrackedSchedules;
	@Mock
	private MotechSchedulerService schedulerService;
	@Mock
	private AllEnrollments allEnrollments;

	@Before
	public void setUp() {
		initMocks(this);
	}

	@Test
	public void shouldCreateTheScheduleWhenAPatientIsEnrolled() {
		String scheduleName = "scheduleName";
		String externalId = "externalId";

		Milestone milestone = new Milestone("milestoneName", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
		when(allTrackedSchedules.get(scheduleName)).thenReturn(new Schedule(scheduleName, new WallTime(10, WallTimeUnit.Week), milestone));

		LocalDate referenceDate = DateUtil.newDate(2012, 1, 2);
		ScheduleTrackingService scheduleTrackingService = new ScheduleTrackingServiceImpl(schedulerService, allTrackedSchedules, allEnrollments);
		scheduleTrackingService.enroll(new EnrollmentRequest(externalId, scheduleName, new Time(8, 10), referenceDate));

		verify(allTrackedSchedules, times(1)).get(scheduleName);

		ArgumentCaptor<Enrollment> enrollmentArgumentCaptor = ArgumentCaptor.forClass(Enrollment.class);
		verify(allEnrollments, times(1)).add(enrollmentArgumentCaptor.capture());
		Enrollment enrollment = enrollmentArgumentCaptor.getValue();
		assertEquals(externalId, enrollment.getExternalId());
		assertEquals(scheduleName, enrollment.getSchedule().getName());

		ArgumentCaptor<CronSchedulableJob> cronSchedulableJobArgumentCaptor = ArgumentCaptor.forClass(CronSchedulableJob.class);
		verify(schedulerService).scheduleJob(cronSchedulableJobArgumentCaptor.capture());
		CronSchedulableJob cronSchedulableJob = cronSchedulableJobArgumentCaptor.getValue();
		assertEquals("0 10/0 8-8 * * ?", cronSchedulableJob.getCronExpression());
		assertEquals(DateUtil.today().toDate(), cronSchedulableJob.getStartTime());
		assertEquals(referenceDate.plusWeeks(10).toDate(), cronSchedulableJob.getEndTime());

	}
}
