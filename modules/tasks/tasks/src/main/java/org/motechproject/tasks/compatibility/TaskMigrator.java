package org.motechproject.tasks.compatibility;

import org.motechproject.tasks.domain.mds.task.Task;

/**
 * An interface that can be implemented by classes which want to apply migrations to tasks.
 */
public interface TaskMigrator {

    /**
     * Applies migration to tasks.
     * @param task the task to migrate
     */
    void migrate(Task task);
}
