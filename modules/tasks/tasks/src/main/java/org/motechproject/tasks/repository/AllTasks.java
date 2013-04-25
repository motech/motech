package org.motechproject.tasks.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.tasks.domain.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllTasks extends MotechBaseRepository<Task> {

    @Autowired
    public AllTasks(@Qualifier("taskDbConnector") final CouchDbConnector connector) {
        super(Task.class, connector);
    }

    public void addOrUpdate(Task task) {
        if (task.getId() != null) {
            Task existing = get(task.getId());

            existing.setAction(task.getAction());
            existing.setActionInputFields(task.getActionInputFields());
            existing.setAdditionalData(task.getAdditionalData());
            existing.setDescription(task.getDescription());
            existing.setEnabled(task.isEnabled());
            existing.setFilters(task.getFilters());
            existing.setTrigger(task.getTrigger());
            existing.setName(task.getName());
            existing.setValidationErrors(task.getValidationErrors());

            update(existing);
        } else {
            add(task);
        }
    }

}
