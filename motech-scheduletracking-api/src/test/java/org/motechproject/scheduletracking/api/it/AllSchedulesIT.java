package org.motechproject.scheduletracking.api.it;

import org.ektorp.CouchDbConnector;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.scheduletracking.api.domain.ScheduleFactory;
import org.motechproject.scheduletracking.api.domain.json.ScheduleRecord;
import org.motechproject.scheduletracking.api.repository.AllSchedules;
import org.motechproject.scheduletracking.api.repository.TrackedSchedulesJsonReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:testApplicationSchedulerTrackingAPI.xml")
public class AllSchedulesIT {
    @Autowired
    TrackedSchedulesJsonReader trackedSchedulesJsonReader;
    @Autowired
    ScheduleFactory scheduleFactory;
    @Autowired
    @Qualifier("scheduleTrackingDbConnector")
    CouchDbConnector db;

    @Test
    public void shouldSaveScheduleDefinitionsOnInit() {
        List<ScheduleRecord> records = trackedSchedulesJsonReader.records();

        AllSchedules allSchedules = new AllSchedules(db, trackedSchedulesJsonReader, scheduleFactory);

        List<ScheduleRecord> result = allSchedules.getAll();
        assertArrayEquals(records.toArray(), result.toArray());
    }

    @Test
    public void shouldDeleteExistingSchedulesOnInit() {
        List<ScheduleRecord> records = trackedSchedulesJsonReader.records();

        new AllSchedules(db, trackedSchedulesJsonReader, scheduleFactory);
        AllSchedules allSchedules = new AllSchedules(db, trackedSchedulesJsonReader, scheduleFactory);

        List<ScheduleRecord> result = allSchedules.getAll();
        assertArrayEquals(records.toArray(), result.toArray());
    }

    @Test
    public void findScheduleByName() {
        AllSchedules allSchedules = new AllSchedules(db, trackedSchedulesJsonReader, scheduleFactory);
        ScheduleRecord scheduleRecord = trackedSchedulesJsonReader.records().get(0);

        assertEquals(new ScheduleFactory().build(scheduleRecord), allSchedules.getByName(scheduleRecord.name()));
    }

    @Test
    public void returnNullIfScheduleNameDoesNotExist() {
        AllSchedules allSchedules = new AllSchedules(db, trackedSchedulesJsonReader, scheduleFactory);
        assertEquals(null, allSchedules.getByName("INVALID_NAME"));
    }
}
