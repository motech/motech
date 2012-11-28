package org.motechproject.tasks.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.tasks.domain.TaskStatusMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@View(name = "by_taskId", map = "function(doc) { if(doc.type === 'TaskStatusMessage') emit(doc.task); }")
public class AllTaskStatusMessages extends MotechBaseRepository<TaskStatusMessage> {

    @Autowired
    public AllTaskStatusMessages(CouchDbConnector db) {
        super(TaskStatusMessage.class, db);
    }

    public List<TaskStatusMessage> byTaskId(final String taskId) {
        return queryView("by_taskId", taskId);
    }

}
