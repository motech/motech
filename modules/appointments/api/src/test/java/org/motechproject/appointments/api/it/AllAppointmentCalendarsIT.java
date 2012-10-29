package org.motechproject.appointments.api.it;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.appointments.api.model.AppointmentCalendar;
import org.motechproject.appointments.api.model.Visit;
import org.motechproject.appointments.api.repository.AllAppointmentCalendars;
import org.motechproject.appointments.api.service.contract.VisitResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.motechproject.util.DateUtil.newDateTime;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:META-INF/motech/*.xml")
public class AllAppointmentCalendarsIT {
    @Autowired
    private AllAppointmentCalendars allAppointmentCalendars;

    @After
    public void tearDown() {
        allAppointmentCalendars.removeAll();
    }

    @Test
    public void testSaveAppointmentCalender() {
        AppointmentCalendar appointmentCalendar = new AppointmentCalendar().externalId("externalId");
        allAppointmentCalendars.saveAppointmentCalendar(appointmentCalendar);
        assertNotNull(appointmentCalendar.getId());
    }

    @Test
    public void testFindByExternalId() {
        Visit visit1 = new Visit().name("Visit 1");
        Visit visit2 = new Visit().name("Visit 2");

        AppointmentCalendar appointmentCalendar = new AppointmentCalendar().externalId("foo").addVisit(visit1).addVisit(visit2);
        allAppointmentCalendars.saveAppointmentCalendar(appointmentCalendar);

        AppointmentCalendar savedCalender = allAppointmentCalendars.findByExternalId("foo");
        assertNotNull(savedCalender);
        assertEquals(appointmentCalendar.getId(), savedCalender.getId());
    }

    @Test
    public void shouldFetchVisitsWithDueInRange() {
        Visit visit1 = new Visit().name("visit1").addAppointment(newDateTime(2011, 6, 5, 0, 0, 0), null).visitDate(newDateTime(2011, 6, 5, 0, 0, 0));
        Visit visit2 = new Visit().name("visit2").addAppointment(newDateTime(2011, 7, 1, 0, 0, 0), null);
        Visit visit3 = new Visit().name("visit3").addAppointment(newDateTime(2011, 8, 3, 0, 0, 0), null).visitDate(newDateTime(2011, 8, 3, 0, 0, 0));
        Visit visit4 = new Visit().name("visit4").addAppointment(newDateTime(2011, 10, 1, 0, 0, 0), null);
        Visit visit5 = new Visit().name("visit5").addAppointment(newDateTime(2011, 10, 2, 0, 0, 0), null);

        AppointmentCalendar appointmentCalendar1 = new AppointmentCalendar().externalId("foo1").addVisit(visit1).addVisit(visit2);
        AppointmentCalendar appointmentCalendar2 = new AppointmentCalendar().externalId("foo2").addVisit(visit3).addVisit(visit4).addVisit(visit5);
        allAppointmentCalendars.add(appointmentCalendar1);
        allAppointmentCalendars.add(appointmentCalendar2);

        DateTime start = newDateTime(2011, 7, 1, 0, 0, 0);
        DateTime end = newDateTime(2011, 10, 1, 0, 0, 0);
        List<VisitResponse> visitsWithDueInRange = allAppointmentCalendars.findVisitsWithDueDateInRange(start, end);

        assertEquals(asList(new String[]{"visit2", "visit3", "visit4"}), extract(visitsWithDueInRange, on(VisitResponse.class).getName()));
        assertEquals(asList(new String[]{"foo1", "foo2", "foo2"}), extract(visitsWithDueInRange, on(VisitResponse.class).getExternalId()));
    }


    @Test
    public void shouldFetchMissedVisits() {
        Visit visit1 = new Visit().name("visit1").addAppointment(newDateTime(2011, 6, 5, 0, 0, 0), null).visitDate(newDateTime(2011, 6, 5, 0, 0, 0));
        Visit visit2 = new Visit().name("visit2").addAppointment(newDateTime(2011, 7, 1, 0, 0, 0), null);
        Visit visit3 = new Visit().name("visit3").addAppointment(newDateTime(2011, 8, 3, 0, 0, 0), null).visitDate(newDateTime(2011, 8, 3, 0, 0, 0));
        Visit visit4 = new Visit().name("visit4").addAppointment(newDateTime(2011, 10, 1, 0, 0, 0), null);
        Visit visit5 = new Visit().name("visit5").addAppointment(newDateTime(2011, 10, 2, 0, 0, 0), null);

        AppointmentCalendar appointmentCalendar1 = new AppointmentCalendar().externalId("foo1").addVisit(visit1).addVisit(visit2);
        AppointmentCalendar appointmentCalendar2 = new AppointmentCalendar().externalId("foo2").addVisit(visit3).addVisit(visit4).addVisit(visit5);
        allAppointmentCalendars.add(appointmentCalendar1);
        allAppointmentCalendars.add(appointmentCalendar2);
        List<VisitResponse> visitsWithDueInRange = allAppointmentCalendars.findMissedVisits();

        assertEquals(asList(new String[]{"visit2", "visit4", "visit5"}), extract(visitsWithDueInRange, on(VisitResponse.class).getName()));
        assertEquals(asList(new String[]{"foo1", "foo2", "foo2"}), extract(visitsWithDueInRange, on(VisitResponse.class).getExternalId()));
    }

    @Test
    public void shouldFetchVisitsByExternalId() {
        Visit visit1 = new Visit().name("visit1").addAppointment(newDateTime(2011, 6, 5, 0, 0, 0), null).visitDate(newDateTime(2011, 6, 5, 0, 0, 0));
        Visit visit2 = new Visit().name("visit2").addAppointment(newDateTime(2011, 7, 1, 0, 0, 0), null);
        Visit visit3 = new Visit().name("visit3").addAppointment(newDateTime(2011, 8, 3, 0, 0, 0), null).visitDate(newDateTime(2011, 8, 3, 0, 0, 0));
        Visit visit4 = new Visit().name("visit4").addAppointment(newDateTime(2011, 10, 1, 0, 0, 0), null);
        Visit visit5 = new Visit().name("visit5").addAppointment(newDateTime(2011, 10, 2, 0, 0, 0), null);

        AppointmentCalendar appointmentCalendar1 = new AppointmentCalendar().externalId("foo1").addVisit(visit1).addVisit(visit2);
        AppointmentCalendar appointmentCalendar2 = new AppointmentCalendar().externalId("foo2").addVisit(visit3).addVisit(visit4).addVisit(visit5);
        allAppointmentCalendars.add(appointmentCalendar1);
        allAppointmentCalendars.add(appointmentCalendar2);
        List<VisitResponse> visitsWithDueInRange = allAppointmentCalendars.findVisitsByExternalId("foo2");

        assertEquals(asList(new String[]{"visit3", "visit4", "visit5"}), extract(visitsWithDueInRange, on(VisitResponse.class).getName()));
        assertEquals(asList(new String[]{"foo2", "foo2", "foo2"}), extract(visitsWithDueInRange, on(VisitResponse.class).getExternalId()));
    }

    @Test
    public void shouldReturnEmptyVisitResponseIfAppointmentCalendarIsNotPresent() {
        assertThat(allAppointmentCalendars.findVisitsByExternalId("someExternalId"), is(Collections.<VisitResponse>emptyList()));
    }

    @Test
    public void shouldReturnVisitsMatchingGivenMetadataPropertyValuePair() {
        Visit visit1 = new Visit().name("visit1").addAppointment(newDateTime(2011, 7, 1, 0, 0, 0), null);
        visit1.addData("key1", "val1");
        visit1.addData("key2", "val2");
        Visit visit2 = new Visit().name("visit2").addAppointment(newDateTime(2011, 7, 1, 0, 0, 0), null);
        visit2.addData("key1", "val1");
        Visit visit3 = new Visit().name("visit3").addAppointment(newDateTime(2011, 8, 3, 0, 0, 0), null).visitDate(newDateTime(2011, 8, 3, 0, 0, 0));
        Visit visit4 = new Visit().name("visit4").addAppointment(newDateTime(2011, 10, 1, 0, 0, 0), null);
        visit4.addData("key1", "val2");

        allAppointmentCalendars.add(new AppointmentCalendar().externalId("foo1").addVisit(visit1).addVisit(visit2));
        allAppointmentCalendars.add(new AppointmentCalendar().externalId("foo2").addVisit(visit3).addVisit(visit4));

        List<VisitResponse> result = allAppointmentCalendars.findByMetadataProperty("key1", "val1");
        assertEquals(2, result.size());

        final List<String> vistNamelList = extract(result, on(VisitResponse.class).getName());
        Collections.sort(vistNamelList);
        assertEquals(asList(new String[]{"visit1", "visit2"}), vistNamelList);
    }
}
