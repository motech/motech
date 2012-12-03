package org.motechproject.scheduletracking.api.it;

import org.ektorp.CouchDbConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.scheduletracking.api.domain.Schedule;
import org.motechproject.scheduletracking.api.domain.ScheduleFactory;
import org.motechproject.scheduletracking.api.domain.json.ScheduleRecord;
import org.motechproject.scheduletracking.api.repository.AllSchedules;
import org.motechproject.scheduletracking.api.repository.TrackedSchedulesJsonReader;
import org.motechproject.scheduletracking.api.repository.TrackedSchedulesJsonReaderImpl;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static junit.framework.Assert.assertNull;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:META-INF/motech/*.xml")
public class AllSchedulesIT {
    @Autowired
    ScheduleFactory scheduleFactory;
    @Autowired
    @Qualifier("scheduleTrackingDbConnector")
    CouchDbConnector db;
    @Autowired
    PlatformSettingsService platformSettingsService;

    private TrackedSchedulesJsonReader schedulesJsonReader;

    private AllSchedules allSchedules;

    @After
    public void tearDown() {
        allSchedules.removeAll();
    }

    @Before
    public void setUp() {
        schedulesJsonReader = new TrackedSchedulesJsonReaderImpl();

        allSchedules = new AllSchedules(db);
        List<ScheduleRecord> scheduleRecords = new TrackedSchedulesJsonReaderImpl().getAllSchedules("/schedules");
        for (ScheduleRecord scheduleRecord : scheduleRecords) {
            Schedule schedule = scheduleFactory.build(scheduleRecord, platformSettingsService.getPlatformLocale());
            allSchedules.add(schedule);
        }
    }

    @Test
    public void findScheduleByName() {
        Schedule scheduleRecord = allSchedules.getAll().get(0);
        assertEquals(scheduleRecord, allSchedules.getByName(scheduleRecord.getName()));
    }

    @Test
    public void returnNullIfScheduleNameDoesNotExist() {
        assertEquals(null, allSchedules.getByName("INVALID_NAME"));
    }

    @Test
    public void shouldAddSchedule() throws URISyntaxException, IOException {
        String scheduleJson = readFileToString(new File(getClass().getResource("/foo_schedules/foo-schedule.json").toURI()));
        ScheduleRecord scheduleRecord = schedulesJsonReader.getSchedule(scheduleJson);
        Schedule schedule = scheduleFactory.build(scheduleRecord, platformSettingsService.getPlatformLocale());

        allSchedules.addOrUpdate(schedule);

        Schedule dbRecord = allSchedules.getByName("foo");
        assertNotNull(dbRecord);
        assertEquals("IPTI1", dbRecord.getFirstMilestone().getName());
    }

    @Test
    public void shouldUpdateExistingSchedule() throws URISyntaxException, IOException {
        String scheduleJson = readFileToString(new File(getClass().getResource("/foo_schedules/foo-schedule.json").toURI()));
        ScheduleRecord scheduleRecord = schedulesJsonReader.getSchedule(scheduleJson);
        Schedule schedule = scheduleFactory.build(scheduleRecord, platformSettingsService.getPlatformLocale());

        allSchedules.addOrUpdate(schedule);

        String newScheduleJson = readFileToString(new File(getClass().getResource("/foo_schedules/new-foo-schedule.json").toURI()));
        ScheduleRecord newScheduleRecord = schedulesJsonReader.getSchedule(newScheduleJson);
        Schedule newSchedule = scheduleFactory.build(newScheduleRecord, platformSettingsService.getPlatformLocale());

        allSchedules.addOrUpdate(newSchedule);

        Schedule dbRecord = allSchedules.getByName("foo");
        assertNotNull(dbRecord);
        assertEquals("IPTI0", dbRecord.getFirstMilestone().getName());
    }

    @Test
    public void shouldRemoveScheduleByName() throws URISyntaxException, IOException {
        String scheduleJson = readFileToString(new File(getClass().getResource("/foo_schedules/foo-schedule.json").toURI()));
        ScheduleRecord scheduleRecord = schedulesJsonReader.getSchedule(scheduleJson);
        Schedule schedule = scheduleFactory.build(scheduleRecord, platformSettingsService.getPlatformLocale());
        allSchedules.addOrUpdate(schedule);

        allSchedules.remove("foo");

        assertNull(allSchedules.getByName("foo"));
    }
}
