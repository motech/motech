package org.motechproject.scheduletracking.api.it;

import org.ektorp.CouchDbConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.scheduletracking.api.domain.ScheduleFactory;
import org.motechproject.scheduletracking.api.domain.json.ScheduleRecord;
import org.motechproject.scheduletracking.api.repository.AllSchedules;
import org.motechproject.scheduletracking.api.repository.TrackedSchedulesJsonReaderImpl;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:testApplicationSchedulerTrackingAPI.xml")
public class AllSchedulesIT {
    @Autowired
    ScheduleFactory scheduleFactory;
    @Autowired
    @Qualifier("scheduleTrackingDbConnector")
    CouchDbConnector db;
    @Autowired
    PlatformSettingsService platformSettingsService;

    private AllSchedules allSchedules;

    @After
    public void tearDown() {
        allSchedules.removeAll();
    }

    @Before
    public void setUp() {
        allSchedules = new AllSchedules(db, scheduleFactory, platformSettingsService);
        List<ScheduleRecord> scheduleRecords = new TrackedSchedulesJsonReaderImpl().getAllSchedules("/schedules");
        for (ScheduleRecord scheduleRecord : scheduleRecords) {
            allSchedules.add(scheduleRecord);
        }
    }

    @Test
    public void findScheduleByName() {
        ScheduleRecord scheduleRecord = allSchedules.getAll().get(0);

        assertEquals(new ScheduleFactory().build(scheduleRecord), allSchedules.getByName(scheduleRecord.name()));
    }

    @Test
    public void returnNullIfScheduleNameDoesNotExist() {
        assertEquals(null, allSchedules.getByName("INVALID_NAME"));
    }
}
