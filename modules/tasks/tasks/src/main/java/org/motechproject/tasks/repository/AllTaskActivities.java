package org.motechproject.tasks.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.tasks.domain.TaskActivity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@View(name = "by_taskId", map = "function(doc) { if(doc.type === 'TaskActivity') emit(doc.task); }")
public class AllTaskActivities extends MotechBaseRepository<TaskActivity> {

    @Autowired
    public AllTaskActivities(CouchDbConnector db) {
        super(TaskActivity.class, db);
    }

    public List<TaskActivity> byTaskId(final String taskId) {
        return queryView("by_taskId", taskId);
    }

}
