package org.motechproject.scheduletracking.api.it;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.EnrollmentStatus;
import org.motechproject.scheduletracking.api.domain.WindowName;
import org.motechproject.scheduletracking.api.domain.json.ScheduleRecord;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.motechproject.scheduletracking.api.repository.AllSchedules;
import org.motechproject.scheduletracking.api.repository.TrackedSchedulesJsonReaderImpl;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.EnrollmentsQuery;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;
import static org.motechproject.scheduletracking.api.utility.DateTimeUtil.*;
import static org.motechproject.util.DateUtil.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:testApplicationSchedulerTrackingAPI.xml")
public class EnrollmentsSearchIT {

    @Autowired
    private ScheduleTrackingService scheduleTrackingService;
    @Autowired
    private AllEnrollments allEnrollments;
    @Autowired
    private AllSchedules allSchedules;

    @Before
    public void setUp(){
        List<ScheduleRecord> scheduleRecords = new TrackedSchedulesJsonReaderImpl().getAllSchedules("/schedules");
        for (ScheduleRecord scheduleRecord : scheduleRecords) {
            allSchedules.add(scheduleRecord);
        }
    }

    @After
    public void tearDown() {
        allEnrollments.removeAll();
        allSchedules.removeAll();
    }

    @Test
    public void shouldReturnExternalIdsOfActiveEnrollmentsThatAreEitherInLateOrMaxWindowForAGivenSchedule() {
        DateTime now = now();
        createEnrollment("entity_1", "IPTI Schedule", "IPTI 1", weeksAgo(1), now, new Time(6, 30), EnrollmentStatus.ACTIVE, null);
        createEnrollment("entity_2", "IPTI Schedule", "IPTI 1", weeksAgo(14), now, new Time(6, 30), EnrollmentStatus.ACTIVE, null);
        createEnrollment("entity_3", "IPTI Schedule", "IPTI 1", yearsAgo(1), now, new Time(6, 30), EnrollmentStatus.ACTIVE, null);
        createEnrollment("entity_4", "IPTI Schedule", "IPTI 2", daysAgo(20), daysAgo(20), new Time(6, 30), EnrollmentStatus.ACTIVE, null);
        createEnrollment("entity_5", "IPTI Schedule", "IPTI 1", yearsAgo(1), now, new Time(6, 30), EnrollmentStatus.DEFAULTED, null);
        createEnrollment("entity_6", "Delivery", "Default", yearsAgo(1), now, new Time(6, 30), EnrollmentStatus.ACTIVE, null);

        EnrollmentsQuery query = new EnrollmentsQuery().havingSchedule("IPTI Schedule").currentlyInWindow(WindowName.late, WindowName.max).havingState(EnrollmentStatus.ACTIVE);
        List<EnrollmentRecord> result = scheduleTrackingService.search(query);
        assertEquals(asList(new String[]{ "entity_3", "entity_4" }), extract(result, on(EnrollmentRecord.class).getExternalId()));
    }

    @Test
    public void shouldReturnExternalIdsOfActiveEnrollmentsThatHaveEnteredDueWindowDuringTheLastWeek() {
        DateTime now = now();
        createEnrollment("entity_1", "IPTI Schedule", "IPTI 1", weeksAgo(10), now, new Time(6, 30), EnrollmentStatus.ACTIVE, null);
        createEnrollment("entity_2", "IPTI Schedule", "IPTI 1", weeksAgo(13).minusDays(1), now, new Time(6, 30), EnrollmentStatus.ACTIVE, null);
        createEnrollment("entity_3", "IPTI Schedule", "IPTI 1", weeksAgo(14).plusHours(1), now, new Time(6, 30), EnrollmentStatus.ACTIVE, null);
        createEnrollment("entity_4", "IPTI Schedule", "IPTI 2", weeksAgo(15), now, new Time(6, 30), EnrollmentStatus.ACTIVE, null);
        createEnrollment("entity_5", "IPTI Schedule", "IPTI 1", weeksAgo(13), now, new Time(6, 30), EnrollmentStatus.DEFAULTED, null);
        createEnrollment("entity_6", "Delivery", "Default", weeksAgo(14), now, new Time(6, 30), EnrollmentStatus.ACTIVE, null);

        EnrollmentsQuery query = new EnrollmentsQuery().havingSchedule("IPTI Schedule").havingState(EnrollmentStatus.ACTIVE).havingWindowStartingDuring(WindowName.due, weeksAgo(1), now);
        List<EnrollmentRecord> result = scheduleTrackingService.search(query);
        assertEquals(asList(new String[]{ "entity_2", "entity_3" }), extract(result, on(EnrollmentRecord.class).getExternalId()));
    }

