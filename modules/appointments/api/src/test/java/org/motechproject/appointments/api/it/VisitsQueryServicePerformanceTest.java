package org.motechproject.appointments.api.it;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.appointments.api.model.AppointmentCalendar;
import org.motechproject.appointments.api.model.Visit;
import org.motechproject.appointments.api.repository.AllAppointmentCalendars;
import org.motechproject.appointments.api.service.AppointmentService;
import org.motechproject.appointments.api.service.contract.VisitsQuery;
import org.motechproject.appointments.api.service.impl.VisitsQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static java.lang.System.currentTimeMillis;
import static org.motechproject.commons.date.util.DateUtil.newDateTime;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:META-INF/motech/*.xml")
public class VisitsQueryServicePerformanceTest {
    private static final int MAX_CALENDARS = 1000;

    @Autowired
    AllAppointmentCalendars allAppointmentCalendars;

    @Autowired
    AppointmentService appointmentService;
    @Autowired
    VisitsQueryService visitsQueryService;
    private Random random;

    @Before
    public void setUp() {
        List<AppointmentCalendar> calendars = new ArrayList<AppointmentCalendar>();

        random = new Random();

        for (int i = 0; i < MAX_CALENDARS; i++) {
            AppointmentCalendar calendar = new AppointmentCalendar().externalId(UUID.randomUUID().toString());
            int numberOfVisits = 10;
            for (int j = 0; j < numberOfVisits; j++)
                calendar.addVisit(randomVisit());
            calendars.add(calendar);
        }

        for (AppointmentCalendar calendar : calendars)
            allAppointmentCalendars.add(calendar);

        allAppointmentCalendars.getAll();
    }

    @After
    public void tearDown() {
        allAppointmentCalendars.removeAll();
    }

    private Visit randomVisit() {
        Visit visit = new Visit().addAppointment(randomDateTime(), null);
        if (rand(0, 1) == 0)
            visit.visitDate(visit.appointment().dueDate());
        return visit;
    }

    private DateTime randomDateTime() {
        return newDateTime(rand(2009, 2011), rand(1, 12), rand(1, 28), 0, 0, 0);
    }

    private int rand(int start, int end) {
        return start + random.nextInt(end - start);
    }

    @Test
    @Ignore
    public void execute() {
        DateTime start = newDateTime(2011, 7, 1, 0, 0, 0);
        DateTime end = newDateTime(2011, 10, 1, 0, 0, 0);
        long execStart = currentTimeMillis();
        visitsQueryService.search(new VisitsQuery().withDueDateIn(start, end).unvisited());
        long execEnd = currentTimeMillis();
        System.out.println(execEnd - execStart);
    }
}
