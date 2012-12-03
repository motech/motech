package org.motechproject.scheduletracking.api.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.scheduletracking.api.domain.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllSchedules extends MotechBaseRepository<Schedule> {

    @Autowired
    public AllSchedules(@Qualifier("scheduleTrackingDbConnector") CouchDbConnector db) {
        super(Schedule.class, db);
    }

    @View(name = "by_name", map = "function(doc) { if(doc.type === 'Schedule') emit(doc.name); }")
    public Schedule getByName(String name) {
        List<Schedule> records = queryView("by_name", name);
        if (records.isEmpty()) {
            return null;
        }
        return singleResult(records);
    }

    public void addOrUpdate(Schedule schedule) {
        Schedule existingRecord = getByName(schedule.getName());
        if (existingRecord == null) {
            add(schedule);
        } else {
            existingRecord.merge(schedule);
            update(existingRecord);
        }
    }

    public void remove(String scheduleName) {
        Schedule schedule = getByName(scheduleName);
        if (schedule != null) {
            remove(schedule);
        }
    }
}
