package org.motechproject.tasks.compatibility;

import org.motechproject.tasks.domain.mds.task.Task;
import org.motechproject.tasks.repository.TasksDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Set;

/**
 * The manager used for applying migrations to tasks. It gets all {@link TaskMigrator} beans from the
 * Spring context and applies them to the task being migrated. It can be called manually, it also triggers during
 * initialization to make sure the tasks in the database are up to date.
 */
@Component
public class TaskMigrationManager {

    @Autowired
    private Set<TaskMigrator> migrators;

    @Autowired
    private TasksDataService tasksDataService;

    /**
     * Migrates all tasks in the database.
     */
    @PostConstruct
    public void init() {
        // @Transactional does not work with @PostConstruct
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

    /**
     * Migrates a single task.
     * @param task the task to migrate
     */
    @Transactional
    public void migrateTask(Task task) {
        for (TaskMigrator migrator : migrators) {
            migrator.migrate(task);
        }
    }
}
