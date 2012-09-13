package org.motechproject.appointments.api.it;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.appointments.api.model.AppointmentCalendar;
import org.motechproject.appointments.api.model.Visit;
import org.motechproject.appointments.api.repository.AllAppointmentCalendars;
import org.motechproject.appointments.api.service.contract.VisitResponse;
import org.motechproject.appointments.api.service.contract.VisitsQuery;
import org.motechproject.appointments.api.service.impl.VisitsQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.motechproject.util.DateUtil.newDateTime;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:META-INF/motech/*.xml")
public class VisitsQueryServiceIT {
    @Autowired
    AllAppointmentCalendars allAppointmentCalendars;
    @Autowired
    VisitsQueryService visitsQueryService;

    @After
    public void tearDown() throws Exception {
        allAppointmentCalendars.removeAll();
    }

    @Test
    public void shouldFetchMissedVisits() {
        Visit visit1 = new Visit().name("visit1").addAppointment(newDateTime(2011, 6, 5, 0, 0, 0), null).visitDate(newDateTime(2011, 6, 5, 0, 0, 0));
        Visit visit2 = new Visit().name("visit2").addAppointment(newDateTime(2011, 7, 1, 0, 0, 0), null);
        Visit visit3 = new Visit().name("visit3").addAppointment(newDateTime(2011, 8, 3, 0, 0, 0), null).visitDate(newDateTime(2011, 8, 3, 0, 0, 0));
        Visit visit4 = new Visit().name("visit4").addAppointment(newDateTime(2011, 9, 5, 0, 0, 0), null);
        Visit visit5 = new Visit().name("visit5").addAppointment(newDateTime(2011, 10, 2, 0, 0, 0), null);

        AppointmentCalendar appointmentCalendar1 = new AppointmentCalendar().externalId("foo1").addVisit(visit1).addVisit(visit2);
        AppointmentCalendar appointmentCalendar2 = new AppointmentCalendar().externalId("foo2").addVisit(visit3).addVisit(visit4).addVisit(visit5);
        allAppointmentCalendars.add(appointmentCalendar1);
        allAppointmentCalendars.add(appointmentCalendar2);

        DateTime start = newDateTime(2011, 7, 1, 0, 0, 0);
        DateTime end = newDateTime(2011, 10, 1, 0, 0, 0);
        List<VisitResponse> missedVisits = visitsQueryService.search(new VisitsQuery().withDueDateIn(start, end).unvisited());
        assertEquals(asList("visit2", "visit4"), extract(missedVisits, on(VisitResponse.class).getName()));
    }

    @Test
    public void shouldReturnVisitResponseWithExternalId() {
        Visit visit1 = new Visit().name("visit1").addAppointment(newDateTime(2011, 6, 5, 0, 0, 0), null).visitDate(newDateTime(2011, 6, 5, 0, 0, 0));
        Visit visit2 = new Visit().name("visit2").addAppointment(newDateTime(2011, 7, 1, 0, 0, 0), null);
        Visit visit3 = new Visit().name("visit3").addAppointment(newDateTime(2011, 7, 2, 0, 0, 0), null);

        AppointmentCalendar appointmentCalendar1 = new AppointmentCalendar().externalId("foo1").addVisit(visit1).addVisit(visit2);
        AppointmentCalendar appointmentCalendar2 = new AppointmentCalendar().externalId("foo2").addVisit(visit3);
        allAppointmentCalendars.add(appointmentCalendar1);
        allAppointmentCalendars.add(appointmentCalendar2);

        List<VisitResponse> visitsResults = visitsQueryService.search(new VisitsQuery().havingExternalId("foo1"));
        assertEquals(asList("foo1", "foo1"), extract(visitsResults, on(VisitResponse.class).getExternalId()));
    }

    @Test
    public void shouldFetchVisitsThatAreDueForaGivenExternalId() {
        Visit visit1 = new Visit().name("visit1").addAppointment(newDateTime(2011, 6, 5, 0, 0, 0), null).visitDate(newDateTime(2011, 6, 5, 0, 0, 0));
        Visit visit2 = new Visit().name("visit2").addAppointment(newDateTime(2011, 7, 1, 0, 0, 0), null);
        Visit visit3 = new Visit().name("visit3").addAppointment(newDateTime(2011, 8, 3, 0, 0, 0), null).visitDate(newDateTime(2011, 8, 3, 0, 0, 0));
        Visit visit4 = new Visit().name("visit4").addAppointment(newDateTime(2011, 9, 5, 0, 0, 0), null);
        Visit visit5 = new Visit().name("visit5").addAppointment(newDateTime(2011, 10, 2, 0, 0, 0), null);
        Visit visit6 = new Visit().name("visit6").addAppointment(newDateTime(2011, 10, 20, 0, 0, 0), null);
        Visit visit7 = new Visit().name("visit7").addAppointment(newDateTime(2011, 11, 2, 0, 0, 0), null);

        AppointmentCalendar appointmentCalendar1 = new AppointmentCalendar().externalId("foo1").addVisit(visit1).addVisit(visit2);
        AppointmentCalendar appointmentCalendar2 = new AppointmentCalendar().externalId("foo2").addVisit(visit3).addVisit(visit4).addVisit(visit5);
        AppointmentCalendar appointmentCalendar3 = new AppointmentCalendar().externalId("foo3").addVisit(visit6).addVisit(visit7);
        allAppointmentCalendars.add(appointmentCalendar1);
        allAppointmentCalendars.add(appointmentCalendar2);
        allAppointmentCalendars.add(appointmentCalendar3);

        DateTime start = newDateTime(2011, 7, 1, 0, 0, 0);
        DateTime end = newDateTime(2011, 11, 1, 0, 0, 0);
        List<VisitResponse> dueVisits = visitsQueryService.search(new VisitsQuery().havingExternalId("foo2").withDueDateIn(start, end).unvisited());
        assertEquals(asList("visit4", "visit5"), extract(dueVisits, on(VisitResponse.class).getName()));
    }
}