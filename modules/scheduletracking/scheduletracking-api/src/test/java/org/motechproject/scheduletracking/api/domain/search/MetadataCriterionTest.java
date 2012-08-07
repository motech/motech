package org.motechproject.scheduletracking.api.domain.search;

import org.junit.Test;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.Schedule;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MetadataCriterionTest {

    @Test
    public void shouldFetchFromDbUsingCriteria() {
        AllEnrollments allEnrollments = mock(AllEnrollments.class);
        List<Enrollment> result = mock(List.class);
        when(allEnrollments.findByMetadataProperty("foo", "bar")).thenReturn(result);
        assertEquals(result, new MetadataCriterion("foo", "bar").fetch(allEnrollments, null));
    }

    @Test
    public void shouldFilterByMetadata() {
        Schedule schedule = new Schedule("my_schedule");
        List<Enrollment> enrollments = new ArrayList<Enrollment>();
        HashMap<String,String> metadata1 = new HashMap<String, String>(),metadata2 = new HashMap<String, String>(), metadata3 = new HashMap<String, String>(), metadata4 = new HashMap<String, String>();

        metadata1.put("foo","bar");
        metadata1.put("fuu", "bar");
        enrollments.add(new Enrollment().setExternalId("entity1").setSchedule(schedule).setCurrentMilestoneName(null).setStartOfSchedule(null).setEnrolledOn(null).setPreferredAlertTime(null).setStatus(null).setMetadata(metadata1));

        metadata2.put("foo", "baz");
        metadata2.put("fuu", "biz");
        enrollments.add(new Enrollment().setExternalId("entity2").setSchedule(schedule).setCurrentMilestoneName(null).setStartOfSchedule(null).setEnrolledOn(null).setPreferredAlertTime(null).setStatus(null).setMetadata(metadata2));

        metadata3.put("foo","bar");
        enrollments.add(new Enrollment().setExternalId("entity3").setSchedule(schedule).setCurrentMilestoneName(null).setStartOfSchedule(null).setEnrolledOn(null).setPreferredAlertTime(null).setStatus(null).setMetadata(metadata3));

        metadata4.put("foo", "boz");
        metadata4.put("fuu", "ber");
        enrollments.add(new Enrollment().setExternalId("entity4").setSchedule(schedule).setCurrentMilestoneName(null).setStartOfSchedule(null).setEnrolledOn(null).setPreferredAlertTime(null).setStatus(null).setMetadata(metadata4));

        enrollments.add(new Enrollment().setExternalId("entity5").setSchedule(schedule).setCurrentMilestoneName(null).setStartOfSchedule(null).setEnrolledOn(null).setPreferredAlertTime(null).setStatus(null).setMetadata(null));

        List<Enrollment> filtered = new MetadataCriterion("foo", "bar").filter(enrollments, null);
        assertEquals(asList(new String[]{ "entity1", "entity3" }), extract(filtered, on(Enrollment.class).getExternalId()));
    }


}