    @Test
    public void shouldReturnExternalIdsOfActiveEnrollmentsThatAreCompletedDuringTheLastWeek() {
        DateTime referenceDate = newDateTime(2012, 10, 1, 0, 0, 0);
        createEnrollment("entity_1", "IPTI Schedule", "IPTI 2", referenceDate, referenceDate, new Time(6, 30), EnrollmentStatus.ACTIVE, null);
        scheduleTrackingService.fulfillCurrentMilestone("entity_1", "IPTI Schedule", newDate(2012, 10, 10), new Time(0, 0));
        createEnrollment("entity_2", "IPTI Schedule", "IPTI 2", referenceDate, referenceDate, new Time(6, 30), EnrollmentStatus.ACTIVE, null);
        scheduleTrackingService.fulfillCurrentMilestone("entity_2", "IPTI Schedule", newDate(2012, 10, 20), new Time(0, 0));
        createEnrollment("entity_3", "IPTI Schedule", "IPTI 1", referenceDate, referenceDate, new Time(6, 30), EnrollmentStatus.ACTIVE, null);
        scheduleTrackingService.fulfillCurrentMilestone("entity_3", "IPTI Schedule", newDate(2012, 10, 8), new Time(0, 0));
        scheduleTrackingService.fulfillCurrentMilestone("entity_3", "IPTI Schedule", newDate(2012, 10, 9), new Time(0, 0));
        createEnrollment("entity_4", "IPTI Schedule", "IPTI 2", referenceDate, referenceDate, new Time(6, 30), EnrollmentStatus.ACTIVE, null);
        createEnrollment("entity_5", "IPTI Schedule", "IPTI 1", referenceDate, referenceDate, new Time(6, 30), EnrollmentStatus.DEFAULTED, null);
        createEnrollment("entity_6", "Delivery", "Default", referenceDate, referenceDate, new Time(6, 30), EnrollmentStatus.ACTIVE, null);

        EnrollmentsQuery query = new EnrollmentsQuery().havingSchedule("IPTI Schedule").completedDuring(newDateTime(2012, 10, 9, 0, 0, 0), newDateTime(2012, 10, 15, 23, 59, 59));
        List<EnrollmentRecord> result = scheduleTrackingService.search(query);

        assertThat(extract(result, on(EnrollmentRecord.class).getExternalId()), hasItems("entity_1", "entity_3"));
    }

    @Test
    public void forAGivenEntityShouldReturnAllSchedulesWithDatesWhoseDueDateFallsInTheLastWeek() {
        DateTime iptiReferenceDate = newDateTime(2012, 1, 1, 0, 0 ,0);
        DateTime deliveryReferenceDateTime = newDateTime(2011, 7, 9, 0, 0 ,0);
        createEnrollment("entity_1", "IPTI Schedule", "IPTI 1", iptiReferenceDate, iptiReferenceDate, new Time(6, 30), EnrollmentStatus.ACTIVE, null);
        createEnrollment("entity_1", "Delivery", "Default", deliveryReferenceDateTime, deliveryReferenceDateTime, new Time(6, 30), EnrollmentStatus.ACTIVE, null);
        createEnrollment("entity_1", "IPTI Schedule", "IPTI 1", iptiReferenceDate, iptiReferenceDate, new Time(6, 30), EnrollmentStatus.DEFAULTED, null);
        createEnrollment("entity_2", "IPTI Schedule", "IPTI 1", iptiReferenceDate, iptiReferenceDate, new Time(6, 30), EnrollmentStatus.ACTIVE, null);

        EnrollmentsQuery query = new EnrollmentsQuery()
                                    .havingExternalId("entity_1")
                                    .havingState(EnrollmentStatus.ACTIVE)
                                    .havingWindowStartingDuring(WindowName.due, newDateTime(2012, 4, 1, 0, 0, 0), newDateTime(2012, 4, 7, 23, 59, 59));

        List<EnrollmentRecord> result = scheduleTrackingService.searchWithWindowDates(query);
        assertEquals(asList(new String[]{ "IPTI Schedule", "Delivery" }), extract(result, on(EnrollmentRecord.class).getScheduleName()));
        assertEquals(asList(new DateTime[]{ newDateTime(2012, 4, 1, 0, 0, 0), newDateTime(2012, 4, 7, 0, 0, 0)}), extract(result, on(EnrollmentRecord.class).getStartOfDueWindow()));
    }

