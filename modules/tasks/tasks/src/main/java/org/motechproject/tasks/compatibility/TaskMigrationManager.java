package org.motechproject.tasks.compatibility;

import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.repository.TasksDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Set;

@Component
public class TaskMigrationManager {

    @Autowired
    private Set<TaskMigrator> migrators;

    @Autowired
    private TasksDataService tasksDataService;

    @PostConstruct
    public void init() {
        tasksDataService.doInTransaction(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                List<Task> allTasks = tasksDataService.retrieveAll();
                for (Task task : allTasks) {
                    migrateTask(task);
                }
            }
        });
    }

    @Transactional
    public void migrateTask(Task task) {
        for (TaskMigrator migrator : migrators) {
            migrator.migrate(task);
        }
    }
}
