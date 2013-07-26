package org.motechproject.tasks.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.DocumentNotFoundException;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult;
import org.ektorp.support.View;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.tasks.domain.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A repository class for storing a {@link Task} into a couchdb database
 */
@Repository
public class AllTasks extends MotechBaseRepository<Task> {

    @Autowired
    public AllTasks(@Qualifier("taskDbConnector") final CouchDbConnector connector) {
        super(Task.class, connector);
    }

    public void addOrUpdate(Task task) {
        boolean exists = null != task.getId();
        Task existing = null;

        if (exists) {
            // When a user imports a task it contains '_id' property
            // but in database a task with this id may not exist
            // that's why we have to check if the task really exists in db
            try {
                existing = get(task.getId());
            } catch (DocumentNotFoundException ex) {
                exists = false;
            }
        }

        if (exists) {
            existing.setActions(task.getActions());
            existing.setDescription(task.getDescription());
            existing.setEnabled(task.isEnabled());
            existing.setHasRegisteredChannel(task.hasRegisteredChannel());
            existing.setTaskConfig(task.getTaskConfig());
            existing.setTrigger(task.getTrigger());
            existing.setName(task.getName());
            existing.setValidationErrors(task.getValidationErrors());

            update(existing);
        } else {
            add(task);
        }
    }

    @View(
            name = "by_triggerSubject",
            map = "function(doc) { if(doc.type === 'Task') emit(doc.trigger.subject); }"
    )
    public List<Task> byTriggerSubject(final String subject) {
        return queryView("by_triggerSubject", subject);
    }

    @View(
        name = "byModuleName",
        map = "function(doc) {" +
                 "if (doc.type === 'Task') {" +
                    "emit(doc.trigger.moduleName, doc._id);" +
                    "for (var i = 0; i < doc.actions.length; i++) {" +
                        "emit(doc.actions[i].moduleName, doc._id);" +
                    "}" +
                 "}" +
              "}"
    )
    public List<Task> dependentOnModule(String moduleName) {
        ViewResult idsResult = db.queryView(createQuery("byModuleName").key(moduleName));
        Set<String> taskIds = new HashSet<>();
        for (ViewResult.Row row : idsResult.getRows()) {
            taskIds.add(row.getValue());
        }
        return db.queryView(new ViewQuery().allDocs().includeDocs(true).keys(taskIds), Task.class);
    }
}