    @Test
    public void shouldFindEnrollmentsByMetadataProperties() {
        HashMap<String,String> metadata;
        metadata = new HashMap<String, String>();
        metadata.put("foo", "bar");
        metadata.put("fuu", "baz");
        createEnrollment("entity1", "Delivery", "milestone1", newDateTime(2010, 1, 1, 0, 0, 0), newDateTime(2010, 1, 1, 0, 0, 0), new Time(0, 0), EnrollmentStatus.ACTIVE, metadata);
        metadata = new HashMap<String, String>();
        metadata.put("foo", "cad");
        metadata.put("fuu", "cab");
        createEnrollment("entity2", "Delivery", "milestone1", newDateTime(2010, 1, 1, 0, 0, 0), newDateTime(2010, 1, 1, 0, 0, 0), new Time(0, 0), EnrollmentStatus.ACTIVE, metadata);
        metadata = new HashMap<String, String>();
        metadata.put("foo", "bar");
        metadata.put("fuu", "baz");
        createEnrollment("entity3", "Delivery", "milestone1", newDateTime(2010, 1, 1, 0, 0, 0), newDateTime(2010, 1, 1, 0, 0, 0), new Time(0, 0), EnrollmentStatus.ACTIVE, metadata);
        metadata = new HashMap<String, String>();
        metadata.put("foo", "biz");
        metadata.put("fuu", "boz");
        createEnrollment("entity4", "Delivery", "milestone1", newDateTime(2010, 1, 1, 0, 0, 0), newDateTime(2010, 1, 1, 0, 0, 0), new Time(0, 0), EnrollmentStatus.ACTIVE, metadata);
        metadata = new HashMap<String, String>();
        metadata.put("foo", "quz");
        metadata.put("fuu", "qux");
        createEnrollment("entity5", "Delivery", "milestone1", newDateTime(2010, 1, 1, 0, 0, 0), newDateTime(2010, 1, 1, 0, 0, 0), new Time(0, 0), EnrollmentStatus.ACTIVE, metadata);

        assertEquals(asList(new String[]{ "entity1", "entity3" }), extract(allEnrollments.findByMetadataProperty("foo", "bar"), on(Enrollment.class).getExternalId()));
        assertEquals(asList(new String[] { "entity4" }), extract(allEnrollments.findByMetadataProperty("fuu", "boz"), on(Enrollment.class).getExternalId()));
    }

    private Enrollment createEnrollment(String externalId, String scheduleName, String currentMilestoneName, DateTime referenceDateTime, DateTime enrollmentDateTime, Time preferredAlertTime, EnrollmentStatus enrollmentStatus, Map<String,String> metadata) {
        Enrollment enrollment = new Enrollment().setExternalId(externalId).setSchedule(allSchedules.getByName(scheduleName)).setCurrentMilestoneName(currentMilestoneName).setStartOfSchedule(referenceDateTime).setEnrolledOn(enrollmentDateTime).setPreferredAlertTime(preferredAlertTime).setStatus(enrollmentStatus).setMetadata(metadata);
        allEnrollments.add(enrollment);
        return enrollment;
    }
}
