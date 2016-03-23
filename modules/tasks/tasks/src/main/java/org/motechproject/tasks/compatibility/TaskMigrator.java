package org.motechproject.tasks.compatibility;

import org.motechproject.tasks.domain.Task;

public interface TaskMigrator {

    void migrate(Task task);
}
