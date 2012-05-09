package org.motechproject.scheduletracking.api.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.scheduletracking.api.domain.Schedule;
import org.motechproject.scheduletracking.api.domain.ScheduleFactory;
import org.motechproject.scheduletracking.api.domain.json.ScheduleRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllSchedules extends MotechBaseRepository<ScheduleRecord> {

    private TrackedSchedulesJsonReader trackedSchedulesJsonReader;
    private ScheduleFactory scheduleFactory;

    @Autowired
    public AllSchedules(@Qualifier("scheduleTrackingDbConnector") CouchDbConnector db, TrackedSchedulesJsonReader trackedSchedulesJsonReader, ScheduleFactory scheduleFactory) {
        super(ScheduleRecord.class, db);
        this.trackedSchedulesJsonReader = trackedSchedulesJsonReader;
        this.scheduleFactory = scheduleFactory;
        removeAll();
        List<ScheduleRecord> records = trackedSchedulesJsonReader.records();
        for (ScheduleRecord record : records)
            add(record);
    }

    @View(name = "by_name", map = "function(doc) { emit(doc.name); }")
    public Schedule getByName(String name) {
        List<ScheduleRecord> records = queryView("by_name", name);
        if (records.isEmpty())
            return null;
        return scheduleFactory.build(records.get(0));
    }
}