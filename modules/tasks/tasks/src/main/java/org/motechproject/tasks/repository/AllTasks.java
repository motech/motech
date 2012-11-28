package org.motechproject.tasks.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.tasks.domain.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@View(name = "by_id", map = "function(doc) { if(doc.type === 'Task') emit(doc._id); }")
public class AllTasks extends MotechBaseRepository<Task> {

    @Autowired
    public AllTasks(final CouchDbConnector connector) {
        super(Task.class, connector);
    }

    public void addOrUpdate(Task task) {
        addOrReplace(task, "id", task.getId());
    }

}
